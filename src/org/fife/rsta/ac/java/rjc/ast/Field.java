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
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;


public class Field extends AbstractASTNode implements Member {

	private Modifiers modifiers;
	private Type type;
	private boolean deprecated;
	private String docComment;


	public Field(Scanner s, Modifiers modifiers, Type type, Token t) {
		super(t.getLexeme(), s.createOffset(t.getOffset()));
		setDeclarationEndOffset(s.createOffset(t.getOffset() + t.getLength()));
		if (modifiers==null) {
			modifiers = new Modifiers();
		}
		this.modifiers = modifiers;
		this.type = type;
	}


	public String getDocComment() {
		return docComment;
	}


	public Modifiers getModifiers() {
		return modifiers;
	}


	public Type getType() {
		return type;
	}


	public boolean isDeprecated() {
		return deprecated;
	}


	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}


	public void setDocComment(String comment) {
		docComment = comment;
	}


}