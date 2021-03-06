package abs.ixi.xml;

/**
 * Ultra simple event handler for events generated by an event driven parser.
 */
public interface ParseEventHandler {

	public void onStartElement(StringBuilder name, StringBuilder[] attr_names, StringBuilder[] attr_values);

	public boolean onEndElement(StringBuilder name);

	public void onError(String errorMessage);

	public void onOtherXML(StringBuilder other);

	public void saveParserState(ParserState state);

	public ParserState getParserState();

	public Element getParsedElement();

	public boolean hasParsedElement();

	public void onElementCData(StringBuilder cdata);

	public void onElementText(StringBuilder cdata);

	/**
	 * Callback interface to enable event based processing in XML Parsing
	 * process.
	 */
	public interface ParseEventCallback {
		/**
		 * Get invoked when {@link ParseEventHandler} has finished parsing an
		 * element. Once the method is invoked, Parsed element is removed from
		 * the queue.
		 * 
		 * @return true if the parser should quit processing otherwise false
		 */
		public boolean onParsedElement(Element element);
	}

}