/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.ast.jsType;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;


public class JSR223JavaScriptTypesFactory extends JavaScriptTypesFactory {

	public JSR223JavaScriptTypesFactory(TypeDeclarationFactory typesFactory) {
		super(typesFactory);
	}

	@Override
	public JavaScriptType makeJavaScriptType(TypeDeclaration type) {
		return new JSR223Type(type);
	}
}
