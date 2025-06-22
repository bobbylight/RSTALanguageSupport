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
import java.util.*;

import org.fife.rsta.ac.java.classreader.attributes.*;


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
	 * The <code>Signature</code> attribute, or <code>null</code> if there
	 * isn't one for this method.
	 */
	private Signature signatureAttr;

	/**
	 * The <code>Code</code> attribute, or <code>null</code> if this method
	 * is abstract or native.
	 */
	private Code codeAttr;

	/**
	 * All attributes of this method that aren't explicitly covered by the
	 * private members {@link #signatureAttr} and {@link #codeAttr}.
	 */
	private List<AttributeInfo> attributes;

	/**
	 * The type of all parameters to this method.  Note that this cache will
	 * be short-lived, as classes that take type parameters will pass their
	 * type arguments down to individual <code>MethodInfo</code>s when doing
	 * completions, to ensure types are as correct as possible.
	 */
	private String[] paramTypes;

	/**
	 * Cached return type.
	 */
	private String fullyQualifiedReturnType;

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
	 * @param accessFlags The access flags.
	 * @param nameIndex The name index.
	 * @param descriptorIndex The descriptor index.
	 */
	public MethodInfo(ClassFile cf, int accessFlags, int nameIndex,
						int descriptorIndex) {
		super(cf, accessFlags);
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		attributes = new ArrayList<>(1); // Usually only 0 or 1?
	}


	/**
	 * Adds the specified attribute to this field.
	 *
	 * @param info Information about the attribute.
	 */
	private void addAttribute(AttributeInfo info) {
		attributes.add(info);
	}


	private void appendParamDescriptors(StringBuilder sb) {

		String[] paramTypes = getParameterTypes();
		for (int i=0; i<paramTypes.length; i++) {
			sb.append(paramTypes[i]).append(" param").append(i);
			if (i<paramTypes.length-1) {
				sb.append(", ");
			}
		}

	}


	/**
	 * Called internally by {@link ClassFile} whenever its
	 * {@link ClassFile#setTypeParamsToTypeArgs(Map)} method is called.  This
	 * clears this method's local cache of parameter/return types.  They'll be
	 * lazily recomputed the next time they are needed.  This allows this
	 * <code>MethodInfo</code> to be used for code completion for instances
	 * of the same class initialized with different type arguments.<p>
	 *
	 * Note that if this method does not have parameterized arguments or
	 * return type, calling this method won't affect its behavior.
	 */
	void clearParamTypeInfo() {
		paramTypes = null;
		fullyQualifiedReturnType = null;
	}


	/**
	 * Creates and returns an array of types of all parameters of this
	 * method.  If this method takes any generic type arguments, these
	 * types are grabbed from the parent <code>ClassFile</code> instance,
	 * whose type argument values should have been initialized via
	 * {@link ClassFile#setTypeParamsToTypeArgs(Map)}.
	 *
	 * @return The array of parameter types.
	 * @see #createParamTypesFromDescriptor(boolean)
	 * @see #createParamTypesFromTypeSignature()
	 */
	private String[] createParamTypes() {
		String[] types = createParamTypesFromTypeSignature();
		if (types==null) {
			types = createParamTypesFromDescriptor(true);
		}
		return types;
	}


	/**
	 * Creates an array of types of each parameter by looking at the method's
	 * descriptor field.  This technique should work with Java 1.0+, but won't
	 * pick up on generic types added in Java 5.
	 *
	 * @return The parameter types.
	 * @see #createParamTypesFromTypeSignature()
	 */
	private String[] createParamTypesFromDescriptor(boolean qualified) {

		String descriptor = getDescriptor();
		int rparen = descriptor.indexOf(')');
		String paramDescriptors = descriptor.substring(1, rparen);
		//String returnDescriptor = descriptor.substring(rparen+1);

		List<String> paramTypeList = new ArrayList<>();
		String type;

		while (!paramDescriptors.isEmpty()) {

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
					type = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/')+1);
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
		types = paramTypeList.toArray(types);
		return types;

	}


	/**
	 * Creates an array of types of each parameter by looking at the method's
	 * <code>Signature</code> attribute, and querying the parent
	 * <code>ClassFile</code> instance for any type argument values.  This
	 * attribute was introduced in Java 5, and is the only way to detect
	 * generic parameters.
	 *
	 * @return The parameter types.
	 * @see #createParamTypesFromDescriptor(boolean)
	 */
	private String[] createParamTypesFromTypeSignature() {

		String[] params = null;

		if (signatureAttr!=null) {
			List<String> paramTypes = signatureAttr.
					getMethodParamTypes(this, cf, false);
			if (paramTypes!=null) {
				params = new String[paramTypes.size()];
				params = paramTypes.toArray(params);
			}
		}

		return params;

	}


	/**
	 * Returns the specified attribute.
	 *
	 * @param index The index of the attribute.
	 * @return The attribute.
	 */
	public AttributeInfo getAttribute(int index) {
		return attributes.get(index);
	}


	/**
	 * Returns the number of attributes of this field.
	 *
	 * @return The number of attributes.
	 */
	public int getAttributeCount() {
		return attributes.size();
	}


	@Override
	public String getDescriptor() {
		return cf.getUtf8ValueFromConstantPool(descriptorIndex);
	}


	@Override
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

			StringBuilder sb = new StringBuilder(getName());

			sb.append('(');
			int paramCount = getParameterCount();
			for (int i=0; i<paramCount; i++) {
				sb.append(getParameterType(i, false));
				if (i<paramCount-1) {
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
	 * @see #getParameterType(int, boolean)
	 */
	public int getParameterCount() {
		if (paramTypes==null) {
			paramTypes = createParamTypes();
		}
		return paramTypes.length;
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
		if (index>=0 && index<getParameterCount()) {
			if (codeAttr!=null) {
				return codeAttr.getParameterName(index);
			}
		}
		return null;
	}


	/**
	 * Returns a string representing the type of parameter to this method.
	 *
	 * @param index The index of the parameter.
	 * @param fullyQualified Whether the returned type should be fully
	 *        qualified.  Note that if fully qualified information is not
	 *        available for the parameters to this method, this parameter will
	 *        be ignored (but I'm not sure that ever happens).
	 * @return The type of the parameter.
	 * @see #getParameterCount()
	 * @see #getParameterTypes()
	 */
	public String getParameterType(int index, boolean fullyQualified) {
		if (paramTypes==null) {
			paramTypes = createParamTypes();
		}
		String type = paramTypes[index];
		if (!fullyQualified) {
			int dot = type.lastIndexOf('.');
			if (dot>-1) {
				type = type.substring(dot+1);
			}
		}
		return type;
	}


	/**
	 * Returns an array if strings representing the types of all parameters
	 * to this method.  If this method takes no parameters, a zero-length
	 * array is returned.
	 *
	 * @return The array.  These types will likely be fully qualified.
	 * @see #getParameterCount()
	 * @see #getParameterType(int, boolean)
	 */
	public String[] getParameterTypes() {
		if (paramTypes==null) {
			paramTypes = createParamTypes();
		}
		return paramTypes.clone();
	}

	/**
	 * Returns the return type of this method.
	 *
	 * @param fullyQualified Whether the returned value should be fully-qualified.
	 * @return The return type of this method.
	 */
	public String getReturnTypeString(boolean fullyQualified) {
		if (fullyQualifiedReturnType == null) {
			fullyQualifiedReturnType = getReturnTypeStringFromTypeSignature(true);
			if (fullyQualifiedReturnType == null) {
				fullyQualifiedReturnType = getReturnTypeStringFromDescriptor(true);
			}
		}
		if (!fullyQualified) {
			if (fullyQualifiedReturnType != null && fullyQualifiedReturnType.contains(".")) {
				return fullyQualifiedReturnType.substring(fullyQualifiedReturnType.lastIndexOf(".") +1);
			}
		}
		return fullyQualifiedReturnType;
	}


	/**
	 * Returns the return type of this method, as determined by a snippet
	 * of the method descriptor.  This should work with all class files
	 * created in Java 1.0+, but won't discover the generic types added in
	 * Java 5.
	 *
	 * @return The return type of this method.
	 * @see #getReturnTypeStringFromTypeSignature(boolean)
	 */
	/*
	 * TODO: This is identical to FieldInfo.getTypeString(), except for the
	 * 'V' case.  It is also very similar to #getParameterTypes().  Try
	 * to refactor common code from these methods.
	 */
	private String getReturnTypeStringFromDescriptor(boolean qualified) {

		String descriptor = getDescriptor();
		int rparen = descriptor.indexOf(')');
		descriptor = descriptor.substring(rparen+1); // return type desc.
		StringBuilder sb = new StringBuilder();

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
				String clazz = descriptor.substring(braceCount+1, descriptor.length()-1);
				clazz = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/')+1);
				sb.append(clazz);
				break;

			// Invalid field descriptor
			default:
				sb.append("UNSUPPORTED_TYPE_").append(descriptor);
				break;

		}

		sb.append("[]".repeat(braceCount));
		return sb.toString();

	}


	/**
	 * Returns the return type of this method, as determined by the
	 * <code>Signature</code> attribute that was added in Java 5.  This allows
	 * us to check for generic types.
	 *
	 * @return The return type of this method.
	 * @see #getReturnTypeStringFromDescriptor(boolean)
	 */
	private String getReturnTypeStringFromTypeSignature(boolean qualified) {
		String retType = null;
		if (signatureAttr!=null) {
			retType = signatureAttr.getMethodReturnType(this, cf, qualified);
		}

		return retType;

	}


	/**
	 * Returns the signature of this method, as determined from its method
	 * descriptor.
	 *
	 * @return The signature of this method.
	 */
	public String getSignature() {

		StringBuilder sb = new StringBuilder();

		// Return type.
		if (!isConstructor()) { // Don't print "void" return type.
			sb.append(getReturnTypeString(false));
			sb.append(' ');
		}

		// Method name and param list.
		sb.append(getName());
		sb.append('(');
		appendParamDescriptors(sb);
		sb.append(')');

		// "throws" clause.
		for (AttributeInfo ai : attributes) {
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
	@Override
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
			if (ai instanceof Signature) {
				mi.signatureAttr = (Signature)ai;
			}
			else if (ai instanceof Code) {
				mi.codeAttr = (Code)ai;
			}
			else if (ai!=null) {
				mi.addAttribute(ai);
			}
		}
		return mi;
	}


	/**
	 * Reads an attribute for this method from the specified input stream.
	 *
	 * @param in The input stream to read from.
	 * @return The attribute read, possibly <code>null</code> if it was known
	 *         to be unimportant for our purposes.
	 * @throws IOException If an IO error occurs.
	 */
	private AttributeInfo readAttribute(DataInputStream in) throws IOException {

		AttributeInfo ai;

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
            ai = new Exceptions(this, exceptionIndexTable);
		}

		// TODO: Handle other Attribute types.

		// Attributes common to all members, or unhandled attributes.
		else {
			ai = super.readAttribute(in, attrName, attributeLength);
			//if (ai!=null) { // "Deprecated" attribute returns null
			//	LOG.log(System.Logger.Level.WARNING,
			//    "-------------- " + ai.getName());
			//}
		}

		return ai;

	}


}
