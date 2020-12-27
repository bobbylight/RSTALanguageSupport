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
class PercentageOrLengthCompletionGenerator implements CompletionGenerator {

	private boolean includePercentage;

	private static final String ICON_KEY = "css_propertyvalue_unit";

	private static final Pattern DIGITS = Pattern.compile("\\d*");


	public PercentageOrLengthCompletionGenerator(boolean includePercentage) {
		this.includePercentage = includePercentage;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Completion> generate(CompletionProvider provider, String input){

		List<Completion> completions = new ArrayList<>();

		if (DIGITS.matcher(input).matches()) {

			// Font-relative lengths
			completions.add(new POrLCompletion(provider, input + "em"));
			completions.add(new POrLCompletion(provider, input + "ex"));
			completions.add(new POrLCompletion(provider, input + "ch"));
			completions.add(new POrLCompletion(provider, input + "rem"));

			// Viewport-percentage lengths
			completions.add(new POrLCompletion(provider, input + "vh"));
			completions.add(new POrLCompletion(provider, input + "vw"));
			completions.add(new POrLCompletion(provider, input + "vmin"));
			completions.add(new POrLCompletion(provider, input + "vmax"));

			// Absolute length units
			completions.add(new POrLCompletion(provider, input + "px"));
			completions.add(new POrLCompletion(provider, input + "in"));
			completions.add(new POrLCompletion(provider, input + "cm"));
			completions.add(new POrLCompletion(provider, input + "mm"));
			completions.add(new POrLCompletion(provider, input + "pt"));
			completions.add(new POrLCompletion(provider, input + "pc"));

			if (includePercentage) {
				completions.add(new POrLCompletion(provider, input + "%"));
			}

		}

		return completions;
	}


	/**
	 * The type of completion returned by this generator.
	 */
	private static class POrLCompletion extends BasicCssCompletion {

		public POrLCompletion(CompletionProvider provider, String value) {
			super(provider, value, ICON_KEY);
		}

	}


}
