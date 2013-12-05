/*
 * 12/04/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import javax.swing.Icon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A completion for a CSS value.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ValueCompletion extends BasicCompletion {

	private String iconKey;


	public ValueCompletion(CompletionProvider provider, String value,
			String iconKey) {
		super(provider, value);
		this.iconKey = iconKey;
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(iconKey);
	}


}