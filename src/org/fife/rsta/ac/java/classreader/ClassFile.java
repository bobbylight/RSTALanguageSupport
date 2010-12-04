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
	 * Attributes of this class or interface.
	 */
	private AttributeInfo[] attributes;

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
				className = org.fife.rsta.ac.java.Util.replaceChar(
											className, '/', '.');
			}
			else {
				className = className.substring(className.lastIndexOf('/')+1);
			}
			return org.fife.rsta.ac.java.Util.replaceChar(className, '$', '.');
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
	 */
	public FieldInfo getFieldInfo(int index) {
		return fields[index];
	}


	/**
	 * Returns the number of interfaces this class or interface implements.
	 *
	 * @return The number of implemented interfaces.
	 * @see #getImplementedInterfaceName(int)
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

		// Get some basic stuff out of the way.
		readHeader(in);
		readVersion(in);
		readConstantPoolInfos(in);
		readAccessFlags(in);
		readThisClass(in);
		readSuperClass(in);
		readInterfaces(in);
		readFields(in);
		readMethods(in);

		// No need for us to read attributes (code, etc.).
		//readAttributes(in);

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

		if (SOURCE_FILE.equals(attrName)) { // 4.7.7
			int sourceFileIndex = in.readUnsignedShort();
			SourceFile sf = new SourceFile(this, sourceFileIndex);
			ai = sf;
		}

		// TODO: Handle other Attribute types.

		else { // An unknown/unsupported attribute.
			int[] info = new int[attributeLength];
			for (int i = 0; i < attributeLength; i++) {
				info[i] = in.readUnsignedByte();
			}
			ai = new UnsupportedAttribute(this, attrName, info);
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


	public String toString() {
		return "[ClassFile: " +
			"accessFlags=" + accessFlags +
			"]";
	}


}