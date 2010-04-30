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

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;


/**
 * A marker for a member of a class or interface.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface Member extends ASTNode {


	public String getDocComment();


	public int getNameEndOffset();


	public int getNameStartOffset();


	public Modifiers getModifiers();


	public String getName();


	public TypeDeclaration getParentTypeDeclaration();


	public Type getType();


	public boolean isDeprecated();


	public void setParentTypeDeclaration(TypeDeclaration dec);


}