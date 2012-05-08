package org.fife.rsta.ac.js.ast.parser;

import java.util.Set;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


public class RhinoJavaScriptAstParser extends JavaScriptAstParser {

	public RhinoJavaScriptAstParser(SourceCompletionProvider provider, int dot,
			boolean preProcessingMode) {
		super(provider, dot, preProcessingMode);
		clearImportCache(provider);
	}

	private void clearImportCache(SourceCompletionProvider provider)
	{
		JavaScriptTypesFactory typesFactory = provider.getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			((RhinoJavaScriptTypesFactory) typesFactory).clearImports();
		}
	}
	
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
	
	private boolean processImportNode(AstNode child, Set set, String entered,
			CodeBlock block, int offset) {
		
		String src = child.toSource();
		if(src.startsWith("importPackage")) {
			processImportPackage(src);
			return true;
		}
		else if(src.startsWith("importClass")) {
			processImportClass(src);
			return true;
		}
		
		
		return false;
	}
	
	private String extractNameFromSrc(String src)
	{
		int startIndex = src.indexOf("(");
		int endIndex = src.indexOf(")");
		if(startIndex != -1 && endIndex != -1) {
			return src.substring(startIndex + 1, endIndex);
		}
		return src;
	}
	
	private void processImportPackage(String src) {
		JavaScriptTypesFactory typesFactory = getProvider().getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			String pkg = extractNameFromSrc(src);
			RhinoJavaScriptTypesFactory rf = (RhinoJavaScriptTypesFactory) typesFactory;
			rf.addImportPackage(pkg);
		}
	}
	
	private void processImportClass(String src) {
		JavaScriptTypesFactory typesFactory = getProvider().getJavaScriptTypesFactory();
		if(typesFactory instanceof RhinoJavaScriptTypesFactory) {
			String cls = extractNameFromSrc(src);
			RhinoJavaScriptTypesFactory rf = (RhinoJavaScriptTypesFactory) typesFactory;
			rf.addImportClass(cls);
		}
	}
	
	
	

}
