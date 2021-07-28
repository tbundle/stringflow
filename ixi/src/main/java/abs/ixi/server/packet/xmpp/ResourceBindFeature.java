package abs.ixi.server.packet.xmpp;

public class ResourceBindFeature implements StreamFeature {
    private String RESOURCE_BIND_FEATURE_XML = "<bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/>"
	    .intern();

    private String xmlns;

    @Override
    public StreamFeatureType getFeatureType() {
	return StreamFeatureType.BIND;
    }

    @Override
    public String getXmlns() {
	return this.xmlns;
    }

    @Override
    public String xml() {
	return RESOURCE_BIND_FEATURE_XML;
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	return sb.append(RESOURCE_BIND_FEATURE_XML);
    }
}
