package abs.ixi.server.common;

import abs.ixi.server.common.LineReader.Line;

public class SingletonLineReaderFactory {
    private static BasicLineReader lineReader;

    public static LineReader<Line> getLineReader() {
	if (lineReader == null) {
	    synchronized (SingletonLineReaderFactory.class) {
		lineReader = new BasicLineReader();
	    }
	}

	return lineReader;
    }

}
