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

import org.fife.rsta.ac.java.rjc.lang.Modifiers;


public interface TypeDeclaration extends ASTNode, TypeDeclarationContainer {


	public TypeDeclaration getChildType(int index);


	public int getBodyEndOffset();


	public int getBodyStartOffset();


	public int getChildTypeCount();


	public String getDocComment();


	public int getMemberCount();


	public Iterator getMemberIterator();


	public Modifiers getModifiers();


	public String getName();


	public String getTypeString();


	public boolean isDeprecated();


	public void setDocComment(String comment);


}