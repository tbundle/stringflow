package abs.ixi.server.app.rs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.app.ApplicationInitializationError;
import abs.ixi.server.app.RequestContext;
import abs.ixi.server.app.XmppRequest;
import abs.ixi.server.app.XmppResponse;
import abs.ixi.util.ReflectionUtils;

/**
 * Captures an operation within a resource class represented by
 * {@link ClassResource}. Typically a method mapped to a request end point is
 * considered an operation
 */
class ResourceOperation {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceOperation.class);

	private static final String SLASH = "/";
	private static final String SEGMENT_PARAM_REGEX = "\\{[a-zA-z]*\\}";

	private Method method;
	private String[] uriSegments;
	private FormalParam[] formalParms;
	private Map<String, Integer> segmentParamsTemplet;

	private Predicate<String> predicate;

	/**
	 * Public constructor to instantiate {@link ResourceOperation}
	 * 
	 * @param method
	 *            the method which is the underlying resource opeartion
	 * @param segment
	 *            complete string specified within {@link URISegment} annotation
	 */
	public ResourceOperation(Method method, String segment) {
		this.predicate = Pattern.compile(SEGMENT_PARAM_REGEX).asPredicate();

		this.method = method;
		this.segmentParamsTemplet = new HashMap<>();

		processURISegmentValue(segment);

		findFormalParams(method);
	}

	/**
	 * Process {@link URISegment} value given on a resource operation. The value
	 * contains segments at start followed by {@link URISegmentParam}. A typical
	 * value on {@link URISegment} annotation will be like
	 * "/segment1/segment2/{param1}/{param2}/{param3}"
	 * 
	 * @param val
	 *            value specified for {@link URISegment} annotation
	 */
	private void processURISegmentValue(String val) {
		String[] parts = val.split(SLASH);

		List<String> s = new ArrayList<>();

		for (int i = 0; i < parts.length; i++) {
			if (isSegmentParam(parts[i])) {
				String segmentParamName = removeCurlyBraces(parts[i]);
				this.segmentParamsTemplet.put(segmentParamName, i);

			} else {
				s.add(parts[i]);
			}
		}

		this.uriSegments = new String[s.size()];
		this.uriSegments = s.toArray(this.uriSegments);
	}

	/**
	 * Checks if the string value supplied is indicated as segment parameter. A
	 * segment parameter is enclosed in curly braces ({segmentParam}).
	 * 
	 * @param part
	 *            part of the uri segment
	 * @return true if the string supplied indicates a segment param otherwise
	 *         false
	 */
	private boolean isSegmentParam(String part) {
		return this.predicate.test(part);
	}

	/**
	 * An operation within a resource class will have its formal parameters
	 * annotated with the server supported annotations. Server supports
	 * {@link URISegmentParam}, {@link QueryParam} and {@link MultipartParam}.
	 * Using java reflection, the method finds the annotation used by a formal
	 * parameter and adds to the maps maintained by this
	 * {@link ResourceOperation}
	 * 
	 * @param method
	 *            operation for which formal parameters will be processed
	 */
	private void findFormalParams(Method method) {
		Parameter[] fParams = method.getParameters();

		FormalParam[] fp = new FormalParam[fParams.length];
		int counter = 0;
		if (fParams != null && fParams.length > 0) {
			for (Parameter p : fParams) {
				URISegmentParam usParam = p.getAnnotation(URISegmentParam.class);

				if (usParam != null) {
					if (this.segmentParamsTemplet.containsKey(usParam.name())) {
						fp[counter++] = new FormalParam(usParam.name(), p.getType(), FormalParamType.URI_SEGMENT);
						continue;

					} else {
						LOGGER.error(
								"Method segment param name [{}] not found in method request uri templet for method [{}]",
								usParam.name(), method.getName());

						throw new ApplicationInitializationError(
								"Method segment param name not found in method request uri templet");
					}
				}

				QueryParam qp = p.getAnnotation(QueryParam.class);

				if (qp != null) {
					fp[counter++] = new FormalParam(qp.name(), p.getType(), FormalParamType.QUERY);
					continue;
				}

				MultipartParam mp = p.getAnnotation(MultipartParam.class);

				if (mp != null) {
					fp[counter++] = new FormalParam(mp.name(), p.getType(), FormalParamType.MULTIPART);
					continue;
				}
			}
		}

		this.formalParms = fp;
	}

	/**
	 * This is a make-shift method which needs to be re-written. Currently it
	 * just removes curly braces around a parameter name.
	 */
	private String removeCurlyBraces(String val) {
		String value = val.trim();
		return value.substring(1, val.length() - 1);
	}

	/**
	 * Invoked this operation for the given {@link XmppRequest} with
	 * {@link RequestContext}
	 * 
	 * @param request
	 *            incoming user request
	 * @param ctx
	 *            request context
	 * @return {@link XmppResponse} generated by the application
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public XmppResponse invoke(Object obj, XmppRequest request, RequestContext ctx, Endpoint ep)
			throws IllegalAccessException, InvocationTargetException {

		// TODO validate argument and url structure to decide if this operation
		// can be invoked

		Object[] args = prepareArgs(ctx, ep);

		return (XmppResponse) this.method.invoke(obj, args);
	}

	/**
	 * Prepares method arguments for invocation
	 */
	private Object[] prepareArgs(RequestContext ctx, Endpoint ep) {
		Object[] args = new Object[this.formalParms.length];
		int counter = 0;

		for (FormalParam fp : this.formalParms) {
			if (fp.type == FormalParamType.QUERY) {
				args[counter++] = ReflectionUtils.cast(ep.getQueryParam(fp.getName()).toString(), fp.getJavaTpe());

			} else if (fp.type == FormalParamType.URI_SEGMENT) {
				String s = ep.getSegmentAt(this.segmentParamsTemplet.get(fp.getName()) + ep.getCurrentPosition());
				args[counter++] = ReflectionUtils.cast(s, fp.getJavaTpe());

			} else if (fp.type == FormalParamType.MULTIPART) {
				// TODO Get it from xmpp request from the name
			}
		}

		return args;
	}

	public Method getMethod() {
		return method;
	}

	public String[] getUriSegments() {
		return uriSegments;
	}

	class FormalParam {
		private String name;
		private Class<?> javaTpe;
		private FormalParamType type;

		public FormalParam(String name, Class<?> javaTpe, FormalParamType type) {
			this.name = name;
			this.javaTpe = javaTpe;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Class<?> getJavaTpe() {
			return javaTpe;
		}

		public FormalParamType getType() {
			return type;
		}

	}

	enum FormalParamType {
		URI_SEGMENT,

		QUERY,

		MULTIPART;
	}

}
