/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.less;

import org.fife.rsta.ac.css.CssCompletionProvider;
import org.fife.rsta.ac.css.CssLanguageSupport;
import org.fife.ui.autocomplete.DescWindowVisibility;


/**
 * Language support for Less.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LessLanguageSupport extends CssLanguageSupport {


	/**
	 * Constructor.
	 */
	public LessLanguageSupport() {
		setDescWindowVisibility(DescWindowVisibility.ALWAYS);
	}


	/**
	 * Overridden to return a completion provider that understands Less.
	 *
	 * @return A completion provider to use for this language.
	 */
	@Override
	protected CssCompletionProvider createProvider() {
		return new LessCompletionProvider();
	}


}