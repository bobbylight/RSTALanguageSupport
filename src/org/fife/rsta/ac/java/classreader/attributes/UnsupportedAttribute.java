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
package org.fife.rsta.ac.java.classreader.attributes;

import org.fife.rsta.ac.java.classreader.*;


/**
 * An attribute that is unknown/unsupported by this decompiler.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class UnsupportedAttribute extends AttributeInfo {

	private String name;
	private int[] info;


	/**
	 * Constructor.
	 *
	 * @param cf The class file.
	 * @param info The bytes defining the attribute.
	 */
	public UnsupportedAttribute(ClassFile cf, String name, int[] info) {
		super(cf);
		this.name = name;
		this.info = info;
	}


	public int[] getInfo() {
		return info;
	}


	public String getName() {
		return name;
	}


	public void setInfo(int[] info) {
		this.info = info;
	}


	/**
	 * Returns a string representation of this attribute.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this attribute.
	 */
	public String toString() {
		return "[UnsupportedAttribute: " +
				"name=" + getName() +
				"]";
	}


}