package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class ECMAJavaScriptEngine extends JavaScriptEngine {

	public static final String ECMA_ENGINE = "ECMA";

	@Override
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JavaScriptCompletionResolver(provider);
	}

	@Override
	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		if(jsFactory == null)
			jsFactory = JavaScriptTypesFactory.getDefaultJavaScriptTypesFactory(provider.getTypesFactory());

		return jsFactory;
	}


	@Override
	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
		return new JavaScriptAstParser(provider, dot, options);
	}

}
