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
package org.fife.rsta.ac.sh;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Unix shell scripts.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ShellLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all sh text areas.
	 */
	private ShellCompletionProvider provider;


	/**
	 * Lazily creates the shared completion provider instance for sh scripts.
	 *
	 * @return The completion provider.
	 */
	private ShellCompletionProvider getProvider() {
		if (provider==null) {
			provider = new ShellCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		ShellCompletionProvider provider = getProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		ac.setListCellRenderer(new CompletionCellRenderer());
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(false);
		ac.install(textArea);
		textArea.putClientProperty(PROPERTY_AUTO_COMPLETION, ac);
		textArea.setToolTipSupplier(provider);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		AutoCompletion ac = (AutoCompletion)textArea.
								getClientProperty(PROPERTY_AUTO_COMPLETION);
		ac.uninstall();
	}


}