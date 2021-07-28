package abs.ixi.server.common;

/**
 * Although Java comes bundled with various character decoders; however java
 * decoders are very generic and written to decode single byye as well as
 * multi-byte characters. In our case, when server is running in application
 * server mode, incoming IQ stanza packets are expected to have only single byte
 * characters.
 * <p>
 * A typical application request will have IQ stanza packet containing headers
 * and urls followed by MIME stream. In IQ stanza packet in this case, headers
 * will be US_ASCII encoded and XMPP offered packet wrapper will be UTF_8
 * encoded characters. Under the above assumptions,
 * {@code SingleByteCharDecoder} will perform optimal while decoding IQ stanza
 * packets carrying application requests.
 * </p>
 * 
 * <p>
 * This implementation is not thread-safe.
 * </p>
 */
public class SingleByteCharDecoder {
    private byte[] bytes;
    private int offset;

    private DecodeLoop loop;

    public SingleByteCharDecoder() {
	// default do-nothing constructor
    }

    public SingleByteCharDecoder(byte[] bytes) {
	this(bytes, 0);
    }

    public SingleByteCharDecoder(byte[] bytes, int offset) {
	this.bytes = bytes;
	this.offset = offset;

	this.loop = this.new DecodeLoop();
    }

    /**
     * Initializes decoder with the bytes data which is used by
     * {@link DecodeLoop}
     * 
     * @param bytes
     */
    public void init(byte[] bytes) {
	this.init(bytes, 0);
    }

    /**
     * Initializes decoder with the bytes data which is used by
     * {@link DecodeLoop}
     * 
     * @param bytes
     * @param offset
     */
    public void init(byte[] bytes, int offset) {
	this.bytes = bytes;
	this.offset = offset;
    }

    public DecodeLoop getLoop() {
	return this.loop;
    }

    public void reset() {
	this.bytes = null;
	this.offset = 0;
    }

    /**
     * {@code DecodeLoop} iterates on the data held be enclosing
     * {@link SingleByteCharDecoder}
     */
    public class DecodeLoop {
	public boolean hasNext() {
	    return offset < bytes.length;
	}

	public char next() {
	    return (char) bytes[offset++];
	}
    }
}
