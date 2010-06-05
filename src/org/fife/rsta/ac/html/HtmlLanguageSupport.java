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
package org.fife.rsta.ac.html;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for HTML.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HtmlLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all HTML text areas.
	 */
	private HtmlCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public HtmlLanguageSupport() {
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(false);
		setShowDescWindow(true);
	}


	/**
	 * {@inheritDoc}
	 */
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new HtmlCellRenderer();
	}


	private HtmlCompletionProvider getProvider() {
		if (provider==null) {
			provider = new HtmlCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		HtmlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(null);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}