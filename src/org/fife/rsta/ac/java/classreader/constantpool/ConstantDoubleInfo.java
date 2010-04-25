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
 * Class corresponding to the <code>CONSTANT_Double_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantDoubleInfo extends ConstantPoolInfo {

	private int highBytes;
	private int lowBytes;


	/**
	 * Constructor.
	 *
	 * @param highBytes
	 * @param lowBytes
	 */
	public ConstantDoubleInfo(int highBytes, int lowBytes) {
		super(CONSTANT_Double);
		this.highBytes = highBytes;
		this.lowBytes = lowBytes;
	}


	/**
	 * Returns the <code>double</code> value represented by this constant.
	 *
	 * @return The double value.
	 */
	public double getDoubleValue() {
		long bits = (((long)highBytes<<32)) + lowBytes;
		return Double.longBitsToDouble(bits);
	}


	public int getHighBytes() {
		return highBytes;
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
		return "[ConstantDoubleInfo: " +
				"value=" + getDoubleValue() +
				"]";
	}


}