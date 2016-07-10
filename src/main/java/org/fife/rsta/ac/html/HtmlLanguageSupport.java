/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for HTML.  This currently provides the following:
 * 
 * <ul>
 *    <li>Code completion for HTML5 tags and attributes.</li>
 *    <li>Automatic creation of closing tags for non-self-closing tags.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HtmlLanguageSupport extends AbstractMarkupLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all HTML text areas.
	 */
	private HtmlCompletionProvider provider;

	/**
	 * A cached set of tags that require closing tags.
	 */
	private static Set<String> tagsToClose = new HashSet<String>();


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
	@Override
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
	 * Dirty hack to share this with others, such as PHP and JSP supports.
	 * Note that we should be passing doctype information here.
	 * 
	 * @return The set of tags to close.
	 */
	public static Set<String> getTagsToClose() {
		return tagsToClose;
	}


	/**
	 * Returns a set of tags that require a closing tag, based on a resource
	 * in this class's package.
	 *
	 * @param res The resource.
	 * @return The set of tags that require closing.
	 */
	private static final Set<String> getTagsToClose(String res) {
		Set<String> tags = new HashSet<String>();
		InputStream in = HtmlLanguageSupport.class.getResourceAsStream(res);
		if (in!=null) { // Never happens
			String line = null;
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				while ((line=r.readLine())!=null) {
					if (line.length()>0 && line.charAt(0)!='#') {
						tags.add(line.trim());
					}
				}
				r.close();
			} catch (IOException ioe) { // Never happens
				ioe.printStackTrace();
			}
		}
		return tags;
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


	static {
		tagsToClose = getTagsToClose("html5_close_tags.txt");
	}


}