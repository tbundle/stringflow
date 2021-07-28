package abs.ixi.server.io.multipart;

import static java.util.Objects.requireNonNull;

import abs.ixi.util.StringUtils;

/**
 * Typically a MIME multipart has a <i>Content-Type</i> header attached to it
 * indicating the content type which applications use to process the content for
 * user.
 * <p>
 * There is ever growing list of mime types and Stringflow SDK can transport any
 * content type on the wire as it does not process the content as such (core sdk
 * only transports it). However content type identification is vital for
 * applications to process the files (multipart) correctly. {@code ContentType}
 * captures the attributes to represent a valid {@link MimeType}. Additionally
 * it offers convinience methods for application around {@code ContentType}
 * handling.
 * </p>
 * <p>
 * {@code ContentType} should not be confused with {@link MimeType}.
 * {@link MimeType} is an enumeration for commonely used mime types allowing
 * users to avoid processing mime type strings. {@code MimeType} does not
 * represent the list of supported mimes in sdk; on the other hand,
 * {@code ContentType} is a generic way of handling <i>"Content-Type"</i>
 * mime-header values.
 * </p>
 * 
 * @author Yogi
 *
 */
public class ContentType {
	private static final String SLASH = "/";

	private String type;
	private String subtype;

	/**
	 * Public constructor to instantiate {@code ContentType} from a mimeType;
	 * for example "image/jpeg" will result in a {@code ContentType} instance
	 * with type "image" and subtype "jpeg". If there is only one string is
	 * specified the type will be initialized with that value and subtype will
	 * be null.
	 * 
	 * @param mimeType
	 * @throws IllegalArgumentException if the mimeType String is not a valid
	 *             mimetype string. The validation of mimeType is done only in
	 *             terms its construction; for example mimeType argument can not
	 *             have more than two slash (/) characters in it.
	 */
	public ContentType(String mimeType) {
		requireNonNull(mimeType, "mimeType can not be null");

		String[] arr = mimeType.split(SLASH);

		if (arr.length == 1) {
			this.type = arr[0];
		} else if (arr.length == 2) {
			this.type = arr[0];
			this.subtype = arr[1];
		} else {
			throw new IllegalArgumentException(mimeType + " is not a valid mimeType");
		}
	}

	/**
	 * Public constructor to instantiate {@code ContentType} from a mimeType;
	 * Mime Type has two parts: (1) type and (2) subtype. Although both type and
	 * subtypes are required to represent a valid MIME type; the constructor
	 * enforces only "type" as required argument. If subtype is not given, it be
	 * initialized with null.
	 * 
	 * @param type
	 * @param subtype
	 * @throws NullPointerException if the type value is Null
	 */
	public ContentType(String type, String subtype) {
		this.type = requireNonNull(type, "type can not be null");
		this.subtype = subtype;
	}

	/**
	 * @return mime type string for example "application/json"
	 */
	public String getMimeType() {
		return this.type + "/" + this.subtype;
	}

	/**
	 * @return true if this {@link ContentType} is an Audio otherwise false
	 */
	public boolean isAudio() {
		return StringUtils.safeEquals(type, "audio", false);
	}

	/**
	 * @return true if this {@link ContentType} is an Video otherwise false
	 */
	public boolean isVideo() {
		return StringUtils.safeEquals(type, "video", false);
	}

}
