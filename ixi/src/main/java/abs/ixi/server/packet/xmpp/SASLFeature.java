package abs.ixi.server.packet.xmpp;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.server.sys.secure.sasl.SASLMechanismName;
import abs.ixi.util.CollectionUtils;

public class SASLFeature implements StreamFeature {
    private static final String MECHANISMS_OPEN_TAG = "<mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>"
	    .intern();
    private static final String MECHANISMS_CLOSE_TAG = "</mechanisms>".intern();
    private static final String MECHANISM_OPEN_TAG = "<mechanism>".intern();
    private static final String MECHANISM_CLOSE_TAG = "</mechanism>".intern();

    private String xmlns;
    private List<SASLMechanismName> mechanisms;

    public SASLFeature() {
	this.mechanisms = new ArrayList<>();
    }

    public List<SASLMechanismName> getMechanisms() {
	return mechanisms;
    }

    public void setMmechanisms(List<SASLMechanismName> mechanisms) {
	this.mechanisms = mechanisms;
    }

    public void addMechnism(SASLMechanismName mechanism) {
	this.mechanisms.add(mechanism);
    }

    @Override
    public String getXmlns() {
	return this.xmlns;
    }

    @Override
    public StreamFeatureType getFeatureType() {
	return StreamFeatureType.SASL;
    }

    @Override
    public String xml() {
	StringBuilder sb = new StringBuilder();

	sb.append(MECHANISMS_OPEN_TAG);

	if (!CollectionUtils.isNullOrEmpty(this.mechanisms)) {

	    for (SASLMechanismName mechanism : this.mechanisms) {
		sb.append(MECHANISM_OPEN_TAG).append(mechanism.name())
			.append(MECHANISM_CLOSE_TAG);
	    }
	}

	sb.append(MECHANISMS_CLOSE_TAG);

	return sb.toString();
    }

    @Override
    public StringBuilder appendXml(StringBuilder sb) {
	sb.append(MECHANISMS_OPEN_TAG);

	if (!CollectionUtils.isNullOrEmpty(this.mechanisms)) {

	    for (SASLMechanismName mechanism : this.mechanisms) {
		sb.append(MECHANISM_OPEN_TAG).append(mechanism.name())
			.append(MECHANISM_CLOSE_TAG);
	    }
	}

	sb.append(MECHANISMS_CLOSE_TAG);

	return sb;
    }

}
