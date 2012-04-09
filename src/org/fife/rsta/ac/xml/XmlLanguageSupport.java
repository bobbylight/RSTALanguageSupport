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

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


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
 */
public class XmlLanguageSupport extends AbstractLanguageSupport {

	/**
	 * Whether syntax errors are squiggle-underlined in the editor.
	 */
	private boolean showSyntaxErrors;


	/**
	 * Constructor.
	 */
	public XmlLanguageSupport() {
		setAutoCompleteEnabled(false);
		setParameterAssistanceEnabled(false);
		setShowDescWindow(false);
		setShowSyntaxErrors(true);
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
	public void install(RSyntaxTextArea textArea) {

		// No code completion yet; this exists solely to support the tree
		// view and identifying syntax errors.

		XmlParser parser = new XmlParser(this);
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);

		installKeyboardShortcuts(textArea);

	}


	/**
	 * Installs extra keyboard shortcuts supported by this language support.
	 *
	 * @param textArea The text area to install the shortcuts into.
	 */
	private void installKeyboardShortcuts(RSyntaxTextArea textArea) {

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		int c = textArea.getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_MASK;

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
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		XmlParser parser = getParser(textArea);
		if (parser!=null) {
			textArea.removeParser(parser);
		}

		uninstallKeyboardShortcuts(textArea);

	}


	/**
	 * Uninstalls any keyboard shortcuts specific to this language support.
	 * 
	 * @param textArea The text area to uninstall the actions from.
	 */
	private void uninstallKeyboardShortcuts(RSyntaxTextArea textArea) {

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();
		int c = textArea.getToolkit().getMenuShortcutKeyMask();
		int shift = InputEvent.SHIFT_MASK;

		im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_O, c | shift));
		am.remove("GoToType");

	}


}