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
package org.fife.rsta.ac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.fife.ui.autocomplete.LinkRedirector;
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
	 * Given the chance to modify external URL's in documentation when they
	 * are clicked.  This field is shared amongst all language supports.
	 */
	private LinkRedirector externalLinkRedirector;

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
	 * Adds language support for a language.  This is a hook for applications
	 * using this library to add language support for custom languages.
	 *
	 * @param style The language to add support for.  This should be one of
	 *        the values defined in {@link SyntaxConstants}.  Any previous
	 *        language support for this language is removed. 
	 * @param lsClassName The class name of the <code>LanguageSupport</code>.
	 */
	public void addLanguageSupport(String style, String lsClassName) {
		styleToSupport.put(style,  lsClassName);
	}


	/**
	 * Creates the mapping of syntax styles to language supports.
	 */
	private void createSupportMap() {

		styleToSupport = new HashMap();

		String prefix = "org.fife.rsta.ac.";

		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_C,
				prefix + "c.CLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_GROOVY,
				prefix + "groovy.GroovyLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_HTML,
				prefix + "html.HtmlLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_JAVA,
				prefix + "java.JavaLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT,
				prefix + "js.JavaScriptLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_JSP,
				prefix + "jsp.JspLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_PERL,
				prefix + "perl.PerlLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_PHP,
				prefix + "php.PhpLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL,
				prefix + "sh.ShellLanguageSupport");
		addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_XML,
				prefix + "xml.XmlLanguageSupport");

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
	 * Sets the redirector for external URL's found in code completion
	 * documentation.  When a non-local link is clicked, this redirector is
	 * given the chance to modify the URL fetched and displayed.<p>
	 * 
	 * This method must be called before retrieving any language support via
	 * {@link #getSupportFor(String)}, since language supports are cached and
	 * shared amongst all relevant <code>RSyntaxTextArea</code>s.  If you need
	 * to modify the link redirector at runtime, you can grab it from the
	 * getter method.
	 *
	 * @param redirector The new link redirector.
	 */
	public void setExternalLinkRedirector(LinkRedirector redirector) {
		this.externalLinkRedirector = redirector;
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