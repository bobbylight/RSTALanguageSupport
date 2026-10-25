/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
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


	@Override
	public TypeDeclaration getParentTypeDeclaration() {
		return parentTypeDec;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStatic() {
		Modifiers modifiers = getModifiers();
		return modifiers!=null && modifiers.isStatic();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentTypeDeclaration(TypeDeclaration dec) {
		if (dec==null) {
			throw new InternalError("Parent TypeDeclaration cannot be null");
		}
		parentTypeDec = dec;
	}


}