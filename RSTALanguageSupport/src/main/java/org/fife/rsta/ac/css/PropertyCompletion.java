/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.css;

import javax.swing.Icon;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;


/**
 * A completion for a CSS property name.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PropertyCompletion extends ShorthandCompletion {

	private String iconKey;


	PropertyCompletion(CompletionProvider provider, String property,
			String iconKey) {
		super(provider, property, property + ": ");
		this.iconKey = iconKey;
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(iconKey);
	}


}
