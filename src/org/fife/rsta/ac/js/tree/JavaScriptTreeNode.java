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

import javax.swing.Icon;

import org.fife.rsta.ac.SourceTreeNode;


/**
 * Tree node for JavaScript outline trees.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptTreeNode extends SourceTreeNode {

	//private String text;
	private Icon icon;


	public JavaScriptTreeNode(Object userObject) {
		super(userObject);
	}


	public JavaScriptTreeNode(Object userObject, boolean sorted) {
		super(userObject, sorted);
	}


	public Icon getIcon() {
		return icon;
	}


	public String getText(boolean selected) {
		return (String)getUserObject();
	}


	public void setIcon(Icon icon) {
		this.icon = icon;
	}

/*
	public void setText(String text) {
		this.text = text;
	}
*/

}