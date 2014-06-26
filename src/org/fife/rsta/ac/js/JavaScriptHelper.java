/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import java.io.StringReader;

import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;
import org.fife.rsta.ac.js.resolver.JavaScriptCompletionResolver;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;


public class JavaScriptHelper {

	private static final String INFIX = org.mozilla.javascript.ast.InfixExpression.class
			.getName();

	
	/**
	 * Test whether the start of the variable is the same name as the variable
	 * being initialised. This is not possible.
	 * 
	 * @param target Name of variable being created
	 * @param initialiser name of initialiser
	 * @return true if name is different
	 */
	public static boolean canResolveVariable(AstNode target, AstNode initialiser) {
		String varName = target.toSource();
		try {
			String init = initialiser.toSource();
			String[] splitInit = init.split("\\.");
			if (splitInit.length > 0) {
				return !varName.equals(splitInit[0]);
			}
		} 
		catch(Exception e) {
			//AstNode can throw exceptions if toSource() is invalid e.g new Date(""..toString());
		}
		return false;
	}


	/**
	 * Parse Text with JavaScript Parser and return AstNode from the expression
	 * etc..
	 * 
	 * @param text to parse
	 * @return expression statement text from source
	 */
	public static final ParseText parseEnteredText(String text) {
		CompilerEnvirons env = new CompilerEnvirons();
		env.setIdeMode(true);
		env.setErrorReporter(new ErrorReporter() {

			public void error(String message, String sourceName, int line,
					String lineSource, int lineOffset) {
			}


			public EvaluatorException runtimeError(String message,
					String sourceName, int line, String lineSource,
					int lineOffset) {
				return null;
			}


			public void warning(String message, String sourceName, int line,
					String lineSource, int lineOffset) {

			}
		});
		env.setRecoverFromErrors(true);
		Parser parser = new Parser(env);
		StringReader r = new StringReader(text);
		ParseText pt = new ParseText();
		try {
			AstRoot root = parser.parse(r, null, 0);
			ParseTextVisitor visitor = new ParseTextVisitor(text);
			root.visitAll(visitor);
			pt.isNew = visitor.isNew();
			pt.text = visitor.getLastNodeSource();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pt;
	}


	/**
	 * @param node AstNode to look for function
	 * @return function lookup name from it's AstNode. i.e concat function name
	 *         and parameters. If no function is found, then return null
	 */
	public static String getFunctionNameLookup(AstNode node, SourceCompletionProvider provider) {
		FunctionCall call = findFunctionCallFromNode(node);
		return provider.getJavaScriptEngine().getJavaScriptResolver(provider).getFunctionNameLookup(call, provider);
	}


	/**
	 * Iterate back up through parent nodes and check whether inside a function
	 * 
	 * If the node is a function, then the Parsed parent node structure is:
	 * FunctionCall
	 *   --> PropertyGet
	 *    --> Name
	 * 
	 * Anything other structure should be rejected.
	 * 
	 * @param node
	 * @return
	 */
	public static FunctionCall findFunctionCallFromNode(AstNode node) {
		AstNode parent = node;
		for(int i=0; i<3; i++)
		{
			if(parent == null || (parent instanceof AstRoot))
				break;
			if (parent instanceof FunctionCall) {
				return (FunctionCall) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}


	/**
	 * Convert AstNode to TypeDeclaration
	 * 
	 * @param typeNode AstNode to convert
	 * @param provider SourceProvider
	 * @return TypeDeclaration if node resolves to supported type, e.g Number,
	 *         New etc.., otherwise null
	 */
	public static final TypeDeclaration tokenToNativeTypeDeclaration(
			AstNode typeNode, SourceCompletionProvider provider) {
		if (typeNode != null) {
			switch (typeNode.getType()) {
case Token.EXPR_RESULT:
	AstNode expr = ((org.mozilla.javascript.ast.ExpressionStatement)typeNode).getExpression();
	if (expr.getType()==Token.NAME) {
		return provider.resolveTypeDeclation(((Name) expr)
				.getIdentifier());
	}
	break;
				case Token.CATCH:
					return getTypeDeclaration(TypeDeclarations.ECMA_ERROR, provider);
				case Token.NAME:
					return provider.resolveTypeDeclation(((Name) typeNode)
							.getIdentifier());
				case Token.NEW:
					return processNewNode(typeNode, provider);
				case Token.NUMBER:
					return getTypeDeclaration(TypeDeclarations.ECMA_NUMBER, provider);
				case Token.OBJECTLIT:
					return getTypeDeclaration(TypeDeclarations.ECMA_OBJECT, provider);
				case Token.STRING:
					return getTypeDeclaration(TypeDeclarations.ECMA_STRING, provider);
				case Token.TRUE:
				case Token.FALSE:
					return getTypeDeclaration(TypeDeclarations.ECMA_BOOLEAN, provider);
				case Token.ARRAYLIT: 
					return createArrayType(typeNode, provider);
				case Token.GETELEM: {
					TypeDeclaration dec = findGetElementType(typeNode, provider);
					if(dec != null) {
						return dec;
					}
					break;
				}
				case Token.THIS : {
					//ask the provider for the base object
					String self = provider.getSelf();
					if(self == null)
						self = TypeDeclarations.ECMA_GLOBAL;
					
					return getTypeDeclaration(self, provider);
				}
				//xml support
				case Token.XML : {
					if(provider.isXMLSupported())
					{
						return getTypeDeclaration(TypeDeclarations.ECMA_XML, provider);
					}
				}

			}

			if (isInfixOnly(typeNode)) {
				TypeDeclaration dec = getTypeFromInFixExpression(typeNode, provider);
				if (dec != null) {
					return dec;
				}
			}
		}
		return null;

	}
	
	/**
	 * Check the Get Element and extract the Array type from the variable
	 * e.g
	 * var a = [1, 2, 3];
	 * var b = a[1]; //b resolves to Number 
	 * @param node
	 * @param provider
	 * @return
	 */
	private static TypeDeclaration findGetElementType(AstNode node, SourceCompletionProvider provider)
	{
		ElementGet getElement = (ElementGet) node;
		//get target
		AstNode target = getElement.getTarget();
		if(target != null) {
			JavaScriptCompletionResolver resolver = new JavaScriptCompletionResolver(provider);
			TypeDeclaration typeDec = resolver.resolveNode(target);
			if(typeDec != null) {
				if(typeDec instanceof ArrayTypeDeclaration) {
					return ((ArrayTypeDeclaration) typeDec).getArrayType();
				}
			}
		}
		return  null;
	}
	
	/**
	 * Create array type from AstNode and try to determine the array type
	 * @param typeNode
	 * @param provider
	 * @return
	 */
	private static TypeDeclaration createArrayType(AstNode typeNode, SourceCompletionProvider provider)
	{
		TypeDeclaration array = getTypeDeclaration(TypeDeclarations.ECMA_ARRAY, provider);
		if(array != null) {
			//create a new ArrayTypeDeclaration
			ArrayTypeDeclaration arrayDec = new ArrayTypeDeclaration(array.getPackageName(), array.getAPITypeName(), array.getJSName());
			ArrayLiteral arrayLit = (ArrayLiteral) typeNode;
			//iterate through array and resolve the underlying types
			arrayDec.setArrayType(findArrayType(arrayLit, provider));
			return arrayDec;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Find the array type from ArrayLiteral. Iterates through elements and checks all the types are the same
	 * @param arrayLit
	 * @param provider
	 * @return TypeDeclaration if all elements are of the same type else TypeDeclarationFactory.getDefaultTypeDeclaration();
	 */
	private static TypeDeclaration findArrayType(ArrayLiteral arrayLit, SourceCompletionProvider provider) {
		TypeDeclaration dec = null;
		boolean first = true;
		JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
		for (AstNode element : arrayLit.getElements()) {
			TypeDeclaration elementType = resolver.resolveNode(element);
			if(first) {
				dec = elementType;
				first = false;
			}
			else {
				if(elementType != null && !elementType.equals(dec)) {
					dec = provider.getTypesFactory().getDefaultTypeDeclaration();
					break;
				}
			}
		}
		return dec != null ? dec : provider.getTypesFactory().getDefaultTypeDeclaration();
	}


	private static TypeDeclaration processNewNode(AstNode typeNode, SourceCompletionProvider provider) {
		String newName = findNewExpressionString(typeNode);
		if (newName != null) {
			
			TypeDeclaration newType = createNewTypeDeclaration(newName);
			if(newType.isQualified()) {
				return newType;
			} else {
				return findOrMakeTypeDeclaration(newName, provider);
			}
		}
		return null;
	}
	
	
	public static TypeDeclaration findOrMakeTypeDeclaration(String name, SourceCompletionProvider provider)
	{
		TypeDeclaration newType = getTypeDeclaration(name, provider);
		if (newType == null) {
			newType = createNewTypeDeclaration(name);
		}
		return newType;
	}
	
	public static TypeDeclaration createNewTypeDeclaration(String newName)
	{
		// create a new Type
		String pName = newName.indexOf('.') > 0 ? newName.substring(0,
				newName.lastIndexOf('.')) : "";
		String cName = newName.indexOf('.') > 0 ? newName.substring(
				newName.lastIndexOf('.') + 1, newName.length())
				: newName;
		return new TypeDeclaration(pName, cName, newName);
	}


	public static boolean isInfixOnly(AstNode typeNode) {
		return typeNode instanceof InfixExpression
				&& typeNode.getClass().getName().equals(INFIX);
	}


	/**
	 * Visitor for infix expression to work out whether the variable should be a
	 * string number literal Only works by determining the presence of
	 * StringLiterals and NumberLiterals. StringLiteral will override type to
	 * evaluate to String.
	 * 
	 * TODO most probably need some work on this
	 */
	private static class InfixVisitor implements NodeVisitor {

		private String type = null;
		private SourceCompletionProvider provider;
		
		private InfixVisitor(SourceCompletionProvider provider) {
			this.provider = provider;
		}
		
		public boolean visit(AstNode node) {

			if (!(node instanceof InfixExpression)) // ignore infix expression
			{
				JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
				TypeDeclaration dec = resolver.resolveNode(node);
				
				boolean isNumber = TypeDeclarations.ECMA_NUMBER.equals(dec.getAPITypeName()) || TypeDeclarations.ECMA_BOOLEAN.equals(dec.getAPITypeName());
				if(isNumber && (type == null || (isNumber && TypeDeclarations.ECMA_NUMBER.equals(type)))) {
					type = TypeDeclarations.ECMA_NUMBER;
				}
				else {
					type = TypeDeclarations.ECMA_STRING;
				}
					
			}

			return true;
		}
	}


	/**
	 * Use a visitor to visit all the nodes to work out which type to return e.g
	 * 1 + 1 returns Number 1 + "" returns String true returns Boolean etc..
	 * 
	 * @param node
	 * @return
	 */
	private static TypeDeclaration getTypeFromInFixExpression(AstNode node, SourceCompletionProvider provider) {
		InfixExpression infix = (InfixExpression) node;
		switch(infix.getType())
		{
			case Token.ADD:
			case Token.SUB:
			case Token.MOD:
			case Token.MUL:
			case Token.DIV:
			{
				InfixVisitor visitor = new InfixVisitor(provider);
				infix.visit(visitor);
				return getTypeDeclaration(visitor.type, provider);
			}
		}
		//else
		AstNode rightExp = infix.getRight();
		JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
		TypeDeclaration dec = resolver.resolveNode(rightExp);
		return dec;
	}
	
	
	public static String convertNodeToSource(AstNode node)
	{
		try
		{
			return node.toSource();
		}
		catch(Exception e)
		{
			Logger.log(e.getMessage());
		}
		return null;
	}


	/**
	 * 
	 * Returns the index of the first ( working backwards if there is no
	 * matching closing bracket
	 * 
	 * @param text
	 */
	public static int findIndexOfFirstOpeningBracket(String text) {
		int index = 0;
		if (text != null && text.length() > 0) {
			char[] chars = text.toCharArray();
			for (int i = chars.length - 1; i >= 0; i--) {
				switch (chars[i]) {
					case '(':
						index--;
						break;
					case ')':
						index++;
						break;
				}
				if (index == -1)
					return i + 1; // index + 1 to remove the last (
			}
		}
		else {
			return 0;
		}
		return 0;
	}
	
	public static int findIndexOfFirstOpeningSquareBracket(String text) {
		int index = 0;
		if (text != null && text.length() > 0) {
			char[] chars = text.toCharArray();
			for (int i = chars.length - 1; i >= 0; i--) {
				switch (chars[i]) {
					case '[':
						index--;
						break;
					case ']':
						index++;
						break;
				}
				if (index == -1)
					return i + 1; // index + 1 to remove the last (
			}
		}
		else {
			return 0;
		}
		return 0;
	}
	
	
	/**
	 * Returns the node name from 'Token.NEW' AstNode e.g new Object --> Object
	 * new Date --> Date etc..
	 * 
	 * @param node NewExpression node
	 * @return Extracts the Name identifier from NewExpression
	 */
	private static String findNewExpressionString(AstNode node) {
		NewExpression newEx = (NewExpression) node;
		AstNode target = newEx.getTarget();
		String source = target.toSource();
		int index = source.indexOf('(');
		if (index != -1) {
			source = source.substring(0, index);
		}
		return source;
	}


	/**
	 * Convenience method to lookup TypeDeclaration through the
	 * TypeDeclarationFactory.
	 * 
	 * @param name
	 * @return
	 */
	public static TypeDeclaration getTypeDeclaration(String name, SourceCompletionProvider provider) {
		return provider.getTypesFactory().getTypeDeclaration(name);
	}


	public static int findLastIndexOfJavaScriptIdentifier(String input) {
		int index = -1;
		if (input != null) {
			char c[] = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (!Character.isJavaIdentifierPart(c[i])) {
					index = i;
				}
			}
		}
		return index;
	}
	
	/**
	 * 
	 * @param text to trim
	 * @return text up to the last dot e.g a.getText().length returns a.getText()
	 */
	public static String removeLastDotFromText(String text) {
		int trim = text.length();
		if (text.lastIndexOf('.') != -1) {
			trim = text.lastIndexOf('.');
		}

		String parseText = text.substring(0, trim);

		return parseText;
	}
	
	/**
	 * Trims the text from the last , from the string
	 * Looks for ( or [ starting at the end of the string to find out where in the string to substring.
	 * Do not need to trim off if inside either () or [].
	 * e.g
	 * 1, "".charAt(position).indexOf(2, "")
	 * 
	 * String should be trimmed at the 1, not the 2,
	 * 
	 * @param text
	 * @return
	 */
	public static String trimFromLastParam(String text) {
		int trim = 0;
		if (text.lastIndexOf(',') != -1) {
			int i1 = 0;
			int i2 = 0;
			char[] chars = text.toCharArray();
			for (int i = chars.length - 1; i >= 0; i--) {
				switch (chars[i]) {
					case '(':
						i1--;
						break;
					case '[':
						i2--;
						break;
					case ')':
						i1++;
						break;
					case ']':
						i2++;
						break;
					case ',': {
						if(i1 == 0 && i2 ==0) {
							return text.substring(i+1, text.length()).trim();
						}
						break;
					}
				}
			}
			//not trimmed yet, so find last index and trim there
			trim = text.lastIndexOf(',') + 1;
		}
		//all else fails, trim 
		String parseText = text.substring(trim, text.length());

		return parseText.trim();
	}
	
	
	private static class ParseTextVisitor implements NodeVisitor {

		private AstNode lastNode;
		private String text;
		private boolean isNew;
		
		private ParseTextVisitor(String text) {
			this.text = text;
		}
		

		public boolean visit(AstNode node) {
			switch (node.getType()) {
				case Token.NAME:
				case Token.STRING:
				case Token.NUMBER:
				case Token.OBJECTLIT:
				case Token.ARRAYLIT:
				case Token.TRUE:
				case Token.FALSE:
					lastNode = node;
					break;
				case Token.NEW:
					isNew = true;
					break;
			}
			return true;
		}


		public String getLastNodeSource() {
			return lastNode != null ? lastNode.toSource() : isNew ? "" : text;
		}
		
		public boolean isNew()
		{
			return isNew;
		}

	}
	
	public static class ParseText
	{
		public String text = "";
		public boolean isNew;
		
	}

}
