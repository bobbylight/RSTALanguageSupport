package org.fife.rsta.ac.js.ast.type.ecma.v5;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.ecma.v3.TypeDeclarationsECMAv3;


public class TypeDeclarationsECMAv5 extends TypeDeclarationsECMAv3 {

	@Override
	protected void loadTypes() {
		//load all v3 types because all these extend them
		super.loadTypes();
		//override main types with v5
		addTypeDeclaration(ECMA_ARRAY, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Array", "Array", false, false));
		addTypeDeclaration(ECMA_DATE, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Date", "Date", false, false));
		addTypeDeclaration(ECMA_FUNCTION, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Function", "Function", false, false));
		addTypeDeclaration(ECMA_OBJECT, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5Object", "Object", false, false));
		addTypeDeclaration(ECMA_STRING, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5String", "String", false, false));
		addTypeDeclaration(ECMA_JSON, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5", "JS5JSON", "JSON", false, false));
		
		addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ArrayFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5ArrayFunctions", "Array", false, false));
		addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5DateFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5DateFunctions", "Date", false, false));
		addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5FunctionFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5FunctionFunctions", "Function", false, false));
		addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5ObjectFunctions", "Object", false, false));
		addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5StringFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.ecma5.functions", "JS5StringFunctions", "String", false, false));
		
	}
	
}
