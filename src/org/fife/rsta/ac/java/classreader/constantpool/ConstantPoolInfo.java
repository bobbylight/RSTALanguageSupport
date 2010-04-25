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
 * A <code>ConstantPool</code> table entry.<p>
 *
 * See <a href="http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080">
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080</a>
 * for more information.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class ConstantPoolInfo implements ConstantTypes {

	private int tag;


	/**
	 * Constructor.
	 */
	public ConstantPoolInfo(int tag) {
		this.tag = tag;
	}


	/**
	 * Returns the tag item for this structure.
	 *
	 * @return The tag item.
	 */
	public int getTag() {
		return tag;
	}


}