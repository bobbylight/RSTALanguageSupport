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
 * Represents a class or interface.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantClassInfo extends ConstantPoolInfo {

	/**
	 * An index into the constant_pool table.  The entry at this index
	 * must be a <code>CONSTANT_Utf8_info</code> structure representing
	 * a valid, fully-qualified class or interface name encoded in internal
	 * form.
	 */
	private int nameIndex;


	/**
	 * Constructor.
	 *
	 * @param nameIndex The index into the constant pool containing a
	 *        {@link ConstantUtf8Info} representing the fully-qualified
	 *        class or interface name, encoded in internal form.
	 */
	public ConstantClassInfo(int nameIndex) {
		super(CONSTANT_Class);
		this.nameIndex = nameIndex;
	}


	/**
	 * Returns the index into the constant pool table for a
	 * <code>ConstantUtf8Info</code> structure representing a valid,
	 * fully-qualified class or interface name, encoded in internal form.
	 *
	 * @return The index into the constant pool table.
	 */
	public int getNameIndex() {
		return nameIndex;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[ConstantClassInfo: " +
				"nameIndex=" + getNameIndex() +
				"]";
	}


}