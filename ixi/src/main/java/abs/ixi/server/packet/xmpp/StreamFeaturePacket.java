package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.List;

public class StreamFeaturePacket extends XMPPPacket {
    private static final long serialVersionUID = -6236475446869555913L;

    public static final PacketXmlElement XML_ELM_NAME = PacketXmlElement.STREAM_FEATURE;

    private static final String FEATURE_OPEN_TAG = "<stream:features>";
    private static final String FEATURE_CLOSE_TAG = "</stream:features>";

    private List<StreamFeature> features;
    private String sourceId;

    public StreamFeaturePacket() {
	this.features = new ArrayList<>();
    }

    public List<StreamFeature> getFeatures() {
	return this.features;
    }

    public void setFeatures(List<StreamFeature> features) {
	this.features = features;
    }

    public void addFeature(StreamFeature feature) {
	this.features.add(feature);
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    @Override
    public String getSourceId() {
	return this.sourceId;
    }

    @Override
    public String xml() {
	StringBuilder sb = new StringBuilder();
	sb.append(FEATURE_OPEN_TAG);

	if (features != null) {
	    for (StreamFeature feature : features) {
		feature.appendXml(sb);
	    }
	}

	sb.append(FEATURE_CLOSE_TAG);

	return sb.toString();
    }

    @Override
    public PacketXmlElement getXmlElementName() {
	return XML_ELM_NAME;
    }

}
