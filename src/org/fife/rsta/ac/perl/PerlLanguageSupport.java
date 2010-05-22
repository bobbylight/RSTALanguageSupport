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

import java.io.File;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.IOUtil;
import org.fife.rsta.ac.perl.PerlCompletionProvider;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
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
	 * The parser.  This is shared amongst all Perl text areas.
	 */
	private PerlParser parser;

	/**
	 * The root directory of the Perl install.
	 */
	private static File perlInstallLocation;

	/**
	 * Whether to use the system "perldoc" command for function descriptions.
	 * This parameter is ignored if {@link #perlInstallLocation} is
	 * <code>false</code>.
	 */
	private static boolean useSystemPerldoc;


	/**
	 * Determine the Perl install on the user's PATH, if any.
	 */
	static {

		String path = IOUtil.getEnvSafely("PATH");

		if (path!=null) {

			String perlLoc = "perl";
			if (File.separatorChar=='\\') {
				perlLoc += ".exe";
			}

			String[] dirs = path.split(File.pathSeparator);
			for (int i=0; i<dirs.length; i++) {
				File temp = new File(dirs[i], perlLoc);
				System.out.println(temp.getAbsolutePath());
				if (temp.isFile()) {
					perlInstallLocation = new File(dirs[i]).getParentFile();
					break;
				}
			}

		}

	}


	/**
	 * Constructor.
	 */
	public PerlLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * Returns the location at which Perl is installed.
	 *
	 * @return The location at which Perl is installed.
	 * @see #setPerlInstallLocation(File)
	 */
	public static File getPerlInstallLocation() {
		return perlInstallLocation;
	}


	/**
	 * Returns the shared parser, lazily creating it if necessary.
	 *
	 * @return The parser.
	 */
	private PerlParser getParser() {
		if (parser==null) {
			parser = new PerlParser();
		}
		return parser;
	}


	/**
	 * Returns the Perl parser running on a text area with this Java language
	 * support installed.
	 *
	 * @param textArea The text area.
	 * @return The Perl parser.  This will be <code>null</code> if the text
	 *         area does not have this <tt>PerlLanguageSupport</tt> installed.
	 */
	public PerlParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof PerlParser) {
			return (PerlParser)parser;
		}
		return null;
	}


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
	 * Returns whether to use the system "perldoc" command when getting
	 * descriptions of Perl functions.  If this is <code>false</code>, then
	 * a built-in snapshot of Perl 5.10 descriptions will be used.  This will
	 * perform better, but may be out of date if your version of Perl is newer.
	 * <p>
	 *
	 * Note that this parameter is ignored if {@link #getPerlInstallLocation()}
	 * returns <code>null</code>.
	 *
	 * @return Whether to use the system "perldoc" command.
	 * @see #setUseSystemPerldoc(boolean)
	 */
	public static boolean getUseSystemPerldoc() {
		return useSystemPerldoc;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		PerlCompletionProvider provider = getProvider();
		AutoCompletion ac = new AutoCompletion(provider);
		CompletionCellRenderer ccr = new CompletionCellRenderer();
		ccr.setShowTypes(false);
		ac.setListCellRenderer(ccr);
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

		PerlParser parser = getParser();
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);

	}


	/**
	 * Returns whether text areas with this language support installed are
	 * parsed for syntax errors.<p>
	 * 
	 * Note that if {@link #getPerlInstallLocation()}
	 * returns <code>null</code> or an invalid value, parsing will not occur
	 * even if this value is <code>true</code>.
	 *
	 * @return Whether parsing is enabled.
	 * @see #setParsingEnabled(boolean)
	 */
	public boolean isParsingEnabled() {
System.out.println("DEBUG: isParsingEnabled() returning: " + getParser().isEnabled());
		return getParser().isEnabled();
	}


	/**
	 * Toggles whether text areas with this language support installed are
	 * parsed for syntax errors.
	 *
	 * @param enabled Whether parsing should be enabled.
	 * @see #isParsingEnabled()
	 */
	public void setParsingEnabled(boolean enabled) {
System.out.println("DEBUG: setParsingEnabled(" + enabled + ")");
		getParser().setEnabled(enabled);
	}


	/**
	 * Sets the location at which Perl is installed.
	 *
	 * @param loc The location at which Perl is installed.
	 * @see #getPerlInstallLocation()
	 */
	public static void setPerlInstallLocation(File loc) {
		perlInstallLocation = loc;
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
	 * Sets whether to use the system "perldoc" command when getting
	 * descriptions of Perl functions.  If this is <code>false</code>, then
	 * a built-in snapshot of Perl 5.10 descriptions will be used.  This will
	 * perform better, but may be out of date if your version of Perl is newer.
	 * <p>
	 *
	 * Note that this parameter is ignored if {@link #getPerlInstallLocation()}
	 * returns <code>null</code>.
	 *
	 * @param use Whether to use the system "perldoc" command.
	 * @see #getUseSystemPerldoc()
	 */
	public static void setUseSystemPerldoc(boolean use) {
		useSystemPerldoc = use;
	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		PerlParser parser = getParser(textArea);
		if (parser!=null) {
			textArea.removeParser(parser);
		}

	}


}