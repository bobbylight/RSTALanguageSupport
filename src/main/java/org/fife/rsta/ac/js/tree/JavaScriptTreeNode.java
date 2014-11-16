/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.tree;

import java.util.List;
import javax.swing.Icon;
import javax.swing.text.Position;

import org.fife.rsta.ac.SourceTreeNode;
import org.fife.rsta.ac.js.util.RhinoUtil;

import org.mozilla.javascript.ast.AstNode;


/**
 * Tree node for JavaScript outline trees.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptTreeNode extends SourceTreeNode {

	/**
	 * The location of this source element in the document.
	 */
	private Position pos;

	/**
	 * The text to display in the tree.
	 */
	private String text;

	/**
	 * The icon this node displays in the tree.
	 */
	private Icon icon;


	public JavaScriptTreeNode(List<AstNode> userObject) {
		super(userObject);
	}


	public JavaScriptTreeNode(AstNode userObject) {
		this(RhinoUtil.toList(userObject));
	}


	public JavaScriptTreeNode(AstNode userObject, boolean sorted) {
		super(RhinoUtil.toList(userObject), sorted);
	}


	public Icon getIcon() {
		return icon;
	}


	/**
	 * Returns the length in the document of this source element.
	 *
	 * @return The length of this element.
	 * @see #getOffset()
	 */
	@SuppressWarnings("unchecked")
	public int getLength() {
		int length = 0;
		List<AstNode> nodes = (List<AstNode>)getUserObject();
		for (AstNode node : nodes) {
			length += node.getLength();
		}
		length += nodes.size() - 1;
		return length;
	}


	/**
	 * Returns the offset into the document of this source element.  This
	 * offset tracks modifications in the document and has been updated
	 * accordingly.
	 *
	 * @return The offset.
	 * @see #getLength()
	 */
	public int getOffset() {
		return pos.getOffset();
	}


	public String getText(boolean selected) {
		return text;
	}


	public void setIcon(Icon icon) {
		this.icon = icon;
	}


	/**
	 * Sets the absolute offset of this element in the document.
	 *
	 * @param offs The offset.
	 * @see #getOffset()
	 */
	public void setOffset(Position offs) {
		this.pos = offs;
	}


	/**
	 * Sets the text to display in the tree for this node.
	 *
	 * @param text The text to display.
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * Overridden to return the textual representation displayed in the tree
	 * view.
	 *
	 * @return The text of this tree node.
	 */
	@Override
	public String toString() {
		return getText(false);
	}


}