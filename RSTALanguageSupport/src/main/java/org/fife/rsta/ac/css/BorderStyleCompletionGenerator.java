/*
 * 05/03/2014
 *
 * Copyright (C) 2014 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.css;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A generator that returns completions for border styles.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class BorderStyleCompletionGenerator implements CompletionGenerator {

	private static final String ICON_KEY = "css_propertyvalue_identifier";


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Completion> generate(CompletionProvider provider, String input){

		List<Completion> completions = new ArrayList<>();

		completions.add(new BorderStyleCompletion(provider, "none"));
		completions.add(new BorderStyleCompletion(provider, "hidden"));
		completions.add(new BorderStyleCompletion(provider, "dotted"));
		completions.add(new BorderStyleCompletion(provider, "dashed"));
		completions.add(new BorderStyleCompletion(provider, "solid"));
		completions.add(new BorderStyleCompletion(provider, "double"));
		completions.add(new BorderStyleCompletion(provider, "groove"));
		completions.add(new BorderStyleCompletion(provider, "ridge"));
		completions.add(new BorderStyleCompletion(provider, "inset"));
		completions.add(new BorderStyleCompletion(provider, "outset"));

		return completions;
	}


	/**
	 * The type of completion returned by this generator.
	 */
	private static class BorderStyleCompletion extends BasicCssCompletion {

		BorderStyleCompletion(CompletionProvider provider, String value) {
			super(provider, value, ICON_KEY);
		}

	}


}
