/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


/**
 * Language support for Java.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see JavaOutlineTree
 */
public class JavaLanguageSupport extends AbstractLanguageSupport {

	/**
	 * Maps <tt>JavaParser</tt>s to <tt>Info</tt> instances about them.
	 */
	private Map<JavaParser, Info> parserToInfoMap;

	/**
	 * The shared jar manager to use with all {@link JavaCompletionProvider}s,
	 * or <code>null</code> if each one should have a unique jar manager.
	 */
	private JarManager jarManager;

	/**
	 * Client property installed on text areas that points to a listener.
	 */
	private static final String PROPERTY_LISTENER =
		"org.fife.rsta.ac.java.JavaLanguageSupport.Listener";


	/**
	 * Constructor.
	 */
	public JavaLanguageSupport() {
		parserToInfoMap = new HashMap<>();
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
	@Override
	public void install(RSyntaxTextArea textArea) {

		JavaCompletionProvider p = new JavaCompletionProvider(jarManager);
		// Can't use createAutoCompletion(), as Java's is "special."
		AutoCompletion ac = new JavaAutoCompletion(p, textArea);
		ac.setListCellRenderer(new JavaCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setAutoActivationEnabled(isAutoActivationEnabled());
		ac.setAutoActivationDelay(getAutoActivationDelay());
		ac.setExternalURLHandler(new JavadocUrlHandler());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setParamChoicesRenderer(new JavaParamListCellRenderer());
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		installImpl(textArea, ac);

		textArea.setToolTipSupplier(p);

		Listener listener = new Listener(textArea);
		textArea.putClientProperty(PROPERTY_LISTENER, listener);

		JavaParser parser = new JavaParser(textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);
		textArea.setToolTipSupplier(p);

		Info info = new Info(textArea, p, parser);
		parserToInfoMap.put(parser, info);

		installKeyboardShortcuts(textArea);

		textArea.setLinkGenerator(new JavaLinkGenerator(this));

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
		int shift = InputEvent.SHIFT_DOWN_MASK;

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, c|shift), "GoToType");
		am.put("GoToType", new GoToMemberAction(JavaOutlineTree.class));

	}


