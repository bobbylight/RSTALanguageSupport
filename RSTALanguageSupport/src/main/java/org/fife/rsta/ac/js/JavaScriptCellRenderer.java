/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js;

import javax.swing.JList;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
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


	/**
	 * Constructor.
	 */
	public JavaScriptCellRenderer() {
	}


	@Override
	protected void prepareForOtherCompletion(JList list, Completion c,
			int index, boolean selected, boolean hasFocus) {
		super.prepareForOtherCompletion(list, c, index, selected, hasFocus);
		setIconWithDefault(c);
	}


	@Override
	protected void prepareForTemplateCompletion(JList list,
		TemplateCompletion tc, int index, boolean selected, boolean hasFocus) {
		super.prepareForTemplateCompletion(list, tc, index, selected, hasFocus);
		setIconWithDefault(tc, IconFactory.getIcon(IconFactory.TEMPLATE_ICON));
	}


	@Override
	protected void prepareForVariableCompletion(JList list,
			VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
		super.prepareForVariableCompletion(list, vc, index, selected, hasFocus);
		setIconWithDefault(vc, IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
	}


	@Override
	protected void prepareForFunctionCompletion(JList list,
			FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
		super.prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
		setIconWithDefault(fc, IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
	}


}
