package abs.ixi.xml;

import static abs.ixi.xml.ParserConfiguration.ALLOWED_CHARS_LOW;
import static abs.ixi.xml.ParserConfiguration.ATTR_BLOCK_SIZE;
import static abs.ixi.xml.ParserConfiguration.ATTR_NAME_MAX_LENGTH;
import static abs.ixi.xml.ParserConfiguration.ATTR_VALUE_MAX_LENGTH;
import static abs.ixi.xml.ParserConfiguration.CDATA_MAX_SIZE;
import static abs.ixi.xml.ParserConfiguration.CLOSE_BRACKET;
import static abs.ixi.xml.ParserConfiguration.DOUBLE_QUOTE;
import static abs.ixi.xml.ParserConfiguration.ELM_NAME_MAX_LENGTH;
import static abs.ixi.xml.ParserConfiguration.EQUALS;
import static abs.ixi.xml.ParserConfiguration.ERR_NAME_CHARS;
import static abs.ixi.xml.ParserConfiguration.EXCLAMATION_MARK;
import static abs.ixi.xml.ParserConfiguration.HASH;
import static abs.ixi.xml.ParserConfiguration.MAX_ATTR_COUNT;
import static abs.ixi.xml.ParserConfiguration.OPEN_BRACKET;
import static abs.ixi.xml.ParserConfiguration.QUESTION_MARK;
import static abs.ixi.xml.ParserConfiguration.SEMICOLON;
import static abs.ixi.xml.ParserConfiguration.SINGLE_QUOTE;
import static abs.ixi.xml.ParserConfiguration.SLASH;
import static abs.ixi.xml.ParserConfiguration.WHITE_CHARS;
import static abs.ixi.xml.ParserState.Error.ATTR_COUNT_LIMIT_EXCEEDED;
import static abs.ixi.xml.ParserState.Error.ATTR_NAME_LENGTH_LIMIT_EXCEEDED;
import static abs.ixi.xml.ParserState.Error.ATTR_VALUE_LENGTH_LIMIT_EXCEEDED;
import static abs.ixi.xml.ParserState.Error.CHAR_NOT_ALLOWED;
import static abs.ixi.xml.ParserState.Error.CLOSE_ELEMENT_BEFORE_OPEN;
import static abs.ixi.xml.ParserState.Error.ELEMENT_NAME_SIZE_LIMIT_EXCEEDED;
import static abs.ixi.xml.ParserState.Error.ELEMENT_VALUE_SIZE_LIMIT_EXCEEDED;
import static abs.ixi.xml.ParserState.Error.INVALID_ENTITY;

import java.util.Arrays;

import abs.ixi.server.common.CharArray;
import abs.ixi.server.common.ParserException;
import abs.ixi.util.ArrayUtils;
import abs.ixi.util.CharUtils;

/**
 * A event based parser with very limited capability. It can not handle all the
 * XML constructs correctly. It has been written keeping in mind that it will be
 * used only for XMPP packet parsing. Also, normal text and {@link CDataSection}
 * are handled the same way in the parser currently.
 * 
 * <p>
 * It can not handle comment blocks
 * </p>
 */
public class SwiftParser implements XmlParser {

	/**
	 * {@code SwiftParser} is to be used as singleton. Therefore, restricting
	 * constructor access to package. Parser instance must be retrieved using
	 * {@link XmlParserFactory}
	 */
	SwiftParser() {
		// do-nothing constructor
	}

