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
 * Class corresponding to the <code>CONSTANT_Long_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantLongInfo extends ConstantPoolInfo {

	private int highBytes; // u4
	private int lowBytes; // u4


	/**
	 * Constructor.
	 *
	 * @param highBytes
	 * @param lowBytes
	 */
	public ConstantLongInfo(int highBytes, int lowBytes) {
		super(CONSTANT_Long);
		this.highBytes = highBytes;
		this.lowBytes = lowBytes;
	}


	public int getHighBytes() {
		return highBytes;
	}


	public long getLongValue() {
		return (((long)highBytes<<32)) + lowBytes;
	}


	public int getLowBytes() {
		return lowBytes;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[ConstantLongInfo: " +
				"value=" + getLongValue() +
				"]";
	}


}