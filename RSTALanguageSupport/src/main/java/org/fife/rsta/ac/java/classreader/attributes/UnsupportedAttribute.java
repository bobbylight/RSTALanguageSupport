/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java.classreader.attributes;

import org.fife.rsta.ac.java.classreader.*;


/**
 * An attribute that is unknown/unsupported by this decompiler.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class UnsupportedAttribute extends AttributeInfo {

	private String name;
	//private int[] info;


	/**
	 * Constructor.
	 *
	 * @param cf The class file.
	 * @param name The name of the attribute.
	 */
	public UnsupportedAttribute(ClassFile cf, String name/*, int[] info*/) {
		super(cf);
		this.name = name;
		//this.info = info;
	}

	/*
	public int[] getInfo() {
		return info;
	}
	*/

	@Override
	public String getName() {
		return name;
	}


	/**
	 * Returns a string representation of this attribute.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this attribute.
	 */
	@Override
	public String toString() {
		return "[UnsupportedAttribute: " +
				"name=" + getName() +
				"]";
	}


}
