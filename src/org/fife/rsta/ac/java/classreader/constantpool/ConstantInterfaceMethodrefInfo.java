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


/**
 * Class corresponding to a <code>CONSTANT_InterfaceMethodref_info</code>
 * structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ConstantInterfaceMethodrefInfo extends ConstantPoolInfo {


	private int classIndex;

	private int nameAndTypeIndex;


	/**
	 * Constructor.
	 *
	 * @param classIndex
	 * @param nameAndTypeIndex
	 */
	public ConstantInterfaceMethodrefInfo(int classIndex, int nameAndTypeIndex){
		super(CONSTANT_InterfaceMethodref);
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}


	public int getClassIndex() {
		return classIndex;
	}


	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[ConstantInterfaceMethodrefInfo: " +
		"classIndex=" + getClassIndex() +
		"; nameAndTypeIndex=" + getNameAndTypeIndex() +
		"]";
	}


}