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
package org.fife.rsta.ac.js.ast;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.WhileLoop;


public class VariableResolver {

	// HashMap of local variables mapped Name --> JSVariableDeclaration
	private HashMap localVariables = new HashMap();
	// HashMap of system variables mapped Name --> JSVariableDeclaration
	// system variables do not get cleared as they are always available to the
	// system
	private HashMap systemVariables = new HashMap();

	private SourceCompletionProvider provider;


	public VariableResolver(SourceCompletionProvider provider) {
		this.provider = provider;
	}


	public void addLocalVariable(JSVariableDeclaration declaration) {
		localVariables.put(declaration.getName(), declaration);
	}


	public void addSystemVariable(JSVariableDeclaration declaration) {
		systemVariables.put(declaration.getName(), declaration);
	}


	public void removeSystemVariable(String name) {
		systemVariables.remove(name);
	}


	/**
	 * Find JSVariableDeclaration and check is in scope of caret position
	 * 
	 * @param name
	 * @param dot
	 * @return JSVariableDeclaration from the name
	 */
	public JSVariableDeclaration findDeclaration(String name, int dot) {
		JSVariableDeclaration findDeclaration = findDeclaration(localVariables,
				name, dot);
		return findDeclaration == null ? findDeclaration(systemVariables, name,
				dot) : findDeclaration;
	}


	/**
	 * Find JSVariableDeclaration and check is in scope of caret position
	 * 
	 * @param name
	 * @param dot
	 * @return JSVariableDeclaration from the name
	 */
	public JSVariableDeclaration findDeclaration(HashMap variables,
			String name, int dot) {
		JSVariableDeclaration dec = (JSVariableDeclaration) variables.get(name);
		if (dec != null) {
			int decOffs = dec.getOffset();
			if (dot <= decOffs) {
				return dec;
			}
		}
		return null;
	}


	public TypeDeclaration getTypeDeclarationForVariable(String name, int dot) {
		JSVariableDeclaration dec = findDeclaration(name, dot);
		return dec != null ? dec.getTypeDeclaration() : null;
	}


	/**
	 * Clear all variables
	 */
	public void reset() {
		localVariables.clear();
	}


	/**
	 * Resolve the entered text by chopping up the text and working from left to
	 * right, resolving each type in turn
	 * 
	 * @param entered
	 * @param provider
	 * @param dot
	 * @return
	 */
	public TypeDeclaration resolveType(String entered,
			SourceCompletionProvider provider, int dot) {
		String[] enteredSplit = entered.split("\\.");

		// check whether entered is a function
		TypeDeclaration variableType = lookupVariable(enteredSplit[0], dot);

		if (variableType == null) {
			// else parse the content and try to parse content on anything but
			// Name Tokens. Name Tokens are for variable lookup and
			// this has been done already
			AstNode node = compileNode(enteredSplit[0]);
			if (node != null) {
				JSVariableDeclaration dec = null;
				switch (node.getType()) {
					case Token.NAME: {
						variableType = lookupVariable(((Name) node)
								.getIdentifier(), dot);

						if (variableType != null) // now resolve the variable
						{
							// resolve type from original type, may need to
							// drill down
							// e.g var a = 1; var b = a.toString(); //b resolves
							// to String
							variableType = resolveTypeForFunction(variableType,
									enteredSplit, provider, dot, entered);
						}
						break;
					}
					case Token.CALL: {
						FunctionCall call = (FunctionCall)node;
						List args = call.getArguments();
						AstNode arg = null;
						if(args.size() > 0)
						{
							arg = (AstNode) args.get(countParamsFromString(enteredSplit[0]));
						}
						if(arg != null)
						{
							dec = makeRootJSVariableDeclaration(arg);
							variableType = provider.resolveTypeDeclation(dec
								.getTypeNode().toSource());
						}
						break;
					}
					case Token.GETPROP:
						// get first part of String ... and work our way through
						// to the right
						dec = makeRootJSVariableDeclaration(node);
						variableType = provider.resolveTypeDeclation(dec
								.getTypeNode().toSource());
						break;

					default: {
						// make JSTypeDeclaration from node
						// check whether function delimiter (.)
						dec = makeRootJSVariableDeclaration(node);
						if (enteredSplit.length > 1) {
							variableType = resolveTypeForFunction(
									JSVariableDeclaration
											.tokenToTypeDeclaration(dec
													.getTypeNode(), provider),
									enteredSplit, provider, dot, entered);
						}
						else {
							// try to resolve type
							variableType = resolveTypeForFunction(
									JSVariableDeclaration
											.tokenToTypeDeclaration(dec
													.getTypeNode(), provider),
									enteredSplit, provider, dot, entered);
						}
					}
				}
			}
		}
		else {
			// resolve type from original type, may need to drill down
			// e.g var a = 1; var b = a.toString(); //b resolves to String
			variableType = resolveTypeForFunction(variableType, enteredSplit,
					provider, dot, entered);
		}

		return variableType;
	}


