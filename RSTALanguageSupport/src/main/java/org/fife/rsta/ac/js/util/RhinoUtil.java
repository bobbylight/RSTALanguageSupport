/*
 * 06/05/2014
 *
 * Copyright (C) 2014 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;


/**
 * Utility methods for walking ASTs from Rhino.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class RhinoUtil {

	private static final System.Logger LOG = System.getLogger(RhinoUtil.class.getName());

	/**
	 * Private constructor to prevent instantiation.
	 */
	private RhinoUtil() {
	}


	/**
	 * Iterates through a function's parameters and returns a string
	 * representation of them, suitable for presentation as part of the
	 * method's signature.
	 *
	 * @param fn The function node.
	 * @return The string representation of the function's arguments.
	 */
	public static String getFunctionArgsString(FunctionNode fn) {
		StringBuilder sb = new StringBuilder("(");
		int paramCount = fn.getParamCount();
		if (paramCount>0) {
			List<AstNode> fnParams = fn.getParams();
			for (int i=0; i<paramCount; i++) {
				String paramName;
				AstNode paramNode = fnParams.get(i);
				switch (paramNode.getType()) {
					case Token.NAME:
						paramName = ((Name)paramNode).getIdentifier();
						break;
					default:
						LOG.log(System.Logger.Level.INFO, "Unhandled class for param: " +
								paramNode.getClass());
						paramName = "?";
						break;
				}
				sb.append(paramName);
				if (i<paramCount-1) {
					sb.append(", ");
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}


	/**
	 * Property keys in object literals can be identifiers or string literals.
	 * This method takes an AST node that was the key of an
	 * <code>ObjectProperty</code> and returns its value, no matter what the
	 * concrete AST node's type.
	 *
	 * @param propKeyNode The AST node for the property key.
	 * @return The property key's value.
	 */
	public static String getPropertyName(AstNode propKeyNode) {
		// TODO: Does Rhino use any other node type for this?
		return (propKeyNode instanceof Name) ?
					((Name)propKeyNode).getIdentifier() :
					((StringLiteral)propKeyNode).getValue();
	}


	public static String getPrototypeClazz(List<AstNode> nodes) {
		return getPrototypeClazz(nodes, -1);
	}


	public static String getPrototypeClazz(List<AstNode> nodes,
                                           int depth) {
		if (depth<0) {
			depth = nodes.size();
		}
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<depth; i++) {
			// These are not always Names, can sometimes be KeywordLiterals...
			// I think that's only in the case of syntax errors?
			sb.append(nodes.get(i).toSource());
			if (i<depth-1) {
				sb.append('.');
			}
		}
		return sb.toString();
	}


	/**
	 * Returns whether an AST node is a <code>Name</code> with the specified
	 * value.
	 *
	 * @param node The AST node.
	 * @param value The expected value.
	 * @return Whether the AST node is a <code>Name</code> with the specified
	 *         value.
	 */
	private static boolean isName(AstNode node, String value) {
		return node instanceof Name && value.equals(((Name)node).getIdentifier());
	}


	public static boolean isPrototypeNameNode(AstNode node) {
		return node instanceof Name &&
				"prototype".equals(((Name)node).getIdentifier());
	}


	public static boolean isPrototypePropertyGet(PropertyGet pg) {
		return pg!=null && pg.getLeft() instanceof Name &&
				isPrototypeNameNode(pg.getRight());
	}


	/**
	 * Returns whether a <code>PropertyGet</code> is a simple one, referencing
	 * an object's value 1 level deep.  For example, <code>Object.create</code>.
	 *
	 * @param pg The <code>PropertyGet</code>.
	 * @param expectedObj The expected object value.
	 * @param expectedField The expected string value.
	 * @return Whether the object is what was expected.
	 */
	public static boolean isSimplePropertyGet(PropertyGet pg,
                                              String expectedObj, String expectedField) {
		return pg!=null && isName(pg.getLeft(), expectedObj) &&
				isName(pg.getRight(), expectedField);
	}


	public static List<AstNode> toList(AstNode... nodes) {
		List<AstNode> list = new ArrayList<>();
        Collections.addAll(list, nodes);
		return list;
	}


}
