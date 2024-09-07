/*
 * 07/05/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.jsp;

import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.rsta.ac.html.HtmlLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for JSP.  Features currently include:
 *
 * <ul>
 *    <li>Code completion for HTML5 tags and attributes.</li>
 *    <li>Code completion for JSTL.</li>
 *    <li>Automatic creation of closing tags for non-self-closing tags.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JspLanguageSupport extends AbstractMarkupLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all JSP text areas.
	 */
	private JspCompletionProvider provider;

	/**
	 * A cached set of tags that require closing tags.
	 */
	private static Set<String> tagsToClose = new HashSet<>();


	/**
	 * Constructor.
	 */
	public JspLanguageSupport() {
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(false);
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


	private JspCompletionProvider getProvider() {
		if (provider==null) {
			provider = new JspCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {

		HtmlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		installKeyboardShortcuts(textArea);

		textArea.setToolTipSupplier(null);

	}

	@Override
	public void install(RSyntaxTextArea textArea, KeyStroke keyStroke) {
		HtmlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		ac.setTriggerKey(keyStroke);
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
