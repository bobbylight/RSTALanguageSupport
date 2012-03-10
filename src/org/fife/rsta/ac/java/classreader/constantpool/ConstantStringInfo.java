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
package org.fife.rsta.ac.java.classreader.constantpool;

import org.fife.rsta.ac.java.classreader.*;


/**
 * Class corresponding to the <code>CONSTANT_String_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantStringInfo extends ConstantPoolInfo {

	private ClassFile cf;
	private int stringIndex;


	/**
	 * Constructor.
	 *
	 * @param stringIndex
	 */
	public ConstantStringInfo(ClassFile cf, int stringIndex) {
		super(CONSTANT_String);
		this.cf = cf;
		this.stringIndex = stringIndex;
	}


	public int getStringIndex() {
		return stringIndex;
	}


	/**
	 * Returns the string represented by this constant.
	 *
	 * @return The string value represented.
	 */
	public String getStringValue() {
		return '"' + cf.getUtf8ValueFromConstantPool(getStringIndex()) + '"';
				
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[ConstantStringInfo: " +
				"stringIndex=" + getStringIndex() +
				"]";
	}


}