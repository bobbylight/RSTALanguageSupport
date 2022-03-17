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

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;


/**
 * Represents a field in a class file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Field extends AbstractMember {

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


	@Override
	public String getDocComment() {
		return docComment;
	}


	@Override
	public Modifiers getModifiers() {
		return modifiers;
	}


	@Override
	public Type getType() {
		return type;
	}


	@Override
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
