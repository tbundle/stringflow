package abs.ixi.server;

/**
 * Interface oto indicate if the entity can be converted to XML string.
 * Implementation classes MUST esacpe XML characters while generating the XML
 * string.
 */
public interface XMLConvertible {

	/**
	 * Generate XML String out of the this entity. Implementation class is free
	 * to decide how to generate it and what all fields to be included in the
	 * genrated xml string.
	 * 
	 * @return XML String
	 */
	public String xml();

	public StringBuilder appendXml(StringBuilder sb);
}
