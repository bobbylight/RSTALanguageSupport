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
package org.fife.rsta.ac.perl;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.perl.PerlCompletionProvider;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Perl.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PerlLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all Perl text areas.
	 */
	private PerlCompletionProvider provider;


	/**
	 * Lazily creates the shared completion provider instance for Perl.
	 *
	 * @return The completion provider.
	 */
	private PerlCompletionProvider getProvider() {
		if (provider==null) {
			provider = new PerlCompletionProvider();
		}
		return provider;
	}


	/**
	 * Returns whether parens are inserted when auto-completing functions.
	 *
	 * @return Whether parens are inserted.
	 * @see #setUseParensWithFunctions(boolean)
	 */
	public boolean getUseParensWithFunctions() {
		return getProvider().getUseParensWithFunctions();
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		PerlCompletionProvider provider = getProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		//ac.setListCellRenderer(new CCellRenderer());
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);
		ac.install(textArea);
		textArea.putClientProperty(PROPERTY_AUTO_COMPLETION, ac);
		textArea.setToolTipSupplier(provider);

	}


	/**
	 * Toggles whether parens are inserted when auto-completing functions.
	 *
	 * @param use Whether parens are inserted.
	 * @see #getUseParensWithFunctions()
	 */
	public void setUseParensWithFunctions(boolean use) {
		getProvider().setUseParensWithFunctions(use);
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