package abs.ixi.server.packet.xmpp;

/**
 * Http wrapper around bosh body packet. This is not good way. Later we have to
 * improve it. BoshBody should be inside HttpPacket.
 * 
 * @author Gudia
 *
 */
public class BoshHttpPacket extends BOSHBody {
	private static final String HTTP_SUCCESS_RESPONSE_HEADER = "HTTP/1.1 200 OK \n"
			+ "Content-Type: text/xml; charset=utf-8 \n";

	private static final String NEW_LINE = "\n";

	private static final long serialVersionUID = 735006563715611709L;

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		sb.append(HTTP_SUCCESS_RESPONSE_HEADER);
		sb.append(NEW_LINE);
		sb.append(super.appendXml(sb));
		return super.appendXml(sb);
	}
}
