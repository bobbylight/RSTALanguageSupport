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
 * Class representing a <code>CONSTANT_NameAndType_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantNameAndTypeInfo extends ConstantPoolInfo {

	private int nameIndex;
	private int descriptorIndex;


	/**
	 * Constructor.
	 *
	 * @param nameIndex
	 * @param descriptorIndex
	 */
	public ConstantNameAndTypeInfo(int nameIndex, int descriptorIndex) {
		super(CONSTANT_NameAndType);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}


	public int getDescriptorIndex() {
		return descriptorIndex;
	}


	public int getNameIndex() {
		return nameIndex;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[ConstantNameAndTypeInfo: " +
				"descriptorIndex=" + getDescriptorIndex() +
				"; nameIndex=" + getNameIndex() +
				"]";
	}


}