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
package org.fife.rsta.ac.bsh.classreader.constantpool;


/**
 * Constant types used by {@link ConstantPoolInfo}s.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface ConstantTypes {

	public static final int CONSTANT_Class				= 7;

	public static final int CONSTANT_Fieldref			= 9;

	public static final int CONSTANT_Methodref			= 10;

	public static final int CONSTANT_InterfaceMethodref	= 11;

	public static final int CONSTANT_String				= 8;

	public static final int CONSTANT_Integer			= 3;

	public static final int CONSTANT_Float				= 4;

	public static final int CONSTANT_Long				= 5;

	public static final int CONSTANT_Double				= 6;

	public static final int CONSTANT_NameAndType		= 12;

	public static final int CONSTANT_Utf8				= 1;

	public static final int CONSTANT_MethodHandle		= 15;

	public static final int CONSTANT_MethodType			= 16;

	public static final int CONSTANT_InvokeDynamic		= 18;

}
