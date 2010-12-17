/*
 * 12/16/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;


/**
 * The renderer used for parameter completions (for methods) in Java.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaParamListCellRenderer extends DefaultListCellRenderer {


	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.width += 32;
		return d;
	}


	/**
	 * Returns the renderer.
	 *
	 * @param list The list of choices being rendered.
	 * @param value The {@link Completion} being rendered.
	 * @param index The index into <code>list</code> being rendered.
	 * @param selected Whether the item is selected.
	 * @param hasFocus Whether the item has focus.
	 */
	public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean selected, boolean hasFocus) {
		super.getListCellRendererComponent(list, value, index, selected,
											hasFocus);
		JavaSourceCompletion ajsc = (JavaSourceCompletion)value;
		setIcon(ajsc.getIcon());
		return this;
	}


}