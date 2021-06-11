/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.asm6502;

import org.fife.rsta.ac.css.PropertyValueCompletionProvider;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;


/**
 * A completion provider for 6502 assembler.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Asm6502CompletionProvider extends LanguageAwareCompletionProvider {


	/**
	 * Constructor.
	 */
	public Asm6502CompletionProvider() {
		setDefaultCompletionProvider(createCodeCompletionProvider());
		setCommentCompletionProvider(createCommentCompletionProvider());
	}


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 * @see #createCommentCompletionProvider()
	 */
	private CompletionProvider createCodeCompletionProvider() {
		return new Asm6502CodeCompletionProvider();
	}


	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 */
	private CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}


}
