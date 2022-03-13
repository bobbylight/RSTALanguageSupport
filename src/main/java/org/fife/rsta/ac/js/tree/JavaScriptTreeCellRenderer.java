/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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


	@Override
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
