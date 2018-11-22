/*
 * 12/16/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import org.fife.ui.autocomplete.Completion;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JList;


/**
 * The renderer used for parameter completions (for methods) in Java.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaParamListCellRenderer extends JavaCellRenderer {


	public JavaParamListCellRenderer() {
		// Param completions don't display type info, etc., because all
		// completions for a single parameter have the same type (or subclass
		// that type).
		setSimpleText(true);
	}


	/**
	 * Returns the preferred size of a particular cell.  Note that the parent
	 * class {@link JavaCellRenderer} doesn't override this method, because
	 * it doesn't use the cells to dictate the preferred size of the list, due
	 * to the large number of completions it shows at a time.
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		d.width += 32; // Looks better when less scrunched.
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
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean selected, boolean hasFocus) {
		super.getListCellRendererComponent(list, value, index, selected,
											hasFocus);
		JavaSourceCompletion ajsc = (JavaSourceCompletion)value;
		setIcon(ajsc.getIcon());
		return this;
	}


}
