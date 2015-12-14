/*
 * 12/03/2015
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.ts;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

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
		AutoCompletion ac = new AutoCompletion(provider);
		return ac;
	}
	
	public void install(RSyntaxTextArea textArea) {

		// We use a custom auto-completion.
		AutoCompletion ac = createAutoCompletion();
		ac.install(textArea);
		installImpl(textArea, ac);

//		Listener listener = new Listener(textArea);
//		textArea.putClientProperty(PROPERTY_LISTENER, listener);
//
//		parser = new JavaScriptParser(this, textArea);
//		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
//		textArea.addParser(parser);
//		//textArea.setToolTipSupplier(provider);
//
//		Info info = new Info(provider, parser);
//		parserToInfoMap.put(parser, info);

//		installKeyboardShortcuts(textArea);
		
//		textArea.setLinkGenerator(new JavaScriptLinkGenerator(this));
	}


	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}
