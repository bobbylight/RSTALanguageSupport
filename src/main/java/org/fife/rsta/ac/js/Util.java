/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js;


/**
 * Utility classes for the JavaScript code completion.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Util {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}


	/**
	 * Generates an HTML summary from a JSDoc comment.
	 *
	 * @param jsDoc The JSDoc comment.
	 * @return The HTML version.
	 */
	public static String jsDocToHtml(String jsDoc) {
		return org.fife.rsta.ac.java.Util.docCommentToHtml(jsDoc);
	}


}
