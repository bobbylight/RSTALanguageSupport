/*
 * 04/07/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.xml;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;

import org.fife.rsta.ac.AbstractMarkupLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.html.HtmlCellRenderer;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
//import org.xml.sax.EntityResolver;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;


/**
 * Language support for XML.  Currently supported features include:
 * 
 * <ul>
 *    <li>Squiggle underlining of basic XML structure errors.</li>
 *    <li>Usage of {@link XmlOutlineTree}, a tree view modeling the XML in
 *        the <code>RSyntaxTextArea</code>.</li>
 * </ul>
 *
 * Possible future features include:
 * 
 * <ul>
 *    <li>DTD/Schema validation.</li>
 *    <li>Code completion based off of other tags in the XML.</li>
 *    <li>Code completion based off of the relevant DTD or schema.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 * @see XmlOutlineTree
 */
public class XmlLanguageSupport extends AbstractMarkupLanguageSupport {

	/**
	 * The shared completion provider instance for all XML editors.
	 */
	private XmlCompletionProvider provider;

	/**
	 * Whether syntax errors are squiggle-underlined in the editor.
	 */
	private boolean showSyntaxErrors;


	/**
	 * Constructor.
	 */
	public XmlLanguageSupport() {
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(false);
		setShowDescWindow(false);
		setShowSyntaxErrors(true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
		return new HtmlCellRenderer();
	}


	/**
	 * Lazily creates the shared completion provider instance for XML.
	 *
	 * @return The completion provider.
	 */
	private XmlCompletionProvider getProvider() {
		if (provider==null) {
			provider = new XmlCompletionProvider();
		}
		return provider;
	}


	/**
	 * Returns the XML parser running on a text area with this XML language
	 * support installed.
	 *
	 * @param textArea The text area.
	 * @return The XML parser.  This will be <code>null</code> if the text
	 *         area does not have this <tt>XmlLanguageSupport</tt> installed.
	 */
	public XmlParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof XmlParser) {
			return (XmlParser)parser;
		}
		return null;
	}


	/**
	 * Returns whether syntax errors are squiggle-underlined in the editor.
	 *
	 * @return Whether errors are squiggle-underlined.
	 * @see #setShowSyntaxErrors(boolean)
	 */
	public boolean getShowSyntaxErrors() {
		return showSyntaxErrors;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void install(RSyntaxTextArea textArea) {

		// Code completion currently only completes words found elsewhere in
		// the editor.
		XmlCompletionProvider provider = getProvider();
		AutoCompletion ac = createAutoCompletion(provider);
		ac.install(textArea);
		installImpl(textArea, ac);

		XmlParser parser = new XmlParser(this);
//EntityResolver resolver = new EntityResolver() {
//	public InputSource resolveEntity(String publicId, String systemId)
//			throws SAXException, IOException {
//		InputStream in = getClass().getResourceAsStream("/theme.dtd");
//		return new InputSource(in);
//	}
//};
//parser.setValidationConfig(new DtdValidationConfig(resolver));
//InputStream in = getClass().getResourceAsStream("/test.xsd");
//try {
//parser.setValidationConfig(new SchemaValidationConfig(XMLConstants.W3C_XML_SCHEMA_NS_URI, in));
//} catch (IOException e) {
//	e.printStackTrace();
//}
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);

		installKeyboardShortcuts(textArea);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installKeyboardShortcuts(RSyntaxTextArea textArea) {

		super.installKeyboardShortcuts(textArea);

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		int c = textArea.getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_DOWN_MASK;

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, c|shift), "GoToType");
		am.put("GoToType", new GoToMemberAction(XmlOutlineTree.class));

	}


	/**
	 * Sets whether syntax errors are squiggle-underlined in the editor.
	 *
	 * @param show Whether syntax errors are squiggle-underlined.
	 * @see #getShowSyntaxErrors()
	 */
	public void setShowSyntaxErrors(boolean show) {
		showSyntaxErrors = show;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldAutoCloseTag(String tag) {
		return true;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		XmlParser parser = getParser(textArea);
		if (parser!=null) {
			textArea.removeParser(parser);
		}

		uninstallKeyboardShortcuts(textArea);

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void uninstallKeyboardShortcuts(RSyntaxTextArea textArea) {

		super.uninstallKeyboardShortcuts(textArea);

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		int c = textArea.getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_DOWN_MASK;

		im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_O, c | shift));
		am.remove("GoToType");

	}


}
