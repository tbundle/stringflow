package abs.ixi.xml;

/**
 * Represents CDATA section in XML DOM. Currently {@link SwiftParser} does not
 * support it.
 */
public class CDataSection extends Text {
	private static final long serialVersionUID = 1L;

	public CDataSection(String cdata) {
		super(cdata);
	}

	public String getCData() {
		return getText();
	}

	@Override
	public String stringify() {
		return super.stringify();
	}

	@Override
	public int children() {
		return 0;
	}

	@Override
	public String stringifyChildren() {
		return super.stringifyChildren();
	}

	@Override
	public String val() {
		return super.val();
	}
	
	@Override
	public CDataSection clone() {
		return new CDataSection(getCData());
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
