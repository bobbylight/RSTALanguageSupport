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
package org.fife.rsta.ac.java.rjc.notices;

import org.fife.rsta.ac.java.rjc.lexer.Token;


/**
 * A notice (e.g., a warning or error) from a parser.
 *
 * @author Robert Futrell
 * @version 0.1
 */
public class ParserNotice {

	private int line;
	private int column;
	private int length;
	private String message;


	public ParserNotice(Token t, String msg) {
		line = t.getLine();
		column = t.getColumn();
		length = t.getLexeme().length();
		message = msg;
	}


	/**
	 * Constructor.
	 *
	 * @param line The line of the notice.
	 * @param column The column of the notice.
	 * @param length The length of the code the message is concerned with.
	 * @param message The message.
	 */
	public ParserNotice(int line, int column, int length, String message) {
		this.line = line;
		this.column = column;
		this.length = length;
		this.message = message;
	}


	/**
	 * Returns the character offset into the line of the parser notice,
	 * if any.
	 *
	 * @return The column.
	 */
	public int getColumn() {
		return column;
	}


	/**
	 * Returns the length of the code the message is concerned with.
	 *
 	 * @return The length of the code the message is concerned with.
	 */
	public int getLength() {
		return length;
	}


	/**
	 * Returns the line number the notice is about, if any.
	 *
	 * @return The line number.
	 */
	public int getLine() {
		return line;
	}


	/**
	 * Returns the message from the parser.
	 *
	 * @return The message from the parser.
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * Returns a string representation of this parser notice.
	 *
	 * @return This parser notice as a string.
	 */
	public String toString() {
		return "(" + getLine() + ", " + getColumn() + ": " + getMessage();
	}


}