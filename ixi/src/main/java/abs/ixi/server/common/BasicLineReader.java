package abs.ixi.server.common;

import abs.ixi.server.common.ByteArray.ByteRange;

/**
 * Basic implementation of {@link LineReader} interface. It reads through the
 * byte stream and returns a {@link ByteRange}
 */
public class BasicLineReader implements LineReader<abs.ixi.server.common.LineReader.Line> {
	@Override
	public Line readLine(ByteArray src) {
		return readLine(src, false);
	}

	@Override
	public Line readLine(ByteArray src, boolean skipBlankLines) {
		int start = src.position();
		int end = start;

		while (src.hasNext()) {
			byte b = src.next();

			if (LF == b) {
				end = CR == src.get(src.position() - 2) ? src.position() - 2 : src.position() - 1;

				if (skipBlankLines && start == end) {
					start = src.position();
					continue;

				} else {
					return new Line(start, end, end == src.position() - 2 ? LineBreak.CRLF : LineBreak.LF);
				}
			}
		}

		return new Line(start, src.limit());
	}

	@Override
	public Line readLine(CharArray src) {
		return readLine(src, false);
	}

	@Override
	public Line readLine(CharArray src, boolean skipBlankLines) {
		int start = src.position();
		int end = start;

		while (src.hasNext()) {
			char b = src.next();

			if (LF == b) {
				end = CR == src.get(src.position() - 2) ? src.position() - 2 : src.position() - 1;

				if (skipBlankLines && start == end) {
					start = src.position();
					continue;

				} else {
					return new Line(start, end, end == src.position() - 2 ? LineBreak.CRLF : LineBreak.LF);
				}
			}
		}

		return new Line(start, src.limit());
	}
}
