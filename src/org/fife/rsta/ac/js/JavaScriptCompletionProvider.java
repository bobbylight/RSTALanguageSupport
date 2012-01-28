/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js;

import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.mozilla.javascript.ast.AstRoot;


/**
 * Completion provider for JavaScript.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptCompletionProvider extends
		LanguageAwareCompletionProvider {

	/**
	 * The AST for the JS.
	 */
	private AstRoot astRoot;

	/**
	 * The provider used for source code, kept here since it's used so much.
	 */
	private SourceCompletionProvider sourceProvider;


	public JavaScriptCompletionProvider() {
		super(new SourceCompletionProvider());
		this.sourceProvider = (SourceCompletionProvider)
										getDefaultCompletionProvider();
		sourceProvider.setParent(this);
		//setDocCommentCompletionProvider(new DocCommentCompletionProvider());

	}


	/**
	 * Returns the AST for the JavaScript in the editor.
	 *
	 * @return The AST.
	 */
	public synchronized AstRoot getASTRoot() {
		return astRoot;
	}


	/**
	 * Sets the AST for the JavaScript in this editor.
	 *
	 * @param root The AST.
	 */
	public synchronized void setASTRoot(AstRoot root) {
		this.astRoot = root;
	}


}