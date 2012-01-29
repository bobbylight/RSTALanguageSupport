/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js.tree;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


/**
 * Renderer for JavaScript outline trees.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaScriptTreeCellRenderer extends DefaultTreeCellRenderer {


	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
							row, hasFocus);
		if (value instanceof JavaScriptTreeNode) { // Should always be true
			JavaScriptTreeNode node = (JavaScriptTreeNode)value;
			setText(node.getText(sel));
			setIcon(node.getIcon());
		}
		return this;
	}


}