	private int countParamsFromString(String text)
	{
		return text.split(",").length -1;
	}
	
	private TypeDeclaration lookupVariable(String name, int dot) {
		String[] split = name.split("\\.");
		TypeDeclaration variableType = getTypeDeclarationForVariable(name, dot);
		if (variableType != null) {
			variableType = resolveTypeForFunction(variableType, split,
					provider, dot, name);
		}
		return variableType;
	}


	/**
	 * Resolve type for function (AstNode).toSource()
	 * 
	 * @param typeDec
	 * @param enteredSplit
	 * @param provider
	 * @param dot
	 * @return
	 */
	private TypeDeclaration resolveTypeForFunction(TypeDeclaration typeDec,
			String[] enteredSplit, SourceCompletionProvider provider, int dot, String originalEnteredText) {

		TypeDeclaration resolved = null;
		if (provider.getJavaScriptTypesFactory() != null) {
			JavaScriptType cachedType = provider.getJavaScriptTypesFactory()
					.getCachedType(typeDec, provider.getJarManager(), provider, enteredSplit[0], originalEnteredText);
			if (cachedType != null) {
				resolved = resolveType(cachedType, enteredSplit, 0, provider, originalEnteredText);
			}
		}

		return resolved;
	}


	/**
	 * Add all completions to CachedType
	 * 
	 * @param cachedType
	 * @param enteredText
	 * @param index
	 * @param provider
	 * @return
	 */
	private TypeDeclaration resolveType(JavaScriptType cachedType,
			String[] enteredText, int index, SourceCompletionProvider provider, String originalEnteredText) {

		if (cachedType == null)
			return null;

		int newIndex = index + 1;
		if (newIndex == enteredText.length) {
			// reached the end of the road
			return cachedType.getType();
		}
		else {
			// get next string
			String text = enteredText[newIndex];
			text = JavaScriptHelper.convertTextToLookup(text);

			// look up JSCompletion
			JSCompletion completion = cachedType.getCompletion(text);
			if (completion != null) {
				String type = completion.getType(true);
				if (type != null) {
					TypeDeclaration newType = TypeDeclarationFactory.Instance()
							.getTypeDeclaration(type);
					if (newType != null) {
						return lookupType(newType, provider, enteredText, newIndex, originalEnteredText);
					}
					else {
						return createNewTypeDeclaration(provider, type,
								enteredText, newIndex, originalEnteredText);
					}
				}
			}
		}
		return null;
	}


	private TypeDeclaration createNewTypeDeclaration(
			SourceCompletionProvider provider, String type,
			String[] enteredText, int newIndex, String originalEnteredText) {
		if (provider.getJavaScriptTypesFactory() != null) {
			ClassFile cf = provider.getJarManager().getClassEntry(type);
			if (cf != null) {
				TypeDeclaration newType = provider.getJavaScriptTypesFactory()
						.createNewTypeDeclaration(cf);
				if (newType != null) {
					return lookupType(newType, provider, enteredText, newIndex, originalEnteredText);
				}
			}
		}
		return null;
	}


	private TypeDeclaration lookupType(TypeDeclaration type,
			SourceCompletionProvider provider, String[] enteredText, int index, String originalEnteredText) {
		if (provider.getJavaScriptTypesFactory() != null) {
			JavaScriptType newCachedType = provider.getJavaScriptTypesFactory()
					.getCachedType(type, provider.getJarManager(), provider, enteredText[index], originalEnteredText);
			return resolveType(newCachedType, enteredText, index, provider, originalEnteredText);
		}
		return null;
	}


	/**
	 * Parse Text with Java Parser and return AstNode from the expression etc..
	 * 
	 * @param text
	 * @return
	 */
	private AstNode compileNode(String text) {
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
		try {
			AstRoot root = parser.parse(r, null, 0);
			AstNode child = (AstNode) root.getFirstChild();
			switch (child.getType()) {
				case Token.EXPR_VOID:
				case Token.EXPR_RESULT:
					return ((ExpressionStatement) child).getExpression();
				case Token.SWITCH:
					return ((SwitchStatement) child).getExpression();
				case Token.IF:
					return ((IfStatement) child).getCondition();
				case Token.WHILE:
					return ((WhileLoop) child).getCondition();
				case Token.FOR:
					return ((ForLoop) child).getInitializer();
					// TODO return other types
				default:
					return child;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Make a dummy JSVariableDeclaration
	 * 
	 * @param node
	 * @return
	 */
	private JSVariableDeclaration makeRootJSVariableDeclaration(AstNode node) {
		JSVariableDeclaration dec = new JSVariableDeclaration("root", 0,
				provider);
		dec.setTypeNode(node);
		return dec;
	}

}
