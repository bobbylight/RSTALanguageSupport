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
package org.fife.rsta.ac.java.classreader;

import java.io.*;


/**
 * An entry in the exception table of a {@link Code} attribute.  This denotes
 * either a <tt>catch</tt> or <tt>finally</tt> block (the section of code it
 * covers, the type of <tt>Throwable</tt> it handles, and the location of the
 * exception handler code).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ExceptionTableEntry {

	/**
	 * The parent class file.
	 */
	private ClassFile cf;

	/**
	 * Start of the range in the code array at which the exception handler is
	 * active.
	 */
	private int startPC; // u2

	/**
	 * End of the range in the code array at which the exception handler is
	 * active.  This value must be either a valid index into the code array of
	 * the opcode of an instruction, or be equal to
	 * {@link Code#getCodeLength()}.
	 */
	private int endPC; // u2

	/**
	 * The start of the exception handler.  This value must be a valid index
	 * into the code array and must be the index of the opcode of an
	 * instruction.
	 */
	private int handlerPC; // u2

	/**
	 * If the value of {@link #catchType} is nonzero, it must be a valid
	 * index into the constant pool.  The constant pool entry at that index
	 * must be a {@link ConstantClassInfo} structure representing a class of
	 * exceptions that this exception handler is designated to catch.  This
	 * class must be the class <code>Throwable</code> or one of its subclasses.
	 * The exception handler will be called only if the thrown exception is
	 * an instance of the given class or one of its subclasses.<p>
	 *
	 * If the value of {@link #catchType} is zero, this exception handler is
	 * for all exceptions.  This is used to implement <code>finally</code>.
	 */
	private int catchType; // u2


	/**
	 * Constructor.
	 *
	 * @param cf The parent class file.
	 */
	public ExceptionTableEntry(ClassFile cf) {
		this.cf = cf;
	}


	/**
	 * Returns the name of the <tt>Throwable</tt> type caught and handled
	 * by this exception handler.
	 *
	 * @param fullyQualified Whether the name should be fully qualified.
	 * @return The name of the <tt>Throwable</tt> type, or <code>null</code>
	 *         if this entry denotes a <tt>finally</tt> block.
	 */
	public String getCaughtThrowableType(boolean fullyQualified) {
		return catchType==0 ? null :
				cf.getClassNameFromConstantPool(catchType, fullyQualified);
	}


	public int getEndPC() {
		return endPC;
	}


	public int getHandlerPC() {
		return handlerPC;
	}


	public int getStartPC() {
		return startPC;
	}


	/**
	 * Reads an exception table entry from an input stream.
	 *
	 * @param cf The class file.
	 * @param in The input stream to read from.
	 * @return The exception table entry.
	 * @throws IOException If an IO error occurs.
	 */
	public static ExceptionTableEntry read(ClassFile cf, DataInputStream in)
											throws IOException {
		ExceptionTableEntry entry = new ExceptionTableEntry(cf);
		entry.startPC = in.readUnsignedShort();
		entry.endPC = in.readUnsignedShort();
		entry.handlerPC = in.readUnsignedShort();
		entry.catchType = in.readUnsignedShort();
		return entry;
	}


}