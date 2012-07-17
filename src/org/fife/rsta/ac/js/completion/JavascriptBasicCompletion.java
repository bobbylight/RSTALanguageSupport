package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Basic JavaScript completion that requires no icon
 * e.g for or while
 */
public class JavascriptBasicCompletion extends BasicCompletion implements
		JSCompletionUI {

	public JavascriptBasicCompletion(CompletionProvider provider,
			String replacementText, String shortDesc, String summary) {
		super(provider, replacementText, shortDesc, summary);
	}


	public JavascriptBasicCompletion(CompletionProvider provider,
			String replacementText, String shortDesc) {
		super(provider, replacementText, shortDesc);
	}


	public JavascriptBasicCompletion(CompletionProvider provider,
			String replacementText) {
		super(provider, replacementText);
	}


	public Icon getIcon() {
		return IconFactory.getIcon(IconFactory.getEmptyIcon());
	}


	public int getRelevance() {
		return BASIC_COMPLETION_RELEVANCE;
	}

}
