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
package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;


/**
 * A node in the Java outline tree.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaTreeNode extends DefaultMutableTreeNode {

	private ASTNode astNode;
	private Icon icon;


	protected JavaTreeNode(ASTNode node) {
		this(node, null);
	}


	protected JavaTreeNode(ASTNode node, String iconName) {
		super(node);
		this.astNode = node;
		if (iconName!=null) {
			setIcon(IconFactory.get().getIcon(iconName));
		}
	}


	public JavaTreeNode(String text, String iconName) {
		super(text);
		if (iconName!=null) {
			this.icon = IconFactory.get().getIcon(iconName);
		}
	}


	public ASTNode getASTNode() {
		return astNode;
	}


	public Icon getIcon() {
		return icon;
	}


	public String getText(boolean selected) {
		Object obj = getUserObject();
		return obj!=null ? obj.toString() : null;
	}


	public void setIcon(Icon icon) {
		this.icon = icon;
	}


	/**
	 * Overridden to return the same thing as <tt>getText(false)</tt>, so
	 * we look nice with <tt>ToolTipTree</tt>s.
	 *
	 * @return A string representation of this tree node.
	 */
	public String toString() {
		return getText(false);
	}


}