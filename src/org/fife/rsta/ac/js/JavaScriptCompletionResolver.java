package org.fife.rsta.ac.js;

import java.io.IOException;
import java.io.StringReader;

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
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;


/**
 * Compiles the entered text using Rhino and tries to resolve the JavaScriptType from the AstRoot
 * e.g
 * var a = "";
 * "" --> String JavaScriptType
 * var b = a.toString()
 * a.toString --> String JavaScriptType
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

		int trim = text.lastIndexOf('.') == -1 ? text.length() : text
				.lastIndexOf('.');

		String parseText = text.substring(0, trim);

		env.setRecoverFromErrors(true);
		Parser parser = new Parser(env);
		StringReader r = new StringReader(parseText);
		AstRoot root = parser.parse(r, null, 0);
		// iterateNode(root);
		CompilerNodeVisitor visitor = new CompilerNodeVisitor();
		root.visitAll(visitor);
		return lastJavaScriptType;

	}


	public TypeDeclaration resolveNode(AstNode node) {
		CompilerNodeVisitor visitor = new CompilerNodeVisitor();
		node.visit(visitor);
		return lastJavaScriptType != null ? lastJavaScriptType.getType()
				: TypeDeclarationFactory.getDefaultTypeDeclaration();
	}


	private class CompilerNodeVisitor implements NodeVisitor {

		public boolean visit(AstNode node) {

			Logger.log(node.toSource());
			Logger.log(node.shortName());
			
			if(ignore(node))
				return true;
			
			JavaScriptType jsType = null;
			TypeDeclaration dec = JavaScriptHelper
					.tokenToNativeTypeDeclaration(node, provider);
			if (dec != null) {
				// lookup JavaScript completions type
				jsType = provider.getJavaScriptTypesFactory().getCachedType(
						dec, provider.getJarManager(), provider,
						node.toSource(), node.toSource());

				if (jsType != null) {
					lastJavaScriptType = jsType;
					// stop here
					return false;
				}
			}
			else if (lastJavaScriptType != null) {
				// lookup from source name
				jsType = lookupFromName(node, lastJavaScriptType);
				if (jsType == null) {
					// lookup name through the functions of lastJavaScriptType
					jsType = lookupFunctionCompletion(node, lastJavaScriptType);
				}
			}

			lastJavaScriptType = jsType;

			return true;
		}
		
		/**
		 * Test node to check whether to ignore resolving
		 * @param node node to test
		 * @return true to ignore
		 */
		private boolean ignore(AstNode node)
		{
			switch(node.getType())
			{
				case Token.NAME : return node.getParent() instanceof FunctionCall; //is a parameter of a function
			}
			return false;
		}
	}


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
	 * 
	 * @param node
	 * @param javaScriptType
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
										lookupText, lookupText);
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
								provider, text, text);
			}
		}
		return null;
	}
	

}
