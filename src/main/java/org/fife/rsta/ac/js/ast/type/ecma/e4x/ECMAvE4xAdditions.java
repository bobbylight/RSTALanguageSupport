package org.fife.rsta.ac.js.ast.type.ecma.e4x;

import org.fife.rsta.ac.js.ast.type.ECMAAdditions;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;


public class ECMAvE4xAdditions implements ECMAAdditions {

	@Override
	public void addAdditionalTypes(TypeDeclarations typeDecs) {
		typeDecs.addTypeDeclaration(TypeDeclarations.ECMA_GLOBAL, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x", "E4XGlobal", "Global", false, false));
		
		typeDecs.addTypeDeclaration(TypeDeclarations.ECMA_NAMESPACE, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x", "E4XNamespace", "Namespace", false, false));
		
		typeDecs.addTypeDeclaration(TypeDeclarations.ECMA_QNAME, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x", "E4XQName", "QName", false, false));
		
		typeDecs.addTypeDeclaration(TypeDeclarations.ECMA_XML, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x", "E4XXML", "XML", false, false));
		
		typeDecs.addTypeDeclaration(TypeDeclarations.ECMA_XMLLIST, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x", "E4XXMLList", "XMLList", false, false));
		
		typeDecs.addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XGlobalFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x.functions", "E4XGlobalFunctions", "Global", false, false));
		
		typeDecs.addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x.functions", "E4XXMLFunctions", "XML", false, false));
		
		typeDecs.addTypeDeclaration("org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLListFunctions", new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api.e4x.functions", "E4XXMLListFunctions", "XMList", false, false));
		
		//xml lookups for constructors and class completions
		typeDecs.addECMAObject(TypeDeclarations.ECMA_NAMESPACE, true);
		typeDecs.addECMAObject(TypeDeclarations.ECMA_QNAME, true);
		typeDecs.addECMAObject(TypeDeclarations.ECMA_XML, true);
		typeDecs.addECMAObject(TypeDeclarations.ECMA_XMLLIST, true);
	}
	
}
