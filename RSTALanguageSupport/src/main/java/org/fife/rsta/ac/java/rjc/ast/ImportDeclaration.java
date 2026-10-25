/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * An import declaration in a class file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ImportDeclaration extends AbstractASTNode {

	private boolean isStatic;


	public ImportDeclaration(Scanner s, int offs, String info, boolean isStatic) {
		super(info, s.createOffset(offs), s.createOffset(offs+info.length()));
		setStatic(isStatic);
	}


	public boolean isStatic() {
		return isStatic;
	}


	public boolean isWildcard() {
		return getName().endsWith(".*");
	}


	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}


}
