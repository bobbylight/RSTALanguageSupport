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

import org.fife.rsta.ac.js.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.mozilla.javascript.ast.AstNode;


/**
 * JavaScript Variable Declaration class <code>TypeDeclarations</code>
 */
public class JavaScriptVariableDeclaration {

	private String name;
	private int offset;
	private TypeDeclaration typeDec;
	private SourceCompletionProvider provider;


	/**
	 * @param name of the variable
	 * @param offset position within script
	 * @param provider JavaScript source provider
	 */
	public JavaScriptVariableDeclaration(String name, int offset,
			SourceCompletionProvider provider) {
		this.name = name;
		this.offset = offset;
		this.provider = provider;
	}


	/**
	 * Lookup TypeDeclaration from the Rhino <code>AstNode</code>
	 * 
	 * @param typeNode - Rhino AstNode linked to this variable
	 */
	public void setTypeDeclaration(AstNode typeNode) {
		typeDec = new JavaScriptCompletionResolver(provider)
				.resolveNode(typeNode);
	}
	
	/**
	 * Set TypeDeclaration
	 * @param typeDec
	 */
	public void setTypeDeclaration(TypeDeclaration typeDec) {
		this.typeDec = typeDec;
	}


	/**
	 * @return TypeDeclaration for the variable
	 */
	public TypeDeclaration getTypeDeclaration() {
		return typeDec;
	}


	/**
	 * @return JavaScript name for the type declaration e.g String, Number etc..
	 */
	public String getJavaScriptTypeName() {
		TypeDeclaration dec = getTypeDeclaration();
		return dec != null ? dec.getJSName() : TypeDeclarationFactory
				.getDefaultTypeDeclaration().getJSName();
	}


	/**
	 * @return Name of the variable
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return variable position within the script
	 */
	public int getOffset() {
		return offset;
	}

}