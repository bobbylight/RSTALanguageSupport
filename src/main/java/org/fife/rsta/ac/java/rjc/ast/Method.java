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

import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;


// TODO: Implement me correctly
public class Method extends AbstractMember {

	private Modifiers modifiers;
	private Type type;
	private List<FormalParameter> parameters;
	private List<String> thrownTypeNames;
	private CodeBlock body;
	private boolean deprecated;
	private String docComment;


	public Method(Scanner s, Modifiers modifiers, Type type, Token nameToken,
					List<FormalParameter> params, List<String> thrownTypeNames) {
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


	public boolean getBodyContainsOffset(int offs) {
		return offs>=getBodyStartOffset() && offs<getBodyEndOffset();
	}


	public int getBodyEndOffset() {
		return body==null ? Integer.MAX_VALUE : body.getNameEndOffset();
	}


	public int getBodyStartOffset() {
		return getNameStartOffset();
	}


	public String getDocComment() {
		return docComment;
	}


	public Modifiers getModifiers() {
		return modifiers;
	}


	public String getNameAndParameters() {
		StringBuilder sb = new StringBuilder(getName());
		sb.append('(');
		int count = getParameterCount();
		for (int i=0; i<count; i++) {
			//sb.append(getParameter(i).toString());
			FormalParameter fp = getParameter(i);
			sb.append(fp.getType().getName(false));
			sb.append(' ');
			sb.append(fp.getName());
			if (i<count-1) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	public FormalParameter getParameter(int index) {
		return parameters.get(index);
	}


	public int getParameterCount() {
		return parameters.size();
	}


	public Iterator<FormalParameter> getParameterIterator() {
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