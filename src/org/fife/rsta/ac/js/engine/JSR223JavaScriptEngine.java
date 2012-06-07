package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JSR223JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JSR223JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class JSR223JavaScriptEngine implements JavaScriptEngine {

	
public static final String JSR223_ENGINE = "JSR223";
	
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JSR223JavaScriptCompletionResolver(provider);
	}


	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		return new JSR223JavaScriptTypesFactory();
	}


	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, boolean preProcessingMode) {
		return new JavaScriptAstParser(provider, dot, preProcessingMode);
	}
}
