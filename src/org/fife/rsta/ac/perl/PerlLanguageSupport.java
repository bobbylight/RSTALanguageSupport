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
	 * Returns the location at which Perl is installed.
	 *
	 * @return The location at which Perl is installed.
	 * @see #setPerlInstallLocation(File)
	 */
	public static File getPerlInstallLocation() {
		return perlInstallLocation;
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
		ac.setShowDescWindow(true);
		ac.setParameterAssistanceEnabled(true);
		ac.install(textArea);
		textArea.putClientProperty(PROPERTY_AUTO_COMPLETION, ac);
		textArea.setToolTipSupplier(provider);
PerlParser parser = new PerlParser();
textArea.addParser(parser);
textArea.putClientProperty("perlParser", parser);
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

		AutoCompletion ac = (AutoCompletion)textArea.
								getClientProperty(PROPERTY_AUTO_COMPLETION);
		ac.uninstall();

		PerlParser parser = (PerlParser)textArea.getClientProperty("perlParser");
		if (parser!=null) {
			textArea.removeParser(parser);
		}

	}


}