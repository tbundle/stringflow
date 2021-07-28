package abs.ixi.server.app;

import java.util.HashMap;
import java.util.Map;

import abs.ixi.server.io.MalformedXMPPRequestException;
import abs.ixi.util.StringUtils;

public class HeaderParser {
	public static final String BOUNDARY = "boundary";
	public static final String NEW_LINE = "\n";
	public static final char EQUALS = '=';
	
	public static Map<String, Object> parseHeader(String data) throws MalformedXMPPRequestException {
		Map<String, Object> headerMap = new HashMap<>();
		
		String headerData = data.trim();
		
		if (StringUtils.isNullOrEmpty(headerData))
			return headerMap;

		String[] headers = headerData.split(NEW_LINE);

		if (headers.length == 0)
			return headerMap;

		for (String header : headers) {
			String headerKey = header.substring(0, header.indexOf(EQUALS)).trim();
			String headerValue = header.substring(header.indexOf(EQUALS) + 1).trim();

			if (StringUtils.isNullOrEmpty(headerKey) || StringUtils.isNullOrEmpty(headerValue)) {
				
				throw new MalformedXMPPRequestException("Bad header format");
			}

			headerMap.put(headerKey, headerValue);
		}

		return headerMap;
	}

	public static void main(String... args) throws Exception {
		String headerData = "uri = abc/xyz \n" + "content-length = 1000 \n" + "a = b \n" + " b = c \n" + " c = d \n";

		Map<String, Object> map = parseHeader(headerData);

		map.entrySet().forEach(v -> {
			System.out.println(v.getKey() + "=" + v.getValue());
		});

	}
}
