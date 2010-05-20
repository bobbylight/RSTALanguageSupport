/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.c;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for C.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing C.
	 */
	private CCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public CLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * {@inheritDoc}
	 */
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		CCellRenderer r = new CCellRenderer();
		//r.setAlternateBackground(null);
		return r;
	}


	private CCompletionProvider getProvider() {
		if (provider==null) {
			provider = new CCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		CCompletionProvider provider = getProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setListCellRenderer(getDefaultCompletionCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		textArea.putClientProperty(PROPERTY_AUTO_COMPLETION, ac);
		textArea.setToolTipSupplier(provider);

		addAutoCompletion(ac);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		AutoCompletion ac = (AutoCompletion)textArea.
								getClientProperty(PROPERTY_AUTO_COMPLETION);
		ac.uninstall();
		removeAutoCompletion(ac);
	}


}