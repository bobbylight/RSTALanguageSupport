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
package org.fife.rsta.ac.java.classreader.attributes;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantUtf8Info;


/**
 * The <code>SourceFile</code> attribute, an optional fixed-length attribute
 * in the attributes table of a {@link ClassFile}.  There can be no more than
 * one <code>SourceFile</code> attribute for a given <code>ClassFile</code>.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SourceFile extends AttributeInfo {

	/**
	 * Index into the constant pool of a {@link ConstantUtf8Info} structure
	 * representing the name of the source file from which this class file
	 * was compiled.
	 */
	private int sourceFileIndex;


	/**
	 * Constructor.
	 *
	 * @param cf The class file.
	 * @param sourceFileIndex Index into the constant pool of a
	 *        {@link ConstantUtf8Info} structure representing the source file
	 *        name.
	 */
	public SourceFile(ClassFile cf, int sourceFileIndex) {
		super(cf);
		this.sourceFileIndex = sourceFileIndex;
	}


	/**
	 * Returns the name of the source file that was compiled to create this
	 * class file.
	 *
	 * @return The name of the source file.
	 */
	public String getSourceFileName() {
		return getClassFile().getUtf8ValueFromConstantPool(sourceFileIndex);
	}


	/**
	 * Returns a string representation of this attribute.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this attribute.
	 */
	@Override
	public String toString() {
		return "[SourceFile: " +
				"file=" + getSourceFileName() +
				"]";
	}


}
