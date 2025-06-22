package org.fife.rsta.ac.js.resolver;

import java.io.IOException;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSMethodData;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;


public class JSR223JavaScriptCompletionResolver extends
		JavaScriptCompletionResolver {

	/**
	 * RhinoCompletionProvider constructor
	 * - resolves Rhino specific types
	 * Used to resolve Static class e.g. java.lag.String methods and fields
	 *
	 * @param provider
	 */
	public JSR223JavaScriptCompletionResolver(SourceCompletionProvider provider) {
		super(provider);
	}


	/**
	 * Try to resolve standard JavaScript type. If null, then look for static class
	 */
	@Override
	protected TypeDeclaration resolveNativeType(AstNode node) {
		TypeDeclaration dec = super.resolveNativeType(node);
		if (dec == null) {
			dec = testJavaStaticType(node);
		}

		return dec;
	}




	@Override
	public String getLookupText(JSMethodData methodData, String name) {
		StringBuilder sb = new StringBuilder(name);
		sb.append('(');
		int count = methodData.getParameterCount();
		String[] parameterTypes = methodData.getMethodInfo()
				.getParameterTypes();
		for (int i = 0; i < count; i++) {
			String paramName = methodData.getParameterType(parameterTypes, i, provider);
			sb.append(paramName);
			if (i < count - 1) {
				sb.append(",");
			}
		}
		sb.append(')');
		return sb.toString();
	}




	@Override
	public String getFunctionNameLookup(FunctionCall call, SourceCompletionProvider provider) {
		if (call != null) {
			StringBuilder sb = new StringBuilder();
			if (call.getTarget() instanceof PropertyGet) {
				PropertyGet get = (PropertyGet) call.getTarget();
				sb.append(get.getProperty().getIdentifier());
			}
			sb.append("(");
			int count = call.getArguments().size();

			for (int i = 0; i < count; i++) {
				AstNode paramNode = call.getArguments().get(i);
				JavaScriptResolver resolver = provider.getJavaScriptEngine().getJavaScriptResolver(provider);
				Logger.log("PARAM: " + JavaScriptHelper.convertNodeToSource(paramNode));
				try {
					TypeDeclaration type = resolver.resolveParamNode(JavaScriptHelper.convertNodeToSource(paramNode));
					String resolved = type != null ? type.getQualifiedName() : "any";
					sb.append(resolved);
					if (i < count - 1) {
						sb.append(",");
					}
				} catch (IOException io) {
					io.printStackTrace();
				}
			}
			sb.append(")");
			return sb.toString();
		}
		return null;
	}


	/**
	 * Try to resolve the Token.NAME AstNode and return a TypeDeclaration.
	 *
	 * @param node node to resolve
	 * @return TypeDeclaration if the name can be resolved as a Java Class else null
	 */
	@Override
	protected TypeDeclaration findJavaStaticType(AstNode node) {
		// check parent is of type property get
		String testName = null;
		if (node.getParent() != null
				&& node.getParent().getType() == Token.GETPROP) { // ast parser

			String name = node.toSource();
			try {
				String longName = node.getParent().toSource();

				if (longName.indexOf('[') == -1 && longName.indexOf(']') == -1 &&
						longName.indexOf('(') == -1 && longName.indexOf(')') == -1) {

					// trim the text to the short name
					int index = longName.lastIndexOf(name);
					if (index > -1) {
						testName = longName.substring(0, index + name.length());
					}
				}
			} catch (Exception e) {
				Logger.log(e.getMessage());
			}
		}
		else {
			testName = node.toSource();
		}

		if (testName != null) {
			TypeDeclaration dec = JavaScriptHelper.getTypeDeclaration(testName, provider);

			if (dec == null)
				dec = JavaScriptHelper.createNewTypeDeclaration(testName);

			ClassFile cf = provider.getJavaScriptTypesFactory().getClassFile(
					provider.getJarManager(), dec);
			if (cf != null) {
                return provider.getJavaScriptTypesFactory()
                        .createNewTypeDeclaration(cf, true, false);
			}
		}
		return null;
	}

}
