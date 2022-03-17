/*
 * 12/04/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.css;

import javax.swing.Icon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A basic completion type for CSS code completion.  Basically just a
 * {@link BasicCompletion} with a key for the icon for this completion type.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class BasicCssCompletion extends BasicCompletion {

	private String iconKey;


	BasicCssCompletion(CompletionProvider provider, String value,
			String iconKey) {
		super(provider, value);
		this.iconKey = iconKey;
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(iconKey);
	}


}
