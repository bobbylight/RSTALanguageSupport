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

import org.fife.rsta.ac.SourceTreeNode;


/**
 * The tree node in <code>XmlOutlineTree</code>s.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see XmlOutlineTree
 */
public class XmlTreeNode extends SourceTreeNode {

	private String name;
	private String mainAttr;
	private Position offset;
	private Position endOffset;


	public XmlTreeNode(String name) {
		super(name);
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


	public String getText(boolean selected) {
		String text = name;
		if (mainAttr!=null) {
			if (selected) {
				text += " " + mainAttr;
			}
			else {
				text = "<html>" + text + " <font color='#808080'>" + mainAttr;
			}
		}
		return text;
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


	/**
	 * Returns a string representation of this tree node.
	 *
	 * @return A string representation of this tree node.
	 */
	public String toString() {
		return getText(true);
	}


}