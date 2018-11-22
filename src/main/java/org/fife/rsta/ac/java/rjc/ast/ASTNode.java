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


/**
 * A node in a Java AST.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface ASTNode {


	/**
	 * Returns the "name" of this node.  This will be the name of the method,
	 * the name of the member or local variable, etc.  For {@link CodeBlock}s
	 * it will be {@link CodeBlock#NAME}.<p>
	 *
	 * Note that this may not be unique.  For example, a class with an
	 * overloaded method will have multiple methods with the same "name,"
	 * just with different signatures.
	 *
	 * @return The "name" of this node.
	 */
	String getName();


	/**
	 * Returns the end offset of the "name" of this node.
	 *
	 * @return The end offset.
	 */
	int getNameEndOffset();


	/**
	 * Returns the start offset of the "name" of this node.
	 *
	 * @return The start offset.
	 */
	int getNameStartOffset();


}
