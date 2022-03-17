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
package org.fife.rsta.ac.java.rjc.lexer;


/**
 * A lexical token in a Java file.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public interface Token extends TokenTypes {


	/**
	 * Returns the column (offset into the line) of this token.
	 *
	 * @return The column of the token.
	 * @see #getLine()
	 */
	int getColumn();


	/**
	 * Returns the text of this token.
	 *
	 * @return This token's textual value.
	 */
	String getLexeme();


	/**
	 * Returns the length of this token.
	 *
	 * @return The token's length.
	 */
	int getLength();


	/**
	 * Returns the line this token is on.
	 *
	 * @return The token's line number.
	 * @see #getColumn()
	 */
	int getLine();


	/**
	 * Returns the offset into the document of this token.
	 *
	 * @return The token's offset.
	 * @see #getLine()
	 * @see #getColumn()
	 */
	int getOffset();


	/**
	 * Returns the type of this token.
	 *
	 * @return The type of this token.
	 */
	int getType();


	/**
	 * Returns whether this token is a primitive data type
	 * (int, float, string, etc.).
	 *
	 * @return Whether this token is a primitive data type.
	 */
	boolean isBasicType();


	/**
	 * Returns whether this token is an identifier.
	 *
	 * @return Whether this token is an identifier.
	 */
	boolean isIdentifier();


	/**
	 * Returns whether this token is invalid.
	 *
	 * @return Whether this token is invalid.
	 */
	boolean isInvalid();


	/**
	 * Returns whether this token is an operator.
	 *
	 * @return Whether this token is an operator.
	 */
	boolean isOperator();


	/**
	 * Returns whether this token is of the specified type.
	 *
	 * @param type The type to check.
	 * @return Whether this token is of the specified type.
	 */
	boolean isType(int type);


}
