package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JSR233JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JSR233JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class JSR233JavaScriptEngine implements JavaScriptEngine {

	
public static final String JSR233_ENGINE = "JSR233";
	
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JSR233JavaScriptCompletionResolver(provider);
	}


	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		return new JSR233JavaScriptTypesFactory();
	}


	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, boolean preProcessingMode) {
		return new JavaScriptAstParser(provider, dot, preProcessingMode);
	}
}
