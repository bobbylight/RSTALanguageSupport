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

import org.mozilla.javascript.ast.AstNode;


public class JavaScriptHelper {

	/**
	 * Convert text into lookup for functions
	 * 
	 * @param text
	 * @return
	 */
	public static String convertTextToLookup(String text) {
		int start = text.indexOf('(');
		int end = text.indexOf(')');
		if (start != -1 && end != -1) {
			// have a method, so convert to lookup String
			StringBuffer sb = new StringBuffer();
			String name = text.substring(0, start);
			sb.append(name.trim());
			sb.append("(");
			String params = text.substring(start + 1, end).trim();
			// count commas
			int count = params.length() > 0 ? params.split(",").length : 0;
			for (int i = 0; i < count; i++) {
				sb.append("p");
				if (i < count - 1) {
					sb.append(", ");
				}
			}
			sb.append(")");
			return sb.toString();
		}
		else
			return text;
	}


	/**
	 * Test whether the start of the variable is the same name as the variable
	 * being initialised. This is not possible.
	 * 
	 * @param target Name of variable being created
	 * @param initialiser name of initialiser
	 * @return
	 */
	public static boolean canResolveVariable(AstNode target, AstNode initialiser) {
		String varName = target.toSource();
		String init = initialiser.toSource();
		String[] splitInit = init.split("\\.");
		if (splitInit.length > 0) {
			return !varName.equals(splitInit[0]);
		}
		return false;
	}
}
