/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.bsh.rjc.ast;

import org.fife.rsta.ac.bsh.rjc.lexer.Scanner;


public class Package extends AbstractASTNode {


	public Package(Scanner s, int offs, String pkg) {
		super(pkg, s.createOffset(offs), s.createOffset(offs+pkg.length()));
	}


}