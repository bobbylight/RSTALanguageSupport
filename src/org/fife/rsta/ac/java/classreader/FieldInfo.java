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
import java.util.*;

import org.fife.rsta.ac.java.classreader.attributes.*;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantUtf8Info;


/**
 * Represents a "field_info" structure as defined by the Java VM spec.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class FieldInfo extends MemberInfo {

	/**
	 * Index into the constant pool of a {@link ConstantUtf8Info} structure
	 * representing the field name, as a simple name.
	 */
	private int nameIndex; // u2

	/**
	 * Index into the constant pool of a {@link ConstantUtf8Info} structure
	 * representing a valid field descriptor.
	 */
	private int descriptorIndex; // u2

	/**
	 * An array of attributes of this field.
	 */
	private List attributes;

	public static final String CONSTANT_VALUE		= "ConstantValue";


	/**
	 * Constructor.
	 *
	 * @return The access flags, as a bit field.
	 * @see AccessFlags
	 */
	public FieldInfo(ClassFile cf, int accessFlags, int nameIndex,
					int descriptorIndex) {
		super(cf, accessFlags);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		attributes = new ArrayList(1); // Usually 0 or 1?
	}


	/**
	 * Adds the specified attribute to this field.
	 *
	 * @param info Information about the attribute.
	 */
	public void addAttribute(AttributeInfo info) {
		attributes.add(info);
	}


	/**
	 * Returns the specified attribute.
	 *
	 * @param index The index of the attribute.
	 * @return The attribute.
	 */
	public AttributeInfo getAttribute(int index) {
		return (AttributeInfo)attributes.get(index);
	}


	/**
	 * Returns the number of attributes of this field.
	 *
	 * @return The number of attributes.
	 */
	public int getAttributeCount() {
		return attributes.size();
	}


	public String getConstantValueAsString() {
		ConstantValue cv = getConstantValueAttributeInfo();
		return cv==null ? null : cv.getConstantValueAsString();
	}


	/**
	 * Returns the {@link ConstantValue} attribute info for this field, if any.
	 *
	 * @return The <code>ConstantValue</code> attribute, or <code>null</code>
	 *         if there isn't one.
	 */
	private ConstantValue getConstantValueAttributeInfo() {
		for (int i=0; i<getAttributeCount(); i++) {
			AttributeInfo ai = (AttributeInfo)attributes.get(i);
			if (ai instanceof ConstantValue) {
				return (ConstantValue)ai;
			}
		}
		return null;
	}


	/**
	 * Returns the field descriptor of this field.
	 *
	 * @return The field descriptor of this field.
	 */
	public String getDescriptor() {
		return cf.getUtf8ValueFromConstantPool(descriptorIndex);
	}


	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return cf.getUtf8ValueFromConstantPool(nameIndex);
	}


	/**
	 * Returns the index into the constant pool of a {@link ConstantUtf8Info}
	 * structure representing the field name, as a simple name.
	 *
	 * @return The index into the constant pool.
	 */
	public int getNameIndex() {
		return nameIndex;
	}


	/**
	 * Returns the type of this field, as determined from its field descriptor.
	 *
	 * @return The type of this field.
	 */
	public String getTypeString() {

		StringBuffer sb = new StringBuffer();

		String descriptor = getDescriptor();
		int braceCount = descriptor.lastIndexOf('[') + 1;

		switch (descriptor.charAt(braceCount)) {

			// BaseType
			case 'B':
				sb.append("byte");
				break;
			case 'C':
				sb.append("char");
				break;
			case 'D':
				sb.append("double");
				break;
			case 'F':
				sb.append("float");
				break;
			case 'I':
				sb.append("int");
				break;
			case 'J':
				sb.append("long");
				break;
			case 'S':
				sb.append("short");
				break;
			case 'Z':
				sb.append("boolean");
				break;

			// ObjectType
			case 'L':
				String clazz = descriptor.substring(braceCount+1,
													descriptor.length()-1);
				clazz = clazz.replaceAll("/", ".");
				sb.append(clazz);
				break;

			// Invalid field descriptor
			default:
				sb.append("UNSUPPORTED_TYPE_").append(descriptor);
				break;

		}

		for (int i=0; i<braceCount; i++) {
			sb.append("[]");
		}

		return sb.toString();

	}


	public boolean isConstant() {
		return getConstantValueAttributeInfo()!=null;
	}


	/**
	 * Reads a <code>FieldInfo</code> structure from the specified input
	 * stream.
	 *
	 * @param cf The class file containing this field.
	 * @param in The input stream to read from.
	 * @return The field information read.
	 * @throws IOException If an IO error occurs.
	 */
	public static FieldInfo read(ClassFile cf, DataInputStream in)
									throws IOException {
		FieldInfo info = new FieldInfo(cf, in.readUnsignedShort(),
										in.readUnsignedShort(),
										in.readUnsignedShort());
		int attrCount = in.readUnsignedShort();
		for (int i=0; i<attrCount; i++) {
			AttributeInfo ai = info.readAttribute(in);
			info.addAttribute(ai);
		}
		return info;
	}


	/**
	 * Reads an attribute for this field from an input stream.
	 *
	 * @param in The input stream to read from.
	 * @return The attribute read.
	 * @throws IOException If an IO error occurs.
	 */
	private AttributeInfo readAttribute(DataInputStream in) throws IOException {

		AttributeInfo ai = null;

		int attributeNameIndex = in.readUnsignedShort();
		int attributeLength = in.readInt();

		String attrName = cf.getUtf8ValueFromConstantPool(attributeNameIndex);

		if (CONSTANT_VALUE.equals(attrName)) { // 4.7.2
			int constantValueIndex = in.readUnsignedShort();
			ConstantValue cv = new ConstantValue(cf, constantValueIndex);
			ai = cv;
		}

		// Attributes common to all members, or unhandled attributes.
		else {
			ai = super.readAttribute(in, attrName, attributeLength);
		}

		return ai;

	}


}