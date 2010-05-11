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
package org.fife.rsta.ac.sh;

import java.io.File;

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * A completion provider for Unix shell scripts.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ShellCompletionProvider extends CCompletionProvider {

	/**
	 * Whether local man pages should be used for function descriptions.
	 */
	private static boolean useLocalManPages;


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
	 * {@inheritDoc}
	 */
	public char getParameterListEnd() {
		return '\0';
	}


	/**
	 * {@inheritDoc}
	 */
	public char getParameterListStart() {
		return '\0';
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
	public static boolean getUseLocalManPages() {
		return useLocalManPages;
	}


	/**
	 * {@inheritDoc}
	 */
	protected String getXmlResource() {
		return "data/sh.xml";
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
	public static void setUseLocalManPages(boolean use) {
		useLocalManPages = use;
	}



	static {
		useLocalManPages = File.separatorChar=='/';
	}


}