/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.asm6502;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;


/**
 * Language support for 6502 assembler.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Asm6502LanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas editing 6502 assembler.
	 */
	private Asm6502CompletionProvider provider;


	/**
	 * Constructor.
	 */
	public Asm6502LanguageSupport() {
		setAutoActivationEnabled(true);
		setAutoActivationDelay(500);
		setParameterAssistanceEnabled(true);
		//setShowDescWindow(true);
	}

	@Override
	public void install(RSyntaxTextArea textArea, KeyStroke keyStroke) {
		AutoCompletion ac = defaultInstallation(textArea);
		ac.setTriggerKey(keyStroke);

	}


	//@Override
	//protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
	//	return new CssCellRenderer();
	//}


	private Asm6502CompletionProvider getProvider() {
		if (provider==null) {
			provider = new Asm6502CompletionProvider();
		}
		return provider;
	}


	@Override
	public void install(RSyntaxTextArea textArea) {
		defaultInstallation(textArea);
	}


	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
		textArea.setToolTipSupplier(null);
	}


	private AutoCompletion defaultInstallation(RSyntaxTextArea textArea) {
		Asm6502CompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);
		textArea.setToolTipSupplier(provider);
		return ac;
	}

}
