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

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class JSVariableCompletion extends VariableCompletion implements
		JSCompletionUI {

	private JavaScriptVariableDeclaration dec;
	private boolean localVariable;


	public JSVariableCompletion(CompletionProvider provider,
			JavaScriptVariableDeclaration dec) {
		this(provider, dec, true);
	}


	public JSVariableCompletion(CompletionProvider provider,
			JavaScriptVariableDeclaration dec, boolean localVariable) {
		super(provider, dec.getName(), dec.getJavaScriptTypeName());
		this.dec = dec;
		this.localVariable = localVariable;
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
		return TypeDeclarationFactory.lookupJSType(dec.getJavaScriptTypeName(),
				qualified);
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


	public Icon getIcon() {
		return IconFactory
				.getIcon(localVariable ? IconFactory.LOCAL_VARIABLE_ICON
						: IconFactory.GLOBAL_VARIABLE_ICON);
	}


	public int getSortIndex() {
		return localVariable ? LOCAL_VARIABLE_INDEX : GLOBAL_VARIABLE_INDEX;
	}


	public boolean equals(Object obj) {
		if (obj == this)
			return true;

		if (obj instanceof VariableCompletion) {
			VariableCompletion comp = (VariableCompletion) obj;
			return getName().equals(comp.getName());
		}

		return super.equals(obj);
	}


	public int hashCode() {
		return getName().hashCode();
	}

}