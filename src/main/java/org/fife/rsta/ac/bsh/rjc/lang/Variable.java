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
package org.fife.rsta.ac.bsh.rjc.lang;

import org.fife.rsta.ac.bsh.rjc.lexer.Token;


/**
 * Base class for variable type (local variables, formal parameters...).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class Variable {

	private boolean isFinal;
	private Type type;
	private Token name;


	public Variable(boolean isFinal, Type type, Token name) {
		this.isFinal = isFinal;
		this.type = type;
		this.name = name;
	}


	public String getName() {
		return name.getLexeme();
	}


	public Type getType() {
		return type;
	}


	public boolean isFinal() {
		return isFinal;
	}


}
