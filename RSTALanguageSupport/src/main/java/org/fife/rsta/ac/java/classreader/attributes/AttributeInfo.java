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

import java.io.*;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.Util;


/**
 * Represents an "attribute_info" structure in a class file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AttributeInfo {

	private ClassFile cf;
	private int attributeNameIndex; // u2


	protected AttributeInfo(ClassFile cf) {
		this.cf = cf;
	}


	public ClassFile getClassFile() {
		return cf;
	}


	/**
	 * Returns the name of this attribute.
	 *
	 * @return The name of this attribute.
	 */
	public String getName() {
		return cf.getUtf8ValueFromConstantPool(attributeNameIndex);
	}


	/**
	 * Reads an unknown/unsupported attribute from an input stream.
	 *
	 * @param cf The class file containing the attribute.
	 * @param in The input stream to read from.
	 * @param attrName The name of the attribute.
	 * @param attrLength The length of the data to read from <code>in</code>,
	 *        in bytes.
	 * @return The attribute.
	 * @throws IOException If an IO error occurs.
	 */
	public static UnsupportedAttribute readUnsupportedAttribute(ClassFile cf,
										DataInputStream in, String attrName,
										int attrLength) throws IOException {
		/*
		int[] info = new int[attrLength];
		for (int i=0; i<attrLength; i++) {
			info[i] = in.readUnsignedByte();
		}
		*/
		Util.skipBytes(in, attrLength);
		return new UnsupportedAttribute(cf, attrName);
	}


}
