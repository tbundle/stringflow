package abs.ixi.xml;

import java.io.Serializable;

/**
 * Represents a XML Node inside a DOM.
 */
public interface XMLNode extends Cloneable, Serializable {
	/**
	 * Returns number of children for this node. If this is leaf node, it will
	 * return zero
	 */
	public int children();

	/**
	 * makes a deep copy of this XML Node and returns it
	 * 
	 * @return
	 */
	public XMLNode clone();

	/**
	 * Returns XML representation of this XML node
	 */
	public String stringify();

	/**
	 * Returns XML string with all the children nodes of this node
	 * 
	 * @return
	 */
	public String stringifyChildren();

	/**
	 * Returns concatenated String of {@link Text} and {@link CDataSection}
	 * nodes in the children. If there is no child of type {@link Text} or
	 * {@link CDataSection} exist, this method will return null
	 */
	public String val();

}
