package abs.ixi.xml;

public class XMLUtils {
	public static final char OPEN_BRACKET = '<';
	public static final char CLOSE_BRACKET = '>';
	public static final char SLASH = '/';
	public static final char SPACE = ' ';
	public static final char SINGLE_QUOTE = '\'';
	public static final char EQUALS = '=';

	public static final String LANG_ATTRIBUTE = "lang".intern();
	public static final String VERSION_ATTRIBUTE = "version".intern();
	public static final String XMLNS_ATTRIBUTE = "xmlns".intern();

	private static final String[] decoded = { "&", "<", ">", "\"", "\'" };
	private static final String[] encoded = { "&amp;", "&lt;", "&gt;", "&quot;", "&apos;" };

	private static final String[] decoded_1 = { "<", ">", "\"", "\'", "&" };
	private static final String[] encoded_1 = { "&lt;", "&gt;", "&quot;", "&apos;", "&amp;" };

	public static String escape(String input) {
		if (input != null) {
			return translateAll(input, decoded, encoded);
		} else {
			return null;
		}
	}

	public static String translateAll(String input, String[] patterns, String[] replacements) {
		String result = input;

		for (int i = 0; i < patterns.length; i++) {
			result = result.replace(patterns[i], replacements[i]);
		}

		return result;
	}

	public static String unescape(String input) {
		if (input != null) {
			return translateAll(input, encoded_1, decoded_1);
		} else {
			return null;
		}
	}
}
