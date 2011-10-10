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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;


/**
 * A tree node that can toggle whether its children appear sorted.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SortableTreeNode extends DefaultMutableTreeNode {

	private boolean sorted;
	private Vector sortedChildren;
	private int sortPriority;


	public SortableTreeNode(Object userObject) {
		this(userObject, false);
	}


	public SortableTreeNode(Object userObject, boolean sorted) {
		super(userObject);
		setSorted(sorted);
	}


	public void add(MutableTreeNode child) {
		super.add(child);
		if (sorted) {
			createSortMap(); // TODO: Find index and add for performance
		}
	}


	public Enumeration children() {
		if (sorted) {
			if (children==null) {
				return EMPTY_ENUMERATION;
			}
			return sortedChildren.elements();
		}
		return super.children();
	}


	/**
	 * Returns a comparator used to sort the child nodes of this node.
	 * The default implementation sorts alphabetically.  Subclasses may want
	 * to override to return a comparator that groups by type of node and sorts
	 * by group, etc.
	 *
	 * @return A comparator.
	 */
	public Comparator createComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				SortableTreeNode stn1 = (SortableTreeNode)o1;
				SortableTreeNode stn2 = (SortableTreeNode)o2;
				int res = stn1.getSortPriority() - stn2.getSortPriority();
				if (res==0) {
					res = o1.toString().compareToIgnoreCase(o2.toString());
				}
				return res;
			}
		};
	}


	public void createSortMap() {
		sortedChildren = new Vector();
		if (children!=null) {
			sortedChildren.addAll(children);
			Collections.sort(sortedChildren, createComparator());
		}
	}


	public TreeNode getChildAfter(TreeNode child) {
		if (sorted) {
			if (child==null) {
				throw new IllegalArgumentException("child cannot be null");
			}
			int index = getIndex(child);
			if (index==-1) {
				throw new IllegalArgumentException("child node not contained");
			}
			return index<getChildCount()-1 ? getChildAt(index+1) : null;
		}
		return super.getChildAfter(child);
	}


	public TreeNode getChildAt(int index) {
		System.out.println(index);
		return sorted ? (TreeNode)sortedChildren.get(index) :
			super.getChildAt(index);
	}


	public TreeNode getChildBefore(TreeNode child) {
		if (sorted) {
			if (child==null) {
				throw new IllegalArgumentException("child cannot be null");
			}
			int index = getIndex(child);
			if (index==-1) {
				throw new IllegalArgumentException("child node not contained");
			}
			return index> 0 ? getChildAt(index - 1) : null;
		}
		return super.getChildBefore(child);
	}


	public int getIndex(TreeNode child) {
		if (sorted) {
			if (child==null) {
				throw new IllegalArgumentException("child cannot be null");
			}
			for (int i=0; i<sortedChildren.size(); i++) {
				TreeNode node = (TreeNode)sortedChildren.get(i);
				if (node.equals(child)) {
					return i;
				}
			}
			return -1;
		}
		return super.getIndex(child);
	}


	/**
	 * Returns the relative priority of this node against others when being
	 * sorted (lower is higher priority).
	 *
	 * @return The relative priority.
	 * @see #setSortPriority(int)
	 */
	public int getSortPriority() {
		return sortPriority;
	}


	public boolean isSorted() {
		return sorted;
	}


	/**
	 * Sets whether this tree node (and any child sortable tree nodes) are
	 * sorting their children.
	 *
	 * @param sorted Whether sorting is enabled.
	 * @see #isSorted()
	 */
	public void setSorted(boolean sorted) {
		if (sorted!=this.sorted) {
			this.sorted = sorted;
			if (sorted && sortedChildren==null) {
				createSortMap();
			}
			for (int i=0; i<children.size(); i++) {
				Object child = children.get(i);
				if (child instanceof SortableTreeNode) {
					((SortableTreeNode)child).setSorted(sorted);
				}
			}
		}
	}


	/**
	 * Sets the relative sort priority of this tree node when it is compared
	 * against others (lower is higher priority).
	 *
	 * @param priority The relative priority.
	 * @see #getSortPriority()
	 */
	public void setSortPriority(int priority) {
		this.sortPriority = priority;
	}


}