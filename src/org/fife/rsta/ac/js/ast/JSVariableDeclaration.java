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

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;


/**
 * JavaScript Variable Declaration class to resolve <code>ASTNodes</code> to
 * <code>TypeDeclarations</code>
 */
public class JSVariableDeclaration {

	private String name;
	private int offset;
	private TypeDeclaration typeDec;
	private AstNode typeNode;
	private SourceCompletionProvider provider;


	public JSVariableDeclaration(String name, int offset,
			SourceCompletionProvider provider) {
		this.name = name;
		this.offset = offset;
		this.provider = provider;
	}


	public void setTypeNode(AstNode typeNode) {
		this.typeNode = typeNode;
		typeDec = tokenToTypeDeclaration(typeNode, provider);
	}


	public AstNode getTypeNode() {
		return typeNode;
	}


	public void setTypeDeclaration(TypeDeclaration typeDec) {
		this.typeDec = typeDec;
	}


	public TypeDeclaration getTypeDeclaration() {
		return typeDec;
	}


	public String getJavaScriptTypeName() {
		TypeDeclaration dec = getTypeDeclaration();
		return dec != null ? dec.getJSName() : getDefaultTypeDeclaration()
				.getJSName();
	}


	public String getName() {
		return name;
	}


	public int getOffset() {
		return offset;
	}


	public static TypeDeclaration getDefaultTypeDeclaration() {
		return getTypeDeclaration(TypeDeclarationFactory.ANY);
	}


	public static final TypeDeclaration tokenToTypeDeclaration(
			AstNode typeNode, SourceCompletionProvider provider) {
		if (typeNode != null) {
			switch (typeNode.getType()) {
				case Token.CATCH:
					return getTypeDeclaration(TypeDeclarationFactory.ECMA_ERROR);
				case Token.NAME:
					return provider.resolveTypeDeclation(((Name) typeNode)
							.getIdentifier());
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
				case Token.CALL:
				case Token.GETPROP:
					// need to parse the source e.g a.toString(), split it
					// and work from left to right to resolve the variable
					// type
					return provider.resolveTypeFromFunctionNode(typeNode);

			}
			if (typeNode instanceof InfixExpression) {
				TypeDeclaration dec = getTypeFromInFixExpression(typeNode);
				if (dec != null) {
					return dec;
				}
			}
		}
		return getDefaultTypeDeclaration();

	}
	
	
	private static TypeDeclaration getTypeFromInFixExpression(AstNode node) {
		InfixVisitor visitor = new InfixVisitor();
		node.visit(visitor);
		return getTypeDeclaration(visitor.type);
	}


	public static TypeDeclaration getTypeDeclaration(String name) {
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


	/**
	 * Visitor for infix expression to work out whether the variable should be a
	 * string number literal Only works by determining the presence of
	 * StringLiterals and NumberLiterals. StringLiteral will override type to
	 * evaluate to String.
	 * 
	 * TODO most probably need some work on this
	 */
	private static class InfixVisitor implements NodeVisitor {

		private String type = null;


		public boolean visit(AstNode node) {

			if (!(node instanceof InfixExpression)) // ignore infix expression
			{
				switch (node.getType()) {
					case Token.STRING:
						type = TypeDeclarationFactory.ECMA_STRING;
						break;
					case Token.NUMBER:
						if (type == null) {
							type = TypeDeclarationFactory.ECMA_NUMBER;
						}
						break;
					default:
						if (type == null
								|| !TypeDeclarationFactory.ECMA_STRING
										.equals(type)) {
							type = TypeDeclarationFactory.ANY;
						}
						break;
				}
			}

			return true;
		}
	}

}