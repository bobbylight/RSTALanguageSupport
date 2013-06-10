package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class EMCAJavaScriptEngine implements JavaScriptEngine {

	public static final String EMCA_ENGINE = "EMCA";
	
	private JavaScriptTypesFactory jsFactory;
	
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JavaScriptCompletionResolver(provider);
	}

	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		if(jsFactory == null)
			jsFactory = JavaScriptTypesFactory.getDefaultJavaScriptTypesFactory();
		
		return jsFactory;
	}


	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, boolean preProcessingMode) {
		return new JavaScriptAstParser(provider, dot, preProcessingMode);
	}

}
