/*
 * 08/22/2015
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.less;

import org.fife.rsta.ac.css.CssCompletionProvider;
import org.fife.rsta.ac.css.CssLanguageSupport;


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
		setShowDescWindow(true);
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