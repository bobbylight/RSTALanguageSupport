package org.fife.rsta.ac.js.ast.parser;

import org.fife.rsta.ac.js.SourceCompletionProvider;


public class JavaScriptAstParserFactory {
	
	
	public static final String RHINO = "RHINO";
	
	public static final JavaScriptAstParser instance(String name, SourceCompletionProvider provider, int dot, boolean preProcessingMode)
	{
		if(RHINO.equals(name)) {
			return new RhinoJavaScriptAstParser(provider, dot, preProcessingMode);
		}
		//default
		return new JavaScriptAstParser(provider, dot, preProcessingMode);
	}
}
