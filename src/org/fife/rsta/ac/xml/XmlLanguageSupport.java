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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.XMLParser;


/**
 * Language support for XML.  At present, this is mostly a stub.  Currently
 * supported features include:
 * 
 * <ul>
 *    <li>Usage of {@link XmlOutlineTree}, a tree view modeling the XML in
 *        the <code>RSyntaxTextArea</code>.</li>
 * </ul>
 *
 * Possible future features include:
 * 
 * <ul>
 *    <li>Simple XML structure validation.</li>
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
	 * The parser shared amongst all editors with this language support
	 * installed.
	 */
	private XmlParser parser;

	/**
	 * Mapping of <code>RSyntaxTextArea</code>s to <code>XmlOutlineTree</code>s.
	 * If we support a true parser in the future, like we do for Java or
	 * JavaScript, this will go away, and the trees will listen for "AST"
	 * updates like they do.
	 */
	private Map rstaToOutlineTreeMap;


	/**
	 * Constructor.
	 */
	public XmlLanguageSupport() {
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
		rstaToOutlineTreeMap = new HashMap();
	}


	/**
	 * Returns the shared parser, lazily creating it if necessary.
	 *
	 * @return The parser.
	 */
	private XmlParser getParser() {
		if (parser==null) {
			parser = new XmlParser(this);
		}
		return parser;
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
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		// No code completion yet; this exists solely to support the tree
		// view.

		XmlParser parser = getParser();
		textArea.addParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
textArea.addParser(new XMLParser(textArea));
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
	 * Tells all XML outline trees to re-parse the RSTAs' contents.
	 */
	void refreshOutlineTrees() {
		Set entries = rstaToOutlineTreeMap.entrySet();
		for (Iterator i=entries.iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry)i.next();
			Object value = entry.getValue();
			if (value instanceof XmlOutlineTree) { // Should always be true
				((XmlOutlineTree)value).reparse();
			}
		}
	}


	/**
	 * This method is public due to an unfortunate implementation detail that
	 * will go away in the future.  DO NOT CALL THIS METHOD DIRECTLY.
	 */
	public void registerOutlineTree(RSyntaxTextArea textArea,
			XmlOutlineTree tree) {
		Object previous = rstaToOutlineTreeMap.put(textArea, tree);
		if (previous instanceof XmlOutlineTree) {
			((XmlOutlineTree)previous).uninstall();
		}
	}


	/**
	 * This method is public due to an unfortunate implementation detail that
	 * will go away in the future.  DO NOT CALL THIS METHOD DIRECTLY.
	 */
	public void unregisterOutlineTree(RSyntaxTextArea textArea) {
		rstaToOutlineTreeMap.remove(textArea);
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

	}


}