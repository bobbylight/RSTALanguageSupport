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
 * The "<code>ConstantValue</code>" attribute, as defined by 4.7.2 of the
 * JVM specification.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantValue extends AttributeInfo {

	/**
	 * An index into the constant pool that gives the constant value
	 * represented by this attribute.
	 */
	private int constantValueIndex; // u2


	/**
	 * CConstructor.
	 *
	 * @param cf The class file.
	 * @param constantValueIndex The index into the constant pool that gives
	 *        the constant value represented by this attribute.
	 */
	public ConstantValue(ClassFile cf, int constantValueIndex) {
		super(cf);
		this.constantValueIndex = constantValueIndex;
	}


	/**
	 * Returns the index into the constant pool that gives the constant value
	 * represented by this attribute.
	 *
	 * @return The index.
	 */
	public int getConstantValueIndex() {
		return constantValueIndex;
	}


	/**
	 * Returns the constant's value, as a string.
	 *
	 * @return The constant's value, as a string.
	 */
	public String getConstantValueAsString() {

		ClassFile cf = getClassFile();
		ConstantPoolInfo cpi = cf.getConstantPoolInfo(getConstantValueIndex());

		if (cpi instanceof ConstantDoubleInfo) {
			ConstantDoubleInfo cdi = (ConstantDoubleInfo)cpi;
			double value = cdi.getDoubleValue();
			return Double.toString(value);
		}
		else if (cpi instanceof ConstantFloatInfo) {
			ConstantFloatInfo cfi = (ConstantFloatInfo)cpi;
			float value = cfi.getFloatValue();
			return Float.toString(value);
		}
		else if (cpi instanceof ConstantIntegerInfo) {
			ConstantIntegerInfo cii = (ConstantIntegerInfo)cpi;
			int value = cii.getIntValue();
			return Integer.toString(value);
		}
		else if (cpi instanceof ConstantLongInfo) {
			ConstantLongInfo cli = (ConstantLongInfo)cpi;
			long value = cli.getLongValue();
			return Long.toString(value);
		}
		else if (cpi instanceof ConstantStringInfo) {
			ConstantStringInfo csi = (ConstantStringInfo)cpi;
			return csi.getStringValue();
		}
		else {
			return "INVALID_CONSTANT_TYPE_" + cpi.toString();
		}

	}


}