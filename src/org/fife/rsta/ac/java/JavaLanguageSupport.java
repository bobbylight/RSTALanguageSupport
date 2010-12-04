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
import java.util.Iterator;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
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
		private String replacementTextPrefix;

		public JavaAutoCompletion(JavaCompletionProvider provider,
									RSyntaxTextArea textArea) {
			super(provider);
			this.textArea = textArea;
		}

		private String getCurrentLineText() {

			int caretPosition = textArea.getCaretPosition();
			Element root = textArea.getDocument().getDefaultRootElement();
			int line= root.getElementIndex(caretPosition);
			Element elem = root.getElement(line);
			int endOffset = elem.getEndOffset();
			int lineStart = elem.getStartOffset();

			String text = "";
			try {
				text = textArea.getText(lineStart, endOffset-lineStart).trim();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			return text;

		}

		protected int refreshPopupWindow() {
			// Force the parser to re-parse
			JavaParser parser = getParser(textArea);
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			String style = textArea.getSyntaxEditingStyle();
			parser.parse(doc, style);
			return super.refreshPopupWindow();
		}

		/**
		 * Overridden to allow for prepending to the replacement text.  This
		 * allows us to insert fully qualified class names. instead of
		 * unqualified ones, if necessary (i.e. if the user tries to
		 * auto-complete <code>javax.swing.text.Document</code>, but they've
		 * explicitly imported <code>org.w3c.dom.Document</code> - we need to
		 * insert the fully qualified name in that case).
		 */
		protected String getReplacementText(Completion c, Document doc,
											int start, int len) {
			String text = super.getReplacementText(c, doc, start, len);
			if (replacementTextPrefix!=null) {
				text = replacementTextPrefix + text;
				replacementTextPrefix = null;
			}
			return text;
		}

		protected void insertCompletion(Completion c) {

			// If we're inserting a class name, we might also need to
			// add an import statement for the class.
			if (c instanceof ClassCompletion) {

				String text = getCurrentLineText();

				// Make sure we're not currently typing an import statement.
				if (!text.startsWith("import ")) {

					JavaCompletionProvider provider = (JavaCompletionProvider)
														getCompletionProvider();
					CompilationUnit cu = provider.getCompilationUnit();
					int offset = 0;
					boolean alreadyImported = false;
					ClassCompletion classCompletion = (ClassCompletion)c;

					// Try to bail early, if possible.
					if (cu==null) { // Can never happen, right?
						super.insertCompletion(c);
						return;
					}
					if ("java.lang".equals(classCompletion.getPackageName())) {
						// Package java.lang is "imported" by default.
						super.insertCompletion(c);
						return;
					}

					String className = classCompletion.getClassName(false);
					String fqClassName = classCompletion.getClassName(true);

					// Loop through all import statements.
					for (Iterator i=cu.getImportIterator(); i.hasNext(); ) {

						ImportDeclaration id = (ImportDeclaration)i.next();
						offset = id.getNameEndOffset() + 1;

						// Pulling in static methods, etc. from a class - skip
						if (id.isStatic()) {
							continue;
						}

						// Importing all classes in the package...
						else if (id.isWildcard()) {
							String imported = id.getName();
							int dot = imported.lastIndexOf('.');
							String importedPkg = imported.substring(0, dot);
							dot = fqClassName.lastIndexOf('.');
							String classPkg = fqClassName.substring(0, dot);
							if (importedPkg.equals(classPkg)) {
								alreadyImported = true;
								break;
							}
							else {
								System.out.println("Not the same: " + importedPkg + ", " + classPkg);
							}
						}

						// Importing a single class from a package...
						else {

							String fullyImportedClassName = id.getName();
							int dot = fullyImportedClassName.lastIndexOf('.');
							String importedClassName = fullyImportedClassName.
														substring(dot + 1);

							// If they explicitly imported a class with the
							// same name, but it's in a different package, then
							// the user is required to fully-qualify the class
							// in their code (if unqualified, it would be
							// assumed to be of the type of the qualified
							// class).
							if (className.equals(importedClassName)) {
								offset = -1; // Means "must fully qualify"
								if (fqClassName.equals(fullyImportedClassName)){
									alreadyImported = true;
									break;
								}
							}

						}

					}

					// If the class wasn't imported, we'll need to add an
					// import statement!
					if (!alreadyImported) {

						// If there are no previous imports, add the import
						// statement after the package line (if any).
						// TODO: Divine a better place for the import statement.
						if (offset == 0) {
							Package pkg = cu.getPackage();
							offset = pkg!=null ? pkg.getNameEndOffset()+1 : 0;
						}

						// We read through all imports, but didn't find our class.
						// Add a new import statement after the last one.
						if (offset > -1) {
							//System.out.println(classCompletion.getAlreadyEntered(textArea));
							String importToAdd = (offset > 0 ? "\nimport " : "import ") + fqClassName + ";";
							textArea.insert(importToAdd, offset);
							//textArea.setCaretPosition(caretPosition + importToAdd.length());
						}

						// Otherwise, either the class was imported, or a class
						// with the same name was explicitly imported.
						else {
							// Another class with the same name was imported.
							// We must insert the fully-qualified class name
							// so the compiler resolves the correct class.
							int dot = fqClassName.lastIndexOf('.');
							if (dot>-1) {
								String pkg = fqClassName.substring(0, dot+1);
								replacementTextPrefix = pkg;
							}
							//System.out.println("JavaLanguageSupport.JavaAutoCompletion.insertCompletion()");
						}

					}

				}

			}

			super.insertCompletion(c);

		}

	}


}