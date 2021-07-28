package abs.ixi.server.packet.xmpp;

import abs.ixi.server.io.multipart.ContentType;
import abs.ixi.util.StringUtils;
import abs.ixi.xml.Element;
import abs.ixi.xml.XMLUtils;

public class MessageMedia implements MessageContent {
	private static final long serialVersionUID = -8638274049621144737L;

	public static final String XML_ELM_NAME = "media".intern();

	public static final String ID = "id".intern();
	public static final String CONTENT_TYPE = "content-type".intern();
	public static final String THUMB = "thumb".intern();

	public static final String CONTENT_TYPE_OPEN_TAG = "<content-type>".intern();
	public static final String CONTENT_TYPE_CLOSE_TAG = "</content-type>".intern();
	public static final String THUMB_OPEN_TAG = "<thumb>".intern();
	public static final String THUMB_CLOSE_TAG = "</thumb>".intern();
	public static final String MEDIA_CLOSE_TAG = "</media>".intern();

	private String mediaId;
	private ContentType contentType;
	private String thumb;

	public MessageMedia(String mediaId) {
		this(mediaId, null);
	}

	public MessageMedia(String mediaId, ContentType contentType) {
		this.mediaId = mediaId;
		this.contentType = contentType;
	}

	public MessageMedia(Element mediaElem) {
		this(mediaElem.getAttribute(ID));

		for (Element child : mediaElem.getChildren()) {
			if (StringUtils.safeEquals(child.getName(), CONTENT_TYPE, false)) {
				this.setContentType(new ContentType(child.val()));

			} else if (StringUtils.safeEquals(child.getName(), THUMB, false)) {
				this.setThumb(child.val());
			}
		}
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	@Override
	public boolean isContentType(MessageContentType type) {
		return MessageContentType.MEDIA == type;
	}

	@Override
	public String toString() {
		return mediaId;
	}

	@Override
	public String xml() {
		StringBuilder sb = new StringBuilder();

		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(ID).append(XMLUtils.EQUALS)
				.append(XMLUtils.SINGLE_QUOTE).append(this.mediaId).append(XMLUtils.SINGLE_QUOTE)
				.append(XMLUtils.CLOSE_BRACKET);

		if (this.getContentType() != null) {
			sb.append(CONTENT_TYPE_OPEN_TAG).append(this.getContentType().getMimeType()).append(CONTENT_TYPE_CLOSE_TAG);
		}

		if (!StringUtils.isNullOrEmpty(this.getThumb())) {
			sb.append(THUMB_OPEN_TAG).append(this.getThumb()).append(THUMB_CLOSE_TAG);
		}

		sb.append(MEDIA_CLOSE_TAG);

		return sb.toString();
	}

	@Override
	public StringBuilder appendXml(StringBuilder sb) {
		sb.append(XMLUtils.OPEN_BRACKET).append(XML_ELM_NAME).append(XMLUtils.SPACE).append(ID).append(XMLUtils.EQUALS)
				.append(XMLUtils.SINGLE_QUOTE).append(this.mediaId).append(XMLUtils.SINGLE_QUOTE)
				.append(XMLUtils.CLOSE_BRACKET);

		if (this.getContentType() != null) {
			sb.append(CONTENT_TYPE_OPEN_TAG).append(this.getContentType().getMimeType()).append(CONTENT_TYPE_CLOSE_TAG);
		}

		if (!StringUtils.isNullOrEmpty(this.getThumb())) {
			sb.append(THUMB_OPEN_TAG).append(this.getThumb()).append(THUMB_CLOSE_TAG);
		}

		sb.append(MEDIA_CLOSE_TAG);

		return sb;
	}

}
