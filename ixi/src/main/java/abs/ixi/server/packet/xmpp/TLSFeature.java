package abs.ixi.server.packet.xmpp;

public class TLSFeature implements StreamFeature {
    private static final String START_TLS_OPEN_TAG = "<starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'>"
	    .intern();
    private static final String START_TLS_CLOSE_TAG = "</starttls>".intern();
    private static final String REQUIRED_TAG = "<required/>".intern();

    private String xmlns;
    private boolean required;

    public TLSFeature(boolean required) {
	this.required = required;
    }

    public boolean isRequired() {
	return required;
    }

    public void setRequired(boolean required) {
	this.required = required;
    }

    @Override
    public StreamFeatureType getFeatureType() {
	return StreamFeatureType.TLS;
    }

    @Override
    public String getXmlns() {
	return this.xmlns;
    }

    @Override
    public String xml() {
	StringBuilder sb = new StringBuilder();
	sb.append(START_TLS_OPEN_TAG);

	if (required) {
	    sb.append(REQUIRED_TAG);
	}

	sb.append(START_TLS_CLOSE_TAG);

	return sb.toString();
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	sb.append(START_TLS_OPEN_TAG);

	if (required) {
	    sb.append(REQUIRED_TAG);
	}

	sb.append(START_TLS_CLOSE_TAG);

	return sb;
    }
}
