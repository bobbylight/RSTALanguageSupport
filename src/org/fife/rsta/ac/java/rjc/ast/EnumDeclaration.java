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

import java.util.List;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;


public class EnumDeclaration extends AbstractTypeDeclarationNode {

//	private EnumBody enumBody;


	public EnumDeclaration(Scanner s, int offs, String name) {
		super(name, s.createOffset(offs), s.createOffset(offs+name.length()));
	}


	/**
	 * {@inheritDoc}
	 */
	public List getAccessibleMembersOfType(String type, int offs) {
		// TODO: Implement me
		return null;
	}


	public String getTypeString() {
		return "enum";
	}


//	public void setEnumBody(EnumBody enumBody) {
//		this.enumBody = enumBody;
//	}


}