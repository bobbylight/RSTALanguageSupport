/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js.completion;

import org.fife.rsta.ac.js.ast.JSVariableDeclaration;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class JSVariableCompletion extends VariableCompletion {

	private JSVariableDeclaration dec;


	public JSVariableCompletion(CompletionProvider provider, String name,
			JSVariableDeclaration dec) {
		super(provider, name, dec.getJavaScriptTypeName());
		this.dec = dec;
	}


	public JSVariableDeclaration getVariableDeclaration() {
		return dec;
	}


}