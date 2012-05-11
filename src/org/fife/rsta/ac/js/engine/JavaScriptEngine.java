package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


/**
 * JavaScript Engine Interface used for resolving Types
 * SourceCompletionProvider needs a JavaScriptEngine for creating the following:
 * 
 *  JavaScriptResolver
 *  JavaScriptTypesFactory
 *  JavaScriptParser
 */
public interface JavaScriptEngine {

	
	/**
	 * @param provider SourceCompletionProvider
	 * @return JavaScriptResolver used to resolve JavaScriptType and TypeDeclaration
	 */
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider);
	
	/**
	 * 
	 * @param provider SourceCompletionProvider
	 * @return JavaScriptTypesFactory that holds a cache of JavaScriptType
	 */
	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider);
	
	/**
	 * 
	 * @param provider SourceCompletionProvider
	 * @param dot caret position
	 * @param preProcessingMode flag whether processing script before JTextComponent's text within SourceCompletionProvider  
	 * @return JavaScriptParser that converts AstRoot to CodeBlock
	 */
	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, boolean preProcessingMode);
	
}
