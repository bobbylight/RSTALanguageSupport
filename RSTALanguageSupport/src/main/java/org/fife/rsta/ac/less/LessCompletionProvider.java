/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.less;

import org.fife.rsta.ac.css.CssCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A completion provider for Less.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class LessCompletionProvider extends CssCompletionProvider {


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 */
	@Override
	protected CompletionProvider createCodeCompletionProvider() {
		return new LessCodeCompletionProvider();
	}


}