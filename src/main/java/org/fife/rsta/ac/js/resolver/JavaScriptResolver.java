package org.fife.rsta.ac.js.resolver;

import java.io.IOException;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptType;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSMethodData;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;


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
	 * Resolve node type to TypeDeclaration. Called instead of #compileText(String text) when document is already parsed
	 * @param text The type of node to resolve
	 * @return TypeDeclaration for node or null if not found.
	 */
	public abstract TypeDeclaration resolveParamNode(String text) throws IOException;
	
	
	
	/**
	 * Compiles Text and resolves the type.
	 * e.g.
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
	
	/**
	 * Get lookup string for function completions
	 * @param method JSMethodData holding method information
	 * @param name name of method
	 * @return The lookup text.
	 */
	public abstract String getLookupText(JSMethodData method, String name);
	
	/**
	 * Returns same string format as {@link #getLookupText(JSMethodData, String)} but from AstNode Function 
	 * @param call
	 * @param provider
	 * @return The function name.
	 */
	public abstract String getFunctionNameLookup(FunctionCall call, SourceCompletionProvider provider);
	
}
