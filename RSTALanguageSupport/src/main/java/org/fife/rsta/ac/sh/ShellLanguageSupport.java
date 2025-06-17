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
package org.fife.rsta.ac.sh;

import java.io.File;
import javax.swing.ListCellRenderer;

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
	 * Whether local man pages should be used.
	 */
	private boolean useLocalManPages;


	/**
	 * Constructor.
	 */
	public ShellLanguageSupport() {
		setParameterAssistanceEnabled(false);
		setShowDescWindow(true);
		useLocalManPages = File.separatorChar=='/';
	}


	@Override
	protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
		return new CompletionCellRenderer();
	}


	/**
	 * Lazily creates the shared completion provider instance for sh scripts.
	 *
	 * @return The completion provider.
	 */
	private ShellCompletionProvider getProvider() {
		if (provider==null) {
			provider = new ShellCompletionProvider();
			ShellCompletionProvider.setUseLocalManPages(getUseLocalManPages());
		}
		return provider;
	}


	/**
	 * Returns whether the local system's man pages should be used for
	 * descriptions of functions.  If this returns <tt>false</tt>, or man
	 * cannot be found (e.g. if this is Windows), a shorter description will
	 * be used instead.
	 *
	 * @return Whether to use the local man pages in function descriptions.
	 * @see #setUseLocalManPages(boolean)
	 */
	public boolean getUseLocalManPages() {
		return useLocalManPages;
	}


	@Override
	public void install(RSyntaxTextArea textArea) {

		ShellCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}


	/**
	 * Sets whether the local system's man pages should be used for
	 * descriptions of functions.  If this is set to <tt>false</tt>, or man
	 * cannot be found (e.g. if this is Windows), a shorter description will
	 * be used instead.
	 *
	 * @param use Whether to use the local man pages in function descriptions.
	 * @see #getUseLocalManPages()
	 */
	public void setUseLocalManPages(boolean use) {
		if (use!=useLocalManPages) {
			useLocalManPages = use;
			if (provider!=null) {
				ShellCompletionProvider.setUseLocalManPages(useLocalManPages);
			}
		}
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}
