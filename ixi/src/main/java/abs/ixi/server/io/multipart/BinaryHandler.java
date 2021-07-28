package abs.ixi.server.io.multipart;

import abs.ixi.server.common.ByteArray;

public class BinaryHandler extends AbstractContentHandler<ByteArray> {
	public BinaryHandler(Multipart content) {
		super(content);
	}

	@Override
	public ByteArray getContent() {
		return null;
	}

}
