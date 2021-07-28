package abs.ixi.server.app.rs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import abs.ixi.util.StringUtils;

/**
 * {@code ResourceEndpoint} is the complete URI to uniquely identify an
 * operation on a resource. From implementation standpoint, it wraps a
 * {@link URI} instance inside and offers convenience method to match a
 * resource, an operation within a resource and also the formal parameters of
 * the operation.
 * <p>
 * Additionally it also tracks the URI segments which has been mapped through
 * the matching process within server.
 * </p>
 */
public class Endpoint {
    private static String SLASH = "/";
    private static final String AND = "&";
    private static final String EQUALS = "=";

    private String[] pathSegments;
    private int psIdx;

    private Map<String, Object> queryParams;

    public Endpoint(String path, String query, int psIdx) throws URISyntaxException {
	assert !StringUtils.isNullOrEmpty(path) : "URI can not be null";

	this.queryParams = new HashMap<>();

	prepareQueryParams(query);

	this.pathSegments = path.split(SLASH);
	this.psIdx = psIdx;
    }

    private void prepareQueryParams(String query) throws URISyntaxException {
	if (!StringUtils.isNullOrEmpty(query)) {
	    String[] parms = query.split(AND);

	    for (String param : parms) {
		String[] p = param.split(EQUALS);

		if (p.length == 2) {
		    this.queryParams.put(p[0], p[1]);

		} else {
		    throw new URISyntaxException(query, "Request Uri's Query params are in bad format");

		}
	    }
	}
    }

    public Map<String, Object> getQueryParams() {
	return queryParams;
    }

    /**
     * Returns true of there is a path segment remaining to match; returns false
     * otherwise.
     */
    public boolean hasUnmatchedSegment() {
	return this.psIdx < (this.pathSegments.length - 1) ? true : false;
    }

    /**
     * @return next segment which needs to be matched. The index does not
     *         advanced in this call therefore calling this method multiple
     *         times will return same result.
     */
    public String unmatchedSegment() {
	return hasUnmatchedSegment() ? this.pathSegments[this.psIdx] : null;
    }

    String getSegmentAt(int position) {
	return position < this.pathSegments.length ? this.pathSegments[position] : null;
    }

    int getCurrentPosition() {
	return psIdx;
    }

    /**
     * Advances URI segment tracking. When a URI segment is matched
     */
    public boolean advance() {
	if (this.psIdx < this.pathSegments.length) {
	    this.psIdx++;
	    return true;
	}

	return false;
    }

    /**
     * @return number of path segments which are unmatched as yet
     */
    public int unmatchedPathSegmentCount() {
	return this.pathSegments.length - this.psIdx;
    }

    /**
     * @return query param from the uri. returns null if not found
     */
    public Object getQueryParam(String key) {
	return this.queryParams.get(key);
    }

}
