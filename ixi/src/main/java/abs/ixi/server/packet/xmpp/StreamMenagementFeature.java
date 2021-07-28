package abs.ixi.server.packet.xmpp;

public class StreamMenagementFeature implements StreamFeature {
    private static final String SM_FEATURE_XML = "<sm xmlns='urn:xmpp:sm:3'/>"
	    .intern();

    private String xmlns;

    @Override
    public StreamFeatureType getFeatureType() {
	return StreamFeatureType.SM;
    }

    @Override
    public String getXmlns() {
	return this.xmlns;
    }

    @Override
    public String xml() {
	return SM_FEATURE_XML;
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {

	return sb.append(SM_FEATURE_XML);
    }

}
