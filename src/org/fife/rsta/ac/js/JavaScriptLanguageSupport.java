/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mozilla.javascript.ast.AstRoot;


/**
 * Language support for JavaScript.  This requires Rhino.
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


	public JavaScriptLanguageSupport() {
		parserToInfoMap = new HashMap();
		setDefaultCompletionCellRenderer(new JavaScriptCellRenderer());
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
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

		JavaScriptCompletionProvider p = new JavaScriptCompletionProvider();
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

		JavaScriptParser parser = new JavaScriptParser(textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);

		Info info = new Info(textArea, p, parser);
		parserToInfoMap.put(parser, info);

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