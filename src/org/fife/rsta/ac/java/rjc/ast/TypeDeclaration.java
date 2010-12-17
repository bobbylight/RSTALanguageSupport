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

import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;


public interface TypeDeclaration extends ASTNode, TypeDeclarationContainer {


	public boolean getBodyContainsOffset(int offs);


	public int getBodyEndOffset();


	public int getBodyStartOffset();


	public TypeDeclaration getChildType(int index);


	/**
	 * Returns the child type declaration of this one that contains the
	 * specified offset, if any.
	 *
	 * @param offs The offset.
	 * @return The type declaration, or <code>null</code> if the offset is
	 *         outside of any child type declaration.
	 */
	public TypeDeclaration getChildTypeAtOffset(int offs);


	public int getChildTypeCount();


	public String getDocComment();


	/**
	 * Returns an iterator over all fields defined in this type.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 * @see #getMemberIterator()
	 */
	public Iterator getFieldIterator();


	public int getMemberCount();


	/**
	 * Returns an iterator over all members of this type.  Note 
	 * that an exception may be thrown if a method is added to this type
	 * while this iterator is being used.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 */
	public Iterator getMemberIterator();


	/**
	 * Returns an iterator over all methods defined in this type.
	 *
	 * @return The iterator.
	 * @see #getFieldIterator()
	 * @see #getMemberIterator()
	 */
	public Iterator getMethodIterator();


	/**
	 * Returns all methods declared in this type with the given name.  Does
	 * not check for methods with this name in subclasses.
	 *
	 * @param name The name to check for.
	 * @return Any method overloads with that name, or an empty list if none.
	 */
	public List getMethodsByName(String name);


	public Modifiers getModifiers();


	public String getName();


	/**
	 * Returns the package this type is in.
	 *
	 * @return The package, or <code>null</code> if it's in the default package.
	 */
	public Package getPackage();


	public String getTypeString();


	public boolean isDeprecated();


	public void setDocComment(String comment);


}