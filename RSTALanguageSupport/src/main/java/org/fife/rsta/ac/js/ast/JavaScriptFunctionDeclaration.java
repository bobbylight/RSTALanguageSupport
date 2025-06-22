package org.fife.rsta.ac.js.ast;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


public class JavaScriptFunctionDeclaration extends JavaScriptDeclaration {

	private TypeDeclaration typeDec;

	private String functionName;


	public JavaScriptFunctionDeclaration(String name, int offset, CodeBlock block, TypeDeclaration typeDec) {
		super(name, offset, block);
		this.typeDec = typeDec;
	}


	public TypeDeclaration getTypeDeclaration() {
		return typeDec;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionName() {
		return functionName;
	}

}
