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
package org.fife.rsta.ac.bsh.rjc.ast;

import org.fife.rsta.ac.bsh.rjc.lang.Type;
import org.fife.rsta.ac.bsh.rjc.lexer.Scanner;


/**
 * Base class for local variables and formal parameters.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LocalVariable extends AbstractASTNode {

	private boolean isFinal;
	private Type type;


	public LocalVariable(Scanner s, boolean isFinal,
								Type type, int offs, String name) {
		super(name, s.createOffset(offs), s.createOffset(offs+name.length()));
		this.isFinal = isFinal;
		this.type = type;
	}


	public Type getType() {
		return type;
	}


	public boolean isFinal() {
		return isFinal;
	}


}