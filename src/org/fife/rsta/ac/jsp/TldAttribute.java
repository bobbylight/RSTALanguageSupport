package org.fife.rsta.ac.jsp;


/**
 * An attribute of an element defined in a TLD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TldAttribute {

	public String name;
	public String desc;
	public boolean required;
	public boolean rtexprvalue;

	public TldAttribute(String name, String desc, boolean required,
						boolean rtexprvalue) {
		this.name = name;
		this.desc = desc;
		this.required = required;
		this.rtexprvalue = rtexprvalue;
	}

}
