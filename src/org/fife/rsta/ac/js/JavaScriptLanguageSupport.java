/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mozilla.javascript.ast.AstRoot;


/**
 * Language support for JavaScript.  This requires Rhino, which is included
 * with this library.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptLanguageSupport extends AbstractLanguageSupport {

	/**
	 * Maps <code>JavaScriptParser</code>s to <code>Info</code> instances
	 * about them.
	 */
	private Map parserToInfoMap;
	private JarManager jarManager;
	private boolean xmlAvailable;
	private boolean strictMode;


	public JavaScriptLanguageSupport() {
	    	
		parserToInfoMap = new HashMap();
		jarManager = new JarManager();
		setDefaultCompletionCellRenderer(new JavaScriptCellRenderer());
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * Creates the provider to use for an RSTA instance editing JavaScript.
	 * Subclasses can override to return custom subclasses of
	 * <code>JavaScriptCompletionProvider</code>.
	 *
	 * @return The provider.
	 */
	protected JavaScriptCompletionProvider createJavaScriptCompletionProvider() {
		return new JavaScriptCompletionProvider(jarManager);
	}


	public JarManager getJarManager() {
	    return jarManager;
	}


	/**
	 * Returns the JS parser running on a text area with this JavaScript
	 * language support installed.
	 *
	 * @param textArea The text area.
	 * @return The JS parser.  This will be <code>null</code> if the text
	 *         area does not have this <code>JavaScriptLanguageSupport</code>
	 *         installed.
	 */
	public JavaScriptParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof JavaScriptParser) {
			return (JavaScriptParser)parser;
		}
		return null;
	}


	public void install(RSyntaxTextArea textArea) {

		JavaScriptCompletionProvider p = createJavaScriptCompletionProvider();
		// We use a custom auto-completion.
		//AutoCompletion ac = createAutoCompletion(p);
		AutoCompletion ac = new JavaScriptAutoCompletion(p, textArea);
		ac.setListCellRenderer(getDefaultCompletionCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setAutoActivationEnabled(isAutoActivationEnabled());
		ac.setAutoActivationDelay(getAutoActivationDelay());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		installImpl(textArea, ac);

		//Listener listener = new Listener(textArea);
		//textArea.putClientProperty(PROPERTY_LISTENER, listener);

		JavaScriptParser parser = new JavaScriptParser(this, textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);

		Info info = new Info(textArea, p, parser);
		parserToInfoMap.put(parser, info);

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
		am.put("GoToType", new GoToMemberAction(JavaScriptOutlineTree.class));

	}


	/**
	 * Returns whether strict mode (more warnings are detected) is enabled.
	 *
	 * @return Whether strict mode is enabled.
	 * @see #setStrictMode(boolean)
	 */
	public boolean isStrictMode() {
		return strictMode;
	}


	/**
	 * Returns whether E4X is supported in parsed JavaScript.
	 *
	 * @return Whether E4X is supported.
	 * @see #setXmlAvailable(boolean)
	 */
	public boolean isXmlAvailable() {
		return xmlAvailable;
	}


	/**
	 * Sets whether strict mode (more warnings are detected) is enabled.
	 *
	 * @param strict Whether strict mode is enabled.
	 * @see #isStrictMode()
	 */
	public void setStrictMode(boolean strict) {
		strictMode = strict;
	}


	/**
	 * Sets whether E4X is supported in parsed JavaScript.
	 *
	 * @param available Whether E4X is supported.
	 * @see #isXmlAvailable()
	 */
	public void setXmlAvailable(boolean available) {
		this.xmlAvailable = available;
	}


	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		JavaScriptParser parser = getParser(textArea);
		Info info = (Info)parserToInfoMap.remove(parser);
		if (info!=null) { // Should always be true
			parser.removePropertyChangeListener(
				JavaScriptParser.PROPERTY_AST, info);
		}
		textArea.removeParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, null);

		//Object listener = textArea.getClientProperty(PROPERTY_LISTENER);
		//if (listener instanceof Listener) { // Should always be true
		//	((Listener)listener).uninstall();
		//	textArea.putClientProperty(PROPERTY_LISTENER, null);
		//}

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

		im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_O, c|shift));
		am.remove("GoToType");

	}


	/**
	 * Manages information about the parsing/auto-completion for a single text
	 * area.  Unlike many simpler language supports,
	 * <code>JavaScriptLanguageSupport</code> cannot share any information
	 * amongst instances of <code>RSyntaxTextArea</code>.
	 */
	private static class Info implements PropertyChangeListener {

		public JavaScriptCompletionProvider provider;
		//public JavaScriptParser parser;

		public Info(RSyntaxTextArea textArea, JavaScriptCompletionProvider provider,
					JavaScriptParser parser) {
			this.provider = provider;
			//this.parser = parser;
			parser.addPropertyChangeListener(JavaScriptParser.PROPERTY_AST, this);
		}

		/**
		 * Called when a text area is re-parsed.
		 *
		 * @param e The event.
		 */
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			if (JavaScriptParser.PROPERTY_AST.equals(name)) {
				AstRoot root = (AstRoot)e.getNewValue();
				provider.setASTRoot(root);
			}

		}

	}


	/**
	 * A hack of <code>AutoCompletion</code> that forces the parser to
	 * re-parse the document when the user presses Ctrl+space.
	 */
	private class JavaScriptAutoCompletion extends AutoCompletion {

		private RSyntaxTextArea textArea;

		public JavaScriptAutoCompletion(JavaScriptCompletionProvider provider,
										RSyntaxTextArea textArea) {
			super(provider);
			this.textArea = textArea;
		}

		protected int refreshPopupWindow() {
			// Force the parser to re-parse
			JavaScriptParser parser = getParser(textArea);
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			String style = textArea.getSyntaxEditingStyle();
			parser.parse(doc, style);
			return super.refreshPopupWindow();
		}

	}


}