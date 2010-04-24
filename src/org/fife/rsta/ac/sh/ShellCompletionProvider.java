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
	 * {@inheritDoc}
	 */
	protected String getXmlResource() {
		return "data/sh.xml";
	}


}