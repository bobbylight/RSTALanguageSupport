package org.fife.rsta.ac.js.engine;

import java.util.HashMap;


public class JavaScriptEngineFactory {

	
	public static final String DEFAULT = JSR223JavaScriptEngine.JSR223_ENGINE;
	
	private HashMap supportedEngines = new HashMap();

	private static JavaScriptEngineFactory Instance = new JavaScriptEngineFactory();

	static {
		Instance().addEngine(EMCAJavaScriptEngine.EMCA_ENGINE, new EMCAJavaScriptEngine());
		Instance().addEngine(JSR223JavaScriptEngine.JSR223_ENGINE, new JSR223JavaScriptEngine());
		Instance().addEngine(RhinoJavaScriptEngine.RHINO_ENGINE, new RhinoJavaScriptEngine());
	}


	private JavaScriptEngineFactory() {
	}


	public static JavaScriptEngineFactory Instance() {
		return Instance;
	}


	public JavaScriptEngine getEngineFromCache(String name) {
		if(name == null) {
			name = DEFAULT;
		}
		return (JavaScriptEngine) supportedEngines.get(name);
	}


	public void addEngine(String name, JavaScriptEngine engine) {
		supportedEngines.put(name, engine);
	}


	public void removeEngine(String name) {
		supportedEngines.remove(name);
	}

}
