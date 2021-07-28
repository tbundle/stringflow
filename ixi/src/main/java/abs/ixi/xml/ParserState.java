package abs.ixi.xml;

import abs.ixi.xml.SwiftParser.Entity;
import abs.ixi.xml.SwiftParser.ParsingStage;

/**
 * Plain java object to hold {@link SwiftParser} state.
 */
public class ParserState {
	ParsingStage stage = ParsingStage.START;
	ParsingStage prevStage = null;

	int curAttrIndex = -1;
	StringBuilder[] attributeNames = null;
	StringBuilder[] attributeValues = null;

	StringBuilder elementCData = null;
	StringBuilder elementName = null;

	byte cDataBoundaryCharIndex = -1;
	Entity entityType = Entity.UNKNOWN;
	boolean slashFound = false;
	boolean highSurrogate = false;

	Error errorType = null;
	String errorMessage = null;

	public StringBuilder getElementName() {
		return elementName;
	}

	public Error getErrorType() {
		return errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean isError() {
		return stage == ParsingStage.ERROR ? true : false;
	}

	public void foundRootElement() {
		this.stage = ParsingStage.END_OF_ROOT;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("<").append(elementName).append(" ");

		if (attributeNames != null) {
			for (int i = 0; i < attributeNames.length; i++) {
				b.append(attributeNames[i]).append("=").append(attributeValues[i]).append(" ");
			}
		}

		return b.toString();
	}

	public static enum Error {
		ATTR_COUNT_LIMIT_EXCEEDED,

		ATTR_NAME_LENGTH_LIMIT_EXCEEDED,

		ATTR_VALUE_LENGTH_LIMIT_EXCEEDED,

		ELEMENT_NAME_SIZE_LIMIT_EXCEEDED,

		ELEMENT_VALUE_SIZE_LIMIT_EXCEEDED,

		INVALID_ENTITY,

		CHAR_NOT_ALLOWED,

		CLOSE_ELEMENT_BEFORE_OPEN,

		MALFORMED_XML,

		UNKNOWN;
	}

}
