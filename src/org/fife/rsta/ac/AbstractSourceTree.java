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

import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * A tree showing the structure of a source file.  You should only add
 * instances of {@link SourceTreeNode} or subclasses to this tree.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see SourceTreeNode
 */
public abstract class AbstractSourceTree extends JTree {

	private boolean sorted;
	private String prefix;
	private boolean gotoSelectedElementOnClick;


	public AbstractSourceTree() {
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		gotoSelectedElementOnClick = true;
	}


	/**
	 * Expands all nodes in the specified tree.  Subclasses should implement
	 * this in a way logical for the language.
	 */
	public abstract void expandInitialNodes();


	/**
	 * Filters visible tree nodes based on the specified prefix.
	 *
	 * @param prefix The prefix.  If this is <code>null</code>, all possible
	 *        children are shown.
	 */
	public void filter(String prefix) {
		if ((prefix==null && this.prefix!=null) ||
				(prefix!=null && !prefix.equals(this.prefix))) {
			if (prefix!=null) {
				prefix = prefix.toLowerCase();
			}
			this.prefix = prefix;
			Object root = getModel().getRoot();
			if (root instanceof SourceTreeNode) {
				((SourceTreeNode)root).filter(prefix);
			}
			((DefaultTreeModel)getModel()).reload();
			expandInitialNodes();
		}
	}


	/**
	 * Returns whether, when a source element is selected in this tree, the
	 * same source element should be selected in the editor.
	 *
	 * @return Whether to highlight the source element in the editor.
	 * @see #setGotoSelectedElementOnClick(boolean)
	 */
	public boolean getGotoSelectedElementOnClick() {
		return gotoSelectedElementOnClick;
	}


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
	 * Refreshes what children are visible in the tree.  This should be called
	 * manually when updating a source tree with a new root, and is also called
	 * internally on filtering and sorting.
	 */
	public void refresh() {
		Object root = getModel().getRoot();
		if (root instanceof SourceTreeNode) {
			SourceTreeNode node = (SourceTreeNode)root;
			node.refresh();
			((DefaultTreeModel)getModel()).reload();
			expandInitialNodes();
		}
	}


	/**
	 * Selects the first visible tree node matching the filter text.
	 */
	public void selectFirstNodeMatchingFilter() {

		if (prefix==null) {
			return;
		}

		DefaultTreeModel model = (DefaultTreeModel)getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
		Enumeration en = root.depthFirstEnumeration();
		String prefixLower = prefix.toLowerCase();

		while (en.hasMoreElements()) {
			SourceTreeNode stn = (SourceTreeNode)en.nextElement();
			JLabel renderer = (JLabel)getCellRenderer().
					getTreeCellRendererComponent(this, stn.getUserObject(),
							true, true, stn.isLeaf(), 0, true);
			String text = renderer.getText().toLowerCase();
			if (text.startsWith(prefixLower)) {
				setSelectionPath(new TreePath(model.getPathToRoot(stn)));
				return;
			}
		}

	}


	/**
	 * Selects the next visible row.
	 *
	 * @see #selectPreviousVisibleRow()
	 */
	public void selectNextVisibleRow() {
		int currentRow = getLeadSelectionRow();
		if (++currentRow<getRowCount()) {
			TreePath path = getPathForRow(currentRow);
			setSelectionPath(path);
		}
	}


	/**
	 * Selects the previous visible row.
	 *
	 * @see #selectNextVisibleRow()
	 */
	public void selectPreviousVisibleRow() {
		int currentRow = getLeadSelectionRow();
		if (--currentRow>=0) {
			TreePath path = getPathForRow(currentRow);
			setSelectionPath(path);
		}
	}


	/**
	 * Sets whether, when a source element is selected in this tree, the
	 * same source element should be selected in the editor.
	 *
	 * @param gotoSelectedElement Whether to highlight the source element in
	 *        the editor.
	 * @see #getGotoSelectedElementOnClick()
	 */
	public void setGotoSelectedElementOnClick(boolean gotoSelectedElement) {
		gotoSelectedElementOnClick = gotoSelectedElement;
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
			if (root instanceof SourceTreeNode) {
				((SourceTreeNode)root).setSorted(sorted);
			}
			((DefaultTreeModel)getModel()).reload();
			expandInitialNodes();
		}
	}


}