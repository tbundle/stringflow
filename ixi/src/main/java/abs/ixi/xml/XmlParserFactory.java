package abs.ixi.xml;

/**
 * Factory to instantiate xml parser ({@link SwiftParser}). The factory is
 * singleton meaning it maintains single instance of the parser and returns it
 */
public class XmlParserFactory {
    private static XmlParser xmlParser;

    public static XmlParser getParser() {
	if (xmlParser == null) {
	    synchronized (XmlParserFactory.class) {
		if (xmlParser == null) {
		    xmlParser = new SwiftParser();
		}
	    }
	}

	return xmlParser;
    }
}
