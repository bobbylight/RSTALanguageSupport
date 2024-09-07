/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.c;

import javax.swing.*;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.asm6502.Asm6502CompletionProvider;
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
	@Override
	protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
		return new CCellRenderer();
	}

	@Override
	public void install(RSyntaxTextArea textArea, KeyStroke keyStroke) {
		AutoCompletion ac= defaultInstallation(textArea);
		ac.setTriggerKey(keyStroke);

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
	@Override
	public void install(RSyntaxTextArea textArea) {
		defaultInstallation(textArea);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}

	private AutoCompletion defaultInstallation(RSyntaxTextArea textArea) {
		CCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		textArea.setToolTipSupplier(provider);
		return ac;
	}

}
