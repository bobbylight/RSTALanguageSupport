/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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


	PerlCodeCompletionProvider(PerlCompletionProvider parent) {
		this.parent = parent;
	}


	@Override
	public char getParameterListEnd() {
		return parent.getParameterListEnd();
	}


	@Override
	public char getParameterListStart() {
		return parent.getParameterListStart();
	}


	@Override
	public boolean isValidChar(char ch) {
		return super.isValidChar(ch) || ch=='@' || ch=='$' || ch=='%';
	}


}
