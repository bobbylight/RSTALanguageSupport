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
package org.fife.rsta.ac.java.classreader;

import java.io.*;

import org.fife.rsta.ac.java.classreader.attributes.Code;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantClassInfo;


/**
 * An entry in the exception table of a {@link Code} attribute.
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