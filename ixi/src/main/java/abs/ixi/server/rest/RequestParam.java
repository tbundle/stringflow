package abs.ixi.server.rest;

/**
 * {@link RequestParam} represents a parameter for http request
 * 
 * @author Yogi
 *
 * @param <T>
 */
public class RequestParam<T> {
	private String name;
	private Class<T> type;
	private String description;
	private boolean mandatory;

	public RequestParam(String name, Class<T> type, boolean mandatory) {
		this(name, type, null, mandatory);
	}

	public RequestParam(String name, Class<T> type, String description, boolean mandatory) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.mandatory = mandatory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * A REST Endpoint may use various type of request params. Thisis to
	 * enumerate those types.
	 * 
	 * @author Yogi
	 *
	 */
	public enum Type {
		/**
		 * Query param in a url
		 */
		QUERY_PARAM,

		/**
		 * Url path param
		 */
		PATH_PARAM,

		/**
		 * POST body param
		 */
		BODY_PARAM;
	}
}
