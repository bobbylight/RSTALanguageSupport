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
import org.fife.rsta.ac.java.classreader.constantpool.*;


/**
 * Implementation of the "<code>Exceptions</code>" attribute found in
 * {@link MethodInfo}s.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Exceptions extends AttributeInfo {

	/**
	 * The method this attribute is describing.
	 */
	private MethodInfo mi;

	/**
	 * Indices into the constant pool of {@link ConstantClassInfo}s, each
	 * representing a class type that this method is declared to throw.
	 */
	private int[] exceptionIndexTable;


	/**
	 * Constructor.
	 *
	 * @param mi The method this attribute is describing.
	 */
	public Exceptions(MethodInfo mi, int[] exceptionIndexTable) {
		super(mi.getClassFile());
		this.exceptionIndexTable = exceptionIndexTable;
	}


	/**
	 * Returns the fully-qualified name of the specified exception.
	 *
	 * @param index The index of the exception whose name to retrieve.
	 * @return The name of the exception.
	 */
	public String getException(int index) {
		ClassFile cf = getClassFile();
		ConstantPoolInfo cpi = cf.getConstantPoolInfo(
										exceptionIndexTable[index]);
		ConstantClassInfo cci = (ConstantClassInfo)cpi;
		int nameIndex = cci.getNameIndex();
		String name = cf.getUtf8ValueFromConstantPool(nameIndex);
		name = org.fife.rsta.ac.java.Util.replaceChar(name, '/', '.');
		return name;
	}


	/**
	 * Returns the number of exceptions this attribute knows about.
	 *
	 * @return The number of exceptions.
	 */
	public int getExceptionCount() {
		return exceptionIndexTable==null ? 0 : exceptionIndexTable.length;
	}


	/**
	 * Returns information about the method this attribute is describing.
	 *
	 * @return The method information.
	 */
	public MethodInfo getMethodInfo() {
		return mi;
	}


}