/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Java.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaLanguageSupport extends AbstractLanguageSupport {

	/**
	 * Maps <tt>JavaParser</tt>s to <tt>Info</tt> instances about them.
	 */
	private Map parserToInfoMap;

	/**
	 * The shared jar manager to use with all {@link JavaCompletionProvider}s,
	 * or <code>null</code> if each one should have a unique jar manager.
	 */
	private JarManager jarManager;


	/**
	 * Constructor.
	 */
	public JavaLanguageSupport() {
		parserToInfoMap = new HashMap();
		jarManager = new JarManager();
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
	}


	/**
	 * Returns the completion provider running on a text area with this Java
	 * language support installed.
	 *
	 * @param textArea The text area.
	 * @return The completion provider.  This will be <code>null</code> if
	 *         the text area does not have this <tt>JavaLanguageSupport</tt>
	 *         installed.
	 */
	public JavaCompletionProvider getCompletionProvider(
											RSyntaxTextArea textArea) {
		AutoCompletion ac = getAutoCompletionFor(textArea);
		return (JavaCompletionProvider)ac.getCompletionProvider();
	}


	/**
	 * Returns the shared jar manager instance.
	 * NOTE: This method will be removed over time, as the Java support becomes
	 * more robust!
	 *
	 * @return The shared jar manager.
	 */
	public JarManager getJarManager() {
		return jarManager;
	}


	/**
	 * Returns the Java parser running on a text area with this Java language
	 * support installed.
	 *
	 * @param textArea The text area.
	 * @return The Java parser.  This will be <code>null</code> if the text
	 *         area does not have this <tt>JavaLanguageSupport</tt> installed.
	 */
	public JavaParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof JavaParser) {
			return (JavaParser)parser;
		}
		return null;
	}


	/**
	 * {@inheritDoc}
	 */
	public void install(RSyntaxTextArea textArea) {

		JavaCompletionProvider p = new JavaCompletionProvider(jarManager);
		// Can't use createAutoCompletion(), as Java's is "special."
		AutoCompletion ac = new JavaAutoCompletion(p, textArea);
		ac.setListCellRenderer(new JavaCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setAutoActivationEnabled(isAutoActivationEnabled());
		ac.setAutoActivationDelay(getAutoActivationDelay());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(p);

		JavaParser parser = new JavaParser(textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);

		Info info = new Info(textArea, p, parser);
		parserToInfoMap.put(parser, info);

	}


	/**
	 * {@inheritDoc}
	 */
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		JavaParser parser = getParser(textArea);
		Info info = (Info)parserToInfoMap.remove(parser);
		if (info!=null) { // Should always be true
			parser.removePropertyChangeListener(
				JavaParser.PROPERTY_COMPILATION_UNIT, info);
		}
		textArea.removeParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, null);

	}


	/**
	 * Manages information about the parsing/auto-completion for a single text
	 * area.  Unlike many simpler language supports,
	 * <tt>JavaLanguageSupport</tt> cannot share any information amongst
	 * instances of <tt>RSyntaxTextArea</tt>.
	 */
	private static class Info implements PropertyChangeListener {

		public RSyntaxTextArea textArea;
		public JavaCompletionProvider provider;
		public JavaParser parser;

		public Info(RSyntaxTextArea textArea, JavaCompletionProvider provider,
					JavaParser parser) {
			this.textArea = textArea;
			this.provider = provider;
			this.parser = parser;
			parser.addPropertyChangeListener(
							JavaParser.PROPERTY_COMPILATION_UNIT, this);
		}

		/**
		 * Called when a text area is re-parsed.
		 *
		 * @param e The event.
		 */
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			if (JavaParser.PROPERTY_COMPILATION_UNIT.equals(name)) {
				CompilationUnit cu = (CompilationUnit)e.getNewValue();
//				structureTree.update(file, cu);
//				updateTable();
				provider.setCompilationUnit(cu);
			}

		}

	}


	/**
	 * A hack of <tt>AutoCompletion</tt> that forces the <tt>JavaParser</tt>
	 * to re-parse the document when the user presses ctrl+C.
	 */
	private class JavaAutoCompletion extends AutoCompletion {

		private RSyntaxTextArea textArea;

		public JavaAutoCompletion(JavaCompletionProvider provider,
									RSyntaxTextArea textArea) {
			super(provider);
			this.textArea = textArea;
		}

		protected int refreshPopupWindow() {
			// Force the parser to re-parse
			JavaParser parser = getParser(textArea);
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			String style = textArea.getSyntaxEditingStyle();
			parser.parse(doc, style);
			return super.refreshPopupWindow();
		}

	}


}