package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;


public class JavaScriptTemplateCompletion extends ShorthandCompletion implements
		JSCompletionUI {

	private static final String PREFIX = "<html><nobr>";


	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String replacementText) {
		super(provider, inputText, replacementText);
	}


	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String replacementText, String shortDesc) {
		super(provider, inputText, replacementText, shortDesc);
	}


	public JavaScriptTemplateCompletion(CompletionProvider provider,
			String inputText, String replacementText, String shortDesc,
			String summary) {
		super(provider, inputText, replacementText, shortDesc, summary);
	}


	public Icon getIcon() {
		return IconFactory.getIcon(IconFactory.TEMPLATE_ICON);
	}


	public int getSortIndex() {
		return TEMPLATE_INDEX;
	}


	public String getShortDescriptionText() {
		StringBuffer sb = new StringBuffer(PREFIX);
		sb.append(getInputText());
		sb.append(" - ");
		sb.append(getShortDescription());
		return sb.toString();
	}

}
