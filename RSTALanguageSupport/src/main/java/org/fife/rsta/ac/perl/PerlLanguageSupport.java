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
package org.fife.rsta.ac.perl;

import java.io.File;
import javax.swing.*;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.IOUtil;
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
	 * The Perl install location currently being used.
	 */
	private static File perlInstallLoc;

	/**
	 * The root directory of the default Perl install.
	 */
	private static final File DEFAULT_PERL_INSTALL_LOC;

	/**
	 * Whether parens should be used around arguments to functions.
	 */
	private static boolean useParensWithFunctions;

	/**
	 * Whether to use the system "perldoc" command for function descriptions.
	 * This parameter is ignored if {@link #DEFAULT_PERL_INSTALL_LOC} is
	 * <code>false</code>.
	 */
	private static boolean useSystemPerldoc;


	/*
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
            for (String dir : dirs) {
                File temp = new File(dir, perlLoc);
                //System.out.println(temp.getAbsolutePath());
                if (temp.isFile()) {
					perlInstallLoc = new File(dir).getParentFile();
                    break;
                }
            }

		}

		DEFAULT_PERL_INSTALL_LOC = perlInstallLoc;
	}


	/**
	 * Constructor.
	 */
	public PerlLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
		CompletionCellRenderer ccr = new CompletionCellRenderer();
		ccr.setShowTypes(false);
		return ccr;
	}


	/**
	 * Returns the location of the first Perl install located on the user's
	 * PATH.
	 *
	 * @return The "default" location at which Perl is installed.
	 * @see #getPerlInstallLocation()
	 */
	public static File getDefaultPerlInstallLocation() {
		return DEFAULT_PERL_INSTALL_LOC;
	}


	/**
	 * Returns the Perl install that is being used for syntax checking.
	 *
	 * @return The location at which Perl is installed, or <code>null</code>
	 *         if none is currently selected.
	 * @see #setPerlInstallLocation(File)
	 */
	public static File getPerlInstallLocation() {
		return perlInstallLoc;
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
	 * Returns the Perl parser running on a text area with this Perl language
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
	 * Returns the value to use for <code>PERL5LIB</code> when parsing Perl
	 * code.
	 *
	 * @return The value, or <code>null</code> to use the system default.
	 * @see #setPerl5LibOverride(String)
	 */
	public String getPerl5LibOverride() {
		return getParser().getPerl5LibOverride();
	}


	/**
	 * Returns whether parens are inserted when auto-completing functions.
	 *
	 * @return Whether parens are inserted.
	 * @see #setUseParensWithFunctions(boolean)
	 */
	public boolean getUseParensWithFunctions() {
		return useParensWithFunctions;
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
	 * Returns whether warnings are enabled when checking syntax.
	 *
	 * @return Whether warnings are enabled.
	 * @see #setWarningsEnabled(boolean)
	 */
	public boolean getWarningsEnabled() {
		return getParser().getWarningsEnabled();
	}


	@Override
	public void install(RSyntaxTextArea textArea) {

		PerlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

		PerlParser parser = getParser();
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);

	}

	@Override
	public void install(RSyntaxTextArea textArea, KeyStroke keyStroke) {
		PerlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		ac.setTriggerKey(keyStroke);

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
		return getParser().isEnabled();
	}


	/**
	 * Returns whether taint mode is enabled when checking syntax.
	 *
	 * @return Whether taint mode is enabled.
	 * @see #setTaintModeEnabled(boolean)
	 */
	public boolean isTaintModeEnabled() {
		return getParser().isTaintModeEnabled();
	}


	/**
	 * Toggles whether text areas with this language support installed are
	 * parsed for syntax errors.
	 *
	 * @param enabled Whether parsing should be enabled.
	 * @see #isParsingEnabled()
	 */
	public void setParsingEnabled(boolean enabled) {
		getParser().setEnabled(enabled);
	}


	/**
	 * Sets the value to use for <code>PERL5LIB</code> when parsing Perl
	 * code.
	 *
	 * @param override The value, or <code>null</code> to use the system
	 *        default.
	 * @see #getPerl5LibOverride()
	 */
	public void setPerl5LibOverride(String override) {
		getParser().setPerl5LibOverride(override);
	}


	/**
	 * Sets the Perl install to use for syntax checking, perldoc, etc.
	 *
	 * @param loc The root directory of the Perl installation, or
	 *        <code>null</code> for none.
	 * @see #getPerlInstallLocation()
	 */
	public static void setPerlInstallLocation(File loc) {
		perlInstallLoc = loc;
	}


	/**
	 * Toggles whether taint mode is enabled when checking syntax.
	 *
	 * @param enabled Whether taint mode should be enabled.
	 * @see #isTaintModeEnabled()
	 */
	public void setTaintModeEnabled(boolean enabled) {
		getParser().setTaintModeEnabled(enabled);
	}


	/**
	 * Toggles whether warnings are returned when checking syntax.
	 *
	 * @param enabled Whether warnings are enabled.
	 * @see #getWarningsEnabled()
	 */
	public void setWarningsEnabled(boolean enabled) {
		getParser().setWarningsEnabled(enabled);
	}


	/**
	 * Toggles whether parens are inserted when auto-completing functions.
	 *
	 * @param use Whether parens are inserted.
	 * @see #getUseParensWithFunctions()
	 */
	public void setUseParensWithFunctions(boolean use) {
		if (use!=useParensWithFunctions) {
			useParensWithFunctions = use;
			if (provider!=null) {
				provider.setUseParensWithFunctions(use);
			}
		}
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
	@Override
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		PerlParser parser = getParser(textArea);
		if (parser!=null) {
			textArea.removeParser(parser);
		}

	}


}
