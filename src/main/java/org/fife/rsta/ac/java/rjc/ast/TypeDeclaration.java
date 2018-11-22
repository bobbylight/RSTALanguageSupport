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

import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;


public interface TypeDeclaration extends ASTNode, TypeDeclarationContainer {


	boolean getBodyContainsOffset(int offs);


	int getBodyEndOffset();


	int getBodyStartOffset();


	TypeDeclaration getChildType(int index);


	/**
	 * Returns the child type declaration of this one that contains the
	 * specified offset, if any.
	 *
	 * @param offs The offset.
	 * @return The type declaration, or <code>null</code> if the offset is
	 *         outside of any child type declaration.
	 */
	TypeDeclaration getChildTypeAtOffset(int offs);


	int getChildTypeCount();


	String getDocComment();


	/**
	 * Returns an iterator over all fields defined in this type.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 * @see #getMemberIterator()
	 */
	Iterator<Field> getFieldIterator();


	Member getMember(int index);


	int getMemberCount();


	/**
	 * Returns an iterator over all members of this type.  Note 
	 * that an exception may be thrown if a method is added to this type
	 * while this iterator is being used.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 */
	Iterator<Member> getMemberIterator();


	/**
	 * Returns an iterator over all methods defined in this type.
	 *
	 * @return The iterator.
	 * @see #getFieldIterator()
	 * @see #getMemberIterator()
	 */
	Iterator<Method> getMethodIterator();


	/**
	 * Returns all methods declared in this type with the given name.  Does
	 * not check for methods with this name in subclasses.
	 *
	 * @param name The name to check for.
	 * @return Any method overloads with that name, or an empty list if none.
	 */
	List<Method> getMethodsByName(String name);


	/**
	 * Returns the modifiers of this type declaration.
	 *
	 * @return The modifier list.  This may be <code>null</code> if no
	 *         modifiers were specified.
	 */
	Modifiers getModifiers();


	/**
	 * Returns the name of this type, unqualified.
	 *
	 * @return The name of this type.
	 * @see #getName(boolean)
	 */
	@Override
	String getName();


	/**
	 * Returns the name of this type.
	 *
	 * @param fullyQualified Whether the name returned should be fully
	 *        qualified.
	 * @return The type's name.
	 * @see #getName()
	 */
	String getName(boolean fullyQualified);


	/**
	 * Returns the package this type is in.
	 *
	 * @return The package, or <code>null</code> if it's in the default package.
	 */
	Package getPackage();


	/**
	 * Returns the parent type declaration.
	 *
	 * @return The parent type declaration, or <code>null</code> if there isn't
	 *             one.
	 * @see #setParentType(TypeDeclaration)
	 */
	TypeDeclaration getParentType();


	String getTypeString();


	boolean isDeprecated();


	/**
	 * Shortcut for <code>getModifiers().isStatic()</code>; useful since
	 * <code>getModifiers()</code> may return <code>null</code>.
	 *
	 * @return Whether this type declaration is static.
	 * @see #getModifiers()
	 */
	boolean isStatic();


	void setDocComment(String comment);


	/**
	 * Sets the parent type declaration for this type declaration.
	 *
	 * @param parentType The parent type declaration.
	 * @see #getParentType()
	 */
	void setParentType(TypeDeclaration parentType);


}
