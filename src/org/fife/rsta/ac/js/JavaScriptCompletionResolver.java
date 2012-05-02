package org.fife.rsta.ac.js;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.ast.JavaScriptType;
import org.fife.rsta.ac.js.ast.TypeDeclaration;
import org.fife.rsta.ac.js.ast.TypeDeclarationFactory;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;


/**
 * Compiles the entered text using Rhino and tries to resolve the JavaScriptType
 * from the AstRoot e.g var a = ""; "" --> String JavaScriptType var b =
 * a.toString() a.toString --> String JavaScriptType
 * 
 * etc..
 * 
 * Note, will resolve any type added to JavaScriptTypesFactory
 * 
 */
public class JavaScriptCompletionResolver {

	private SourceCompletionProvider provider;

	private JavaScriptType lastJavaScriptType;
	private String lastLookupName = null;


	public JavaScriptCompletionResolver(SourceCompletionProvider provider) {
		this.provider = provider;
	}


	public JavaScriptType compileText(String text) throws IOException {
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

		String parseText = trimString(text);
		Logger.log("Parse Text BEFORE: " + parseText);
		int charIndex = JavaScriptHelper.findIndexOfFirstOpeningBracket(parseText);
		parseText = parseText.substring(charIndex, parseText.length());
		Logger.log("Parse Text: " + parseText);
		env.setRecoverFromErrors(true);
		Parser parser = new Parser(env);
		StringReader r = new StringReader(parseText);
		AstRoot root = parser.parse(r, null, 0);
		CompilerNodeVisitor visitor = new CompilerNodeVisitor(charIndex == 0);
		root.visitAll(visitor);
		return lastJavaScriptType;

	}


	private String trimString(String text) {
		int trim = text.length();
		if (text.lastIndexOf('.') != -1) {
			trim = text.lastIndexOf('.');
		}

		String parseText = text.substring(0, trim);

		return parseText;
	}


	public TypeDeclaration resolveNode(AstNode node) {
		CompilerNodeVisitor visitor = new CompilerNodeVisitor(true);
		node.visit(visitor);
		return lastJavaScriptType != null ? lastJavaScriptType.getType()
				: TypeDeclarationFactory.getDefaultTypeDeclaration();
	}


	// TODO not sure how right this is, but is very tricky problem resolving
	// complex completions

	private class CompilerNodeVisitor implements NodeVisitor {

		private boolean ignoreParams;
		private HashSet paramNodes = new HashSet();
		


		private CompilerNodeVisitor(boolean ignoreParams) {
			this.ignoreParams = ignoreParams;
		}


		public boolean visit(AstNode node) {

			Logger.log(node.toSource());
			Logger.log(node.shortName());
			
			if (ignore(node, ignoreParams))
				return true;

			JavaScriptType jsType = null;
			TypeDeclaration dec = JavaScriptHelper
					.tokenToNativeTypeDeclaration(node, provider);
			if (dec != null) {
				// lookup JavaScript completions type
				jsType = provider.getJavaScriptTypesFactory().getCachedType(
						dec, provider.getJarManager(), provider,
						node.toSource());

				if (jsType != null) {
					lastJavaScriptType = jsType;
					// stop here
					return false;
				}
			}
			else if (lastJavaScriptType != null) {
				if (node.getType() == Token.NAME) {
					// lookup from source name
					jsType = lookupFromName(node, lastJavaScriptType);
					if (jsType == null) {
						// lookup name through the functions of
						// lastJavaScriptType
						jsType = lookupFunctionCompletion(node,
								lastJavaScriptType);
					}
					lastJavaScriptType = jsType;
				}
			}

			return true;
		}


		/**
		 * Test node to check whether to ignore resolving, this is for
		 * parameters
		 * 
		 * @param node node to test
		 * @return true to ignore
		 */
		private boolean ignore(AstNode node, boolean ignoreParams) {
			switch (node.getType()) {
				// ignore errors e.g if statement - if(a. //no closing brace
				case Token.EXPR_VOID:
				case Token.EXPR_RESULT:
					return ((ExpressionStatement) node).getExpression()
							.getType() == Token.ERROR;
				case Token.ERROR:
				case Token.GETPROP:
				case Token.SCRIPT:
					return true;
				default: {
					if (isParameter(node)) {
						collectAllNodes(node); // everything within this node
						// is a parameter
						return ignoreParams;
					}
					break;
				}
			}

			if (JavaScriptHelper.isInfixOnly(node))
				return true;

			return false;
		}


