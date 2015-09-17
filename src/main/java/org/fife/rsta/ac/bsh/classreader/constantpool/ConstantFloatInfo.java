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
package org.fife.rsta.ac.bsh.classreader.constantpool;


/**
 * Class corresponding to the <code>CONSTANT_Float_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantFloatInfo extends ConstantPoolInfo {

	private int bytes; // u4


	/**
	 * Constructor.
	 *
	 * @param bytes
	 */
	public ConstantFloatInfo(int bytes) {
		super(CONSTANT_Float);
		this.bytes = bytes;
	}


	public long getBytes() {
		return bytes;
	}


	/**
	 * Returns the float value represented.
	 *
	 * @return The float value.
	 */
	public float getFloatValue() {
		return Float.intBitsToFloat(bytes);
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[ConstantFloatInfo: " +
				"value=" + getFloatValue() +
				"]";
	}


}