/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;


/**
 * A completion provider for CSS files.  It provides TODO: Finish me!
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CssCompletionProvider extends LanguageAwareCompletionProvider {


	/**
	 * Constructor.
	 */
	public CssCompletionProvider() {
		setDefaultCompletionProvider(createCodeCompletionProvider());
		setStringCompletionProvider(createStringCompletionProvider());
		setCommentCompletionProvider(createCommentCompletionProvider());
	}


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 * @see #createCommentCompletionProvider()
	 * @see #createStringCompletionProvider()
	 * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
	 */
	protected CompletionProvider createCodeCompletionProvider() {
		return new PropertyValueCompletionProvider();

	}


	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createStringCompletionProvider()
	 */
	protected CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}


	/**
	 * Returns the completion provider to use when the caret is in a string.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createCommentCompletionProvider()
	 */
	protected CompletionProvider createStringCompletionProvider() {
//		DefaultCompletionProvider cp = new DefaultCompletionProvider();
//		cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
//		cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
//		cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
//		cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
//		cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
//		cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
//		return cp;
return null;
	}


}