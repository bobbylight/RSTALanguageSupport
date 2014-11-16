package org.fife.rsta.ac.js.engine;

import java.util.List;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


/**
 * JavaScript Engine Interface used for resolving Types
 * SourceCompletionProvider needs a JavaScriptEngine for creating the following:
 * 
 *  JavaScriptResolver
 *  JavaScriptTypesFactory
 *  JavaScriptParser
 */
public abstract class JavaScriptEngine {

	
	private TypeDeclarationFactory typesFactory = new TypeDeclarationFactory();
	
	protected JavaScriptTypesFactory jsFactory;
	
	
    public List<String> setTypeDeclarationVersion(String ecmaVersion,
    		boolean xmlSupported, boolean client) {
    	return typesFactory.setTypeDeclarationVersion(ecmaVersion, xmlSupported, client);
    }
    
    public TypeDeclarationFactory getTypesFactory() {
    	return typesFactory;
    }
	
	/**
	 * @param provider SourceCompletionProvider
	 * @return JavaScriptResolver used to resolve JavaScriptType and TypeDeclaration
	 */
	public abstract JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider);
	
	/**
	 * 
	 * @param provider SourceCompletionProvider
	 * @return JavaScriptTypesFactory that holds a cache of JavaScriptType
	 */
	public abstract JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider);
	
	/**
	 * 
	 * @param provider SourceCompletionProvider
	 * @param dot caret position
	 * @param options TypeDeclationsOption to allow configuration options for processing script before JTextComponent's text within SourceCompletionProvider
	 * @return JavaScriptParser that converts AstRoot to CodeBlock
	 * 
	 */
	public abstract JavaScriptParser getParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options);
	
}
