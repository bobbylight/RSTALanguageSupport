/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * Represents a package in a class file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Package extends AbstractASTNode {


	public Package(Scanner s, int offs, String pkg) {
		super(pkg, s.createOffset(offs), s.createOffset(offs+pkg.length()));
	}


}
