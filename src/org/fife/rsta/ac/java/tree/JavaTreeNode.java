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
package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;

import org.fife.rsta.ac.SourceTreeNode;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;


/**
 * Base class for nodes in the Java outline tree.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaTreeNode extends SourceTreeNode {

	private ASTNode astNode;
	private Icon icon;

	protected static final int PRIORITY_TYPE = 0;
	protected static final int PRIORITY_FIELD = 1;
	protected static final int PRIORITY_CONSTRUCTOR = 2;
	protected static final int PRIORITY_METHOD = 3;
	protected static final int PRIORITY_LOCAL_VAR = 4;
	protected static final int PRIORITY_BOOST_STATIC = -16;


	protected JavaTreeNode(ASTNode node) {
		this(node, null);
	}


	protected JavaTreeNode(ASTNode node, String iconName) {
		this(node, iconName, false);
	}


	protected JavaTreeNode(ASTNode node, String iconName, boolean sorted) {
		super(node, sorted);
		this.astNode = node;
		if (iconName!=null) {
			setIcon(IconFactory.get().getIcon(iconName));
		}
	}


	public JavaTreeNode(String text, String iconName) {
		this(text, iconName, false);
	}


	public JavaTreeNode(String text, String iconName, boolean sorted) {
		super(text, sorted);
		if (iconName!=null) {
			this.icon = IconFactory.get().getIcon(iconName);
		}
	}


	/**
	 * Overridden to compare tree text without HTML.
	 */
	@Override
	public int compareTo(SourceTreeNode obj) {
		int res = -1;
		if (obj instanceof JavaTreeNode) {
			JavaTreeNode jtn2 = (JavaTreeNode)obj;
			res = getSortPriority() - jtn2.getSortPriority();
			if (res==0 && ((SourceTreeNode)getParent()).isSorted()) {
				res = getText(false).compareToIgnoreCase(jtn2.getText(false));
			}
		}
		return res;
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