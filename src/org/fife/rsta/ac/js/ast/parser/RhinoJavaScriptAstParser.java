package org.fife.rsta.ac.js.ast.parser;

import java.util.Set;

import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


/**
 * Rhino specific JavaScriptAstParser
 *  
 * reads the importPackage and importClass from the parsed document and adds to the RhinoJavaScriptTypesFactory
 */
public class RhinoJavaScriptAstParser extends JavaScriptAstParser {

	
	public RhinoJavaScriptAstParser(SourceCompletionProvider provider, int dot,
			boolean preProcessingMode) {
		super(provider, dot, preProcessingMode);
		clearImportCache(provider);
	}

	/**
	 * Clear the importPackage and importClass cache
	 * @param provider SourceCompletionProvider
	 */
	private void clearImportCache(SourceCompletionProvider provider)
	{
		JavaScriptTypesFactory typesFactory = provider.getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			((RhinoJavaScriptTypesFactory) typesFactory).clearImports();
		}
	}
	
	/**
	 * Overridden iterateNode to intercept Token.EXPR_RESULT and check for importPackage and importClass named nodes
	 * If found, then process them and extract the imports and add them to RhinoJavaScriptTypesFactory then return
	 * otherwise call super.iterateNode() 
	 */
	protected void iterateNode(AstNode child, Set set, String entered,
			CodeBlock block, int offset) {
		
		//look for importPackage and importClass
		switch (child.getType()) {
			case Token.EXPR_RESULT:
				boolean importFound = processImportNode(child, set, entered, block, offset);
				if(importFound)
					return; //already processed node
				break;
		}
		
		super.iterateNode(child, set, entered, block, offset);
	}
	
	/**
	 * Look for text importPackage and importClass and add to cache
	 * @param child AstNode to check. This will always be Token.EXPR_RESULT AstNode
	 * @param set Set to add completions
	 * @param entered text entered by user if applicable
	 * @param block CodeBlock 
	 * @param offset position of AstNode within document
	 * @return true if either importPackage or importClass is found
	 */
	private boolean processImportNode(AstNode child, Set set, String entered,
			CodeBlock block, int offset) {
		
		String src = JavaScriptHelper.convertNodeToSource(child);
		if(src != null) {
			if(src.startsWith("importPackage")) {
				processImportPackage(src);
				return true;
			}
			else if(src.startsWith("importClass")) {
				processImportClass(src);
				return true;
			}
		}
		
		
		return false;
	}
	
	/**
	 * @param src String to extract name
	 * @return import statement from withing the ( and ) 
	 * e.g  importPackage(java.util)
	 * 		importClass(java.util.HashSet)
	 * 
	 * returns java.util or java.util.HashSet respectively 
	 */
	private String extractNameFromSrc(String src)
	{
		int startIndex = src.indexOf("(");
		int endIndex = src.indexOf(")");
		if(startIndex != -1 && endIndex != -1) {
			return src.substring(startIndex + 1, endIndex);
		}
		return src;
	}
	
	/**
	 * Adds package name to RhinoJavaScriptTypesFactory
	 * @param src source text to extract the package
	 */
	private void processImportPackage(String src) {
		JavaScriptTypesFactory typesFactory = getProvider().getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			String pkg = extractNameFromSrc(src);
			RhinoJavaScriptTypesFactory rf = (RhinoJavaScriptTypesFactory) typesFactory;
			rf.addImportPackage(pkg);
		}
	}
	
	/**
	 * Adds class name to RhinoJavaScriptTypesFactory
	 * @param src source text to extract the class name
	 */
	private void processImportClass(String src) {
		JavaScriptTypesFactory typesFactory = getProvider().getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			String cls = extractNameFromSrc(src);
			RhinoJavaScriptTypesFactory rf = (RhinoJavaScriptTypesFactory) typesFactory;
			rf.addImportClass(cls);
		}
	}
	
	
	

}
