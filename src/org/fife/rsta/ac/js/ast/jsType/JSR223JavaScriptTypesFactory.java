package org.fife.rsta.ac.js.ast.jsType;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


public class JSR223JavaScriptTypesFactory extends JavaScriptTypesFactory {

	public JavaScriptType makeJavaScriptType(TypeDeclaration type) {
		return new JSR223Type(type);
	}
}
