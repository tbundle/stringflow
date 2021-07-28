package abs.ixi.xml;

/**
 * Represents text node in xml DOM
 */
public class Text implements XMLNode {
	private static final long serialVersionUID = 1L;

	private String text;

	public Text(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public int children() {
		return 0;
	}

	@Override
	public String stringify() {
		return this.text;
	}

	@Override
	public String stringifyChildren() {
		return "";
	}

	@Override
	public XMLNode clone() {
		return new Text(text);
	}

	@Override
	public String val() {
		return text;
	}
	
}
