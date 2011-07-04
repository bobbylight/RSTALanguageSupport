package org.fife.rsta.ac.jsp;

import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for JSP.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JspLanguageSupport extends AbstractLanguageSupport {

	/**
	 * The completion provider.  This is shared amongst all JSP text areas.
	 */
	private JspCompletionProvider provider;


	/**
	 * Constructor.
	 */
	public JspLanguageSupport() {
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(false);
		setShowDescWindow(true);
	}


	/**
	 * {@inheritDoc}
	 */
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new HtmlCellRenderer();
	}


	private JspCompletionProvider getProvider() {
		if (provider==null) {
			provider = new JspCompletionProvider();
		}
		return provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		HtmlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(null);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {
		uninstallImpl(textArea);
	}


}