	@Override
	public void parse(ParseEventHandler handler, CharArray charSource) throws ParserException {
		ParserState parserState = handler.getParserState();
		handler.saveParserState(parserState);

		while (charSource.hasNext()) {
			char c = charSource.next();

			if (!isValidChar(parserState, c)) {
				parserState.errorMessage = "Character '" + c + "' is not allowed in XML stream";
				parserState.stage = ParsingStage.ERROR;
				parserState.errorType = ParserState.Error.CHAR_NOT_ALLOWED;
			}

			if (ParsingStage.START == parserState.stage) {
				if (c == OPEN_BRACKET) {
					parserState.stage = ParsingStage.OPEN_BRACKET;
					parserState.slashFound = false;
				}

			} else if (ParsingStage.OPEN_BRACKET == parserState.stage) {
				handleOpenBracketStage(parserState, c);

			} else if (ParsingStage.START_ELEMENT_NAME == parserState.stage) {
				startElementNameStage(handler, parserState, c);

			} else if (ParsingStage.CLOSE_ELEMENT == parserState.stage) {
				closeElement(handler, parserState, c);

			} else if (ParsingStage.END_ELEMENT_NAME == parserState.stage) {
				endElementName(handler, parserState, c);

			} else if (ParsingStage.START_ATTR_NAME == parserState.stage) {
				startAttributeName(parserState, c);

			} else if (ParsingStage.END_ATTR_NAME == parserState.stage) {
				endAttributeName(parserState, c);

			} else if (ParsingStage.START_ATTR_VALUE_SQ == parserState.stage) {
				startAttributeValueSQ(parserState, c);

			} else if (ParsingStage.START_ATTR_VALUE_DQ == parserState.stage) {
				startAttributeValueDQ(parserState, c);

			} else if (ParsingStage.ELEMENT_TEXT == parserState.stage) {
				startElementValue(handler, parserState, c);

			} else if (ParsingStage.START_CDATA == parserState.stage) {
				startElementCData(handler, parserState, c);

			} else if (ParsingStage.START_CDATA_CONTENT == parserState.stage) {
				startCDataContent(handler, parserState, c);

			} else if (ParsingStage.END_CDATA == parserState.stage) {
				endCDataContent(handler, parserState, c);

			} else if (ParsingStage.ENTITY == parserState.stage) {
				entity(parserState, c);

			} else if (ParsingStage.END_OF_ROOT == parserState.stage) {
				parserState.stage = ParsingStage.START;
				charSource.setPosition(charSource.position() - 1);

				return;

			} else if (ParsingStage.OTHER_XML == parserState.stage) {
				otherXml(handler, parserState, c);

			} else if (ParsingStage.ERROR == parserState.stage) {
				handler.onError(parserState.errorMessage);
				parserState = null;

				return;

			} else {
				throw new ParserException("Unknown parser state");
			}
		}

		if (handler.hasParsedElement()) {
			parserState.stage = ParsingStage.START;
		}
	}

