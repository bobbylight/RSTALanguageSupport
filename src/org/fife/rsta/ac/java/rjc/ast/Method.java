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

import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;


// TODO: Implement me correctly
public class Method extends AbstractASTNode implements Member {

	private Modifiers modifiers;
	private Type type;
	private List parameters;
	private List thrownTypeNames;
	private CodeBlock body;
	private boolean deprecated;
	private String docComment;


	public Method(Scanner s, Modifiers modifiers, Type type, Token nameToken,
					List params, List thrownTypeNames) {
		super(nameToken.getLexeme(),
				s.createOffset(nameToken.getOffset()),
				s.createOffset(nameToken.getOffset() + nameToken.getLength()));
		if (modifiers==null) {
			modifiers = new Modifiers();
		}
		this.modifiers = modifiers;
		this.type = type;
		this.parameters = params;
		this.thrownTypeNames = thrownTypeNames;
	}


	public CodeBlock getBody() {
		return body;
	}


	public String getDocComment() {
		return docComment;
	}


	public int getBodyEndOffset() {
		return body==null ? Integer.MAX_VALUE : body.getNameEndOffset();
	}


	public int getBodyStartOffset() {
		return getNameStartOffset();
	}


	public Modifiers getModifiers() {
		return modifiers;
	}


	public String getNameAndParameters() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(');
		int count = getParameterCount();
		for (int i=0; i<count; i++) {
			sb.append(getParameter(i).toString());
			if (i<count-1) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	public FormalParameter getParameter(int index) {
		return (FormalParameter)parameters.get(index);
	}


	public int getParameterCount() {
		return parameters.size();
	}


	public Iterator getParameterIterator() {
		return parameters.iterator();
	}


	public int getThrownTypeNameCount() {
		return thrownTypeNames==null ? 0 : thrownTypeNames.size();
	}


	public Type getType() {
		return type;
	}


	public boolean isConstructor() {
		return type==null;
	}


	public boolean isDeprecated() {
		return deprecated;
	}


	public void setBody(CodeBlock body) {
		this.body = body;
	}


	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}


	public void setDocComment(String comment) {
		docComment = comment;
	}


}