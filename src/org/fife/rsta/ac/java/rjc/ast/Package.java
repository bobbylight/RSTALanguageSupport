/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;


public class Package extends AbstractASTNode {


	public Package(Scanner s, int offs, String pkg) {
		super(pkg, s.createOffset(offs), s.createOffset(offs+pkg.length()));
	}


}