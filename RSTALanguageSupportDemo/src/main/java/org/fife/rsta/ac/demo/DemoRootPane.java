/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.demo;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.TreeNode;

import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.rsta.ac.xml.tree.XmlOutlineTree;
import org.fife.ui.rsyntaxtextarea.ErrorStrip;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;


/**
 * The root pane used by the demos.  This allows both the applet and the
 * stand-alone application to share the same UI.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class DemoRootPane extends JRootPane implements HyperlinkListener,
							SyntaxConstants, Actions {

	private JScrollPane treeSP;
	private AbstractSourceTree tree;
	private RSyntaxTextArea textArea;


	DemoRootPane() {

		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SYNTAX_STYLE_JAVA);
		JavaLanguageSupport jls = (JavaLanguageSupport)support;
		// TODO: This API will change!  It will be easier to do per-editor
		// changes to the build path.
		try {
			jls.getJarManager().addCurrentJreClassFileSource();
		} catch (IllegalArgumentException | IOException ioe) {
			JOptionPane.showMessageDialog(this,
					"Error adding JRE class file source: " + ioe.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}

		// Dummy tree keeps JViewport's "background" looking right initially
		JTree dummy = new JTree((TreeNode)null);
		treeSP = new JScrollPane(dummy);

		textArea = createTextArea();
		setText("CExample.txt", SYNTAX_STYLE_C);
		RTextScrollPane scrollPane = new RTextScrollPane(textArea, true);
		scrollPane.setIconRowHeaderEnabled(true);
		scrollPane.getGutter().setBookmarkingEnabled(true);

		final JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
										treeSP, scrollPane);
		SwingUtilities.invokeLater(() -> sp.setDividerLocation(0.25));
		sp.setContinuousLayout(true);
		//setContentPane(sp);

		setJMenuBar(createMenuBar());

		ErrorStrip errorStrip = new ErrorStrip(textArea);
//errorStrip.setBackground(java.awt.Color.blue);
JPanel cp = new JPanel(new BorderLayout());
cp.add(sp);
cp.add(errorStrip, BorderLayout.LINE_END);
setContentPane(cp);
	}


	private void addItem(Action a, ButtonGroup bg, JMenu menu) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(a);
		bg.add(item);
		menu.add(item);
	}


	private JMenuBar createMenuBar() {

		JMenuBar mb = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new OpenAction(this)));
		menu.addSeparator();
		menu.add(new JMenuItem(new ExitAction()));
		mb.add(menu);

		menu = new JMenu("Language");
		ButtonGroup bg = new ButtonGroup();
        addItem(new StyleAction(this, "6502 Assembly","Asm6502Example.txt",    SYNTAX_STYLE_ASSEMBLER_6502), bg, menu);
		addItem(new StyleAction(this, "C",          "CExample.txt",      SYNTAX_STYLE_C), bg, menu);
		addItem(new StyleAction(this, "CSS",        "CssExample.txt",    SYNTAX_STYLE_CSS), bg, menu);
		addItem(new StyleAction(this, "Groovy",     "GroovyExample.txt", SYNTAX_STYLE_GROOVY), bg, menu);
		addItem(new StyleAction(this, "Java",       "JavaExample.txt",   SYNTAX_STYLE_JAVA), bg, menu);
		addItem(new StyleAction(this, "JavaScript", "JSExample.txt",     SYNTAX_STYLE_JAVASCRIPT), bg, menu);
		addItem(new StyleAction(this, "JSP",        "JspExample.txt",    SYNTAX_STYLE_JSP), bg, menu);
		addItem(new StyleAction(this, "Less",       "LessExample.txt",   SYNTAX_STYLE_LESS), bg, menu);
		addItem(new StyleAction(this, "Perl",       "PerlExample.txt",   SYNTAX_STYLE_PERL), bg, menu);
		addItem(new StyleAction(this, "HTML",       "HtmlExample.txt",   SYNTAX_STYLE_HTML), bg, menu);
		addItem(new StyleAction(this, "PHP",        "PhpExample.txt",    SYNTAX_STYLE_PHP), bg, menu);
		addItem(new StyleAction(this, "sh",         "ShellExample.txt",  SYNTAX_STYLE_UNIX_SHELL), bg, menu);
		addItem(new StyleAction(this, "TypeScript", "TypeScriptExample.txt",  SYNTAX_STYLE_TYPESCRIPT), bg, menu);
		addItem(new StyleAction(this, "XML",        "XMLExample.txt",    SYNTAX_STYLE_XML), bg, menu);
		menu.getItem(0).setSelected(true);
		mb.add(menu);

		menu = new JMenu("LookAndFeel");
		bg = new ButtonGroup();
		LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo info : infos) {
			addItem(new LookAndFeelAction(this, info), bg, menu);
		}
		mb.add(menu);

		menu = new JMenu("View");
		menu.add(new JCheckBoxMenuItem(new ToggleLayeredHighlightsAction(this)));
		mb.add(menu);

		menu = new JMenu("Help");
		menu.add(new JMenuItem(new AboutAction(this)));
		mb.add(menu);

		return mb;

	}


	/**
	 * Creates the text area for this application.
	 *
	 * @return The text area.
	 */
	private RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(25, 80);
		LanguageSupportFactory.get().register(textArea);
		textArea.setCaretPosition(0);
		textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		textArea.setMarkOccurrences(true);
		textArea.setCodeFoldingEnabled(true);
		textArea.setTabsEmulated(true);
		textArea.setTabSize(3);
		//textArea.setBackground(new java.awt.Color(224, 255, 224));
		//textArea.setUseSelectedTextColor(true);
		//textArea.setLineWrap(true);
		ToolTipManager.sharedInstance().registerComponent(textArea);
		return textArea;
	}


	/**
	 * Focuses the text area.
	 */
	void focusTextArea() {
		textArea.requestFocusInWindow();
	}


	RSyntaxTextArea getTextArea() {
		return textArea;
	}


	/**
	 * Called when a hyperlink is clicked in the text area.
	 *
	 * @param e The event.
	 */
	@Override
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
			URL url = e.getURL();
			if (url==null) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
			else {
				JOptionPane.showMessageDialog(this,
									"URL clicked:\n" + url);
			}
		}
	}


	/**
	 * Opens a file in the editor (as opposed to one of the pre-defined
	 * code examples).
	 *
	 * @param file The file to open.
	 */
	public void openFile(File file) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			textArea.read(r, null);
			textArea.setCaretPosition(0);
			r.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			UIManager.getLookAndFeel().provideErrorFeedback(this);
		}
	}


	/**
	 * Displays a tree view of the current source code, if available for the
	 * current programming language.
	 */
	private void refreshSourceTree() {

		if (tree!=null) {
			tree.uninstall();
		}

		String language = textArea.getSyntaxEditingStyle();
		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			tree = new JavaOutlineTree();
		}
		else if (SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT.equals(language)) {
			tree = new JavaScriptOutlineTree();
		}
		else if (SyntaxConstants.SYNTAX_STYLE_XML.equals(language)) {
			tree = new XmlOutlineTree();
		}
		else {
			tree = null;
		}

		if (tree!=null) {
			tree.listenTo(textArea);
			treeSP.setViewportView(tree);
		}
		else {
			JTree dummy = new JTree((TreeNode)null);
			treeSP.setViewportView(dummy);
		}
		treeSP.revalidate();

	}


	/**
	 * Sets the content in the text area to that in the specified resource.
	 *
	 * @param resource The resource to load.
	 * @param style The syntax style to use when highlighting the text.
	 */
	void setText(String resource, String style) {

		textArea.setSyntaxEditingStyle(style);

		ClassLoader cl = getClass().getClassLoader();
		BufferedReader r;
		try {

			r = new BufferedReader(new InputStreamReader(
					cl.getResourceAsStream("examples/" + resource), StandardCharsets.UTF_8));
			textArea.read(r, null);
			r.close();
			textArea.setCaretPosition(0);
			textArea.discardAllEdits();

			refreshSourceTree();

		} catch (RuntimeException re) {
			throw re; // FindBugs
		} catch (Exception e) {
			textArea.setText("Type here to see syntax highlighting");
		}

	}


}
