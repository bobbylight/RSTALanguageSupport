/*
 * 04/07/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.xml.tree;

import java.awt.Component;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;


/**
 * Renders nodes in the XML tree.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class XmlTreeCellRenderer extends DefaultTreeCellRenderer {

	private Icon elemIcon;


	public XmlTreeCellRenderer() {
		URL url = getClass().getResource("tag.png");
		if (url!=null) { // Always true
			elemIcon = new ImageIcon(url);
		}
	}


    public Component getTreeCellRendererComponent(JTree tree, Object value,
			  boolean sel, boolean expanded, boolean leaf, int row,
			  boolean focused) {
    	super.getTreeCellRendererComponent(tree, value, sel, expanded,
    										leaf, row, focused);
    	if (sel) {
    		setText(((XmlTreeNode)value).toStringSelected());
    	}
    	setIcon(elemIcon);
    	return this;
    }

   
}