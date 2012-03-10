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
package org.fife.rsta.ac.java.rjc.ast;

import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * A parameter to a method.
 *
 * @author Robert Futrell
 * @version 1.0
 */
/*
 * FormalParameter:
 *    ['final'] [Annotations] Type VariableDeclaratorId
 *   
 * VariableDeclaratorId:
 *    Identifier { "[" "]" }
 */
public class FormalParameter extends LocalVariable {

	private List annotations;


	public FormalParameter(Scanner s, boolean isFinal,
			Type type, int offs, String name, List annotations) {
		super(s, isFinal, type, offs, name);
		this.annotations = annotations;
	}


	public int getAnnotationCount() {
		return annotations==null ? 0 : annotations.size();
	}


	/**
	 * Overridden to return "<code>getType() getName()</code>".
	 *
	 * @return This parameter, as a string.
	 */
	public String toString() {
		return getType() + " " + getName();
	}


}