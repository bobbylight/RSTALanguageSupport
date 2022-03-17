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
package org.fife.rsta.ac.php;

import java.util.HashSet;
import java.util.Set;
import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.html.HtmlLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for PHP.  Features currently include:
 *
 * <ul>
 *    <li>Code completion for PHP functions.</li>
 *    <li>Code completion for HTML5 tags and attributes.</li>
 *    <li>Automatic creation of closing tags for non-self-closing tags.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PhpLanguageSupport extends AbstractMarkupLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all PHP text areas.
	 */
	private PhpCompletionProvider provider;

	/**
	 * A cached set of tags that require closing tags.
	 */
	private static Set<String> tagsToClose = new HashSet<>();


	/**
	 * Constructor.
	 */
	public PhpLanguageSupport() {
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
		tagsToClose = HtmlLanguageSupport.getTagsToClose();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
		return new HtmlCellRenderer();
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
	@Override
	public void install(RSyntaxTextArea textArea) {

		PhpCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		installKeyboardShortcuts(textArea);

		textArea.setToolTipSupplier(null);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldAutoCloseTag(String tag) {
		return tagsToClose.contains(tag.toLowerCase());
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		uninstallKeyboardShortcuts(textArea);
	}


}
