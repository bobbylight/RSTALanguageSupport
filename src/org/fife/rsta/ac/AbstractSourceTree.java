/*
 * 10/09/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac;

import javax.swing.JTree;


/**
 * A tree showing the structure of a source file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractSourceTree extends JTree {

	private boolean sorted;


	/**
	 * Returns whether the contents of this tree are sorted.
	 *
	 * @return Whether the contents of this tree are sorted.
	 * @see #setSorted(boolean)
	 */
	public boolean isSorted() {
		return sorted;
	}


	/**
	 * Toggles whether the contents of this tree are sorted.
	 *
	 * @param sorted Whether the contents of this tree are sorted.
	 * @see #isSorted()
	 */
	public void setSorted(boolean sorted) {
		if (this.sorted!=sorted) {
			this.sorted = sorted;
			Object root = getModel().getRoot();
			if (root instanceof SortableTreeNode) {
				((SortableTreeNode)root).setSorted(sorted);
			}
		}
	}


}