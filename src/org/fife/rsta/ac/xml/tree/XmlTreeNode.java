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

import javax.swing.text.Position;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.SourceTreeNode;


/**
 * The tree node in <code>XmlOutlineTree</code>s.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see XmlOutlineTree
 */
public class XmlTreeNode extends SourceTreeNode {

	private XmlOutlineTree tree;
	private String name;
	private String mainAttr;
	Position offset;
	Position endOffset;


	public XmlTreeNode(XmlOutlineTree tree, String name) {
		super(name);
		this.tree = tree;
		this.name = name;
	}


	public boolean containsOffset(int offs) {
		return offset!=null && endOffset!=null &&
				offs>=offset.getOffset() && offs<=endOffset.getOffset();
	}


	public int getEndOffset() {
		return endOffset!=null ? endOffset.getOffset() : Integer.MAX_VALUE;
	}


	public int getStartOffset() {
		return offset!=null ? offset.getOffset() : -1;
	}


	public void selectInTree() {
		TreePath path = new TreePath(getPath());
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}


	public void setEndOffset(Position pos) {
		this.endOffset = pos;
	}


	public void setMainAttribute(String attr) {
		this.mainAttr = attr;
	}


	public void setStartOffset(Position pos) {
		this.offset = pos;
	}


	public String toString() {
		String str = name;
		if (mainAttr!=null) {
			str = "<html>" + str + " <font color='#808080'>" + mainAttr;
		}
		return str;
	}


	public String toStringSelected() {
		String str = name;
		if (mainAttr!=null) {
			str += " " + mainAttr;
		}
		return str;
	}


}