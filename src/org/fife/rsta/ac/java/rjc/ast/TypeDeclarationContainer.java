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


/**
 * Interface for tree nodes that can hold type declarations (e.g.
 * {@link CompilationUnit}s and {@link TypeDeclaration}s.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface TypeDeclarationContainer {


	public void addTypeDeclaration(TypeDeclaration typeDec);


}