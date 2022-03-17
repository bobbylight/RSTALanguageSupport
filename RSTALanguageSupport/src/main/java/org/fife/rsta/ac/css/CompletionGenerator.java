package org.fife.rsta.ac.css;

import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Generates completions based on the current user input.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface CompletionGenerator {


	/**
	 * Generates a list of completions based on the current user input.
	 *
	 * @param provider The completion provider querying for completions.
	 * @param input The current user input.
	 * @return The list of completions.  This may be <code>null</code> if
	 *         no completions are appropriate.
	 */
	List<Completion> generate(CompletionProvider provider, String input);


}
