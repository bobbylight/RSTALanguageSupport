/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import javax.swing.Icon;
import javax.swing.JList;

import org.fife.rsta.ac.js.completion.JSCompletionUI;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.EmptyIcon;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.TemplateCompletion;
import org.fife.ui.autocomplete.VariableCompletion;


/**
 * The cell renderer used for JavaScript completion choices.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptCellRenderer extends CompletionCellRenderer {

	private Icon emptyIcon;


	/**
	 * Constructor.
	 */
	public JavaScriptCellRenderer() {
		emptyIcon = new EmptyIcon(16);
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForOtherCompletion(JList list, Completion c,
			int index, boolean selected, boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		if (c instanceof JSCompletionUI) {
			setIcon(((JSCompletionUI) c).getIcon());
		}
		else {
			setIcon(emptyIcon);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForTemplateCompletion(JList list,
		TemplateCompletion tc, int index, boolean selected, boolean hasFocus) {
		super.prepareForTemplateCompletion(list, tc, index, selected, hasFocus);
		if (tc instanceof JSCompletionUI) {
			setIcon(((JSCompletionUI) tc).getIcon());
		}
		else {
			setIcon(IconFactory.getIcon(IconFactory.TEMPLATE_ICON));
		}
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
		if (vc instanceof JSCompletionUI) {
			setIcon(((JSCompletionUI) vc).getIcon());
		}
		else {
			setIcon(IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
		}
	}


	/**
	 * {@inheritDoc}
	 */
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
		if (fc instanceof JSCompletionUI) {
			setIcon(((JSCompletionUI) fc).getIcon());
		}
		else {
			setIcon(IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
		}
	}

}