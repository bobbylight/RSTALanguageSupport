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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.js.completion.JSCompletionUI;
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
	
	private JavaScriptLanguageSupport languageSupport;


	public JavaScriptCompletionProvider(JarManager jarManager, JavaScriptLanguageSupport languageSupport) {
		this(new SourceCompletionProvider(), jarManager, languageSupport);
	}


	public JavaScriptCompletionProvider(SourceCompletionProvider provider,
			JarManager jarManager, JavaScriptLanguageSupport languageSupport) {
		super(provider);
		this.sourceProvider = (SourceCompletionProvider) getDefaultCompletionProvider();
		this.sourceProvider.setJarManager(jarManager);
		this.languageSupport = languageSupport;
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
	
	public JavaScriptLanguageSupport getLanguageSupport() {
		return languageSupport;
	}


	/**
	 * Sets the AST for the JavaScript in this editor.
	 * 
	 * @param root The AST.
	 */
	public synchronized void setASTRoot(AstRoot root) {
		this.astRoot = root;
	}


	public List getCompletions(JTextComponent comp) {
		List completions = super.getCompletions(comp);
		Collections.sort(completions, new CompletionSort());
		return completions;
	}


	private class CompletionSort implements Comparator {

		public int compare(Object o1, Object o2) {
			if (o1 == o2) {
				return 0;
			}

			if (o1 instanceof JSCompletionUI && o2 instanceof JSCompletionUI) {
				return new Integer(((JSCompletionUI) o1).getSortIndex())
						.compareTo(new Integer(((JSCompletionUI) o2)
								.getSortIndex()));
			}

			return -1;
		}

	}
}