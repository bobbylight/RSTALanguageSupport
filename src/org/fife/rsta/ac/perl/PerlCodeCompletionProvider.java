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

import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * The completion provider for Perl code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PerlCodeCompletionProvider extends DefaultCompletionProvider {

	private PerlCompletionProvider parent;


	public PerlCodeCompletionProvider(PerlCompletionProvider parent) {
		this.parent = parent;
	}


	public char getParameterListEnd() {
		return parent.getParameterListEnd();
	}


	public char getParameterListStart() {
		return parent.getParameterListStart();
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isValidChar(char ch) {
		return super.isValidChar(ch) || ch=='@' || ch=='$' || ch=='%';
	}


}