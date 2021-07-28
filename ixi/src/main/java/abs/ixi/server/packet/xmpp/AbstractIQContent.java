package abs.ixi.server.packet.xmpp;

/**
 * Abstract implementation of of {@link IQContent}.
 */
public abstract class AbstractIQContent implements IQContent {
	private static final long serialVersionUID = -5747622091678322654L;
	protected String xmlns;
	protected IQContentType type;

	public AbstractIQContent(String xmlns, IQContentType type) {
		this.xmlns = xmlns;
		this.type = type;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	@Override
	public IQContentType getType() {
		return type;
	}

}
