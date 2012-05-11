package org.fife.rsta.ac.js.resolver;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.TypeDeclaration;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;


public class RhinoJavaScriptCompletionResolver extends
		JavaScriptCompletionResolver {

	/**
	 * RhinoCompletionProvider constructor
	 * - resolves Rhino specific types
	 * Used to resolve Static class e.g java.lag.String methods and fields 
	 * @param provider
	 */
	public RhinoJavaScriptCompletionResolver(SourceCompletionProvider provider) {
		super(provider);
	}


	/**
	 * Try to resolve standard JavaScript type. If null, then look for static class
	 */
	protected TypeDeclaration resolveNativeType(AstNode node) {
		TypeDeclaration dec = super.resolveNativeType(node);
		if (dec == null) {
			dec = testJavaStaticType(node);
		}

		return dec;
	}


	/**
	 * Test whether the node can be resolved as a static Java class.
	 * Only looks for Token.NAME nodes to test
	 * @param node node to test
	 * @return
	 */
	private TypeDeclaration testJavaStaticType(AstNode node) {
		switch (node.getType()) {
			case Token.NAME:
				return findJavaStaticType(node);
		}
		return null;
	}


	/**
	 * Try to resolve the Token.NAME AstNode and return a TypeDeclaration
	 * @param node node to resolve
	 * @return TypeDeclaration if the name can be resolved as a Java Class else null
	 */
	private TypeDeclaration findJavaStaticType(AstNode node) {
		// check parent is of type property get
		String testName = null;
		if (node.getParent() != null
				&& node.getParent().getType() == Token.GETPROP) { // ast
																	// parser
			String name = node.toSource();
			String longName = node.getParent().toSource();

			// trim the text to the short name
			int index = longName.lastIndexOf(name);
			if (index > -1) {
				testName = longName.substring(0, index + name.length());
			}
		}
		else if (node.getParent() != null
				&& node.getParent().getType() == Token.EXPR_RESULT) { // compile
																		// text
			testName = node.toSource();
		}
		else {
			testName = node.toSource();
		}

		if (testName != null) {
			TypeDeclaration dec = JavaScriptHelper
					.createNewTypeDeclaration(testName);
			ClassFile cf = provider.getJavaScriptTypesFactory().getClassFile(
					provider.getJarManager(), dec);
			if (cf != null) {
				TypeDeclaration returnDec = provider.getJavaScriptTypesFactory()
						.createNewTypeDeclaration(cf, true);
				return returnDec;
			}
		}
		return null;
	}

}
