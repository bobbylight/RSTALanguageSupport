/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.groovy;

//import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DescWindowVisibility;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Groovy.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GroovyLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider, shared amongst all text areas.
	 */
	private GroovyCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public GroovyLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setDescWindowVisibility(DescWindowVisibility.ALWAYS);
	}


	private GroovyCompletionProvider getProvider() {
		if (provider==null) {
			provider = new GroovyCompletionProvider();
		}
		return provider;
	}


	@Override
	public void install(RSyntaxTextArea textArea) {

		GroovyCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(provider);

	}


	@Override
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}
