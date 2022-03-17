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
 * Implementation of a token in a Java source file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TokenImpl implements Token {

	private int type;

	/**
	 * The token's text.
	 */
	private String lexeme;

	/**
	 * The line the token is on.
	 */
	private int line;

	/**
	 * The column the token is on.
	 */
	private int column;

	/**
	 * The absolute offset into the source of the token.
	 */
	private int offset;

	/**
	 * Whether the token is invalid (e.g. an invalid char of String).
	 */
	private boolean invalid;


	TokenImpl(int type, String lexeme, int line, int column, int offs) {
		this(type, lexeme, line, column, offs, false);
	}


	TokenImpl(int type, String lexeme, int line, int column, int offs,
					boolean invalid) {
		this.type = type;
		this.lexeme = lexeme;
		this.line = line;
		this.column = column;
		this.offset = offs;
		this.invalid = invalid;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj==this) {
			return true;
		}
		if (obj instanceof Token) {
			Token t2 = (Token)obj;
			return type==t2.getType() && lexeme.equals(t2.getLexeme()) &&
					line==t2.getLine() && column==t2.getColumn() &&
					invalid==t2.isInvalid();
		}
		return false;
	}


	@Override
	public int getColumn() {
		return column;
	}


	@Override
	public int getLength() {
		return lexeme.length();
	}


	@Override
	public String getLexeme() {
		return lexeme;
	}


	@Override
	public int getLine() {
		return line;
	}


	@Override
	public int getOffset() {
		return offset;
	}


	@Override
	public int getType() {
		return type;
	}


	@Override
	public int hashCode() {
		return lexeme.hashCode();
	}


	@Override
	public boolean isBasicType() {
		switch (getType()) {
			case KEYWORD_BYTE:
			case KEYWORD_SHORT:
			case KEYWORD_CHAR:
			case KEYWORD_INT:
			case KEYWORD_LONG:
			case KEYWORD_FLOAT:
			case KEYWORD_DOUBLE:
			case KEYWORD_BOOLEAN:
				return true;
			default:
				return false;
		}
	}


	@Override
	public boolean isIdentifier() {
		return (getType()&IDENTIFIER)>0;
	}


	@Override
	public boolean isInvalid() {
		return invalid;
	}


	@Override
	public boolean isOperator() {
		return (getType()&OPERATOR)>0;
	}


	@Override
	public boolean isType(int type) {
		return this.type==type;
	}


	@Override
	public String toString() {
		return "[TokenImpl: " +
			"type=" + type +
			"; lexeme=\"" + lexeme + "\"" +
			"; line=" + getLine() +
			"; col=" + getColumn() +
			"; offs=" + getOffset() +
			"; invalid=" + isInvalid() +
			"]";
	}


}
