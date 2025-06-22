package org.fife.rsta.ac.js.engine;

import java.util.HashMap;
import java.util.Map;


public final class JavaScriptEngineFactory {


	public static final String DEFAULT = ECMAJavaScriptEngine.ECMA_ENGINE;

	private Map<String, JavaScriptEngine> supportedEngines =
            new HashMap<>();

	private static final JavaScriptEngineFactory INSTANCE = new JavaScriptEngineFactory();

	static {
		instance().addEngine(ECMAJavaScriptEngine.ECMA_ENGINE, new ECMAJavaScriptEngine());
		instance().addEngine(JSR223JavaScriptEngine.JSR223_ENGINE, new JSR223JavaScriptEngine());
		instance().addEngine(RhinoJavaScriptEngine.RHINO_ENGINE, new RhinoJavaScriptEngine());
	}


	private JavaScriptEngineFactory() {
	}


	public static JavaScriptEngineFactory instance() {
		return INSTANCE;
	}


	public JavaScriptEngine getEngineFromCache(String name) {
		if (name == null) {
			name = DEFAULT;
		}
		return supportedEngines.get(name);
	}


	public void addEngine(String name, JavaScriptEngine engine) {
		supportedEngines.put(name, engine);
	}


	public void removeEngine(String name) {
		supportedEngines.remove(name);
	}

}
