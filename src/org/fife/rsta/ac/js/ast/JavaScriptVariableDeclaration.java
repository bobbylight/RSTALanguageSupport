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
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.mozilla.javascript.ast.AstNode;


/**
 * JavaScript Variable Declaration class <code>TypeDeclarations</code>
 */
public class JavaScriptVariableDeclaration {

	private String name;
	private int offset;
	protected TypeDeclaration typeDec;
	protected SourceCompletionProvider provider;

	private boolean reassigned;
	private TypeDeclaration originalTypeDec;
	private CodeBlock block;
	
	private int start;
	private int end;
	

	/**
	 * @param name of the variable
	 * @param offset position within script
	 * @param provider JavaScript source provider
	 */
	public JavaScriptVariableDeclaration(String name, int offset,
			SourceCompletionProvider provider, CodeBlock block) {
		this.name = name;
		this.offset = offset;
		this.provider = provider;
		this.block = block;
	}


	/**
	 * Lookup TypeDeclaration from the Rhino <code>AstNode</code>
	 * 
	 * @param typeNode - Rhino AstNode linked to this variable
	 */
	public void setTypeDeclaration(AstNode typeNode) {
		typeDec = provider.getJavaScriptEngine().getJavaScriptResolver(provider)
				.resolveNode(typeNode);
	}

	/**
	 * Set the TypeDeclaration for the AstNode. Stores the original value so it can be reset 
	 * @param typeNode
	 * @param overrideOriginal
	 * @see #resetVariableToOriginalType()
	 */
	public void setTypeDeclaration(AstNode typeNode, boolean overrideOriginal) {
		// check whether the variable has been reassigned already
		if (!reassigned) {
			originalTypeDec = typeDec;
		}

		setTypeDeclaration(typeNode);

		if (overrideOriginal) {
			originalTypeDec = typeDec;
		}
		reassigned = true;

	}

	/**
	 * Resets the TypeDeclaration to the original value 
	 */
	public void resetVariableToOriginalType() {
		if (reassigned) {
			reassigned = false;
			typeDec = originalTypeDec;
		}
		originalTypeDec = null;
	}


	/**
	 * Set TypeDeclaration
	 * 
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
		return dec != null ? dec.getJSName() : provider.getTypesFactory()
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
	
	public CodeBlock getCodeBlock() {
		return block;
	}
	
	/**
	 * Gets the end offset of this variable.
	 * 
	 * @param end the end offset.
	 */
	public int getEndOffset() {
		return end;
	}
	
	/**
	 * Sets the end offset of this variable.
	 * 
	 * @param end the end offset.
	 * @see #getEndOffset()
	 */
	public void setEndOffset(int end) {
		this.end = end;
	}


	/**
	 * Sets the start offset of this variable.
	 * @param start the start offset
	 * @see #getStartOffset()
	 */
	public void setStartOffSet(int start)
	{
		this.start = start;
	}


	/**
	 * Gets the start offset of this variable.
	 * @param start the start offset
	 * 
	 */
	public int getStartOffSet()
	{
		return start;
	}

}