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

import java.util.Iterator;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.ShorthandCompletionCache;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
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
		
		setShorthandCompletionCache(new JavaScriptShorthandCompletionCache(sourceProvider, new DefaultCompletionProvider(), languageSupport.isXmlAvailable()));
		sourceProvider.setParent(this);
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
	 * Set short hand completion cache
	 */
	public void setShorthandCompletionCache(ShorthandCompletionCache shorthandCache)
	{
		sourceProvider.setShorthandCache(shorthandCache);
		//reset comment completions too
		setCommentCompletions(shorthandCache);
	}
	
	/**
	 * load the comment completions from the short hand cache
	 * @param shorthandCache
	 */
	private void setCommentCompletions(ShorthandCompletionCache shorthandCache)
	{
		DefaultCompletionProvider provider = shorthandCache.getCommentProvider();
		if(provider != null) {
			
			for(Iterator i = shorthandCache.getCommentCompletions().iterator(); i.hasNext();) {
				Completion c = (Completion)i.next();
				provider.addCompletion(c);
			}
			setCommentCompletionProvider(provider);
		}
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