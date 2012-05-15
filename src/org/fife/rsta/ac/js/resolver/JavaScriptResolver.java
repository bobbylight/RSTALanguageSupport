package org.fife.rsta.ac.js.resolver;

import java.io.IOException;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptType;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.mozilla.javascript.ast.AstNode;


public abstract class JavaScriptResolver {
	
	protected SourceCompletionProvider provider;
	
	/**
	 * Base JavaScriptResolver
	 * @param provider SourceCompletionProvider 
	 */
	public JavaScriptResolver(SourceCompletionProvider provider)
	{
		this.provider = provider;
	}
	
	/**
	 * Resolve node type to TypeDeclaration. Called instead of #compileText(String text) when document is already parsed
	 * @param node AstNode to resolve
	 * @return TypeDeclaration for node or null if not found.
	 */
	public abstract TypeDeclaration resolveNode(AstNode node);
	
	/**
	 * Compiles Text and resolves the type.
	 * e.g 
	 * "Hello World".length; //resolve as a Number
	 * 
	 * @param text to compile and resolve  
	 */
	public abstract JavaScriptType compileText(String text) throws IOException;
	
	/**
	 * Resolve node type to TypeDeclaration
	 * @param node AstNode to resolve
	 * @return TypeDeclaration for node or null if not found.
	 */
	protected abstract TypeDeclaration resolveNativeType(AstNode node);
	
}
