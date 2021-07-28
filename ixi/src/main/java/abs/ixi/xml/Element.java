package abs.ixi.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import abs.ixi.server.common.Pair;

/**
 * This extremely simple implementation for XML element. It does not support
 * many features offered in XML specification for an implement. However it is
 * good enough to handle XMPP Stanza elements.
 * 
 * Currently this does not support {@link CDataSection}
 */
public class Element implements XMLNode {
	private static final long serialVersionUID = 1L;

	protected String name = null;
	protected String defxmlns = null;
	protected String xmlns = null;

	protected LinkedList<XMLNode> children;
	protected LinkedList<Pair<String, String>> attributes;

	/**
	 * Performs a deep copy on the supplied element and populates instance
	 * variable of this instance.
	 */
	public Element(Element element) {
		Element src = element.clone();

		this.attributes = src.attributes;
		this.name = src.name;
		this.defxmlns = src.defxmlns;
		this.xmlns = src.xmlns;
		this.children = src.children;
	}

	public Element(String name, StringBuilder[] attributeName, StringBuilder[] attributeValues) {
		this.name = name;
		if (attributeName != null && attributeValues != null) {
			attributes = new LinkedList<>();
			for (int i = 0; i < attributeName.length; i++) {
				if (attributeName[i] == null) {
					break;
				}
				attributes.add(new Pair<String, String>(attributeName[i].toString(), attributeValues[i].toString()));
			}
		}

	}

	/**
	 * Creates a blank element with the name given
	 */
	public Element(String name) {
		setName(name);
	}

	/**
	 * Creates a simple element with given and name and text value
	 */
	public Element(String name, String text) {
		setName(name);

		if (text != null) {
			addText(text);
		}
	}

	public void addAttribute(String attName, String attValue) {
		setAttribute(attName, attValue);
	}

	public void addAttributes(Map<String, String> attrs) {
		if (attributes == null) {
			attributes = new LinkedList<>();
		}

		for (Map.Entry<String, String> entry : attrs.entrySet()) {
			attributes.add(new Pair<String, String>(entry.getKey(), entry.getValue()));
		}
	}

	public void addChild(XMLNode child) {
		if (child == null) {
			throw new NullPointerException("Element child can not be null.");
		}
		if (children == null) {
			children = new LinkedList<>();
		}

		children.add(child);
	}

	public void addChildren(List<Element> children) {
		if (children == null) {
			return;
		}

		if (this.children == null) {
			this.children = new LinkedList<>();
		}

		for (XMLNode child : children) {
			this.children.add(child.clone());
		}
	}

	public Element findChild(String[] elemPath) {
		if (elemPath[0].isEmpty()) {
			elemPath = Arrays.copyOfRange(elemPath, 1, elemPath.length);
		}
		if (!elemPath[0].equals(getName())) {
			return null;
		}

		Element child = this;

		// we must start with 1 not 0 as 0 is name of parent element
		for (int i = 1; (i < elemPath.length) && (child != null); i++) {
			String str = elemPath[i];

			child = child.getChild(str);
		}

		return child;
	}

	public <R> List<R> flatMapChildren(Function<Element, Collection<? extends R>> mapper) {
		if (children != null) {
			LinkedList<R> result = new LinkedList<R>();

			for (XMLNode node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				result.addAll(mapper.apply(el));
			}

			return result;
		}

		return null;
	}

	public void forEachChild(Consumer<Element> consumer) {
		if (children != null) {
			for (XMLNode node : children) {
				if (!(node instanceof Element)) {
					continue;
				}

				Element el = (Element) node;
				consumer.accept(el);
			}
		}
	}

	public String getChildAttribute(String childName, String attName) {
		String result = null;
		Element child = getChild(childName);

		if (child != null) {
			result = child.getAttribute(attName);
		}

		return result;
	}

	/**
	 * Returns value for the supplied attribute. As attributes are stored in a
	 * list, it's a plain linear probe. We may change the data structure to a
	 * {@link Map} if this degrades the performance. The assumption is that XMPP
	 * packets do not have too many attributes.
	 */
	public String getAttribute(String attName) {
		if (attributes != null) {
			for (Pair<String, String> pair : this.attributes) {
				if (pair.getFirst().equals(attName)) {
					return pair.getSecond();
				}
			}
		}

		return null;
	}

	/**
	 * Returns attributes for this element. Any change in the returned
	 * attributes will reflect back in this element attribute list
	 */
	public List<Pair<String, String>> getAttributes() {
		return attributes;
	}

