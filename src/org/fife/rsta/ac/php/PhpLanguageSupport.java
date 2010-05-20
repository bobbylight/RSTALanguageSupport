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
package org.fife.rsta.ac.php;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for PHP.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PhpLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all PHP text areas.
	 */
	private PhpCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public PhpLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * Lazily creates the shared completion provider instance for PHP.
	 *
	 * @return The completion provider.
	 */
	private PhpCompletionProvider getProvider() {
		if (provider==null) {
			provider = new PhpCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		PhpCompletionProvider provider = getProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setListCellRenderer(new HtmlCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		textArea.putClientProperty(PROPERTY_AUTO_COMPLETION, ac);
		textArea.setToolTipSupplier(null);

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