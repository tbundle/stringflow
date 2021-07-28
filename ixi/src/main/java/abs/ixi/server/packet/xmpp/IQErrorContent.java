package abs.ixi.server.packet.xmpp;

public class IQErrorContent extends AbstractIQContent {
	private static final long serialVersionUID = 1L;

	public static final String XML_ELM_NAME = "error";

	public static final String FORBIDDEN_ERROR_XML = "<error type='auth'>"
			+ "<forbidden xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/></error>";

	public static final String NOT_ALLOWED_ERROR_XML = "<error type='cancel'>"
			+ "<not-allowed xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/></error>";

	public static final String CONFLICT_ERROR_XML = "<error code='409' type='cancel'> "
			+ "<conflict xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";

	public static final String NOT_ACCEPTABLE_ERROR_XML = "<error code='406' type='modify'>"
			+ "<not-acceptable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";

	public static final String ITEM_NOT_FOUND_ERROR_XML = "<error type='cancel'>"
			+ "<item-not-found xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";

	public static final String SERVICE_UNAVAILABLE_ERROR_XML = "<error type='cancel'>"
			+ "<service-unavailable xmlns='urn:ietf:params:xml:ns:xmpp-stanzas'/>" + "</error>";

	private String errorXML;

	public IQErrorContent(IQError error) {
		this(null, error.getErrorXMl());
	}

	public IQErrorContent(String errorXML) {
		this(null, errorXML);
	}

	public IQErrorContent(String xmlns, String errorXML) {
		super(xmlns, IQContentType.ERROR);
		this.errorXML = errorXML;
	}

	@Override
	public String xml() {
		return errorXML;
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		return sb.append(errorXML);
	}

	public enum IQError {
		FORBIDDEN_ERROR(FORBIDDEN_ERROR_XML), NOT_ALLOWED_ERROR(NOT_ALLOWED_ERROR_XML), CONFLICT_ERROR(
				CONFLICT_ERROR_XML), NOT_ACCEPTABLE_ERROR(NOT_ACCEPTABLE_ERROR_XML), SERVICE_UNAVAILABLE_ERROR(
						SERVICE_UNAVAILABLE_ERROR_XML), ITEM_NOT_FOUND_ERRRO(ITEM_NOT_FOUND_ERROR_XML);

		private String errorXML;

		private IQError(String errorXml) {
			this.errorXML = errorXml;
		}

		public String getErrorXMl() {
			return this.errorXML;
		}
	}

}
