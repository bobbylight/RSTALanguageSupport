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
package org.fife.rsta.ac.java.classreader.constantpool;


/**
 * Constant types used by {@link ConstantPoolInfo}s.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface ConstantTypes {

	int CONSTANT_Class				= 7;

	int CONSTANT_Fieldref			= 9;

	int CONSTANT_Methodref			= 10;

	int CONSTANT_InterfaceMethodref	= 11;

	int CONSTANT_String				= 8;

	int CONSTANT_Integer			= 3;

	int CONSTANT_Float				= 4;

	int CONSTANT_Long				= 5;

	int CONSTANT_Double				= 6;

	int CONSTANT_NameAndType		= 12;

	int CONSTANT_Utf8				= 1;

	int CONSTANT_MethodHandle		= 15;

	int CONSTANT_MethodType			= 16;

	int CONSTANT_InvokeDynamic		= 18;

}
