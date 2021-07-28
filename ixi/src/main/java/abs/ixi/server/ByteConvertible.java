package abs.ixi.server;

/**
 * Any object which is byte convertible can convert itself into bytes which can
 * be written on the wire directly. This is similar to Java Serialization but
 * more generic way instead of Java devised mechanism to convert an object into
 * bytes.
 */
public interface ByteConvertible {
    /**
     * The object is converted into bytes with a mechanism implemented as part
     * of this method. The returned bytes can be transmitted on wire/network.
     * 
     * @return raw byte representation of this object
     */
    public byte[] getBytes();

    /**
     * Converts this object into bytes and copies the bytes into given array
     * starting from offset.
     * 
     * @param dest
     *            destination byte array in which bytes will be copied
     * @param offset
     *            start index from which bytes will be copied
     * @return return new offset after the content bytes has been copied
     */
    public int getBytes(byte[] dest, int offset);
}
