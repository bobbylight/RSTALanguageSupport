/*
 * 11/21/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for CSS files.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CssLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing C.
	 */
	private CssCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public CssLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	private CssCompletionProvider getProvider() {
		if (provider==null) {
			provider = new CssCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		CssCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}


}