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
 * Implementation of the "<code>method_info</code>" structure as defined in
 * the JVM specification.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class MethodInfo extends MemberInfo implements AccessFlags {

	/**
	 * An index into the constant pool of a {@link ConstantUtf8Info} structure
	 * representing either one of the special method names (
	 * "<code>&lt;init&gt;</code>" or "<code>&lt;clinit&gt;</code>") or a
	 * valid method name in the Java programming language, stored as a simple
	 * name.
	 */
	private int nameIndex; // u2

	/**
	 * An index into the constant pool of a {@link ConstantUtf8Info} structure
	 * representing a valid method descriptor.
	 */
	private int descriptorIndex; // u2

	/**
	 * Attributes of this method.
	 */
	private List attributes;

	/**
	 * The type of all parameters to this method.  If this method takes no
	 * parameters, this will be a zero-length array.
	 */
	private String[] paramTypes;

	/**
	 * Cached return type.
	 */
	private String returnType;

	/**
	 * Cached string representing the name and parameters for this method.
	 */
	private String nameAndParameters;

	/**
	 * Used in class files to denote constructors.
	 */
	private static final String SPECIAL_NAME_CONSTRUCTOR	= "<init>";

	public static final String CODE					= "Code";
	public static final String EXCEPTIONS			= "Exceptions";


	/**
	 * Constructor.
	 *
	 * @param cf The class file defining this method.
	 */
	public MethodInfo(ClassFile cf, int accessFlags, int nameIndex,
						int descriptorIndex) {
		super(cf, accessFlags);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		attributes = new ArrayList(1); // Usually only 0 or 1?
	}


	/**
	 * Adds the specified attribute to this field.
	 *
	 * @param info Information about the attribute.
	 */
	public void addAttribute(AttributeInfo info) {
		attributes.add(info);
	}


	private void appendParamDescriptors(StringBuffer sb) {

		String[] paramTypes = getParameterTypes();
		for (int i=0; i<paramTypes.length; i++) {
			sb.append(paramTypes[i]).append(" param").append(i);
			if (i<paramTypes.length-1) {
				sb.append(", ");
			}
		}
		
	}


	private String[] createParamTypes() {

		String descriptor = getDescriptor();
		int rparen = descriptor.indexOf(')');
		String paramDescriptors = descriptor.substring(1, rparen);
		//String returnDescriptor = descriptor.substring(rparen+1);

		List paramTypeList = new ArrayList();
		String type = null;

		while (paramDescriptors.length()>0) {

			// Can't do lastIndexOf() as there may be > 1 array parameter
			// in the descriptors.
			// int braceCount = paramDescriptors.lastIndexOf('[') + 1;
			int braceCount = -1;
			while (paramDescriptors.charAt(++braceCount) == '[');
			int pos = braceCount;

			switch (paramDescriptors.charAt(pos)) {

				// BaseType
				case 'B':
					type = "byte";
					pos++;
					break;
				case 'C':
					type = "char";
					pos++;
					break;
				case 'D':
					type = "double";
					pos++;
					break;
				case 'F':
					type = "float";
					pos++;
					break;
				case 'I':
					type = "int";
					pos++;
					break;
				case 'J':
					type = "long";
					pos++;
					break;
				case 'S':
					type = "short";
					pos++;
					break;
				case 'Z':
					type = "boolean";
					pos++;
					break;

				// ObjectType
				case 'L':
					String clazz = paramDescriptors.substring(pos + 1,
							paramDescriptors.indexOf(';'));
					clazz = clazz.replaceAll("/", ".");
					type = clazz;
					pos += clazz.length() + 2; // "+2" for the 'L' & semicolon
					break;

				// Invalid method descriptor
				default:
					String temp = "INVALID_TYPE_" + paramDescriptors;
					type = temp;
					pos += paramDescriptors.length();
					break;

			}

			for (int i = 0; i < braceCount; i++) {
				type += "[]";
			}
			paramTypeList.add(type);

			paramDescriptors = paramDescriptors.substring(pos);

		}

		String[] types = new String[paramTypeList.size()];
		types = (String[])paramTypeList.toArray(types);
		return types;

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


	/**
	 * Returns the {@link Code} attribute for this method.
	 *
	 * @return The <code>Code</code> attribute.  This may be <code>null</code>
	 *         if this method is abstract or native.
	 */
	public Code getCodeAttribute() {
		for (int i=0; i<getAttributeCount(); i++) {
			AttributeInfo ai = getAttribute(i);
			if (ai instanceof Code) {
				return (Code)ai;
			}
		}
		return null;
	}


	/**
	 * Returns the field descriptor of this method.
	 *
	 * @return The field descriptor of this method.
	 */
	public String getDescriptor() {
		return cf.getUtf8ValueFromConstantPool(descriptorIndex);
	}


	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		String name = cf.getUtf8ValueFromConstantPool(nameIndex);
		if (SPECIAL_NAME_CONSTRUCTOR.equals(name)) {
			name = cf.getClassName(false);
		}
		return name;
	}


	/**
	 * Returns the name and parameters of this method, in the form
	 * <code>performAction(String, int, Runnable)</code>.
	 *
	 * @return The name and parameters of this method.
	 */
	public String getNameAndParameters() {

		if (nameAndParameters==null) {

			StringBuffer sb = new StringBuffer(getName());

			sb.append('(');
			String[] params = getParameterTypes();
			for (int i=0; i<params.length; i++) {
				sb.append(params[i]);
				if (i<params.length-1) {
					sb.append(", ");
				}
			}
			sb.append(')');

			nameAndParameters = sb.toString();

		}

		return nameAndParameters;

	}


	/**
	 * Returns the number of parameters this method takes.
	 *
	 * @return The number of parameters this method takes.
	 * @see #getParameterTypes()
	 */
	public int getParameterCount() {
		if (paramTypes==null) {
			paramTypes = createParamTypes();
		}
		return paramTypes.length;
	}


	/**
	 * Returns an array if strings representing the types of all parameters
	 * to this method.  If this method takes no parameters, a zero-length
	 * array is returned.
	 *
	 * @return The array.
	 * @see #getParameterCount()
	 */
	public String[] getParameterTypes() {
		if (paramTypes==null) {
			paramTypes = createParamTypes();
		}
		return (String[])paramTypes.clone();
	}


	/**
	 * Returns the return type of this method, as determined by a snippet
	 * of the method descriptor.
	 *
	 * @return The return type of this method.
	 */
	/*
	 * TODO: This is identical to FieldInfo.getTypeString(), except for the
	 * 'V' case.  It is also very similar to #getParameterTypes().  Try
	 * to refactor common code from these methods.
	 */
	public String getReturnTypeString() {

		if (returnType==null) {

			String descriptor = getDescriptor();
			int rparen = descriptor.indexOf(')');
			descriptor = descriptor.substring(rparen+1); // return type desc.
			StringBuffer sb = new StringBuffer();

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
				case 'V':
					sb.append("void");
					break;

				// ObjectType
				case 'L':
					String clazz = descriptor.substring(1, descriptor.length()-1);
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

			returnType = sb.toString();

		}

		return returnType;

	}


	/**
	 * Returns the signature of this method, as determined from its method
	 * descriptor.
	 *
	 * @return The signature of this method.
	 */
	public String getSignature() {

		StringBuffer sb = new StringBuffer();

		// Return type.
		if (!isConstructor()) { // Don't print "void" return type.
			sb.append(getReturnTypeString());
			sb.append(' ');
		}

		// Method name and param list.
		sb.append(getName());
		sb.append('(');
		appendParamDescriptors(sb);
		sb.append(')');

		// "throws" clause.
		for (int i=0; i<getAttributeCount(); i++) {
			AttributeInfo ai = (AttributeInfo)attributes.get(i);
			if (ai instanceof Exceptions) { // At most 1 Exceptions attribute
				sb.append(" throws ");
				Exceptions ex = (Exceptions)ai;
				for (int j=0; j<ex.getExceptionCount(); j++) {
					sb.append(ex.getException(j));
					if (j<ex.getExceptionCount()-1) {
						sb.append(", ");
					}
				}
			}
		}

		return sb.toString();

	}


	/**
	 * Returns whether this method is abstract.
	 *
	 * @return Whether this method is abstract.
	 */
	public boolean isAbstract() {
		return (getAccessFlags()&ACC_ABSTRACT)>0;
	}


	/**
	 * Returns whether this method is a constructor.
	 *
	 * @return Whether this method is a constructor.
	 */
	public boolean isConstructor() {
		String name = cf.getUtf8ValueFromConstantPool(nameIndex);
		return SPECIAL_NAME_CONSTRUCTOR.equals(name);
	}


	/**
	 * Returns whether this method is native.
	 *
	 * @return Whether this method is native.
	 */
	public boolean isNative() {
		return (getAccessFlags()&ACC_NATIVE)>0;
	}


	/**
	 * Returns whether this method is static.
	 *
	 * @return Whether this method is static.
	 */
	public boolean isStatic() {
		return (getAccessFlags()&ACC_STATIC)>0;
	}


	/**
	 * Reads a <code>MethodInfo</code> from an input stream.
	 *
	 * @param cf The class file defining the method.
	 * @param in The input stream to read from.
	 * @return The method information read.
	 * @throws IOException If an IO error occurs.
	 */
	public static MethodInfo read(ClassFile cf, DataInputStream in)
									throws IOException {
		int accessFlags = in.readUnsignedShort();
		int nameIndex = in.readUnsignedShort();
		int descriptorIndex = in.readUnsignedShort();
		MethodInfo mi = new MethodInfo(cf, accessFlags, nameIndex,
										descriptorIndex);
		int attrCount = in.readUnsignedShort();
		for (int j=0; j<attrCount; j++) {
			AttributeInfo ai = mi.readAttribute(in);
			mi.addAttribute(ai);
		}
		return mi;
	}


	/**
	 * Reads an attribute for this method from the specified input stream.
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

		if (CODE.equals(attrName)) { // 4.7.3
			ai = Code.read(this, in);
		}

		else if (EXCEPTIONS.equals(attrName)) { // 4.7.4
			int exceptionCount = in.readUnsignedShort();
			int[] exceptionIndexTable = null;
			if (exceptionCount>0) {
				exceptionIndexTable = new int[exceptionCount];
				for (int i=0; i<exceptionCount; i++) {
					exceptionIndexTable[i] = in.readUnsignedShort();
				}
			}
			Exceptions e = new Exceptions(this, exceptionIndexTable);
			ai = e;
		}

//		else if ("Signature".equals(attrName)) {
//			//System.err.println(">>> " + attributeLength);
//			int u4 = in.readUnsignedShort();
//			String sig = cf.getUtf8ValueFromConstantPool(u4);
//			System.err.println(">>> >>> " + sig);
//		}

		// TODO: Handle other Attribute types.

		// Attributes common to all members, or unhandled attributes.
		else {
			ai = super.readAttribute(in, attrName, attributeLength);
		}

		return ai;

	}


}