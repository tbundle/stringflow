package abs.ixi.server.io.multipart;

/**
 * Factory to instantiate mime parser ({@link MultipartParser). The factory is
 * singleton meaning it maintains single instance of the parser and returns it
 */
public class MultipartParserFactory {
	private static MultipartParser multipartParser;

	public static MultipartParser getParser() {
		if (multipartParser == null) {
			synchronized (MultipartParserFactory.class) {
				if (multipartParser == null) {
					multipartParser = new MultipartParser();
				}
			}
		}

		return multipartParser;
	}
}
