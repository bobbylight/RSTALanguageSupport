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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.java.classreader.attributes.*;
import org.fife.rsta.ac.java.classreader.constantpool.*;


/**
 * Class representing a <code>ClassFile</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ClassFile implements AccessFlags {

	private static final boolean DEBUG = false;

	/**
	 * The class file's minor version number.
	 */
	private int minorVersion; // u2

	/**
	 * The class file's major version number.
	 */
	private int majorVersion; // u2

	/**
	 * Constant pool infos.
	 */
	private ConstantPoolInfo[] constantPool; // Length constant_pool_count-1 !!

	/**
	 * Permissions and properties of this class or interface.
	 */
	private int accessFlags; // u2

	/**
	 * Index into {@link #constantPool} for a <code>ConstantClassInfo</code>
	 * structure representing the class or interface defined in this class file.
	 */
	private int thisClass; // u2

	/**
	 * Index into {@link #constantPool} for a <code>ConstantClassInfo</code>
	 * structure representing the superclass of this class or interface.  If
	 * this value is <code>0</code> then this class must be class
	 * <code>java.lang.Object</code>.  If this is an interface, then this index
	 * must point to information about class <code>java.lang.Object</code>.
	 */
	private int superClass; // u2

	/**
	 * Indices into {@link #constantPool} for <code>ConstantClassInfo</code>s
	 * representing the implemented interfaces of this class or interface.
	 */
	int[] interfaces; // u2[]

	/**
	 * Structures giving complete descriptions of the fields in this class
	 * or interface.
	 */
	private FieldInfo[] fields;

	/**
	 * Structures giving complete descriptions of the methods in this class or
	 * interface.
	 */
	private MethodInfo[] methods;

	/**
	 * Whether this class is deprecated.
	 */
	private boolean deprecated;

	/**
	 * Attributes of this class or interface.
	 */
	private AttributeInfo[] attributes;

	/**
	 * Parameter types, such as "String" in <code>List&lt;String&gt;</code>.
	 */
	private List<String> paramTypes;

	/**
	 * A mapping of type parameters to type arguments.  This is set via
	 * {@link #setTypeParamsToTypeArgs(Map)} during code completion of members of an
	 * instance variable whose type is represented by this class file.  This
	 * <code>ClassFile</code> doesn't use this field itself; rather, it's there
	 * for consumers (such as the Java code completion API) to use. 
	 */
	private Map<String, String> typeMap;

	public static final String DEPRECATED			= "Deprecated";
	public static final String ENCLOSING_METHOD		= "EnclosingMethod";
	public static final String INNER_CLASSES		= "InnerClasses";
	public static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
	public static final String SIGNATURE			= "Signature";
	public static final String SOURCE_FILE			= "SourceFile";

	/**
	 * The 4-byte class file header, "<code>CAFEBABE</code>".
	 */
	private static final byte[] HEADER	= { (byte)0xCA, (byte)0xFE,
											(byte)0xBA, (byte)0xBE };


	public ClassFile(File classFile) throws IOException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(classFile)));
		try {
			init(in);
		} finally {
			in.close();
		}
	}


	public ClassFile(DataInputStream in) throws IOException {
		init(in);
	}


	private void debugPrint(String text) {
		if (DEBUG) {
			System.out.println(text);
		}
	}


	/**
	 * Returns the access flags for this class or interface.
	 *
	 * @return The access flags, as a bit field.
	 * @see AccessFlags
	 */
	public int getAccessFlags() {
		return accessFlags;
	}


	/**
	 * Returns the specified attribute of this class file.
	 *
	 * @param index The index of the attribute.
	 * @return The attribute.
	 * @see #getAttributeCount()
	 */
	public AttributeInfo getAttribute(int index) {
		return attributes[index];
	}


	/**
	 * Returns the number of attributes of this class file.
	 *
	 * @return The number of attributes.
	 * @see #getAttribute(int)
	 */
	public int getAttributeCount() {
		return attributes==null ? 0 : attributes.length;
	}


	/**
	 * Returns the name of this class or interface.
	 *
	 * @param fullyQualified Whether the name should be fully-qualified.
	 * @return The name of this class or interface.
	 * @see #getSuperClassName(boolean)
	 */
	public String getClassName(boolean fullyQualified) {
		return getClassNameFromConstantPool(thisClass, fullyQualified);
	}


	/**
	 * Given an index into the constant pool of a {@link ConstantClassInfo},
	 * this method returns the fully-qualified name of the class it points to.
	 *
	 * @param cpIndex The index into the constant pool.  Note that
	 *        this value is <code>1</code>-based.
	 * @param fullyQualified Whether the returned class name should be fully
	 *        qualified.
	 * @return The fully-qualified class or interface name.
	 */
	protected String getClassNameFromConstantPool(int cpIndex,
												boolean fullyQualified) {

		ConstantPoolInfo cpi = getConstantPoolInfo(cpIndex);

		if (cpi instanceof ConstantClassInfo) {
			ConstantClassInfo cci = (ConstantClassInfo)cpi;
			int index = cci.getNameIndex();
			ConstantUtf8Info cui = (ConstantUtf8Info)getConstantPoolInfo(index);
			String className = cui.getRepresentedString(false);
			if (fullyQualified) {
				className = className.replace('/', '.');
			}
			else {
				className = className.substring(className.lastIndexOf('/')+1);
			}
			return className.replace('$', '.');
		}

		// cpi is never null
		throw new InternalError("Expected ConstantClassInfo, found " +
								cpi.getClass().toString());

	}


	/**
	 * Returns the size of the constant pool, plus <code>1</code>.
	 *
	 * @return The size of the constant pool, plus <code>1</code>.
	 * @see #getConstantPoolInfo(int)
	 */
	public int getConstantPoolCount() {
		return constantPool.length + 1;
	}


	/**
	 * Returns the constant pool entry at the specified index.  Note that
	 * constant pool entries are <code>1</code>-based (that is, valid indices
	 * are <code>1 - getConstantPoolCount()-1</code>).
	 *
	 * @param index The index into the constant pool to retrieve.
	 * @return The constant pool entry, or <code>null</code> if
	 *         <code>index</code> is <code>0</code> (e.g. this
	 *         <code>ClassFile</code> object represents
	 *         <code>java.lang.Object</code>).
	 * @see #getConstantPoolCount()
	 */
	public ConstantPoolInfo getConstantPoolInfo(int index) {
		return index!=0 ? constantPool[index-1] : null;
	}


	/**
	 * Returns the number of fields declared in this class file.
	 *
	 * @return The number of fields.
	 * @see #getFieldInfo(int)
	 */
	public int getFieldCount() {
		return fields==null ? 0 : fields.length;
	}


	/**
	 * Returns the specified field's information.
	 *
	 * @param index The index of the field info.
	 * @return The field's information.
	 * @see #getFieldCount()
	 * @see #getFieldInfoByName(String)
	 */
	public FieldInfo getFieldInfo(int index) {
		return fields[index];
	}


	/**
	 * Returns a field's information by name.
	 *
	 * @param name The name of the field.
	 * @return The field's information.
	 * @see #getFieldCount()
	 * @see #getFieldInfo(int)
	 */
	public FieldInfo getFieldInfoByName(String name) {
		for (int i=0; i<getFieldCount(); i++) {
			if (name.equals(fields[i].getName())) {
				return fields[i];
			}
		}
		return null;
	}


	/**
	 * Returns the number of interfaces this class or interface implements.
	 *
	 * @return The number of implemented interfaces.
	 * @see #getImplementedInterfaceName(int, boolean)
	 */
	public int getImplementedInterfaceCount() {
		return interfaces==null ? 0 : interfaces.length;
	}


	/**
	 * Returns the specified interface implemented by this class or
	 * interface.
	 *
	 * @param index The index of the interface.
	 * @param fullyQualified Whether the returned interface name should be
	 *        fully qualified.
	 * @return The interface name.
	 * @see #getImplementedInterfaceCount()
	 */
	public String getImplementedInterfaceName(int index,
											boolean fullyQualified) {
		return getClassNameFromConstantPool(interfaces[index], fullyQualified);
	}


	/**
	 * Returns the number of methods defined/declared in this class or
	 * interface.
	 *
	 * @return The number of methods.
	 * @see #getMethodInfo(int)
	 */
	public int getMethodCount() {
		return methods==null ? 0 : methods.length;
	}


	/**
	 * Returns information about the specified method defined/declared in
	 * this class file.
	 *
	 * @param index The index of the method.
	 * @return Information about the method.
	 * @see #getMethodCount()
	 */
	public MethodInfo getMethodInfo(int index) {
		return methods[index];
	}


	/**
	 * Returns all method overloads with the specified name.
	 *
	 * @param name The method name.
	 * @return Any method overloads with the given name, or <code>null</code>
	 *         if none.  This is a list of {@link MethodInfo}s.
	 * @see #getMethodInfoByName(String, int)
	 */
	public List<MethodInfo> getMethodInfoByName(String name) {
		return getMethodInfoByName(name, -1);
	}


	/**
	 * Returns all method overloads with the specified name and number of
	 * arguments.
	 *
	 * @param name The method name.
	 * @param argCount The number of arguments.  If this is less than zero,
	 *        all overloads will be returned, regardless of argument count.
	 * @return Any method overloads with the given name and argument count, or
	 *         <code>null</code> if none.  This is a list of
	 *         {@link MethodInfo}s.
	 * @see #getMethodInfoByName(String)
	 */
	public List<MethodInfo> getMethodInfoByName(String name, int argCount) {
		List<MethodInfo> methods = null;
		for (int i=0; i<getMethodCount(); i++) {
			MethodInfo info = this.methods[i];
			if (name.equals(info.getName())) {
				if (argCount<0 || argCount==info.getParameterCount()) {
					if (methods==null) {
						methods = new ArrayList<MethodInfo>(1); // Usually just 1
					}
					methods.add(info);
				}
			}
		}
		return methods;
	}


	/**
	 * Returns the package for this class or interface.
	 *
	 * @return The package, or <code>null</code> if this class or interface
	 *         is not in a package.
	 * @see #getClassName(boolean)
	 */
	public String getPackageName() {
		String className = getClassName(true);
		int dot = className.lastIndexOf('.');
		return dot==-1 ? null : className.substring(0, dot);
	}


	public List<String> getParamTypes() {
		return paramTypes;
	}


	/**
	 * Returns the fully-qualified name of the superclass of this class or
	 * interface.
	 *
	 * @param fullyQualified Whether the returned value should be fully
	 *        qualified.
	 * @return The name of the superclass of this class or interface.  If this
	 *         is an interface, then "<code>java.lang.Object</code>" is
	 *         returned.  If this class file represents
	 *         <code>java.lang.Object</code>, then <code>null</code> is
	 *         returned.
	 * @see #getClassName(boolean)
	 */
	public String getSuperClassName(boolean fullyQualified) {
		if (superClass==0) { // This is java.lang.Object
			return null;
		}
		return getClassNameFromConstantPool(superClass, fullyQualified);
	}


	/**
	 * Returns the currently set type argument for the specified type parameter.
	 *
	 * @param typeParam The type parameter.
	 * @return The type argument, or "<code>Object</code>" if no type
	 *         parameters have been set.  This is because, if the user types,
	 *         say, "<code>java.util.List list;</code>" in Java 5+, the
	 *         type defaults to <code>Object</code>.  The code completion API
	 *         may set the type argument mapping to <code>null</code> if no
	 *         type arguments are scanned, thus we need to return
	 *         <code>Object</code> in this case.
	 * @see #setTypeParamsToTypeArgs(Map)
	 */
	public String getTypeArgument(String typeParam) {
		// If no type arguments are specified for a class that's supposed to
		// have them (according to calling code), return "Object", as Java
		// assumes this.
		return typeMap==null ? "Object" : (String)typeMap.get(typeParam);
	}


	/**
	 * Returns the string value represented by a <code>ConstantUtf8Info</code>
	 * entry in the constant pool.
	 *
	 * @param index The index into the constant pool of a
	 *        <code>ConstantUtf8Info</code> structure.  This should be
	 *        <code>1</code>-based.
	 * @return The string represented.
	 */
	public String getUtf8ValueFromConstantPool(int index) {
		ConstantPoolInfo cpi = getConstantPoolInfo(index);
		ConstantUtf8Info cui = (ConstantUtf8Info)cpi;
		return cui.getRepresentedString(false);
	}


	/**
	 * Returns the version number of this class, as a string.
	 *
	 * @return The class's version number, in the form
	 *         <code>major.minor</code>.
	 */
	public String getVersionString() {
		return majorVersion + "." + minorVersion;
	}


	/**
	 * Parses the class file from a given input stream.
	 *
	 * @param in
	 * @throws IOException If an error occurs reading the class file.
	 */
	private void init(DataInputStream in) throws IOException {
		readHeader(in);
		readVersion(in);
		readConstantPoolInfos(in);
		readAccessFlags(in);
		readThisClass(in);
		readSuperClass(in);
		readInterfaces(in);
		readFields(in);
		readMethods(in);
		readAttributes(in);
	}


	/**
	 * Returns whether this class is deprecated.
	 *
	 * @return Whether this class is deprecated.
	 */
	public boolean isDeprecated() {
		return deprecated;
	}


	/**
	 * Reads this class or interface's access flags.
	 *
	 * @param in
	 * @throws IOException If an error occurs reading the access flags.
	 */
	private void readAccessFlags(DataInputStream in) throws IOException {
		accessFlags = in.readUnsignedShort();
		debugPrint("Access flags: " + accessFlags);
	}


	/**
	 * Reads a single attribute of this class file.
	 *
	 * @param in The input stream to read from.
	 * @return The attribute.
	 * @throws IOException If an IO error occurs.
	 */
	private AttributeInfo readAttribute(DataInputStream in) throws IOException {

		AttributeInfo ai = null;

		int attributeNameIndex = in.readUnsignedShort();
		int attributeLength = in.readInt();

		String attrName = getUtf8ValueFromConstantPool(attributeNameIndex);
		debugPrint("Found class attribute: " + attrName);

		if (SOURCE_FILE.equals(attrName)) { // 4.7.7
			int sourceFileIndex = in.readUnsignedShort();
			SourceFile sf = new SourceFile(this, sourceFileIndex);
			ai = sf;
		}

		else if (SIGNATURE.equals(attrName)) { // 4.8.8
			int signatureIndex = in.readUnsignedShort();
			String sig = getUtf8ValueFromConstantPool(signatureIndex);
			//System.out.println("... Signature: " + sig);
			ai = new Signature(this, sig);
			paramTypes = ((Signature)ai).getClassParamTypes();
		}

		else if (INNER_CLASSES.equals(attrName)) { // 4.8.5
			//String name = getClassName(false) + "." + getName();
			//System.out.println(name + ": Attribute " + attrName + " not supported");
			Util.skipBytes(in, attributeLength);
			//ai = null;
		}

		else if (ENCLOSING_METHOD.equals(attrName)) { // 4.8.6
			//String name = getClassName(false) + "." + getName();
			//System.out.println(name + ": Attribute " + attrName + " not supported");
			Util.skipBytes(in, attributeLength); // 2 u2's, class_index and method_index
			//ai = null;
		}

		else if (DEPRECATED.equals(attrName)) { // 4.7.10
			// No need to read anything else, attributeLength==0
			deprecated = true;
		}

		else if (RUNTIME_VISIBLE_ANNOTATIONS.equals(attrName)) { // 4.8.15
			//String name = getClassFile().getClassName(false) + "." + getName();
			//System.out.println(name + ": Attribute " + attrName + " not supported");
			Util.skipBytes(in, attributeLength);
			//ai = null;
		}

		// TODO: Handle other useful Attribute types, if any.

		else { // An unknown/unsupported attribute.
			System.out.println("Unsupported class attribute: "+  attrName);
			ai = AttributeInfo.readUnsupportedAttribute(this, in, attrName,
					attributeLength);
		}

		return ai;

	}


	/**
	 * Reads this class file's attributes.
	 *
	 * @param in The input stream to read from.
	 * @throws IOException If an IO error occurs.
	 */
	private void readAttributes(DataInputStream in) throws IOException {
		int attributeCount = in.readUnsignedShort();
		if (attributeCount>0) {
			attributes = new AttributeInfo[attributeCount];
			for (int i=0; i<attributeCount; i++) {
				attributes[i] = readAttribute(in);
			}
		}
	}


	/**
	 * Reads the constant pool.
	 *
	 * @param in
	 * @throws IOException If an IO error occurs.
	 */
	private void readConstantPoolInfos(DataInputStream in) throws IOException {

		// NOTE: constant_pool_count is 1-based, so there are
		// constant_pool_count-1 infos!

		int constantPoolCount = in.readUnsignedShort() - 1;
		debugPrint("Constant pool count: " + constantPoolCount);

		constantPool = new ConstantPoolInfo[constantPoolCount];

		for (int i=0; i<constantPoolCount; i++) {
			ConstantPoolInfo cpi = ConstantPoolInfoFactory.readConstantPoolInfo(this, in);
			constantPool[i] = cpi;
			// longs and doubles take up 2 entries in the constant pool,
			// although the second entry is not used
			if (cpi instanceof ConstantLongInfo ||
					cpi instanceof ConstantDoubleInfo) {
				i++;
			}
		}

	}


	/**
	 * Reads the "fields" information.
	 *
	 * @param in
	 * @throws IOException If an IO error occurs.
	 */
	private void readFields(DataInputStream in) throws IOException {
		int fieldCount = in.readUnsignedShort();
		if (fieldCount>0) {
			fields = new FieldInfo[fieldCount];
			for (int i=0; i<fieldCount; i++) {
				fields[i] = FieldInfo.read(this, in);
			}
		}
		debugPrint("fieldCount: " + fieldCount);
	}


	/**
	 * Reads the <code>0xCAFEBABE</code> class file header.
	 *
	 * @param in
	 * @throws IOException If the header is invalid.
	 */
	private void readHeader(DataInputStream in) throws IOException {
		for (int i=0; i<HEADER.length; i++) {
			byte b = in.readByte();
			if (b!=HEADER[i]) {
				throw new IOException("\"CAFEBABE\" header not found");
			}
		}
	}


	/**
	 * Reads the array of indices into the constant pool for the names of the
	 * interfaces implemented by this class or interface.
	 *
	 * @param in
	 * @throws IOException If an IO error occurs reading the input stream.
	 */
	private void readInterfaces(DataInputStream in) throws IOException {
		int interfaceCount = in.readUnsignedShort();
		if (interfaceCount>0) {
			interfaces = new int[interfaceCount];
			for (int i=0; i<interfaceCount; i++) {
				interfaces[i] = in.readUnsignedShort();
			}
		}
		debugPrint("interfaceCount: " + interfaceCount);
	}


	private void readMethods(DataInputStream in) throws IOException {
		int methodCount = in.readUnsignedShort();
		if (methodCount>0) {
			methods = new MethodInfo[methodCount];
			for (int i=0; i<methodCount; i++) {
				methods[i] = MethodInfo.read(this, in);
			}
		}
	}


	private void readSuperClass(DataInputStream in) throws IOException {
		superClass = in.readUnsignedShort();
		ConstantPoolInfo cpi = getConstantPoolInfo(superClass);
		debugPrint("superClass: " + cpi);
	}


	private void readThisClass(DataInputStream in) throws IOException {
		thisClass = in.readUnsignedShort();
		ConstantPoolInfo cpi = getConstantPoolInfo(thisClass);
		debugPrint("thisClass: " + cpi);
	}


	/**
	 * Reads the class file's major and minor version numbers.
	 *
	 * @param in
	 * @throws IOException If the version numbers are invalid.
	 */
	private void readVersion(DataInputStream in) throws IOException {
		minorVersion = in.readUnsignedShort();
		majorVersion = in.readUnsignedShort();
		debugPrint("Class file version: " + getVersionString());
	}


	/**
	 * Sets a mapping of type parameters of this class to type arguments for
	 * a particular instance of this class.  Note that <code>ClassFile</code>
	 * does not directly use this field; it is there for code completion API's
	 * to use to extract the necessary types of arguments, return values, etc.,
	 * of methods (see the {@link MethodInfo} class).
	 *
	 * @param typeMap A mapping of type parameters to type arguments (both
	 *        <code>String</code>s).
	 * @see #getTypeArgument(String)
	 */
	public void setTypeParamsToTypeArgs(Map<String, String> typeMap) {
		this.typeMap = typeMap;
		for (int i=0; i<getMethodCount(); i++) {
			getMethodInfo(i).clearParamTypeInfo();
		}
	}


	@Override
	public String toString() {
		return "[ClassFile: " +
			"accessFlags=" + accessFlags +
			"]";
	}


}