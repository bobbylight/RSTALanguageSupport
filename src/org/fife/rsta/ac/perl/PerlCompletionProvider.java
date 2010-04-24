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

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * A completion provider for Perl.  It provides auto-completion for standard
 * Perl 5.10 functions.  This information is read from an XML file.<p>
 *
 * To toggle whether parameter assistance wraps your parameters in parens,
 * use the {@link #setUseParensWithFunctions(boolean)} method.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PerlCompletionProvider extends CCompletionProvider {

	private boolean useParensWithFunctions;


	/**
	 * {@inheritDoc}
	 */
	protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
		// Add nothing for now.
	}


	/**
	 * {@inheritDoc}
	 */
	protected CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		return cp;
	}


	/**
	 * Overridden to return the null char (meaning "no start character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The end character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	public char getParameterListEnd() {
System.out.println("Returning: " + (getUseParensWithFunctions() ? super.getParameterListEnd() : '\0'));
		return getUseParensWithFunctions() ? super.getParameterListEnd() : '\0';
	}


	/**
	 * Overridden to return the null char (meaning "no start character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The start character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	public char getParameterListStart() {
		return getUseParensWithFunctions() ? super.getParameterListEnd() : '\0';
	}


	/**
	 * Returns whether the user wants to use parens around parameters to
	 * functions.
	 *
	 * @return Whether to use parens around parameters to functions.
	 * @see #setUseParensWithFunctions(boolean)
	 */
	public boolean getUseParensWithFunctions() {
		return useParensWithFunctions;
	}


	/**
	 * {@inheritDoc}
	 */
	protected String getXmlResource() {
		return "data/perl5.xml";
	}


	/**
	 * Sets whether the user wants to use parens around parameters to
	 * functions.
	 *
	 * @param use Whether to use parens around parameters to functions.
	 * @see #getUseParensWithFunctions()
	 */
	public void setUseParensWithFunctions(boolean use) {
		useParensWithFunctions = use;
	}


}