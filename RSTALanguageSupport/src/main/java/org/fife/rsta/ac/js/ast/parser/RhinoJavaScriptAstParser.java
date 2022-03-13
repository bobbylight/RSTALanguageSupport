package org.fife.rsta.ac.js.ast.parser;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;


/**
 * Rhino specific JavaScriptAstParser
 *  
 * reads the importPackage and importClass from the parsed document and adds to the RhinoJavaScriptTypesFactory
 */
public class RhinoJavaScriptAstParser extends JavaScriptAstParser {

	public static final String PACKAGES = "Packages.";
	
	private LinkedHashSet<String> importClasses = new LinkedHashSet<>();
	private LinkedHashSet<String> importPackages = new LinkedHashSet<>();
	
	public RhinoJavaScriptAstParser(SourceCompletionProvider provider, int dot,
			TypeDeclarationOptions options) {
		super(provider, dot, options);
	}

	/**
	 * Clear the importPackage and importClass cache
	 * @param provider SourceCompletionProvider
	 */
	public void clearImportCache(SourceCompletionProvider provider) {
		JavaScriptTypesFactory typesFactory = provider.getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			((RhinoJavaScriptTypesFactory) typesFactory).clearImportCache();
		}
	}
	
	
	@Override
	public CodeBlock convertAstNodeToCodeBlock(AstRoot root,
			Set<Completion> set, String entered) {
		try {
			return super.convertAstNodeToCodeBlock(root, set, entered);
		} finally {
			//merge import packages
			mergeImportCache(importPackages, importClasses);
			//clear, as these are now merged
			importClasses.clear();
			importPackages.clear();
		}
	}
	
	private void mergeImportCache(HashSet<String> packages, HashSet<String> classes) {
		JavaScriptTypesFactory typesFactory = provider.getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			((RhinoJavaScriptTypesFactory) typesFactory).mergeImports(packages, classes);
		}
	}
	
	/**
	 * Overridden iterateNode to intercept Token.EXPR_RESULT and check for importPackage and importClass named nodes
	 * If found, then process them and extract the imports and add them to RhinoJavaScriptTypesFactory then return
	 * otherwise call super.iterateNode() 
	 */
	@Override
	protected void iterateNode(AstNode child, Set <Completion>set,
			String entered, CodeBlock block, int offset) {
		
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
	private boolean processImportNode(AstNode child,
			Set<Completion> set, String entered, CodeBlock block,
			int offset) {
		
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
	
	public static  String removePackages(String src)
	{
		if(src.startsWith(PACKAGES))
		{
			String pkg = src.substring(PACKAGES.length());
			if(pkg != null) {
				StringBuilder sb = new StringBuilder();
				//remove any non java characters
				char[] chars = pkg.toCharArray();
                for (char ch : chars) {
                    if (Character.isJavaIdentifierPart(ch) || ch == '.') {
                        sb.append(ch);
                    }
                }
				if(sb.length() > 0) {
					return sb.toString();
				}
			}
		}
		return src;
	}
	
	
	/**
	 * @param src String to extract name
	 * @return import statement from withing the ( and ) 
	 * e.g.  importPackage(java.util)
	 * 		importClass(java.util.HashSet)
	 * 
	 * returns java.util or java.util.HashSet respectively 
	 */
	private String extractNameFromSrc(String src) {
		int startIndex = src.indexOf("(");
		int endIndex = src.indexOf(")");
		if(startIndex != -1 && endIndex != -1) {
			return removePackages(src.substring(startIndex + 1, endIndex));
		}
		return removePackages(src);
	}
	
	/**
	 * Adds package name to RhinoJavaScriptTypesFactory
	 * @param src source text to extract the package
	 */
	private void processImportPackage(String src) {
		String pkg = extractNameFromSrc(src);
		importPackages.add(pkg);
	}
	
	/**
	 * Adds class name to RhinoJavaScriptTypesFactory
	 * @param src source text to extract the class name
	 */
	private void processImportClass(String src) {
		String cls = extractNameFromSrc(src);
		importClasses.add(cls);
	}
	
	
	

}
