package abs.ixi.xml;

public class ParserInternalError extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public ParserInternalError(String msg) {
		super(msg);
	}
}
