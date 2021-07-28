package abs.ixi.server.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abs.ixi.server.app.rs.ClassResource;

/**
 * Basic implementation of {@link Appfront} interface 
 */
public abstract class BasicAppfront implements Appfront {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicAppfront.class);
	
	protected String uriSegment;
	protected Map<String, ClassResource<RequestReceiver>> receivers;

	protected boolean initialized;

	protected BasicAppfront() {
		// default constructor
	}

	public BasicAppfront(String uriSegment) {
		this(uriSegment, null);
	}

	public BasicAppfront(String uriSegment, List<RequestReceiver> receivers) {
		this.uriSegment = uriSegment;

		if (receivers != null && receivers.size() > 0) {
			generateClassResource(receivers);
		}

	}

	/**
	 * Takes the receiver list for this appfront and generates
	 * {@link ClassResource} instances for each of them.
	 * 
	 * @param receivers
	 */
	private void generateClassResource(List<RequestReceiver> receivers) {
		this.receivers = this.receivers == null ? new HashMap<>() : this.receivers;
		
		LOGGER.debug(">>>>>>>>. receivers > 2  {}", this.receivers);
		
		for (RequestReceiver receiver : receivers) {
			ClassResource<RequestReceiver> cr = new ClassResource<>(receiver);
			LOGGER.debug(">>>>>>>>. CR > 3  {}", cr);
			
			LOGGER.debug(">>>>>>>>. urisegemnet array  > 4  {}", cr.getUriSegments()[0]);
			
			this.receivers.put(cr.getUriSegments()[0], cr);
		}
	}

	public String getUriSegment() {
		return uriSegment;
	}

	void setUriSegment(String uriSegment) {
		this.uriSegment = uriSegment;
	}

	public Map<String, ClassResource<RequestReceiver>> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<RequestReceiver> receivers) {
		generateClassResource(receivers);
	}
	
	public void addReceiver(RequestReceiver receiver) {
		LOGGER.debug("receiver >>>> 1 {}", receiver);
		generateClassResource(Arrays.asList(receiver));
	}
	
	public void addReceiver(List<RequestReceiver> receivers) {
		generateClassResource(receivers);
	}

}
