/*
 * 06/27/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.completion;

import org.fife.rsta.ac.java.JavaTemplateCompletion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Template completions specific to JavaScript.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptTemplateCompletion extends JavaTemplateCompletion {


	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String definitionString, String template) {
		this(provider, inputText, definitionString, template, null);
	}


	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String definitionString, String template,
			String shortDesc) {
		this(provider, inputText, definitionString, template, shortDesc, null);
	}
	
	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String definitionString, String template,
			String shortDesc, String summary) {
		super(provider, inputText, definitionString, template, shortDesc, summary);
	}

	
}