	@Override
	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		JavaParser parser = getParser(textArea);
		Info info = parserToInfoMap.remove(parser);
		if (info!=null) { // Should always be true
			parser.removePropertyChangeListener(
				JavaParser.PROPERTY_COMPILATION_UNIT, info);
		}
		textArea.removeParser(parser);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, null);
		textArea.setToolTipSupplier(null);

		Object listener = textArea.getClientProperty(PROPERTY_LISTENER);
		if (listener instanceof Listener) { // Should always be true
			((Listener)listener).uninstall();
			textArea.putClientProperty(PROPERTY_LISTENER, null);
		}

		uninstallKeyboardShortcuts(textArea);
		textArea.setLinkGenerator(null);

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
		int shift = InputEvent.SHIFT_DOWN_MASK;

		im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_O, c|shift));
		am.remove("GoToType");

	}


	/**
	 * Information about an import statement to add and where it should be
	 * added.  This is used internally when a class completion is inserted,
	 * and it needs an import statement added to the source.
	 */
	private static class ImportToAddInfo {

		private int offs;
		private String text;

		ImportToAddInfo(int offset, String text) {
			this.offs = offset;
			this.text = text;
		}

	}


	/**
	 * Manages information about the parsing/auto-completion for a single text
	 * area.  Unlike many simpler language supports,
	 * <tt>JavaLanguageSupport</tt> cannot share any information amongst
	 * instances of <tt>RSyntaxTextArea</tt>.
	 */
	private static class Info implements PropertyChangeListener {

		private JavaCompletionProvider provider;
		//private JavaParser parser;

		Info(RSyntaxTextArea textArea, JavaCompletionProvider provider,
					JavaParser parser) {
			this.provider = provider;
			//this.parser = parser;
			parser.addPropertyChangeListener(
							JavaParser.PROPERTY_COMPILATION_UNIT, this);
		}

		/**
		 * Called when a text area is re-parsed.
		 *
		 * @param e The event.
		 */
		@Override
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			if (JavaParser.PROPERTY_COMPILATION_UNIT.equals(name)) {
				CompilationUnit cu = (CompilationUnit)e.getNewValue();
				//structureTree.update(file, cu);
				//updateTable();
				provider.setCompilationUnit(cu);
			}

		}

	}


	/**
	 * A hack of <tt>AutoCompletion</tt> that forces the <tt>JavaParser</tt>
	 * to re-parse the document when the user presses ctrl+space.
	 */
	private class JavaAutoCompletion extends AutoCompletion {

		private RSyntaxTextArea textArea;
		private String replacementTextPrefix;

		JavaAutoCompletion(JavaCompletionProvider provider,
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

		/**
		 * Overridden to allow for prepending to the replacement text.  This
		 * allows us to insert fully qualified class names. instead of
		 * unqualified ones, if necessary (i.e. if the user tries to
		 * auto-complete <code>javax.swing.text.Document</code>, but they've
		 * explicitly imported <code>org.w3c.dom.Document</code> - we need to
		 * insert the fully qualified name in that case).
		 */
		@Override
		protected String getReplacementText(Completion c, Document doc,
											int start, int len) {
			String text = super.getReplacementText(c, doc, start, len);
			if (replacementTextPrefix!=null) {
				text = replacementTextPrefix + text;
				replacementTextPrefix = null;
			}
			return text;
		}

		/**
		 * Determines whether the class name being completed has been imported,
		 * and if it hasn't, returns the import statement that should be added
		 * for it.  Alternatively, if the class hasn't been imported, but a
		 * class with the same (unqualified) name HAS been imported, this
		 * method sets things up so the fully-qualified version of this class's
		 * name is inserted.<p>
		 *
		 * Thanks to Guilherme Joao Frantz and Jonatas Schuler for helping
		 * with the patch!
		 *
		 * @param cc The completion being inserted.
		 * @return Whether an import was added.
		 */
		private ImportToAddInfo getShouldAddImport(ClassCompletion cc) {

			String text = getCurrentLineText();

			// Make sure we're not currently typing an import statement.
			if (!text.startsWith("import ")) {

				JavaCompletionProvider provider = (JavaCompletionProvider)
													getCompletionProvider();
				CompilationUnit cu = provider.getCompilationUnit();
				int offset = 0;
				boolean alreadyImported = false;

				// Try to bail early, if possible.
				if (cu==null) { // Can never happen, right?
					return null;
				}
				if ("java.lang".equals(cc.getPackageName())) {
					// Package java.lang is "imported" by default.
					return null;
				}

				String className = cc.getClassName(false);
				String fqClassName = cc.getClassName(true);

				// If the completion is in the same package as the source we're
				// editing (or both are in the default package), bail.
				int lastClassNameDot = fqClassName.lastIndexOf('.');
				boolean ccInPackage = lastClassNameDot>-1;
				Package pkg = cu.getPackage();
				if (ccInPackage && pkg!=null) {
					String ccPkg = fqClassName.substring(0, lastClassNameDot);
					String pkgName = pkg.getName();
					if (ccPkg.equals(pkgName)) {
						return null;
					}
				}
				else if (!ccInPackage && pkg==null) {
					return null;
				}

				// Loop through all import statements.
				Iterator<ImportDeclaration> i = cu.getImportIterator();
				for (; i.hasNext();) {

					ImportDeclaration id = i.next();
					offset = id.getNameEndOffset() + 1;

					// Pulling in static methods, etc. from a class - skip
					if (id.isStatic()) {
						continue;
					}

					// Importing all classes in the package...
					else if (id.isWildcard()) {
						// NOTE: Class may be in default package...
						if (lastClassNameDot>-1) {
							String imported = id.getName();
							int dot = imported.lastIndexOf('.');
							String importedPkg = imported.substring(0, dot);
							String classPkg = fqClassName.substring(0, lastClassNameDot);
							if (importedPkg.equals(classPkg)) {
								alreadyImported = true;
								break;
							}
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
							if (fqClassName.equals(fullyImportedClassName)) {
								alreadyImported = true;
							}
							break;
						}

					}

				}

				// If the class wasn't imported, we'll need to add an
				// import statement!
				if (!alreadyImported) {

					StringBuilder importToAdd = new StringBuilder();

					// If there are no previous imports, add the import
					// statement after the package line (if any).
					if (offset == 0) {
						if (pkg!=null) {
							offset = pkg.getNameEndOffset() + 1;
							// Keep an empty line between package and imports.
							importToAdd.append('\n');
						}
					}

					// We read through all imports, but didn't find our class.
					// Add a new import statement after the last one.
					if (offset > -1) {
						//System.out.println(classCompletion.getAlreadyEntered(textArea));
						if (offset>0) {
							importToAdd.append("\nimport ").append(fqClassName).append(';');
						}
						else {
							importToAdd.append("import ").append(fqClassName).append(";\n");
						}
						// TODO: Determine whether the imports are alphabetical,
						// and if so, add the new one alphabetically.
						return new ImportToAddInfo(offset, importToAdd.toString());
					}

					// Otherwise, either the class was imported, or a class
					// with the same name was explicitly imported.
					else {
						// Another class with the same name was imported.
						// We must insert the fully-qualified class name
						// so the compiler resolves the correct class.
						int dot = fqClassName.lastIndexOf('.');
						if (dot>-1) {
                            replacementTextPrefix = fqClassName.substring(0, dot+1);
						}
					}

				}

			}

			return null;

		}

		/**
		 * Overridden to handle special cases, because sometimes Java code
		 * completions will edit more in the source file than just the text
		 * at the current caret position.
		 */
		@Override
		protected void insertCompletion(Completion c,
				boolean typedParamListStartChar) {

			ImportToAddInfo importInfo = null;

			// We special-case class completions because they may add import
			// statements to the top of our source file.  We don't add the
			// (possible) new import statement until after the completion is
			// inserted; that way, when we treat it as an atomic undo/redo,
			// when the user undoes the completion, the caret stays in the
			// code instead of jumping to the import.
			if (c instanceof ClassCompletion) {
				importInfo = getShouldAddImport((ClassCompletion)c);
				if (importInfo!=null) {
					textArea.beginAtomicEdit();
				}
			}

			try {
				super.insertCompletion(c, typedParamListStartChar);
				if (importInfo!=null) {
					textArea.insert(importInfo.text, importInfo.offs);
				}
			} finally {
				// Be safe and always pair beginAtomicEdit() and endAtomicEdit()
				textArea.endAtomicEdit();
			}

		}

		@Override
		protected int refreshPopupWindow() {
			// Force the parser to re-parse
			JavaParser parser = getParser(textArea);
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			String style = textArea.getSyntaxEditingStyle();
			parser.parse(doc, style);
			return super.refreshPopupWindow();
		}

	}


	/**
	 * Listens for various events in a text area editing Java (in particular,
	 * caret events, so we can track the "active" code block).
	 */
	private class Listener implements CaretListener, ActionListener {

		private RSyntaxTextArea textArea;
		private Timer t;

		Listener(RSyntaxTextArea textArea) {
			this.textArea = textArea;
			textArea.addCaretListener(this);
			t = new Timer(650, this);
			t.setRepeats(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			JavaParser parser = getParser(textArea);
			if (parser==null) {
				return; // Shouldn't happen
			}
			CompilationUnit cu = parser.getCompilationUnit();

			// Highlight the line range of the Java method being edited in the
			// gutter.
			if (cu != null) { // Should always be true
				int dot = textArea.getCaretPosition();
				Point p = cu.getEnclosingMethodRange(dot);
				if (p != null) {
					try {
						int startLine = textArea.getLineOfOffset(p.x);
						// Unterminated blocks can end in Integer.MAX_VALUE
						int endOffs = Math.min(p.y,
								textArea.getDocument().getLength());
						int endLine = textArea.getLineOfOffset(endOffs);
						textArea.setActiveLineRange(startLine, endLine);
					} catch (BadLocationException ble) {
						ble.printStackTrace();
					}
				}
				else {
					textArea.setActiveLineRange(-1, -1);
				}
			}

		}

		@Override
		public void caretUpdate(CaretEvent e) {
			t.restart();
		}

		/**
		 * Should be called whenever Java language support is removed from a
		 * text area.
		 */
		public void uninstall() {
			textArea.removeCaretListener(this);
		}

	}


}
