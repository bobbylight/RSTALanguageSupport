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
package org.fife.rsta.ac.bsh.rjc.ast;

import org.fife.rsta.ac.bsh.rjc.lexer.Offset;


/**
 * Base implementation of an AST node.
 *
 * @author Robert Futrell
 * @version 1.0
 */
abstract class AbstractASTNode implements ASTNode {

	private String name;
	private Offset startOffs;
	private Offset endOffs;


	protected AbstractASTNode(String name, Offset start) {
		this(name, start, null);
	}


	protected AbstractASTNode(String name, Offset start, Offset end) {
		this.name = name;
		startOffs = start;
		endOffs = end;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getNameEndOffset() {
		return endOffs!=null ? endOffs.getOffset() : Integer.MAX_VALUE;
	}


	/**
	 * {@inheritDoc}
	 */
	public int getNameStartOffset() {
		return startOffs!=null ? startOffs.getOffset() : 0;
	}


	public void setDeclarationEndOffset(Offset end) {
		endOffs = end;
	}


	/**
	 * Sets the start and end offsets of this node.
	 *
	 * @param start The start offset.
	 * @param end The end offset.
	 */
	protected void setDeclarationOffsets(Offset start, Offset end) {
		startOffs = start;
		endOffs = end;
	}


	/**
	 * Returns the name of this node (e.g. the value of {@link #getName()}.
	 * Subclasses can override this method if appropriate.
	 *
	 * @return A string representation of this node.
	 */
	@Override
	public String toString() {
		return getName();
	}


}