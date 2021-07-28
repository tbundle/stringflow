package abs.ixi.server.packet.xmpp;

import abs.ixi.server.XMLConvertible;

public interface StreamFeature extends XMLConvertible {

    public StreamFeatureType getFeatureType();

    public String getXmlns();

    public enum StreamFeatureType {
	BIND, TLS, SASL, SM
    }
}
