package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.parser.RhinoJavaScriptAstParser;
import org.fife.rsta.ac.js.resolver.JSR223JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class RhinoJavaScriptEngine implements JavaScriptEngine {

	public static final String RHINO_ENGINE = "RHINO";
	
	private JavaScriptTypesFactory jsFactory;
	
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JSR223JavaScriptCompletionResolver(provider);
	}


	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		
		if(jsFactory == null)
			jsFactory = new RhinoJavaScriptTypesFactory();
		
		return jsFactory;
	}


	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, boolean preProcessingMode) {
		return new RhinoJavaScriptAstParser(provider, dot, preProcessingMode);
	}

}
