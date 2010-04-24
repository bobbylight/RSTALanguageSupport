/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java.classreader.constantpool;


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
	public String toString() {
		return "[ConstantFloatInfo: " +
				"value=" + getFloatValue() +
				"]";
	}


}