		/**
		 * Get all nodes within AstNode and add to an ArrayList
		 * 
		 * @param node
		 */
		private void collectAllNodes(AstNode node) {
			if (node.getType() == Token.CALL) {
				// collect all argument nodes
				FunctionCall call = (FunctionCall) node;
				for (Iterator args = call.getArguments().iterator(); args
						.hasNext();) {
					AstNode arg = (AstNode) args.next();
					VisitorAll all = new VisitorAll();
					arg.visit(all);
					paramNodes.addAll(all.getAllNodes());
				}
			}
		}


		/**
		 * Check the function that a name may belong to contains this actual
		 * parameter
		 * 
		 * @param node Node to check
		 * @return true if the function contains the parameter
		 */
		private boolean isParameter(AstNode node) {
			if (paramNodes.contains(node))
				return true;
			// get all params from this function too
			FunctionCall fc = JavaScriptHelper.findFunctionCallFromNode(node);
			if (fc != null && !(node == fc)) {
				collectAllNodes(fc);
				if (paramNodes.contains(node)) {
					return true;
				}
			}
			return false;
		}		
	}


	/**
	 * Lookup the name of the node within the last JavaScript type. e.g var a =
	 * 1; var b = a.MAX_VALUE; looks up MAX_VALUE within NumberLiteral a where a
	 * is resolve before as a JavaScript Number;
	 * 
	 * @param node
	 * @param lastJavaScriptType
	 * @return
	 */
	private JavaScriptType lookupFromName(AstNode node,
			JavaScriptType lastJavaScriptType) {
		JavaScriptType javaScriptType = null;
		if (lastJavaScriptType != null) {
			String lookupText = null;
			switch (node.getType()) {
				case Token.NAME:
					lookupText = ((Name) node).getIdentifier();
					break;
			}
			if (lookupText == null) {
				// just try the source
				lookupText = node.toSource();
			}
			javaScriptType = lookupJavaScriptType(lastJavaScriptType,
					lookupText);
		}
		return javaScriptType;
	}


	/**
	 * Lookup the function name of the node within the last JavaScript type. e.g
	 * var a = ""; var b = a.toString(); looks up toString() within
	 * StringLiteral a where a is resolve before as a JavaScript String;
	 * 
	 * @param node
	 * @param lastJavaScriptType
	 * @return
	 */
	private JavaScriptType lookupFunctionCompletion(AstNode node,
			JavaScriptType lastJavaScriptType) {
		JavaScriptType javaScriptType = null;
		if (lastJavaScriptType != null) {

			String lookupText = JavaScriptHelper.getFunctionNameLookup(node);
			javaScriptType = lookupJavaScriptType(lastJavaScriptType,
					lookupText);

		}
		// return last type
		return javaScriptType;
	}


	private JavaScriptType lookupJavaScriptType(
			JavaScriptType lastJavaScriptType, String lookupText) {
		JavaScriptType javaScriptType = null;
		if (lookupText != null && !lookupText.equals(lastLookupName)) {
			// look up JSCompletion
			JSCompletion completion = lastJavaScriptType
					.getCompletion(lookupText);
			if (completion != null) {
				String type = completion.getType(true);
				if (type != null) {
					TypeDeclaration newType = TypeDeclarationFactory.Instance()
							.getTypeDeclaration(type);
					if (newType != null) {
						javaScriptType = provider.getJavaScriptTypesFactory()
								.getCachedType(newType,
										provider.getJarManager(), provider,
										lookupText);
					}
					else {
						javaScriptType = createNewTypeDeclaration(provider,
								type, lookupText);
					}
				}
			}
		}
		lastLookupName = lookupText;
		return javaScriptType;
	}


	private JavaScriptType createNewTypeDeclaration(
			SourceCompletionProvider provider, String type, String text) {
		if (provider.getJavaScriptTypesFactory() != null) {
			ClassFile cf = provider.getJarManager().getClassEntry(type);
			TypeDeclaration newType = null;
			if (cf != null) {
				newType = provider.getJavaScriptTypesFactory()
						.createNewTypeDeclaration(cf);
				return provider.getJavaScriptTypesFactory()
						.getCachedType(newType, provider.getJarManager(),
								provider, text);
			}
		}
		return null;
	}


	private class VisitorAll implements NodeVisitor {

		private ArrayList all = new ArrayList();


		public boolean visit(AstNode node) {
			all.add(node);
			return true;
		}


		public ArrayList getAllNodes() {
			return all;
		}
	}

}
