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
package org.fife.rsta.ac.js;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


/**
 * The cell renderer used for JavaScript completion choices.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaScriptCellRenderer extends CompletionCellRenderer {

	private Icon variableIcon;
	private Icon functionIcon;
	private Icon emptyIcon;


	/**
	 * Constructor.
	 */
	public JavaScriptCellRenderer() {
		emptyIcon = new EmptyIcon(16); // Must be done first
		variableIcon = getIcon("img/field_default_obj.gif");
		functionIcon = getIcon("img/methdef_obj.gif");
	}


	/**
	 * Returns an icon.
	 *
	 * @param resource The icon to retrieve.  This should either be a file,
	 *        or a resource loadable by the current ClassLoader.
	 * @return The icon.
	 */
	private Icon getIcon(String resource) {
		URL url = getClass().getResource(resource); // Should never be null
		return url!=null ? new ImageIcon(url) : emptyIcon;
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForOtherCompletion(JList list,
			Completion c, int index, boolean selected, boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		setIcon(emptyIcon);
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected,
										hasFocus);
		setIcon(variableIcon);
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected,
										hasFocus);
		setIcon(functionIcon);
	}


}