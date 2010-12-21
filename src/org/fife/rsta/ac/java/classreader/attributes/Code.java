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

import java.io.*;

import org.fife.rsta.ac.java.classreader.*;


/**
 * A variable-length attribute used in the attributes table of
 * {@link MethodInfo} structures.  A <code>Code</code> attribute contains the
 * JVM instructions and auxiliary information for a single method, instance
 * initialization method, or class or interface initialization method.  Every
 * JVM implementation must recognize <code>Code</code> attributes.  If the
 * method is either <code>native</code> or <code>abstract</code>, its
 * <code>MethodInfo</code> structure must not have a <code>Code</code>
 * attribute.  Otherwise, its <code>MethodInfo</code> structure must have
 * exactly one <code>Code</code> attribute.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Code extends AttributeInfo {

	/**
	 * The parent method.
	 */
	private MethodInfo mi;

	/**
	 * The maximum depth of the operand stack of this method at any point
	 * during its execution.
	 */
	private int maxStack;

	/**
	 * The number of local variables in the local variable array allocated
	 * upon invocation of this method, including the local variables used to
	 * pass parameters to the method on invocation.<p>
	 *
	 * The greatest local variable index for a value of type <code>long</code>
	 * or <code>double</code> is <code>maxLocals-2</code>.  The greatest local
	 * variable index for a value of any other type is <code>maxLocals-1</code>.
	 */
	private int maxLocals;

//	/**
//	 * The actual bytes of JVM code that implement the method.  This must have
//	 * length greater than zero.
//	 */
//	private int[] code;

	/**
	 * The size of the method's code, in bytes.
	 */
	private int codeLength;

	/**
	 * The exception handlers in the <code>code</code> array.  The order of
	 * the handlers in this table is significant.
	 */
	private ExceptionTableEntry[] exceptionTable;

	/**
	 * The names of parameters to the parent method, if debugging was enabled
	 * during compilation.
	 * @see #LOCAL_VARIABLE_TABLE
	 */
	private String[] paramNames;

	/**
	 * Attributes of this <code>Code</code> attribute.
	 */
	private AttributeInfo[] attributes;

	private static final String LOCAL_VARIABLE_TABLE	= "LocalVariableTable";


	/**
	 * Constructor.
	 *
	 * @param mi Information on the parent method.
	 */
	public Code(MethodInfo mi) {
		super(mi.getClassFile());
		this.mi = mi;
	}


//	/**
//	 * Returns the code byte at the specified offset.
//	 *
//	 * @param offset The offset.
//	 * @return The byte.
//	 */
//	public int getByte(int offset) {
//		return code[offset];
//	}


	/**
	 * Returns the length of the code array, in bytes.
	 *
	 * @return The length of the code array.
	 */
	public int getCodeLength() {
		return codeLength;//code.length;
	}


	/**
	 * Returns the number of local variables in the local variable array
	 * allocated upon invocation of this method, including the local variables
	 * used to pass parameters to the method on invocation.<p>
	 *
	 * The greatest local variable index for a value of type <code>long</code>
	 * or <code>double</code> is <code>maxLocals-2</code>.  The greatest local
	 * variable index for a value of any other type is <code>maxLocals-1</code>.
	 *
	 * @return the maximum size of the local variable array.
	 */
	public int getMaxLocals() {
		return maxLocals;
	}


	/**
	 * Returns the maximum depth of the operand stack of this method at any
	 * point during its execution.
	 */
	public int getMaxStack() {
		return maxStack;
	}


	/**
	 * Returns the method containing this code.
	 *
	 * @return The method containing this code.
	 */
	public MethodInfo getMethodInfo() {
		return mi;
	}


	/**
	 * If debugging was enabled during compilation, this method returns the
	 * name of the given parameter to this method.  Otherwise, <code>null</code>
	 * is returned.
	 *
	 * @param index The index of the parameter.
	 * @return The name of the parameter, or <code>null</code>.
	 */
	public String getParameterName(int index) {
		return paramNames==null ? null : paramNames[index];
	}


	/**
	 * Reads a <code>Code</code> attribute from an input stream.
	 *
	 * @param mi The parent method.
	 * @param in The input stream.
	 * @return The <code>Code</code> attribute.
	 * @throws IOException If an IO error occurs.
	 */
	public static Code read(MethodInfo mi, DataInputStream in)
							throws IOException {
		Code code = new Code(mi);
		code.maxStack = in.readUnsignedShort();
		code.maxLocals = in.readUnsignedShort();
//		code.code = new int[in.readInt()];
//		for (int i=0; i<code.code.length; i++) {
//			code.code[i] = in.readUnsignedByte();
//		}
code.codeLength = in.readInt();
skipBytes(in, code.codeLength);
		int exceptionTableLength = in.readUnsignedShort();
		if (exceptionTableLength>0) {
			code.exceptionTable = new ExceptionTableEntry[exceptionTableLength];
			for (int i=0; i<exceptionTableLength; i++) {
				ExceptionTableEntry ete = ExceptionTableEntry.read(
												mi.getClassFile(), in);
				code.exceptionTable[i] = ete;
			}
		}
		int attrCount = in.readUnsignedShort();
		if (attrCount>0) {
			code.attributes = new AttributeInfo[attrCount];
			for (int i=0; i<attrCount; i++) {
				AttributeInfo ai = code.readAttribute(in);
				code.attributes[i] = ai;
			}
		}
		return code;
	}


	/**
	 * Reads an attribute for this <code>Code</code> attribute from an input
	 * stream.
	 *
	 * @param in The input stream to read from.
	 * @return The attribute read.
	 * @throws IOException If an IO error occurs.
	 */
	private AttributeInfo readAttribute(DataInputStream in) throws IOException {

		AttributeInfo ai = null;
		ClassFile cf = mi.getClassFile();

		int attributeNameIndex = in.readUnsignedShort();
		int attributeLength = in.readInt();

		String attrName = cf.getUtf8ValueFromConstantPool(attributeNameIndex);

		// Describes a local variable during execution of this code.  We only
		// use it to grab the names of method parameters.
		if (LOCAL_VARIABLE_TABLE.equals(attrName)) {

			// If this attribute is defined, then this class was compiled with
			// debugging enabled!  We can grab the names of the method
			// parameters, to make code completion a little nicer.  Note that
			// we only grab the names of parameters, not all local variables,
			// for speed and space.

			int paramCount = mi.getParameterCount();
			paramNames = new String[paramCount];
			boolean isStatic = mi.isStatic();

			int localVariableTableLength = in.readUnsignedShort();
			for (int i=0; i<localVariableTableLength; i++) {

				/*int startPC = */in.readUnsignedShort();
				/*int length = */in.readUnsignedShort();
				int nameIndex = in.readUnsignedShort();
				/*int descriptorIndex = */in.readUnsignedShort();

				// Non-static methods have implicit "this" variable passed in,
				// so we must avoid that
				int index = in.readUnsignedShort();
				int adjustedIndex = isStatic ? index : index-1;

				if (adjustedIndex>=0 && adjustedIndex<paramNames.length) {
					String name = cf.getUtf8ValueFromConstantPool(nameIndex);
					//System.out.println("!!! " + getClassFile().
					//	getClassName(false) + "." + getMethodInfo().
					//	getNameAndParameters() + " - " + index + ": " + name);
					paramNames[adjustedIndex] = name;
				}

			}

		}

		else {
			ai = AttributeInfo.readUnsupportedAttribute(cf, in, attrName,
														attributeLength);
		}

		return ai;

	}


}