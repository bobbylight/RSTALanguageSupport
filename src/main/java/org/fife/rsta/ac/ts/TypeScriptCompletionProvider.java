/*
 * 12/03/2015
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.ts;

import org.fife.rsta.ac.js.JsDocCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;


/**
 * Completion provider for TypeScript.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class TypeScriptCompletionProvider extends LanguageAwareCompletionProvider {

//	/**
//	 * The provider used for source code, kept here since it's used so much.
//	 */
//	private SourceCompletionProvider sourceProvider;
	
	private TypeScriptLanguageSupport languageSupport;


	public TypeScriptCompletionProvider(TypeScriptLanguageSupport languageSupport) {
		super(new SourceCompletionProvider());
//		this.sourceProvider = (SourceCompletionProvider) getDefaultCompletionProvider();
		this.languageSupport = languageSupport;
		
		setDocCommentCompletionProvider(new JsDocCompletionProvider());
	}


	public TypeScriptLanguageSupport getLanguageSupport() {
		return languageSupport;
	}


}
