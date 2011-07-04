package org.fife.rsta.ac.jsp;

import java.util.ArrayList;
import java.util.List;


/**
 * An element defined in a TLD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TldElement {

	public String name;
	public String desc;
	public List attributes;


	public TldElement(String name, String desc) {
		this.name = name;
		this.desc = desc;
		this.attributes = new ArrayList();
	}


	public void addAttr(String name, String desc, boolean required,
						boolean rtexprvalue) {
		attributes.add(new TldAttribute(name, desc, required, rtexprvalue));
	}


	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		sb.append(name).append(": ");
		for (int i=0; i<attributes.size(); i++) {
			TldAttribute attr = (TldAttribute)attributes.get(i);
			sb.append(attr.name).append(" (").append(attr.required).append("), ");
		}
		sb.append("]");
		return sb.toString();
	}


}