	private void otherXml(ParseEventHandler handler, ParserState parserState, char c) {
		if (c == CLOSE_BRACKET) {
			parserState.stage = ParsingStage.START;
			handler.onOtherXML(parserState.elementCData);
			parserState.elementCData = null;
			return;
		}

		if (parserState.elementCData == null) {
			parserState.elementCData = new StringBuilder(100);
		}

		parserState.elementCData.append(c);

		if (parserState.elementCData.length() > CDATA_MAX_SIZE) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max cdata size exceeded: " + CDATA_MAX_SIZE + "\n received: "
					+ parserState.elementCData.toString();
			parserState.errorType = ELEMENT_VALUE_SIZE_LIMIT_EXCEEDED;
		}
	}

	private void entity(ParserState parserState, char c) {
		boolean alpha = ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		boolean numeric = !alpha && (c >= '0' && c <= '9');

		boolean valid = true;

		switch (parserState.entityType) {
		case UNKNOWN:
			if (alpha) {
				parserState.entityType = Entity.NAMED;
			} else if (c == HASH) {
				parserState.entityType = Entity.CODEPOINT;
			} else {
				valid = false;
			}
			break;
		case NAMED:
			if (!(alpha || numeric)) {
				if (c != SEMICOLON)
					valid = false;
				else
					parserState.stage = parserState.prevStage;
			}
			break;
		case CODEPOINT:
			if (c == 'x') {
				parserState.entityType = Entity.CODEPOINT_HEX;
			}
			if (numeric) {
				parserState.entityType = Entity.CODEPOINT_DEC;
			} else {
				valid = false;
			}
			break;
		case CODEPOINT_DEC:
			if (!numeric) {
				if (c != SEMICOLON)
					valid = false;
				else
					parserState.stage = parserState.prevStage;
			}
			break;
		case CODEPOINT_HEX:
			if (!((c >= 'a' && c <= 'f') || (c >= 'A' || c <= 'F') || numeric)) {
				if (c != SEMICOLON)
					valid = false;
				else
					parserState.stage = parserState.prevStage;
			}
			break;
		}

		if (valid) {
			switch (parserState.prevStage) {
			case START_ATTR_VALUE_DQ:
			case START_ATTR_VALUE_SQ:
				parserState.attributeValues[parserState.curAttrIndex].append(c);
				break;
			case START_CDATA:
			case ELEMENT_TEXT:
				parserState.elementCData.append(c);
				break;
			default:
				break;
			}
		} else {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Invalid XML entity";
			parserState.errorType = INVALID_ENTITY;
		}
	}

	private void startElementCData(ParseEventHandler handler, ParserState parserState, char c) {
		parserState.cDataBoundaryCharIndex++;

		if (c == ParserConfiguration.CDATA_START[parserState.cDataBoundaryCharIndex]) {
			if (parserState.cDataBoundaryCharIndex == 8) {
				parserState.stage = ParsingStage.START_CDATA_CONTENT;
				parserState.elementCData = new StringBuilder(100);
				parserState.cDataBoundaryCharIndex = -1;
				return;
			}

		} else {
			parserState.stage = ParsingStage.ERROR;
		}
	}

	private void startCDataContent(ParseEventHandler handler, ParserState parserState, char c) {
		if (c == ']') {
			parserState.stage = ParsingStage.END_CDATA;
		} else {
			parserState.elementCData.append(c);
		}
	}

	private void endCDataContent(ParseEventHandler handler, ParserState parserState, char c) {
		if (c == ']') {
			return;
		} else if (c == '>') {
			parserState.stage = ParsingStage.ELEMENT_TEXT;
			handler.onElementCData(parserState.elementCData);
			parserState.elementCData = null;
		} else {
			parserState.stage = ParsingStage.ERROR;
		}
	}

	private void startElementValue(ParseEventHandler handler, ParserState parserState, char c) {
		if (c == OPEN_BRACKET) {
			parserState.stage = ParsingStage.OPEN_BRACKET;
			parserState.slashFound = false;

			if (parserState.elementCData != null) {
				handler.onElementText(parserState.elementCData);
				parserState.elementCData = null;
			}

		} else {

			if (parserState.elementCData == null) {
				parserState.elementCData = new StringBuilder(100);
			}

			parserState.elementCData.append(c);
			if (c == '&') {
				parserState.prevStage = parserState.stage;
				parserState.stage = ParsingStage.ENTITY;
				parserState.entityType = Entity.UNKNOWN;
			}

			if (parserState.elementCData.length() > CDATA_MAX_SIZE) {
				parserState.stage = ParsingStage.ERROR;
				parserState.errorMessage = "Max cdata size exceeded: " + CDATA_MAX_SIZE + "\nreceived: "
						+ parserState.elementCData.toString();
				parserState.errorType = ELEMENT_VALUE_SIZE_LIMIT_EXCEEDED;
			}
		}
	}

	private void startAttributeValueDQ(ParserState parserState, char c) {
		if (c == DOUBLE_QUOTE) {
			parserState.stage = ParsingStage.END_ELEMENT_NAME;

			return;
		}

		parserState.attributeValues[parserState.curAttrIndex].append(c);

		switch (c) {
		case '&':
			parserState.prevStage = parserState.stage;
			parserState.stage = ParsingStage.ENTITY;
			parserState.entityType = Entity.UNKNOWN;
			break;
		case '<':
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in element attribute value: " + c
					+ "\nExisting characters in element attribute value: "
					+ parserState.attributeValues[parserState.curAttrIndex].toString();
			parserState.errorType = CHAR_NOT_ALLOWED;
			break;
		default:
			break;
		}

		if (parserState.attributeValues[parserState.curAttrIndex].length() > ATTR_VALUE_MAX_LENGTH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max attribute value size exceeded: " + ATTR_VALUE_MAX_LENGTH + "\nreceived: "
					+ parserState.attributeValues[parserState.curAttrIndex].toString();
			parserState.errorType = ATTR_VALUE_LENGTH_LIMIT_EXCEEDED;
		}
	}

	private void startAttributeValueSQ(ParserState parserState, char c) {
		if (c == SINGLE_QUOTE) {
			parserState.stage = ParsingStage.END_ELEMENT_NAME;

			return;
		}

		parserState.attributeValues[parserState.curAttrIndex].append(c);

		switch (c) {
		case '&':
			parserState.prevStage = parserState.stage;
			parserState.stage = ParsingStage.ENTITY;
			parserState.entityType = Entity.UNKNOWN;
			break;
		case '<':
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in element attribute value: " + c
					+ "\nExisting characters in element attribute value: "
					+ parserState.attributeValues[parserState.curAttrIndex].toString();
			parserState.errorType = CHAR_NOT_ALLOWED;
			break;
		default:
			break;
		}

		if (parserState.attributeValues[parserState.curAttrIndex].length() > ATTR_VALUE_MAX_LENGTH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max attribute value size exceeded: " + ATTR_VALUE_MAX_LENGTH + "\nreceived: "
					+ parserState.attributeValues[parserState.curAttrIndex].toString();
			parserState.errorType = ATTR_VALUE_LENGTH_LIMIT_EXCEEDED;
		}
	}

	private void endAttributeName(ParserState parserState, char c) {
		if (c == SINGLE_QUOTE) {
			parserState.stage = ParsingStage.START_ATTR_VALUE_SQ;
			parserState.attributeValues[parserState.curAttrIndex] = new StringBuilder(64);
		}

		if (c == DOUBLE_QUOTE) {
			parserState.stage = ParsingStage.START_ATTR_VALUE_DQ;
			parserState.attributeValues[parserState.curAttrIndex] = new StringBuilder(64);
		}
	}

	private void startAttributeName(ParserState parserState, char c) {
		if (CharUtils.isWhiteChar(c) || (c == EQUALS)) {
			parserState.stage = ParsingStage.END_ATTR_NAME;

			return;
		}

		if ((c == ERR_NAME_CHARS[0]) || (c == ERR_NAME_CHARS[1]) || (c == ERR_NAME_CHARS[2])) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in element attribute name: " + c
					+ "\nExisting characters in element attribute name: "
					+ parserState.attributeNames[parserState.curAttrIndex].toString();
			parserState.errorType = CHAR_NOT_ALLOWED;
			return;
		}

		parserState.attributeNames[parserState.curAttrIndex].append(c);

		if (parserState.attributeNames[parserState.curAttrIndex].length() > ATTR_NAME_MAX_LENGTH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max attribute name size exceeded: " + ATTR_NAME_MAX_LENGTH + "\nreceived: "
					+ parserState.attributeNames[parserState.curAttrIndex].toString();
			parserState.errorType = ATTR_NAME_LENGTH_LIMIT_EXCEEDED;
		}
	}

	private void endElementName(ParseEventHandler handler, ParserState parserState, char c) {
		if (c == SLASH) {
			parserState.slashFound = true;
			return;
		}

		if (c == CLOSE_BRACKET) {
			parserState.stage = ParsingStage.ELEMENT_TEXT;
			handler.onStartElement(parserState.elementName, parserState.attributeNames, parserState.attributeValues);
			parserState.attributeNames = null;
			parserState.attributeValues = null;
			parserState.curAttrIndex = -1;

			if (parserState.slashFound) {
				handler.onEndElement(parserState.elementName);
			}

			parserState.elementName = null;

			return;
		}

		if (!CharUtils.isWhiteChar(c)) {
			parserState.stage = ParsingStage.START_ATTR_NAME;

			if (parserState.attributeNames == null) {
				parserState.attributeNames = ArrayUtils.initArray(ATTR_BLOCK_SIZE);
				parserState.attributeValues = ArrayUtils.initArray(ATTR_BLOCK_SIZE);
			} else {
				if (parserState.curAttrIndex == parserState.attributeNames.length - 1) {
					if (parserState.attributeNames.length >= MAX_ATTR_COUNT) {
						parserState.stage = ParsingStage.ERROR;
						parserState.errorMessage = "Attributes nuber limit exceeded: " + MAX_ATTR_COUNT + "\nreceived: "
								+ parserState.elementName.toString();
						parserState.errorType = ATTR_COUNT_LIMIT_EXCEEDED;
						return;
					} else {
						int new_size = parserState.attributeNames.length + ATTR_BLOCK_SIZE;
						parserState.attributeNames = ArrayUtils.resizeArray(parserState.attributeNames, new_size);
						parserState.attributeValues = ArrayUtils.resizeArray(parserState.attributeValues, new_size);
					}
				}
			}

			parserState.attributeNames[++parserState.curAttrIndex] = new StringBuilder(8);
			parserState.attributeNames[parserState.curAttrIndex].append(c);
		}
	}

	private void closeElement(ParseEventHandler handler, ParserState parserState, char c) {
		if (CharUtils.isWhiteChar(c)) {
			return;
		}

		if (c == SLASH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in close element name: " + c
					+ "\nExisting characters in close element name: " + parserState.elementName.toString();
			parserState.errorType = CHAR_NOT_ALLOWED;
			return;
		}

		if (c == CLOSE_BRACKET) {
			parserState.stage = ParsingStage.ELEMENT_TEXT;
			if (!handler.onEndElement(parserState.elementName)) {
				parserState.stage = ParsingStage.ERROR;
				parserState.errorMessage = "Malformed XML: element close found without open for this element: "
						+ parserState.elementName;
				parserState.errorType = CLOSE_ELEMENT_BEFORE_OPEN;
				return;
			}

			parserState.elementName = null;
			return;
		}

		if ((c == ERR_NAME_CHARS[0]) || (c == ERR_NAME_CHARS[1]) || (c == ERR_NAME_CHARS[2])) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in close element name: " + c
					+ "\nExisting characters in close element name: " + parserState.elementName.toString();
			parserState.errorType = CHAR_NOT_ALLOWED;
			return;
		}

		parserState.elementName.append(c);

		if (parserState.elementName.length() > ELM_NAME_MAX_LENGTH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max element name size exceeded: " + ELM_NAME_MAX_LENGTH + "\nreceived: "
					+ parserState.elementName.toString();
			parserState.errorType = ELEMENT_NAME_SIZE_LIMIT_EXCEEDED;
		}
	}

	private void startElementNameStage(ParseEventHandler handler, ParserState parserState, char c) {
		if (CharUtils.isWhiteChar(c)) {
			parserState.stage = ParsingStage.END_ELEMENT_NAME;

			return;
		}

		if (c == SLASH) {
			parserState.slashFound = true;

			return;
		}

		if (c == CLOSE_BRACKET) {
			parserState.stage = ParsingStage.ELEMENT_TEXT;
			handler.onStartElement(parserState.elementName, null, null);

			if (parserState.slashFound) {
				handler.onEndElement(parserState.elementName);
			}

			parserState.elementName = null;

			return;
		}

		if ((c == ERR_NAME_CHARS[0]) || (c == ERR_NAME_CHARS[1]) || (c == ERR_NAME_CHARS[2])) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Not allowed character in start element name: " + c
					+ "\nExisting characters in start element name: " + parserState.elementName.toString();
			parserState.errorType = CHAR_NOT_ALLOWED;

			return;
		}

		parserState.elementName.append(c);

		if (parserState.elementName.length() > ELM_NAME_MAX_LENGTH) {
			parserState.stage = ParsingStage.ERROR;
			parserState.errorMessage = "Max element name size exceeded: " + ELM_NAME_MAX_LENGTH + "\nreceived: "
					+ parserState.elementName.toString();
			parserState.errorType = ELEMENT_NAME_SIZE_LIMIT_EXCEEDED;
		}
	}

	private void handleOpenBracketStage(ParserState parserState, char c) {
		switch (c) {
		case QUESTION_MARK:
			parserState.stage = ParsingStage.OTHER_XML;
			parserState.elementCData = new StringBuilder(100);
			parserState.elementCData.append(c);

			break;

		case EXCLAMATION_MARK:
			parserState.stage = ParsingStage.START_CDATA;
			parserState.elementCData = new StringBuilder(100);
			parserState.cDataBoundaryCharIndex = 1;
			break;

		case SLASH:
			parserState.stage = ParsingStage.CLOSE_ELEMENT;
			parserState.elementName = new StringBuilder(10);
			parserState.slashFound = true;

			break;

		default:
			if (Arrays.binarySearch(WHITE_CHARS, c) < 0) {
				if ((c == ERR_NAME_CHARS[0]) || (c == ERR_NAME_CHARS[1]) || (c == ERR_NAME_CHARS[2])) {
					parserState.stage = ParsingStage.ERROR;
					parserState.errorMessage = "Not allowed character in start element name: " + c;
					parserState.errorType = CHAR_NOT_ALLOWED;
					break;
				}

				parserState.stage = ParsingStage.START_ELEMENT_NAME;
				parserState.elementName = new StringBuilder(10);
				parserState.elementName.append(c);
			}
		}
	}

	private boolean isValidChar(ParserState parserState, char chr) {
		boolean highSurrogate = parserState.highSurrogate;

		parserState.highSurrogate = false;

		if (chr <= 0xD7FF) {
			if (chr >= 0x20)
				return true;
			return ALLOWED_CHARS_LOW[chr];

		} else if (chr <= 0xFFFD) {
			if (chr >= 0xE000)
				return true;

			if (Character.isLowSurrogate(chr)) {
				return highSurrogate;
			} else if (Character.isHighSurrogate(chr)) {
				parserState.highSurrogate = true;
				return true;
			}
		}

		return false;
	}

	@Override
	public String getName() {
		return "SwiftParser";
	}

	public static enum Entity {
		NAMED,

		CODEPOINT,

		CODEPOINT_DEC,

		CODEPOINT_HEX,

		UNKNOWN;
	}

	public static enum ParsingStage {
		START,

		OPEN_BRACKET,

		START_ELEMENT_NAME,

		END_ELEMENT_NAME,

		START_ATTR_NAME,

		END_ATTR_NAME,

		START_ATTR_VALUE_SQ,

		START_ATTR_VALUE_DQ,

		ELEMENT_TEXT,

		START_CDATA,

		START_CDATA_CONTENT,

		END_CDATA,

		OTHER_XML,

		CLOSE_ELEMENT,

		ENTITY,

		END_OF_ROOT,

		ERROR;
	}

}
