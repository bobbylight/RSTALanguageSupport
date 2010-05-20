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
package org.fife.rsta.ac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * Provides language support (code completion, etc.) for programming
 * languages in RSyntaxTextArea.  Different languages may  have varying
 * levels of "support."
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LanguageSupportFactory implements PropertyChangeListener {

	private static LanguageSupportFactory INSTANCE =
										new LanguageSupportFactory();

	/**
	 * Maps syntax styles to supports.  We cheat and initially map styles to
	 * class-names-for-supports, and lazily create the actual
	 * <code>LanguageSupports</code> when necessary.
	 */
	private Map styleToSupport;

	/**
	 * Client property set on RSyntaxTextAreas that points to the current
	 * language support for that text area.
	 */
	private static final String LANGUAGE_SUPPORT_PROPERTY =
									"org.fife.rsta.ac.LanguageSupport";


	/**
	 * Constructor.
	 */
	private LanguageSupportFactory() {
		createSupportMap();
	}


	/**
	 * Creates the mapping of syntax styles to language supports.
	 */
	private void createSupportMap() {

		styleToSupport = new HashMap();

		String prefix = "org.fife.rsta.ac.";

		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_C,
				prefix + "c.CLanguageSupport");
		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_HTML,
				prefix + "html.HtmlLanguageSupport");
		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_JAVA,
				prefix + "java.JavaLanguageSupport");
		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_PERL,
				prefix + "perl.PerlLanguageSupport");
		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_PHP,
				prefix + "php.PhpLanguageSupport");
		styleToSupport.put(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL,
				prefix +"sh.ShellLanguageSupport");

	}


	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The singleton instance.
	 */
	public static LanguageSupportFactory get() {
		return INSTANCE;
	}


	/**
	 * Returns the language support for a programming language.
	 *
	 * @param style The language.  This should be one of the constants defined
	 *        in {@link SyntaxConstants}.
	 * @return The language support, or <code>null</code> if none is registered
	 *         for the language specified.
	 */
	public LanguageSupport getSupportFor(String style) {

		LanguageSupport support = null;

		Object obj = styleToSupport.get(style);
		if (obj instanceof String) {
			try {
				Class clazz = Class.forName((String)obj);
				support = (LanguageSupport)clazz.newInstance();
			} catch (RuntimeException re) { // FindBugs
				throw re;
			} catch (Exception e) {
				// Fall through with support==null, so we don't try again
				e.printStackTrace();
			}
			styleToSupport.put(style, support);
			return support;
		}

		return (LanguageSupport)obj;

	}


	/**
	 * Installs language support on an RSTA depending on its syntax style.
	 *
	 * @param textArea The text area to install language support on.
	 * @see #uninstallSupport(RSyntaxTextArea)
	 */
	private void installSupport(RSyntaxTextArea textArea) {
		String style = textArea.getSyntaxEditingStyle();
		LanguageSupport support = getSupportFor(style);
		if (support!=null) {
			support.install(textArea);
		}
		textArea.putClientProperty(LANGUAGE_SUPPORT_PROPERTY, support);
	}


	/**
	 * Listens for RSyntaxTextAreas to change what language they're
	 * highlighting, so language support can be updated appropriately.
	 *
	 * @param e The event.
	 */
	public void propertyChange(PropertyChangeEvent e) {

		RSyntaxTextArea source = (RSyntaxTextArea)e.getSource();
		String name = e.getPropertyName();
		if (RSyntaxTextArea.SYNTAX_STYLE_PROPERTY.equals(name)) {
			uninstallSupport(source);
			installSupport(source);
		}

	}


	/**
	 * Registers an RSyntaxTextArea to receive language support.  The text area
	 * will get support for the currently highlighted language, and if it
	 * changes what language it is highlighting, the support will change as
	 * appropriate.
	 *
	 * @param textArea The text area to register.
	 */
	public void register(RSyntaxTextArea textArea) {
		installSupport(textArea);
		textArea.addPropertyChangeListener(
				RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, this);
	}


	/**
	 * Uninstalls the language support on an RSyntaxTextArea, if any.
	 *
	 * @param textArea The text area.
	 * @see #installSupport(RSyntaxTextArea)
	 */
	private void uninstallSupport(RSyntaxTextArea textArea) {
		LanguageSupport support = (LanguageSupport)textArea.getClientProperty(
												LANGUAGE_SUPPORT_PROPERTY);
		if (support!=null) {
			support.uninstall(textArea);
		}
	}


	/**
	 * Un-registers an RSyntaxTextArea.  This removes any language support
	 * on it.
	 *
	 * @param textArea The text area.
	 * @see #register(RSyntaxTextArea)
	 */
	public void unregister(RSyntaxTextArea textArea) {
		uninstallSupport(textArea);
		textArea.removePropertyChangeListener(
				RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, this);
	}


}