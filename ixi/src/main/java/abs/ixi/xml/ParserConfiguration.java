package abs.ixi.xml;

import static abs.ixi.server.etc.conf.ProcessConfigAware.MAX_ATTR_COUNT_LIMIT;
import static abs.ixi.server.etc.conf.ProcessConfigAware.PARSER_MAX_ATTRIBUTE_NAME_SIZE;
import static abs.ixi.server.etc.conf.ProcessConfigAware.PARSER_MAX_ATTRIBUTE_NUMBER;
import static abs.ixi.server.etc.conf.ProcessConfigAware.PARSER_MAX_ATTRIBUTE_VALUE_SIZE;
import static abs.ixi.server.etc.conf.ProcessConfigAware.PARSER_MAX_CDATA_SIZE;
import static abs.ixi.server.etc.conf.ProcessConfigAware.PARSER_MAX_ELEMENT_NAME_SIZE;

import java.util.Arrays;

/**
 * Configuration holder class for Parser configurations
 */
public class ParserConfiguration {
	public static final char OPEN_BRACKET = '<';
	public static final char CLOSE_BRACKET = '>';
	public static final char QUESTION_MARK = '?';
	public static final char EXCLAMATION_MARK = '!';
	public static final char SLASH = '/';
	public static final char SPACE = ' ';
	public static final char TAB = '\t';
	public static final char LF = '\n';
	public static final char CR = '\r';
	public static final char AMP = '&';
	public static final char EQUALS = '=';
	public static final char HASH = '#';
	public static final char SEMICOLON = ';';
	public static final char SINGLE_QUOTE = '\'';
	public static final char DOUBLE_QUOTE = '"';
	public static final char[] WHITE_CHARS = { SPACE, LF, CR, TAB };
	public static final char[] ERR_NAME_CHARS = { OPEN_BRACKET, QUESTION_MARK, AMP };
	public static final char[] IGNORE_CHARS = { '\0' };
	public static final boolean[] ALLOWED_CHARS_LOW = new boolean[0x20];
	public static final char[] CDATA_START = { '<', '!', '[', 'C', 'D', 'A', 'T', 'A', '[' };
	public static final char[] CDATA_END = { ']', ']', '>' };

	// Max number of attribute an element can have
	public static int MAX_ATTR_COUNT = 50;

	// Block size is the initial size the attribute array. When parser
	// encounters an element with attributes, it allocates an array of blockSize
	// to hold these attributes. If the number of attributes that this element
	// has is more than the blockSize, parser resizes the attribute array
	// (increasing the size by blockSize). This is the space optimization
	// mechanism which avoids parser allocating attributes array size=
	// maxAttrCount always
	public static int ATTR_BLOCK_SIZE = 6;

	// max length permitted for an attribute name
	public static int ATTR_NAME_MAX_LENGTH = 1024;

	// max length permitted for an attribute value
	public static int ATTR_VALUE_MAX_LENGTH = 10 * 1024;

	// max length permitted for an element name
	public static int ELM_NAME_MAX_LENGTH = 1024;

	// max size permitted for CDATASection
	public static int CDATA_MAX_SIZE = 1024 * 1024;

	static {
		ALLOWED_CHARS_LOW[0x09] = true; // Tab
		ALLOWED_CHARS_LOW[0x0A] = true; // LF
		ALLOWED_CHARS_LOW[0x0D] = true; // CR

		Arrays.sort(IGNORE_CHARS);

		readEnvProperties();
	}

	private static void readEnvProperties() {
		MAX_ATTR_COUNT = Integer.getInteger(MAX_ATTR_COUNT_LIMIT, MAX_ATTR_COUNT);
		ATTR_BLOCK_SIZE = Integer.getInteger(PARSER_MAX_ATTRIBUTE_NUMBER, ATTR_BLOCK_SIZE);
		ATTR_NAME_MAX_LENGTH = Integer.getInteger(PARSER_MAX_ATTRIBUTE_NAME_SIZE, ATTR_NAME_MAX_LENGTH);
		ATTR_VALUE_MAX_LENGTH = Integer.getInteger(PARSER_MAX_ATTRIBUTE_VALUE_SIZE, ATTR_VALUE_MAX_LENGTH);
		ELM_NAME_MAX_LENGTH = Integer.getInteger(PARSER_MAX_ELEMENT_NAME_SIZE, ELM_NAME_MAX_LENGTH);
		CDATA_MAX_SIZE = Integer.getInteger(PARSER_MAX_CDATA_SIZE, CDATA_MAX_SIZE);
	}

}
