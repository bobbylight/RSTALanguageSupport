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
 * Class corresponding to the <code>CONSTANT_Integer_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantIntegerInfo extends ConstantPoolInfo {

	private long bytes; // u4


	/**
	 * Constructor.
	 *
	 * @param stringIndex
	 */
	public ConstantIntegerInfo(long bytes) {
		super(CONSTANT_String);
		this.bytes = bytes;
	}


	public long getBytes() {
		return bytes;
	}


	/**
	 * Returns the <code>int</code>represented by this info.
	 *
	 * @return The <code>int</code> represented.
	 */
	public int getIntValue() {
		return (int)bytes;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[ConstantIntegerInfo: " +
				"bytes=" + getBytes() +
				"]";
	}


}