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
package org.fife.rsta.ac.java.rjc.lexer;


/**
 * A lexical token in a Java file.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public interface Token extends TokenTypes {

	public int getColumn();


	public String getLexeme();


	public int getLength();


	public int getLine();


	public int getOffset();


	public int getType();


	public boolean isBasicType();


	public boolean isIdentifier();


	public boolean isOperator();


	public boolean isInvalid();


}