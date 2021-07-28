package abs.ixi.util;

/**
 * Utility class for {@link Character} data
 */
public class CharUtils {
    // Unicode code points for most used white space characters
    public static final byte CODE_POINT_NULL = 0x0000;
    public static final byte CODE_POINT_SPACE = 0x0020;
    public static final byte CODE_POINT_LF = 0x000A;
    public static final byte CODE_POINT_CR = 0x000D;
    public static final byte CODE_POINT_HTAB = 0x0009;
    public static final byte CODE_POINT_VTAB = 0x000B;

    public static final byte CODE_POINT_BRACE_OPEN = 0x007b;
    public static final byte CODE_POINT_BRACE_CLOSED = 0x007d;

    public static final char SPACE = ' ';
    public static final char LF = '\n';
    public static final char CR = '\r';
    public static final char TAB = '\t';

    /**
     * Tests if the given char is one of following
     * <li>SPACE</li>
     * <li>TAB</li>
     * <li>CR</li>
     * <li>LF</li>
     */
    public static boolean isWhiteChar(char c) {
	return (c == SPACE || c == LF || c == CR || c == TAB) ? true : false;
    }

    /**
     * Tests if the code point given is one of following
     * <li>{@code 0x000D}</code></li>
     * <li>{@code 0x0020}</li>
     * <li>{@code 0x0000A}</li>
     * <li>{@code 0x0009}</li>
     */
    public static boolean isWhiteChar(byte codePoint) {
	return codePoint == CODE_POINT_SPACE || codePoint == CODE_POINT_LF || codePoint == CODE_POINT_CR
		|| codePoint == CODE_POINT_HTAB;
    }
}
