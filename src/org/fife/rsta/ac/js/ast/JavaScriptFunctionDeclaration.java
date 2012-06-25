package org.fife.rsta.ac.js.ast;

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


public class JavaScriptFunctionDeclaration {
	
	private String name;
	private int offset;
	private TypeDeclaration typeDec;
	private CodeBlock block;
	
	public JavaScriptFunctionDeclaration(String name, int offset, CodeBlock block, TypeDeclaration typeDec) {
		this.name = name;
		this.offset = offset;
		this.block = block;
		this.typeDec = typeDec;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public TypeDeclaration getTypeDeclaration()
	{
		return typeDec;
	}
	
	public CodeBlock getCodeBlock()
	{
		return block;	
	}
}
