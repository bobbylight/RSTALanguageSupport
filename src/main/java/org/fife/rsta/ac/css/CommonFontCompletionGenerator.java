/*
 * 04/19/2015
 *
 * Copyright (C) 2015 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A generator that returns completions for common font names (not the
 * standardized generic fonts defined in the CSS spec).
 * 
 * @author Robert Futrell
 * @version 1.0
 */
class CommonFontCompletionGenerator implements CompletionGenerator {

	private static final String ICON_KEY = "css_propertyvalue_identifier";/*font*/


	/**
	 * {@inheritDoc}
	 */
	public List<Completion> generate(CompletionProvider provider, String input){

		List<Completion> completions = new ArrayList<Completion>();

		completions.add(new FontFamilyCompletion(provider, "Georgia"));
		completions.add(new FontFamilyCompletion(provider, "\"Times New Roman\""));
		completions.add(new FontFamilyCompletion(provider, "Arial"));
		completions.add(new FontFamilyCompletion(provider, "Helvetica"));
		completions.add(new FontFamilyCompletion(provider, "Impact"));
		completions.add(new FontFamilyCompletion(provider, "\"Lucida Sans Unicode\""));
		completions.add(new FontFamilyCompletion(provider, "Tahoma"));
		completions.add(new FontFamilyCompletion(provider, "Verdana"));
		completions.add(new FontFamilyCompletion(provider, "Geneva"));
		completions.add(new FontFamilyCompletion(provider, "\"Courier New\""));
		completions.add(new FontFamilyCompletion(provider, "Courier"));
		completions.add(new FontFamilyCompletion(provider, "\"Lucida Console\""));
		completions.add(new FontFamilyCompletion(provider, "Menlo"));
		completions.add(new FontFamilyCompletion(provider, "Monaco"));
		completions.add(new FontFamilyCompletion(provider, "Consolas"));

		return completions;
	}


	/**
	 * The type of completion returned by this generator.
	 * TODO: Get rid of this class and share code with someone else.
	 */
	private static class FontFamilyCompletion extends BasicCssCompletion {

		public FontFamilyCompletion(CompletionProvider provider, String value) {
			super(provider, value, ICON_KEY);
		}

	}


}