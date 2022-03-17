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

/**
 * Represents a method in a Java type.
 *
 * @author Robert Futrell
 * @version 1.0
 */
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


	/**
	 * Returns whether this method's body contains the specified offset.
	 *
	 * @param offs The offset to check.
	 * @return Whether this method's body contains that offset.
	 */
	public boolean getBodyContainsOffset(int offs) {
		return offs>=getBodyStartOffset() && offs<getBodyEndOffset();
	}


	public int getBodyEndOffset() {
		return body==null ? Integer.MAX_VALUE : body.getNameEndOffset();
	}


	public int getBodyStartOffset() {
		return getNameStartOffset();
	}


	@Override
	public String getDocComment() {
		return docComment;
	}


	@Override
	public Modifiers getModifiers() {
		return modifiers;
	}


	/**
	 * Returns this method name and parameters, as a string, in
	 * the format {@code methodName(Type1 param1, Type2 param2)}.
	 *
	 * @return This method's name and parameters.
	 */
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


	/**
	 * Returns the specified formal parameter.
	 *
	 * @param index The parameter's index.
	 * @return The formal parameter.
	 * @see #getParameterCount()
	 */
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


	@Override
	public Type getType() {
		return type;
	}


	public boolean isConstructor() {
		return type==null;
	}


	@Override
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
