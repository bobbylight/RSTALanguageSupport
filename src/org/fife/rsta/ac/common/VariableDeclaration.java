/*
 * 01/11/2010
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.common;

import org.fife.rsta.ac.LanguageSupport;


/**
 * A marker for a variable declaration.  This can be used by
 * {@link LanguageSupport}s to mark variables, and is especially helpful
 * when used in conjunction with {@link CodeBlock}.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see CodeBlock
 */
public class VariableDeclaration {

	private String name;
	private int offset;


	public VariableDeclaration(String name, int offset) {
		this.name = name;
		this.offset = offset;
	}


	public String getName() {
		return name;
	}


	public int getOffset() {
		return offset;
	}


}