/*
 * 11/28/2013
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
		setCommentCompletionProvider(createCommentCompletionProvider());
	}


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 * @see #createCommentCompletionProvider()
	 */
	protected CompletionProvider createCodeCompletionProvider() {
		return new PropertyValueCompletionProvider(false);
	}


	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 */
	protected CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}


}