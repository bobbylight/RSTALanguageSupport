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


/**
 * A type declaration.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface TypeDeclaration extends ASTNode, TypeDeclarationContainer {


	/**
	 * Returns whether this type declaration contains the specified offset.
	 *
	 * @param offs The offset to check.
	 * @return Whether the offset is contained.
	 */
	boolean getBodyContainsOffset(int offs);


	/**
	 * Returns the end-offset of the type declaration's body.
	 *
	 * @return The end offset.
	 * @see #getBodyStartOffset()
	 */
	int getBodyEndOffset();


	/**
	 * Returns the start-offset of the type declaration's body.
	 *
	 * @return The start offset.
	 * @see #getBodyEndOffset()
	 */
	int getBodyStartOffset();


	/**
	 * Returns a child type by index.
	 *
	 * @param index The index.
	 * @return The child type.
	 * @see #getChildTypeCount()
	 */
	TypeDeclaration getChildType(int index);


	/**
	 * Returns the child type declaration of this one that contains the
	 * specified offset, if any.
	 *
	 * @param offs The offset.
	 * @return The type declaration, or <code>null</code> if the offset is
	 *         outside any child type declaration.
	 */
	TypeDeclaration getChildTypeAtOffset(int offs);


	/**
	 * Returns the number of child types.
	 *
	 * @return The number of child types.
	 * @see #getChildType(int)
	 */
	int getChildTypeCount();


	/**
	 * Returns the doc comment for this type declaration.
	 *
	 * @return The doc comment.
	 * @see #setDocComment(String)
	 */
	String getDocComment();


	/**
	 * Returns an iterator over all fields defined in this type.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 * @see #getMemberIterator()
	 */
	Iterator<Field> getFieldIterator();


	/**
	 * Returns a member by index.
	 *
	 * @param index The index.
	 * @return The member.
	 * @see #getMemberCount()
	 * @see #getMemberIterator()
	 */
	Member getMember(int index);


	/**
	 * Returns the number of members in this type declaration.
	 *
	 * @return The number of members.
	 * @see #getMember(int)
	 * @see #getMemberIterator()
	 */
	int getMemberCount();


	/**
	 * Returns an iterator over all members of this type.  Note
	 * that an exception may be thrown if a method is added to this type
	 * while this iterator is being used.
	 *
	 * @return The iterator.
	 * @see #getMethodIterator()
	 * @see #getMember(int)
	 * @see #getMemberCount()
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


	/**
	 * Returns the type of this type declaration, as a string.
	 *
	 * @return The type.
	 */
	String getTypeString();


	/**
	 * Returns whether this type declaration is deprecated.
	 *
	 * @return Whether this type declaration is deprecated.
	 */
	boolean isDeprecated();


	/**
	 * Shortcut for <code>getModifiers().isStatic()</code>; useful since
	 * <code>getModifiers()</code> may return <code>null</code>.
	 *
	 * @return Whether this type declaration is static.
	 * @see #getModifiers()
	 */
	boolean isStatic();


	/**
	 * Sets the doc comment for this type declaration.
	 *
	 * @param comment The new doc comment.
	 * @see #getDocComment()
	 */
	void setDocComment(String comment);


	/**
	 * Sets the parent type declaration for this type declaration.
	 *
	 * @param parentType The parent type declaration.
	 * @see #getParentType()
	 */
	void setParentType(TypeDeclaration parentType);


}
