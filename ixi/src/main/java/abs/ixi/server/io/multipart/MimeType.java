package abs.ixi.server.io.multipart;

import abs.ixi.util.StringUtils;

/**
 * An enumeration represnting supported MIME content types. Stringflow core sdk
 * do not process the MIME content; it simply transmits it on the wire from a
 * {@link ContentSource} or receive it on the wire from server and wraps it into
 * a {@link ContentSource} which may internally persist it onto disk.
 * <p>
 * As sdk do not process the content, it does not make any decision based on
 * content type, therefore it can work even when the below list does not include
 * a content type. This enum is to allow application developers deal with
 * content types in better way. Although this list may not be exhaustive.
 * </p>
 * <p>
 * If this list does not include a content type, dont worry, we still support
 * it.
 * </p>
 * 
 * @author Yogi
 *
 */
public enum MimeType {
	/**
	 * Default content type; Archive document (multiple files embedded) (.bin)
	 */
	APPLICATION_OCTETSTREAM("application", "octet-stream", "arc"),

	/**
	 * Microsoft PowerPoint
	 */
	MS_PPT("application", "vnd.ms-powerpoint", "ppt"),

	/**
	 * Microsoft PowerPoint (OpenXML)
	 */
	MS_OPENXML_PPT("applicatiom", "vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),

	/**
	 * Adobe Portable Document Format (PDF)
	 */
	PDF("application", "pdf", "pdf"),

	/**
	 * RAR Archive
	 */
	ARCHIVE_RAR("application", "x-rar-compressed", "rar"),

	/**
	 * Rich Text Format (RTF)
	 */
	RTF("application", "rtf", "rtf"),

	/**
	 * Bourne shell script
	 */
	BOURNE_SHELL_SCRIPT("application", "x-sh", "sh"),

	/**
	 * BZip Archive
	 */
	ARCHIVE_BZIP("application", "x-bzip", ".bz"),

	/**
	 * BZip2 Archive
	 */
	ARCHIVE_BZIP2("application", "x-bzip2", "bz2"),

	/**
	 * C-Shell Script
	 */
	CSHELL_SCRIPT("application", "x-csh", "csh"),

	/**
	 * Microsoft Word
	 */
	MS_WORD_DOC("application", "msword", "doc"),

	/**
	 * Microsoft Word (OpenXML)
	 */
	MS_WORD_DOCX("application", "vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),

	/**
	 * Microsoft Visio Document
	 */
	MS_VISIO_DOC("application", "vnd.visio", "vsd"),

	/**
	 * MS Embedded OpenType fonts
	 */
	MS_OPEN_FONTS("application", "vnd.ms-fontobject", "eot"),

	/**
	 * Microsoft Excel
	 */
	MS_EXCEL("application", "vnd.ms-excel", "xls"),

	/**
	 * Microsoft Excel (OpenXML format)
	 */
	MS_EXCEL_OPENXML("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),

	/**
	 * OpenDocument presentation
	 */
	OPENDOC_PPT("application", "vnd.oasis.opendocument.presentation", "odp"),

	/**
	 * OpenDocument spreadsheet document
	 */
	OPENDOC_SPREADSHEET("application", "vnd.oasis.opendocument.spreadsheet", "ods"),

	/**
	 * OpenDocument text document
	 */
	OPENDOC_TEXT_DOC("application", "vnd.oasis.opendocument.text", "odt"),

	/**
	 * JSON format
	 */
	JSON("application", "json", "json"),

	/**
	 * Java Archive (JAR)
	 */
	JAR("application", "java-archive", "jar"),

	/**
	 * Tape Archive (TAR)
	 */
	TAR_ARCHIVE("application", "x-tar", "tar"),

	/**
	 * XHTML
	 */
	XHTML("application", "xhtml+xml", "xhtml"),

	/**
	 * XML
	 */
	XML("application", "xml", "xml"),

	/**
	 * ZIP Archive
	 */
	ZIP_ARCHIVE("application", "zip", "zip"),

	/**
	 * 7-zip archive
	 */
	ZIP7_ARCHIVE("application", "x-7z-compressed", "7z"),

	/**
	 * AAC Audio format
	 */
	AUDIO_AAC("audio", "aac", "aac"),

	/**
	 * Audio Video Interleave video/x-msvideo
	 */
	VIDEO_AVI("video", "x-msvideo", "avi"),

	/**
	 * Windows OS/2 Bitmap Graphics
	 */
	IMAGE_BITMAP("image", "bmp", "bmp"),

	/**
	 * Cascading Style Sheets (CSS)
	 */
	TEXT_CSS("text", "css", "css"),

	/**
	 * Comma-separated values (CSV)
	 */
	TEXT_CSV("text", "csv", "csv"),

	/**
	 * Graphics Interchange Format (GIF)
	 */
	IMAGE_GIF("image", "gif", "gif"),

	/**
	 * HyperText Markup Language (HTML)
	 */
	TEXT_HTML("text", "html", "html"),

	/**
	 * HyperText Markup Language (HTML)
	 */
	TEXT_HTM("text", "html", "htm"),

	/**
	 * Icon format
	 */
	IMAGE_ICON("image", "x-icon", "ico"),

	/**
	 * iCalendar format
	 */
	TEXT_CALENDAR("text", "calendar", "ics"),

	/**
	 * JPEG images
	 */
	IMAGE_JPEG("image", "jpeg", "jpeg"),

	/**
	 * JPEG images
	 */
	IMAGE_JPG("image", "jpeg", "jpg"),

	/**
	 * Musical Instrument Digital Interface (MIDI)
	 */
	AUDIO_MIDI("audio", "midi", "midi"),

	/**
	 * Musical Instrument Digital Interface (MIDI)
	 */
	AUDIO_MID("audio", "x-midi", "mid"),

	/**
	 * MPEG Video
	 */
	VIDEO_MPEG("application", "mpeg", "mpeg"),

	/**
	 * OGG audio
	 */
	AUDIO_OGG("audio", "ogg", "oga"),

	/**
	 * OGG Video
	 */
	VIDEO_OGG("video", "ogv", "ogv"),

	/**
	 * Portable Network Graphics
	 */
	IMAGE_PNG("image", "png", "png"),

	/**
	 * Scalable Vector Graphics (SVG)
	 */
	IMAGE_SVG("image", "svg+xml", "svg"),

	/**
	 * Text, (generally ASCII or ISO 8859-n)
	 */
	PLAIN_TEXT("text", "plain", "txt"),

	/**
	 * Waveform Audio Format
	 */
	AUDIO_WAVE("audio", "wav", "wav"),

	/**
	 * 3GPP audio container
	 */
	AUDIO_3GPP("audio", "3gpp", "3gp"),

	/**
	 * 3GPP video container
	 */
	VIDEO_3GPP("video", "3gpp", "3gp");

	private String type;
	private String subtype;
	private String extension;

	private MimeType(String type, String subtype, String extension) {
		this.type = type;
		this.subtype = subtype;
		this.extension = extension;
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getExtension() {
		return extension;
	}

	public String getMimeType() {
		return this.type + "/" + this.subtype;
	}

	/**
	 * Return the {@link MimeType} based on extension
	 * 
	 * @param ext
	 * @return
	 */
	public static MimeType getMimeTypeByExtension(String ext) {
		for (MimeType mt : MimeType.values()) {
			if (StringUtils.safeEquals(mt.getExtension(), ext, false)) {
				return mt;
			}
		}

		throw new IllegalArgumentException("there is no MimeType for extenstion: " + ext);
	}

	/**
	 * Get {@link MimeType} based on a subtype
	 * 
	 * @param subtype
	 * @return
	 */
	public static MimeType getMimeTypeBySubtype(String subtype) {
		for (MimeType mt : MimeType.values()) {
			if (StringUtils.safeEquals(mt.getSubtype(), subtype, false)) {
				return mt;
			}
		}

		throw new IllegalArgumentException("there is no MimeType for subtype: " + subtype);
	}

	/**
	 * Get {@link MimeType} based on mime type string
	 * 
	 * @param mimeType
	 * @return
	 */
	public static MimeType getMimeType(String mimeType) {
		for (MimeType mt : MimeType.values()) {
			if (StringUtils.safeEquals(mt.getMimeType(), mimeType, false)) {
				return mt;
			}
		}

		throw new IllegalArgumentException("there is no MimeType with name: " + mimeType);
	}

}
