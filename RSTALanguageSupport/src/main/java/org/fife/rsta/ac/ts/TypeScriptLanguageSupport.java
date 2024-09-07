/*
 * 12/03/2015
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.ts;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;

/**
 * Language support for TypeScript.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TypeScriptLanguageSupport extends AbstractLanguageSupport {

	private TypeScriptCompletionProvider provider;


	public TypeScriptLanguageSupport() {
		provider = new TypeScriptCompletionProvider(this);
	}


	private AutoCompletion createAutoCompletion() {
        return new AutoCompletion(provider);
	}

	@Override
	public void install(RSyntaxTextArea textArea) {

		// We use a custom auto-completion.
		AutoCompletion ac = createAutoCompletion();
		ac.install(textArea);
		installImpl(textArea, ac);

		//Listener listener = new Listener(textArea);
		//textArea.putClientProperty(PROPERTY_LISTENER, listener);

		//parser = new JavaScriptParser(this, textArea);
		//textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		//textArea.addParser(parser);
		////textArea.setToolTipSupplier(provider);

		//Info info = new Info(provider, parser);
		//parserToInfoMap.put(parser, info);

		//installKeyboardShortcuts(textArea);

		//textArea.setLinkGenerator(new JavaScriptLinkGenerator(this));
	}

	@Override
	public void install(RSyntaxTextArea textArea, KeyStroke keyStroke) {
		AutoCompletion ac = createAutoCompletion();
		ac.install(textArea);
		installImpl(textArea, ac);
		ac.setTriggerKey(keyStroke);
	}


	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}
