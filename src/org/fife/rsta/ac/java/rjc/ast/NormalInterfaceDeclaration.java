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

	private List<Type> extendedList;


	public NormalInterfaceDeclaration(Scanner s, int offs, String name) {
		super(name, s.createOffset(offs), s.createOffset(offs+name.length()));
		extendedList = new ArrayList<Type>(1); // Usually small
	}


	public void addExtended(Type extended) {
		extendedList.add(extended);
	}


	public int getExtendedCount() {
		return extendedList.size();
	}


	public Iterator<Type> getExtendedIterator() {
		return extendedList.iterator();
	}


	public String getTypeString() {
		return "interface";
	}


}