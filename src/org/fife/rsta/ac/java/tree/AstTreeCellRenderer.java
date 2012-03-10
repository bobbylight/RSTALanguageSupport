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

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


/**
 * Renderer for the AST tree in the UI.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class AstTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;


	public Component getTreeCellRendererComponent(JTree tree, Object value,
							boolean sel, boolean expanded, boolean leaf,
							int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
											row, hasFocus);
		if (value instanceof JavaTreeNode) { // Should always be true
			JavaTreeNode node = (JavaTreeNode)value;
			setText(node.getText(sel));
			setIcon(node.getIcon());
		}
		return this;
	}


}