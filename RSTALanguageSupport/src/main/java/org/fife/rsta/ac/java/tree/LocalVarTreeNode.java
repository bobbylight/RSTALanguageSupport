/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.tree;

import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.ui.autocomplete.Util;


/**
 * Tree node for a local variable.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class LocalVarTreeNode extends JavaTreeNode {

	private String text;


	LocalVarTreeNode(LocalVariable var) {

		super(var);
		setIcon(IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON));
		setSortPriority(PRIORITY_LOCAL_VAR);

		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append(var.getName());
		sb.append(" : ");
		sb.append("<font color='#888888'>");
		MemberTreeNode.appendType(var.getType(), sb);
		text = sb.toString();
	}


	@Override
	public String getText(boolean selected) {
		// Strip out HTML tags
		return selected ? Util.stripHtml(text).
				replaceAll("&lt;", "<").replaceAll("&gt;", ">") : text;
	}


}
