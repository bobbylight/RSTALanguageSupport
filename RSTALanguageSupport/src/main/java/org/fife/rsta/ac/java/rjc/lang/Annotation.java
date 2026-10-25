/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.lang;


/**
 * Represents an annotation.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Annotation {

	private Type type;


	/**
	 * Constructor.
	 *
	 * @param type The type.
	 */
	public Annotation(Type type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "@" + type.toString();
	}


}