	public Element getChild(String name) {
		if (children != null) {
			for (XMLNode el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;
					if (elem.getName().equals(name)) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public Element getChildStaticStr(String name) {
		if (children != null) {
			for (XMLNode el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;

					if (elem.getName() == name) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public Element getChild(String name, String child_xmlns) {
		if (child_xmlns == null) {
			return getChild(name);
		}
		if (children != null) {
			for (XMLNode el : children) {
				if (el instanceof Element) {
					Element elem = (Element) el;
					if (elem.getName().equals(name) && (child_xmlns.equals(elem.getXMLNS()))) {
						return elem;
					}
				}
			}
		}

		return null;
	}

	public List<Element> getChildren() {
		if (children != null) {
			LinkedList<Element> result = new LinkedList<Element>();

			for (XMLNode node : children) {
				if (node instanceof Element) {
					result.add((Element) node);
				}
			}

			return result;
		}

		return null;
	}

	public List<Element> getChildren(String[] elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getChildren() : null;
	}

	public String getXMLNS() {
		if (xmlns == null) {
			xmlns = getAttribute("xmlns");
			xmlns = ((xmlns != null) ? xmlns.intern() : null);
		}

		return (xmlns != null) ? xmlns : defxmlns;
	}

	public String getXMLNS(String[] elementPath) {
		Element child = findChild(elementPath);

		return (child != null) ? child.getXMLNS() : null;
	}

	public void removeAttribute(String key) {
		if (attributes != null) {
			attributes.remove(key.intern());
		}
	}

	public boolean removeChild(Element child) {
		boolean res = false;

		if (children != null) {
			res = children.remove(child);
		}

		return res;
	}

	public void setAttribute(String key, String value) {
		if (attributes == null) {
			attributes = new LinkedList<>();
		}

		String k = key.intern();
		String v = value;

		if (k == "xmlns") {
			xmlns = value.intern();
			v = xmlns;
		}

		attributes.add(new Pair<String, String>(k, v));
	}

	/**
	 * Adds text node in the children of this element. This operation will
	 * remove exiting {@link CDataSection} and {@link Text} nodes from the
	 * children
	 */
	public void addText(String text) {
		addText(text, true);
	}

	/**
	 * adds {@link Text} node to this element children. If
	 * {@code removeExisting} flag is on, exisitng {@link CDataSection} and
	 * {@link Text} nodes will be removed
	 * 
	 * @param text
	 * @param removeExiting
	 */
	public void addText(String text, boolean removeExiting) {
		if (removeExiting) {
			removeText();
		}

		addChild(new Text(text));
	}

	/**
	 * Removes {@link Text} nodes and {@link CDataSection} nodes from the
	 * children of this element
	 */
	public void removeText() {
		if (this.children == null)
			return;

		this.children.removeIf((v) -> (v instanceof Text && !(v instanceof CDataSection)));
	}

	/**
	 * Adds {@link CDataSection} node in the children of this element
	 */
	public void addCDATASection(String cdata) {
		addChild(new CDataSection(cdata));
	}

	public void setChildren(List<XMLNode> children) {
		this.children = new LinkedList<>();

		for (XMLNode child : children) {
			this.children.add(child.clone());
		}
	}

	public void setDefXMLNS(String ns) {
		defxmlns = ns.intern();
	}

	public void setName(String argName) {
		this.name = argName.intern();
	}

	public void setXMLNS(String ns) {
		if (ns == null) {
			xmlns = null;
			removeAttribute("xmlns");
		} else {
			xmlns = ns.intern();
			setAttribute("xmlns", xmlns);
		}
	}

	public String getName() {
		return this.name;
	}

	@Override
	public int children() {
		return children != null && !children.isEmpty() ? children.size() : 0;
	}

	@Override
	public String stringify() {
		StringBuilder xml = new StringBuilder();

		xml.append("<").append(name);

		if (attributes != null) {
			for (Pair<String, String> pair : this.attributes) {
				xml.append(" ").append(pair.getFirst()).append("='").append(pair.getSecond()).append("'");
			}
		}

		if (children != null && !children.isEmpty()) {
			xml.append(">");
			xml.append(stringifyChildren());
			xml.append("</").append(name).append(">");
		} else {
			xml.append("/>");
		}

		return xml.toString();
	}

	@Override
	public String stringifyChildren() {
		StringBuilder xml = new StringBuilder();

		if (children != null && !children.isEmpty()) {
			for (XMLNode child : children) {
				xml.append(child.stringify());
			}
		}

		return xml.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Element clone() {
		Element clone = null;

		try {

			clone = (Element) super.clone();

		} catch (CloneNotSupportedException e) {
			throw new ParserInternalError("Clone is not supported on element");
		}

		if (this.attributes != null) {
			clone.attributes = (LinkedList<Pair<String, String>>) this.attributes.clone();

		} else {
			clone.attributes = null;
		}

		if (children != null) {
			clone.setChildren(this.children);

		} else {
			clone.children = null;
		}

		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Element) {
			String objString = ((Element) obj).stringify();
			String thisString = this.stringify();

			return objString == thisString;
		}

		return false;
	}

	@Override
	public String val() {
		if (this.children == null)
			return null;

		StringBuilder val = new StringBuilder();

		for (XMLNode child : this.children) {
			if (child instanceof Text || child instanceof CDataSection) {
				val.append(child.val());
			}
		}

		return val.toString();
	}

	@Override
	public String toString() {
		return "Element " + this.name + "[Children: " + this.children() + "]";
	}

}
