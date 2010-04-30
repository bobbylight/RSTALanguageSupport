/*
 * 04/29/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Offset;


/**
 * Code shared amongst all {@link Member} nodes.
 *
 * @author Robert Futrell
 * @version 1.0
 */
abstract class AbstractMember extends AbstractASTNode implements Member {

	private TypeDeclaration parentTypeDec;


	protected AbstractMember(String name, Offset start) {
		super(name, start);
	}


	protected AbstractMember(String name, Offset start, Offset end) {
		super(name, start, end);
	}


	public TypeDeclaration getParentTypeDeclaration() {
		return parentTypeDec;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setParentTypeDeclaration(TypeDeclaration dec) {
		if (dec==null) {
			throw new InternalError("Parent TypeDeclaration cannot be null");
		}
		parentTypeDec = dec;
	}


}