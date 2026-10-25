/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.ast;


/**
 * Interface for tree nodes that can hold type declarations (e.g.
 * {@link CompilationUnit}s and {@link TypeDeclaration}s).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface TypeDeclarationContainer {


	/**
	 * Adds a type declaration to this container.
	 *
	 * @param typeDec The new type declaration.
	 */
	void addTypeDeclaration(TypeDeclaration typeDec);


}
