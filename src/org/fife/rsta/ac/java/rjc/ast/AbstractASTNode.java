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

import org.fife.rsta.ac.java.rjc.lexer.Offset;


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
	public String toString() {
		return getName();
	}


}