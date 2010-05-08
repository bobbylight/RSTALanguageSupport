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

import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * The completion provider for Perl code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PerlCodeCompletionProvider extends DefaultCompletionProvider {


	/**
	 * {@inheritDoc}
	 */
	public boolean isValidChar(char ch) {
		return super.isValidChar(ch) || ch=='@' || ch=='$' || ch=='%';
	}


}