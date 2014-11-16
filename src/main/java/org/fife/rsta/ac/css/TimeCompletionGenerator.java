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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A generator that returns completions for CSS percentage/length values.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
class TimeCompletionGenerator implements CompletionGenerator {

	private static final String ICON_KEY = "css_propertyvalue_identifier";/*time*/

	private static final Pattern DIGITS = Pattern.compile("\\d*");


	/**
	 * {@inheritDoc}
	 */
	public List<Completion> generate(CompletionProvider provider, String input){

		List<Completion> completions = new ArrayList<Completion>();

		if (DIGITS.matcher(input).matches()) {
			completions.add(new TimeCompletion(provider, input + "s"));
			completions.add(new TimeCompletion(provider, input + "ms"));
		}

		return completions;
	}


	/**
	 * The type of completion returned by this generator.
	 * TODO: Get rid of this class and share code with someone else.
	 */
	private static class TimeCompletion extends BasicCssCompletion {

		public TimeCompletion(CompletionProvider provider, String value) {
			super(provider, value, ICON_KEY);
		}

	}


}