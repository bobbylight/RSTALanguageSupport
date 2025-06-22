package org.fife.rsta.ac.js.engine;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.jsType.RhinoJavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.parser.RhinoJavaScriptAstParser;
import org.fife.rsta.ac.js.resolver.JSR223JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;


public class RhinoJavaScriptEngine extends JavaScriptEngine {

	public static final String RHINO_ENGINE = "RHINO";

	@Override
	public JavaScriptResolver getJavaScriptResolver(SourceCompletionProvider provider) {
		return new JSR223JavaScriptCompletionResolver(provider);
	}


	@Override
	public JavaScriptTypesFactory getJavaScriptTypesFactory(SourceCompletionProvider provider) {

		if (jsFactory == null)
			jsFactory = new RhinoJavaScriptTypesFactory(provider.getTypesFactory());

		return jsFactory;
	}


	@Override
	public JavaScriptParser getParser(SourceCompletionProvider provider, int dot, TypeDeclarationOptions options) {
		return new RhinoJavaScriptAstParser(provider, dot, options);
	}

}
