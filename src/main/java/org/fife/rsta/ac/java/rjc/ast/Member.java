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

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;


/**
 * A marker for a member of a class or interface.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface Member extends ASTNode {


	String getDocComment();


	@Override
	int getNameEndOffset();


	@Override
	int getNameStartOffset();


	Modifiers getModifiers();


	@Override
	String getName();


	TypeDeclaration getParentTypeDeclaration();


	Type getType();


	boolean isDeprecated();


	/**
	 * Shortcut for <code>getModifiers().isStatic()</code>; useful since
	 * <code>getModifiers()</code> may return <code>null</code>.
	 *
	 * @return Whether this member is static.
	 * @see #getModifiers()
	 */
	boolean isStatic();


	void setParentTypeDeclaration(TypeDeclaration dec);


}
