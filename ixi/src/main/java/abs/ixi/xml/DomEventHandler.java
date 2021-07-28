package abs.ixi.xml;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * DomBuilder is an implementation of {@link ParseEventHandler}. It is a very
 * simple implementation of Content Handler ({@link ParseEventHandler}) for
 * {@link SwiftParser}. {@code DomBuilder} can handle multiple document at the
 * same time (multi-root xml) and returns these document in the same order in
 * which they were received on the network.
 */
public class DomEventHandler implements ParseEventHandler {
	private static final Logger LOGGER = Logger.getLogger(DomEventHandler.class.getName());

	private ParserState parserState = null;
	private Element parsedElement;
	private Stack<Element> elmStack = new Stack<Element>();
	private Map<String, String> namespaces = new TreeMap<String, String>();
	private ParseEventCallback callback;

	public DomEventHandler(ParseEventCallback callback) {
		this.callback = callback;
	}

	@Override
	public Element getParsedElement() {
		Element element = parsedElement;
		this.parsedElement = null;

		return element;
	}

	private Element newElement(String name, String cdata, StringBuilder[] attnames, StringBuilder[] attvals) {
		return new Element(name, attnames, attvals);
	}

	@Override
	public void onStartElement(StringBuilder name, StringBuilder[] attr_names, StringBuilder[] attr_values) {
		if (attr_names != null) {
			for (int i = 0; i < attr_names.length; ++i) {
				if (attr_names[i] == null) {
					break;
				}

				if (attr_names[i].toString().startsWith("xmlns:")) {
					namespaces.put(attr_names[i].substring("xmlns:".length(), attr_names[i].length()),
							attr_values[i].toString());
				}
			}
		}

		String tmp_name = name.toString();
		String new_xmlns = null;
		String prefix = null;
		String tmp_name_prefix = null;
		int idx = tmp_name.indexOf(':');

		if (idx > 0) {
			tmp_name_prefix = tmp_name.substring(0, idx);
		}

		if (tmp_name_prefix != null) {
			for (String pref : namespaces.keySet()) {
				if (tmp_name_prefix.equals(pref)) {
					new_xmlns = namespaces.get(pref);
					tmp_name = tmp_name.substring(pref.length() + 1, tmp_name.length());
					prefix = pref;
				}
			}
		}

		Element elem = newElement(tmp_name, null, attr_names, attr_values);
		String ns = elem.getXMLNS();

		if (ns == null) {
			if (elmStack.isEmpty() || elmStack.peek().getXMLNS() == null) {
			} else {
				elem.setDefXMLNS(elmStack.peek().getXMLNS());
			}
		}
		if (new_xmlns != null) {
			elem.setXMLNS(new_xmlns);
			elem.removeAttribute("xmlns:" + prefix);
		}

		if (tmp_name.equals("stream")) {
			addParsedElement(elem);
		} else {
			elmStack.push(elem);
		}
	}

	@Override
	public void onElementCData(StringBuilder cdata) {
		try {
			elmStack.peek().addCDATASection(cdata.toString());
		} catch (EmptyStackException e) {
			// Swallow exception
			// TODO throwing exception could be a good idea
		}
	}

	@Override
	public boolean onEndElement(StringBuilder name) {
		String tmp_name = name.toString();
		String tmp_name_prefix = null;
		int idx = tmp_name.indexOf(':');

		if (idx > 0) {
			tmp_name_prefix = tmp_name.substring(0, idx);
		}

		if (tmp_name_prefix != null) {
			for (String pref : namespaces.keySet()) {
				if (tmp_name_prefix.equals(pref)) {
					tmp_name = tmp_name.substring(pref.length() + 1, tmp_name.length());
				}
			}
		}

		if (elmStack.isEmpty()) {
			// It means we have encountered end of element without start of it.
			elmStack.push(newElement(tmp_name, null, null, null));
			return false;
		}

		Element elem = elmStack.pop();
		if (!elem.getName().equals(tmp_name)){
			return false;
		}

		if (elmStack.isEmpty()) {
			addParsedElement(elem);
		} else {
			elmStack.peek().addChild(elem);
		}

		return true;
	}

	/**
	 * Adds parsed element to parsed root
	 */
	protected void addParsedElement(Element elem) {
		this.parsedElement = elem;
		boolean quit = this.callback.onParsedElement(elem);

		if (quit) {
			// TODO Should implement better mechanism to quit
			this.parserState.foundRootElement();
		}
	}

	@Override
	public void onOtherXML(StringBuilder other) {
		LOGGER.fine("Other XML content: " + other);
	}

	@Override
	public void saveParserState(ParserState state) {
		this.parserState = state;
	}

	@Override
	public ParserState getParserState() {
		return this.parserState == null ? new ParserState() : this.parserState;
	}

	@Override
	public void onError(String errorMessage) {
		LOGGER.fine("XML content parse error.");
		LOGGER.warning(errorMessage);
		LOGGER.warning(parserState.toString());
		printElmStack();
	}

	@Override
	public boolean hasParsedElement() {
		return this.parsedElement != null;
	}

	@Override
	public void onElementText(StringBuilder text) {
		try {
			elmStack.peek().addText(text.toString());

		} catch (EmptyStackException e) {
			// Swallow exception
			// TODO throwing exception could be a good idea
		}
	}

	public void printElmStack() {
		LOGGER.fine("Printing ElmStack Elements");

		Iterator<Element> itr = elmStack.iterator();

		while (itr.hasNext()) {
			LOGGER.fine(itr.next().stringify());
		}
	}

}