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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * An interface declaration:
 * 
 * <pre>
 * NormalInterfaceDeclaration:
 *    'interface' Identifier [TypeParameters] ['extends' TypeList] InterfaceBody
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class NormalInterfaceDeclaration extends AbstractTypeDeclarationNode {

	private List extendedList;


	public NormalInterfaceDeclaration(Scanner s, int offs, String name) {
		super(name, s.createOffset(offs), s.createOffset(offs+name.length()));
		extendedList = new ArrayList(1); // Usually small
	}


	public void addExtended(Type extended) {
		extendedList.add(extended);
	}


	/**
	 * Always returns <code>null</code>, since interfaces cannot contain
	 * actual code.
	 */
	public List getAccessibleMembersOfType(String type, int offs) {
		return null;
	}


	public int getExtendedCount() {
		return extendedList.size();
	}


	public Iterator getExtendedIterator() {
		return extendedList.iterator();
	}


	public String getTypeString() {
		return "interface";
	}


}