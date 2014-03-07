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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.GoToMemberAction;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.ClasspathLibraryInfo;
import org.fife.rsta.ac.java.buildpath.ClasspathSourceLocation;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.js.completion.JavaScriptShorthandCompletion;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.modes.JavaScriptTokenMaker;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;


/**
 * Language support for JavaScript. This requires Rhino, which is included with
 * this library.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptLanguageSupport extends AbstractLanguageSupport {

	/**
	 * Maps <code>JavaScriptParser</code>s to <code>Info</code> instances
	 * about them.
	 */
	private Map<JavaScriptParser, Info> parserToInfoMap;
	private JarManager jarManager;
	private boolean xmlAvailable;
	private boolean client;
	private boolean strictMode;
	private int languageVersion;
	private JsErrorParser errorParser;
	private JavaScriptParser parser;
	private JavaScriptCompletionProvider provider;
	private File jshintrc;
//	private int jshintIndent;
//	private long jshintrcLastModified;

	/**
	 * Client property installed on text areas that points to a listener.
	 */
	private static final String PROPERTY_LISTENER =
		"org.fife.rsta.ac.js.JavaScriptLanguageSupport.Listener";


	public JavaScriptLanguageSupport() {
		parserToInfoMap = new HashMap<JavaScriptParser, Info>();
		jarManager = createJarManager();
		provider = createJavaScriptCompletionProvider();
		setErrorParser(JsErrorParser.RHINO);
//setErrorParser(JsErrorParser.JSHINT);
//setJsHintRCFile(new File("D:/users/robert/.jshintrc"));
		setECMAVersion(null, jarManager); //load default ecma 
		setDefaultCompletionCellRenderer(new JavaScriptCellRenderer());
		setAutoActivationEnabled(true);
		setParameterAssistanceEnabled(true);
		setShowDescWindow(true);
		setLanguageVersion(Integer.MIN_VALUE); // Take Rhino's default
	}


	/**
	 * Creates a jar manager instance for used in JS language support.
	 *
	 * @return The jar manager instance.
	 */
	protected JarManager createJarManager() {
		JarManager jarManager = new JarManager();
		return jarManager;
	}
	
	public void setECMAVersion(String version, JarManager jarManager) {
		//load classes
		try {
			List<String> classes = provider.getProvider().getTypesFactory().
					setTypeDeclarationVersion(version, isXmlAvailable(), isClient());
			provider.getProvider().setXMLSupported(isXmlAvailable());
			if (classes!=null) {
				LibraryInfo info = new ClasspathLibraryInfo(classes,
											new ClasspathSourceLocation());
				jarManager.addClassFileSource(info);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	/**
	 * Creates the provider to use for an RSTA instance editing JavaScript.
	 * Subclasses can override to return custom subclasses of
	 * <code>JavaScriptCompletionProvider</code>.
	 * 
	 * @return The provider.
	 */
	protected JavaScriptCompletionProvider createJavaScriptCompletionProvider() {
		return new JavaScriptCompletionProvider(jarManager, this);
	}


	/**
	 * Returns the engine to use for checking for syntax errors in JavaScript
	 * files.  Note that regardless of the value specified to this method,
	 * Rhino is always used for code completion and the outline tree.
	 *
	 * @return The engine.
	 * @see #setErrorParser(JsErrorParser)
	 */
	public JsErrorParser getErrorParser() {
		return errorParser;
	}


	public JarManager getJarManager() {
		return jarManager;
	}


	public JavaScriptParser getJavaScriptParser() {
		return parser;
	}


	/**
	 * Returns the location of the <code>.jshintrc</code> file to use if using
	 * JsHint as your error parser.  This property is ignored if
	 * {@link #getErrorParser()} does not return {@link JsErrorParser#JSHINT}.
	 *
	 * @return The <code>.jshintrc</code> file, or <code>null</code> if none;
	 *         in that case, the JsHint defaults will be used.
	 * @see #setJsHintRCFile(File)
	 * @see #setErrorParser(JsErrorParser)
	 */
	public File getJsHintRCFile() {
		return jshintrc;
	}


	public int getJsHintIndent() {
		final int DEFAULT = 4;
/*
		if (jshintrc==null) {
			return DEFAULT;
		}
		long lastModified = jshintrc.lastModified();
		if (lastModified!=jshintrcLastModified) {
			RSyntaxDocument doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			try {
				jshintIndent = DEFAULT;
				loadFile(jshintrc, doc);
				for (org.fife.ui.rsyntaxtextarea.Token t : doc) {
					if (t.is(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE, "\"indent\"") ||
							t.is(TokenTypes.LITERAL_CHAR, "'indent'")) {
						t = t.getNextToken();
						while (t!=null && (t.isWhitespace() || t.isSingleChar(':'))) {
							t = t.getNextToken();
						}
						if (t!=null && t.getType()==TokenTypes.LITERAL_NUMBER_DECIMAL_INT) {
							try {
								jshintIndent = Integer.parseInt(t.getLexeme());
								System.out.println("Reloading jshint indent: " + jshintIndent);
							} catch (NumberFormatException nfe) {
								jshintIndent = DEFAULT;
							}
						}
						break;
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				jshintIndent = DEFAULT;
			}
			jshintrcLastModified = lastModified;
		}
		return jshintIndent;
*/
return DEFAULT;
	}

/*
	private void loadFile(File file, RSyntaxDocument doc) throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(file));
		try {
			new RSyntaxTextAreaEditorKit().read(r, doc, 0);
		} catch (BadLocationException ble) {
			throw new IOException(ble.getMessage()); // Never happens
		} finally {
			r.close();
		}
	}
*/

	/**
	 * Sets the JS version to use when parsing the code.
	 *
	 * @return The JS version.  This should be one of the
	 *        <code>VERSION_xxx</code> constants in Rhino's {@link Context}
	 *        class.  If this is set to a value unknown to Rhino, then Rhino's
	 *        default value is used (<code>VERSION_DEFAULT</code>).
	 * @see #setLanguageVersion(int)
	 */
	public int getLanguageVersion() {
		return languageVersion;
	}


	/**
	 * Returns the JS parser running on a text area with this JavaScript
	 * language support installed.
	 * 
	 * @param textArea The text area.
	 * @return The JS parser. This will be <code>null</code> if the text area
	 *         does not have this <code>JavaScriptLanguageSupport</code>
	 *         installed.
	 */
	public JavaScriptParser getParser(RSyntaxTextArea textArea) {
		// Could be a parser for another language.
		Object parser = textArea.getClientProperty(PROPERTY_LANGUAGE_PARSER);
		if (parser instanceof JavaScriptParser) {
			return (JavaScriptParser) parser;
		}
		return null;
	}


	public void install(RSyntaxTextArea textArea) {

		// We use a custom auto-completion.
		// AutoCompletion ac = createAutoCompletion(p);
		AutoCompletion ac = new JavaScriptAutoCompletion(provider, textArea);
		ac.setListCellRenderer(getDefaultCompletionCellRenderer());
		ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
		ac.setAutoActivationEnabled(isAutoActivationEnabled());
		ac.setAutoActivationDelay(getAutoActivationDelay());
		ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
		ac.setExternalURLHandler(new JavaScriptDocUrlhandler(this));
		ac.setShowDescWindow(getShowDescWindow());
		ac.install(textArea);
		installImpl(textArea, ac);

		Listener listener = new Listener(textArea);
		textArea.putClientProperty(PROPERTY_LISTENER, listener);

		parser = new JavaScriptParser(this, textArea);
		textArea.putClientProperty(PROPERTY_LANGUAGE_PARSER, parser);
		textArea.addParser(parser);
		//textArea.setToolTipSupplier(provider);

		Info info = new Info(provider, parser);
		parserToInfoMap.put(parser, info);

		installKeyboardShortcuts(textArea);
		
		// Set XML on JavascriptTokenMaker
		JavaScriptTokenMaker.setE4xSupported(isXmlAvailable());

		textArea.setLinkGenerator(new JavaScriptLinkGenerator(this));
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

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, c | shift), "GoToType");
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
     * @return Whether the JavaScript support supports client/browser objects
     */
	public boolean isClient() {
		return client;
	}


	protected void reparseDocument(int offset) {
		provider.reparseDocument(offset);
	}


	/**
     * Set whether the JavaScript support supports client/browser objects.
     *
     * @param client - true if client mode is supported
     */
	public void setClient(boolean client) {
		this.client = client;
	}
	

	/**
	 * Sets the engine to use for identifying syntax errors in JavaScript
	 * files.  Note that regardless of the value specified to this method,
	 * Rhino is always used for code completion and the outline tree.
	 *
	 * @param errorParser The engine to use.  This cannot be <code>null</code>.
	 * @return Whether this was actually a new error parser.
	 * @see #getErrorParser()
	 */
	public boolean setErrorParser(JsErrorParser errorParser) {
		if (errorParser==null) {
			throw new IllegalArgumentException("errorParser cannot be null");
		}
		if (errorParser!=this.errorParser) {
			this.errorParser = errorParser;
			return true;
		}
		return false;
	}


	/**
	 * Sets the location of the <code>.jshintrc</code> file to use if using
	 * JsHint as your error parser.  This property is ignored if
	 * {@link #getErrorParser()} does not return {@link JsErrorParser#JSHINT}.
	 *
	 * @param file The <code>.jshintrc</code> file, or <code>null</code> if
	 *        none; in that case, the JsHint defaults will be used.
	 * @return Whether the new .jshintrc file is different than the original
	 *         one.
	 * @see #getJsHintRCFile()
	 * @see #setErrorParser(JsErrorParser)
	 */
	public boolean setJsHintRCFile(File file) {
		if ((file==null && jshintrc!=null) || (file!=null && jshintrc==null) ||
				(file!=null && !file.equals(jshintrc))) {
			jshintrc = file;
			return true;
		}
		return false;
	}


	/**
	 * Sets the JS version to use when parsing the code.
	 *
	 * @param languageVersion  The JS version.  This should be one of the
	 *        <code>VERSION_xxx</code> constants in Rhino's {@link Context}
	 *        class.  If this is set to a value unknown to Rhino, then Rhino's
	 *        default value is used (<code>VERSION_DEFAULT</code>).
	 * @see #getLanguageVersion()
	 */
	public void setLanguageVersion(int languageVersion) {
		if (languageVersion<0) {
			languageVersion = Context.VERSION_UNKNOWN;
		}
		this.languageVersion = languageVersion;
	}


	/**
	 * Sets whether strict mode (more warnings are detected) is enabled.
	 * 
	 * @param strict Whether strict mode is enabled.
	 * @return Whether a new value was actually set for this property.
	 * @see #isStrictMode()
	 */
	public boolean setStrictMode(boolean strict) {
		if (strict!=strictMode) {
			strictMode = strict;
			return true;
		}
		return false;
	}


	/**
	 * Sets whether E4X is supported in parsed JavaScript.
	 * 
	 * @param available Whether E4X is supported.
	 * @return Whether a new value was actually set for this property.
	 * @see #isXmlAvailable()
	 */
	public boolean setXmlAvailable(boolean available) {
		if (available!=this.xmlAvailable) {
			this.xmlAvailable = available;
			return true;
		}
		return false;
	}


	public void uninstall(RSyntaxTextArea textArea) {

		uninstallImpl(textArea);

		JavaScriptParser parser = getParser(textArea);
		Info info = parserToInfoMap.remove(parser);
		if (info != null) { // Should always be true
			parser.removePropertyChangeListener(JavaScriptParser.PROPERTY_AST,
					info);
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

	/**
	 * Manages information about the parsing/auto-completion for a single text
	 * area. Unlike many simpler language supports,
	 * <code>JavaScriptLanguageSupport</code> cannot share any information
	 * amongst instances of <code>RSyntaxTextArea</code>.
	 */
	private static class Info implements PropertyChangeListener {

		public JavaScriptCompletionProvider provider;
		
		// public JavaScriptParser parser;

		public Info(JavaScriptCompletionProvider provider, JavaScriptParser parser) {
			this.provider = provider;
			// this.parser = parser;
			parser.addPropertyChangeListener(JavaScriptParser.PROPERTY_AST,
					this);
		}

		/**
		 * Called when a text area is re-parsed.
		 * 
		 * @param e The event.
		 */
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			if (JavaScriptParser.PROPERTY_AST.equals(name)) {
				AstRoot root = (AstRoot) e.getNewValue();
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

		@Override
		protected String getReplacementText(Completion c, Document doc,
				int start, int len) {
			
			String replacement = super.getReplacementText(c, doc, start, len);
			if(c instanceof JavaScriptShorthandCompletion)
			{
				try
				{
					int caret = textArea.getCaretPosition();
					String leadingWS = RSyntaxUtilities.getLeadingWhitespace(doc, caret);
					if (replacement.indexOf('\n')>-1) {
						replacement = replacement.replaceAll("\n", "\n" + leadingWS);
					}
					
				}
				catch(BadLocationException ble){}
			}
			return replacement;
		}

		@Override
		protected int refreshPopupWindow() {
			// Force the parser to re-parse
			JavaScriptParser parser = getParser(textArea);
			RSyntaxDocument doc = (RSyntaxDocument) textArea.getDocument();
			String style = textArea.getSyntaxEditingStyle();
			parser.parse(doc, style);
			return super.refreshPopupWindow();
		}

	}


	/**
	 * Listens for various events in a text area editing Java (in particular,
	 * caret events, so we can track the "active" code block).
	 */
	// TODO: This class shares a lot of code in common with
	// JavaLanguageSupport's version, but is it worth factoring out?
	private class Listener implements CaretListener, ActionListener {

		private RSyntaxTextArea textArea;
		private Timer t;
		private DeepestScopeVisitor visitor;

		public Listener(RSyntaxTextArea textArea) {
			this.textArea = textArea;
			textArea.addCaretListener(this);
			t = new Timer(650, this);
			t.setRepeats(false);
			visitor = new DeepestScopeVisitor();
		}

		public void actionPerformed(ActionEvent e) {

			JavaScriptParser parser = getParser(textArea);
			if (parser==null) {
				return; // Shouldn't happen
			}
			AstRoot astRoot = parser.getAstRoot();

			if (astRoot!=null) {
				int dot = textArea.getCaretPosition();
				visitor.reset(dot);
				astRoot.visit(visitor);
				AstNode scope = visitor.getDeepestScope();
				if (scope!=null && scope!=astRoot) {
					int start = scope.getAbsolutePosition();
					int end = Math.min(start + scope.getLength() - 1,
										textArea.getDocument().getLength());
					try {
						int startLine = textArea.getLineOfOffset(start);
						int endLine = end<0 ? textArea.getLineCount() :
										textArea.getLineOfOffset(end);
						textArea.setActiveLineRange(startLine, endLine);
					} catch (BadLocationException ble) {
						ble.printStackTrace(); // Never happens
					}
				}
				else {
					textArea.setActiveLineRange(-1, -1);
				}
			}

		}

		public void caretUpdate(CaretEvent e) {
			t.restart();
		}

//		private Scope getDeepestScope(Scope root, int offs) {
//			for (Iterator<Node> i=root.iterator(); i.hasNext(); ) {
//				Node child = i.next();
//				if (child instanceof Scope) {
//					Scope scope = (Scope)child;
//					int childStart = scope.getAbsolutePosition();
//					System.out.println("Checking " + scope.toString() + " at offs " + childStart);
//					if (childStart<=offs && childStart+scope.getLength()>offs) {
//						return getDeepestScope(scope, offs);
//					}
//				}
//				else if (child instanceof ExpressionStatement) {
//					ExpressionStatement es = (ExpressionStatement)child;
//					es.get
//				}
//				else {
//					System.out.println("... Skipping: " + child.toString());
//				}
//			}
//			return root;
////			List<Scope> children = root.getChildScopes();
////			if (children!=null) {
////				for (Scope child : children) {
////					int childStart = child.getPosition();
////					if (childStart<=offs && childStart+child.getLength()>offs) {
////						return getDeepestScope(child, offs);
////					}
////				}
////			}
////			return root;
//		}

		/**
		 * Should be called whenever Java language support is removed from a
		 * text area.
		 */
		public void uninstall() {
			textArea.removeCaretListener(this);
		}

	}


	private class DeepestScopeVisitor implements NodeVisitor {

		private int offs;
		private AstNode deepestScope;

		private boolean containsOffs(AstNode node) {
			int start = node.getAbsolutePosition();
			return start<=offs && start+node.getLength()>offs;
		}

		public AstNode getDeepestScope() {
			return deepestScope;
		}

		public void reset(int offs) {
			this.offs = offs;
			deepestScope = null;
		}

		public boolean visit(AstNode node) {

			switch (node.getType()) {
				case Token.FUNCTION:
					if (containsOffs(node)) {
						deepestScope = node;
						return true;
					}
					return false;
				default:
					return true;
				case Token.BLOCK: // Get scope starting at e.g. "function", not "{"
//					if (node.getParent().getType()==Token.FUNCTION) {
//						System.out.println("Skipping block for function");
//						return false;
//					}
//					System.out.println("Non-function block found");
					return true;
			}

		}

	}


}