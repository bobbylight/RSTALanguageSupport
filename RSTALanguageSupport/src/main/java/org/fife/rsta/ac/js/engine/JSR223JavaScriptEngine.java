package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JSR223JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptAstParser;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.resolver.JSR223JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class JSR223JavaScriptEngine extends JavaScriptEngine {


public static final String JSR223_ENGINE = "JSR223";

	@Override
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JSR223JavaScriptCompletionResolver(provider);
	}


	@Override
	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {
		if(jsFactory == null)
			jsFactory = new JSR223JavaScriptTypesFactory(provider.getTypesFactory());
		return jsFactory;
	}


	@Override
	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
		return new JavaScriptAstParser(provider, dot, options);
	}
}
