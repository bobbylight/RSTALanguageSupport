/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;


public class JSVariableDeclaration {

	private String name;
	private int offset;
	private AstNode typeNode;


	public JSVariableDeclaration(String name, int offset) {
		this.name = name;
		this.offset = offset;
	}


	public void setTypeNode(AstNode typeNode) {
		this.typeNode = typeNode;
	}


	public TypeDeclaration getTypeDeclaration() {
		return tokenToTypeDeclaration(typeNode);
	}


	public String getJavaScriptTypeName() {
		TypeDeclaration dec = getTypeDeclaration();
		return dec != null ? dec.getJSName() : null;
	}


	public String getName() {
		return name;
	}


	public int getOffset() {
		return offset;
	}


	public static final TypeDeclaration tokenToTypeDeclaration(AstNode typeNode) {
		if (typeNode != null) {
			switch (typeNode.getType()) {
				case Token.CATCH:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_ERROR);
				case Token.NAME:
					return getTypeDeclaration(((Name) typeNode).getIdentifier());
				case Token.NEW:
					return getTypeDeclaration(findNewExpressionString(typeNode));
				case Token.NUMBER:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_NUMBER);
				case Token.OBJECTLIT:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_OBJECT);
				case Token.STRING:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_STRING);
				case Token.TRUE:
				case Token.FALSE:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_BOOLEAN);
				case Token.ARRAYLIT:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_ARRAY);
			}
		}
		return getTypeDeclaration(TypeDeclarationFactory.ANY);

	}


	private static TypeDeclaration getTypeDeclaration(String name) {
		TypeDeclarationFactory factory = TypeDeclarationFactory.Instance();
		return factory.getTypeDeclaration(name);
	}


	private static String findNewExpressionString(AstNode node) {
		NewExpression newEx = (NewExpression) node;
		AstNode target = newEx.getTarget();
		switch (target.getType()) {
			case Token.NAME:
				return ((Name) target).getIdentifier();
		}
		return null;
	}


}