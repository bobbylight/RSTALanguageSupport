/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import org.fife.rsta.ac.java.JarManager;
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
	 * Jar Manager for source completions
	 */
	/**
	 * The AST for the JS.
	 */
	private AstRoot astRoot;

	/**
	 * The provider used for source code, kept here since it's used so much.
	 */
	private SourceCompletionProvider sourceProvider;


	public JavaScriptCompletionProvider(JarManager jarManager) {
		this(new SourceCompletionProvider(), jarManager);
	}


	public JavaScriptCompletionProvider(SourceCompletionProvider provider,
			JarManager jarManager) {
		super(provider);
		this.sourceProvider = (SourceCompletionProvider) getDefaultCompletionProvider();
		this.sourceProvider.setJarManager(jarManager);
		sourceProvider.setParent(this);

		// setDocCommentCompletionProvider(new DocCommentCompletionProvider());
	}


	/**
	 * Returns the AST for the JavaScript in the editor.
	 * 
	 * @return The AST.
	 */
	public synchronized AstRoot getASTRoot() {
		return astRoot;
	}


	public JarManager getJarManager() {
		return ((SourceCompletionProvider) getDefaultCompletionProvider())
				.getJarManager();
	}


	/**
	 * Sets the AST for the JavaScript in this editor.
	 * 
	 * @param root The AST.
	 */
	public synchronized void setASTRoot(AstRoot root) {
		this.astRoot = root;
	}


	public void setJarManager(JarManager jarManager) {
		((SourceCompletionProvider) getDefaultCompletionProvider())
				.setJarManager(jarManager);
	}

}