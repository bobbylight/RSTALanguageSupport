/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.completion;

import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.TypeDeclarationFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class JSVariableCompletion extends VariableCompletion {

	public JSVariableCompletion(CompletionProvider provider, String name,
			String type) {
		super(provider, name, type);
	}


	/**
	 * @return the type name not qualified
	 */
	public String getType() {
		return getType(false);
	}


	/**
	 * @param qualified whether to return the name as qualified
	 * @return the type name based on qualified
	 */
	public String getType(boolean qualified) {
		return TypeDeclarationFactory.lookupJSType(super.getType(), qualified);
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			temp = temp.substring(lastDot + 1);
		}
		return temp;
	}
}