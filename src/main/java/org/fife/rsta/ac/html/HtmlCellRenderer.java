/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.html;

import javax.swing.Icon;
import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


/**
 * The cell renderer used for HTML.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HtmlCellRenderer extends CompletionCellRenderer {

	private Icon tagIcon;
	private Icon attrIcon;


	/**
	 * Constructor.
	 */
	public HtmlCellRenderer() {
		tagIcon = getIcon("tag.png");
		attrIcon = getIcon("attribute.png");
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected,
										hasFocus);
		setIcon(getEmptyIcon());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForMarkupTagCompletion(JList list,
		MarkupTagCompletion c, int index, boolean selected, boolean hasFocus) {
		super.prepareForMarkupTagCompletion(list, c, index, selected, hasFocus);
		setIcon(tagIcon);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForOtherCompletion(JList list,
			Completion c, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		if (c instanceof AttributeCompletion) {
			setIcon(attrIcon);
		}
		else {
			setIcon(getEmptyIcon());
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected,
			boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected,
										hasFocus);
		setIcon(getEmptyIcon());
	}


}