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
 * A class declaration:
 * 
 * <pre>
 * NormalClassDeclaration:
 *    'class' Identifier [TypeParameters] ['extends' Type] ['implements' TypeList] ClassBody
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class NormalClassDeclaration extends AbstractTypeDeclarationNode {

	// --- "NormalClassDeclaration" fields ---
	private List typeParams;
	private Type extendedType;
	private List implementedList;


	public NormalClassDeclaration(Scanner s, int offs, String className) {
		super(className, s.createOffset(offs), s.createOffset(offs+className.length()));
		implementedList = new ArrayList(0); // Usually not many
		// If parsing java.lang.Object.java, setExtendedType(null) should be
		// called.  This is here for all other classes without an explicit
		// super class declared.
		extendedType = new Type("java.lang.Object");
	}


	public void addImplemented(Type implemented) {
		implementedList.add(implemented);
	}


	public Type getExtendedType() {
		return extendedType;
	}


	public int getImplementedCount() {
		return implementedList.size();
	}


	public Iterator getImplementedIterator() {
		return implementedList.iterator();
	}


	public List getTypeParameters() {
		return typeParams;
	}


	public String getTypeString() {
		return "class";
	}


	public void setExtendedType(Type type) {
		extendedType = type;
	}


	public void setTypeParameters(List typeParams) {
		this.typeParams = typeParams;
	}


}