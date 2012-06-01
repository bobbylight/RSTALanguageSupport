package org.fife.rsta.ac.js.ast.jsType;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


public class JSR233JavaScriptTypesFactory extends JavaScriptTypesFactory {

	public JavaScriptType makeJavaScriptType(TypeDeclaration type) {
		return new JSR233Type(type);
	}
}
