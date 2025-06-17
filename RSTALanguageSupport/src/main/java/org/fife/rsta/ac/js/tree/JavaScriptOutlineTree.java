/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.mozilla.javascript.ast.AstRoot;


/**
 * A tree view showing the outline of JavaScript source, similar to the
 * "Outline" view in the Eclipse JDT.  It also uses Eclipse's icons, just like
 * the rest of this code completion library.<p>
 *
 * You can get this tree automatically updating in response to edits in an
 * <code>RSyntaxTextArea</code> with {@link JavaScriptLanguageSupport}
 * installed by calling {@link #listenTo(RSyntaxTextArea)}.  Note that, if you
 * have an application with multiple RSTA editors, you would want to call this
 * method each time a new editor is focused.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptOutlineTree extends AbstractSourceTree {

	private DefaultTreeModel model;
	private RSyntaxTextArea textArea;
	private JavaScriptParser parser;
	private Listener listener;

	static final int PRIORITY_FUNCTION = 1;
	static final int PRIORITY_VARIABLE = 2;


	/**
	 * Constructor.  The tree created will not have its elements sorted
	 * alphabetically.
	 */
	public JavaScriptOutlineTree() {
		this(false);
	}


	/**
	 * Constructor.
	 *
	 * @param sorted Whether the tree should sort its elements alphabetically.
	 *        Note that outline trees will likely group nodes by type before
	 *        sorting (i.e. methods will be sorted in one group, fields in
	 *        another group, etc.).
	 */
	public JavaScriptOutlineTree(boolean sorted) {
		setSorted(sorted);
		setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
		setRootVisible(false);
		setCellRenderer(new JavaScriptTreeCellRenderer());
		model = new DefaultTreeModel(new DefaultMutableTreeNode("Nothing"));
		setModel(model);
		listener = new Listener();
		addTreeSelectionListener(listener);
	}


	/**
	 * Refreshes listeners on the text area when its syntax style changes.
	 */
	private void checkForJavaScriptParsing() {

		// Remove possible listener on old Java parser (in case they're just
		// changing syntax style AWAY from Java)
		if (parser!=null) {
			parser.removePropertyChangeListener(
						JavaScriptParser.PROPERTY_AST, listener);
			parser = null;
		}

		// Get the Java language support (shared by all RSTA instances editing
		// Java that were registered with the LanguageSupportFactory).
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SyntaxConstants.
													SYNTAX_STYLE_JAVASCRIPT);
		JavaScriptLanguageSupport jls = (JavaScriptLanguageSupport)support;

		// Listen for re-parsing of the editor, and update the tree accordingly
		parser = jls.getParser(textArea);
		if (parser!=null) { // Should always be true
			parser.addPropertyChangeListener(
					JavaScriptParser.PROPERTY_AST, listener);
			// Populate with any already-existing AST
			AstRoot ast = parser.getAstRoot();
			update(ast);
		}
		else {
			update((AstRoot)null); // Clear the tree
		}

	}


	@Override
	public void expandInitialNodes() {

		// First, collapse all rows.
		int j=0;
		while (j<getRowCount()) {
			collapseRow(j++);
		}

		// Expand only functions
		expandRow(0);
		j = 1;
		while (j<getRowCount()) {
			TreePath path = getPathForRow(j);
//			Object comp = path.getLastPathComponent();
//			if (comp instanceof TypeDeclarationTreeNode) {
				expandPath(path);
//			}
			j++;
		}

	}


	private void gotoElementAtPath(TreePath path) {
		Object node = path.getLastPathComponent();
		if (node instanceof JavaScriptTreeNode) {
			JavaScriptTreeNode jstn = (JavaScriptTreeNode)node;
			int len = jstn.getLength();
			if (len>-1) { // Should always be true
				int offs = jstn.getOffset();
				DocumentRange range = new DocumentRange(offs, offs+len);
				RSyntaxUtilities.selectAndPossiblyCenter(textArea, range, true);
			}
		}
	}


	@Override
	public boolean gotoSelectedElement() {
		TreePath path = getLeadSelectionPath();//e.getNewLeadSelectionPath();
		if (path != null) {
			gotoElementAtPath(path);
			return true;
		}
		return false;
	}


	@Override
	public void listenTo(RSyntaxTextArea textArea) {

		if (this.textArea!=null) {
			uninstall();
		}

		// Nothing new to listen to
		if (textArea==null) {
			return;
		}

		// Listen for future language changes in the text editor
		this.textArea = textArea;
		textArea.addPropertyChangeListener(
							RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);

		// Check whether we're currently editing JavaScript
		checkForJavaScriptParsing();

	}


	@Override
	public void uninstall() {

		if (parser!=null) {
			parser.removePropertyChangeListener(
					JavaScriptParser.PROPERTY_AST, listener);
			parser = null;
		}

		if (textArea!=null) {
			textArea.removePropertyChangeListener(
					RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);
			textArea = null;
		}

	}


	/**
	 * Refreshes this tree.
	 *
	 * @param ast The AST.  If this is <code>null</code> then the tree is
	 *        cleared.
	 */
	private void update(AstRoot ast) {
		JavaScriptOutlineTreeGenerator generator =
				new JavaScriptOutlineTreeGenerator(textArea, ast);
		JavaScriptTreeNode root = generator.getTreeRoot();
		model.setRoot(root);
		root.setSorted(isSorted());
		refresh();
	}


	/**
	 * Overridden to also update the UI of the child cell renderer.
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		// DefaultTreeCellRenderer caches colors, so we can't just call
		// ((JComponent)getCellRenderer()).updateUI()...
		setCellRenderer(new JavaScriptTreeCellRenderer());
	}


	/**
	 * Listens for events this tree is interested in (events in the associated
	 * editor, for example), as well as events in this tree.
	 */
	private class Listener implements PropertyChangeListener,
							TreeSelectionListener {

		/**
		 * Called whenever the text area's syntax style changes, as well as
		 * when it is re-parsed.
		 */
		@Override
		public void propertyChange(PropertyChangeEvent e) {

			String name = e.getPropertyName();

			// If the text area is changing the syntax style it is editing
			if (RSyntaxTextArea.SYNTAX_STYLE_PROPERTY.equals(name)) {
				checkForJavaScriptParsing();
			}

			else if (JavaScriptParser.PROPERTY_AST.equals(name)) {
				AstRoot ast = (AstRoot)e.getNewValue();
				update(ast);
			}

		}

		/**
		 * Selects the corresponding element in the text editor when a user
		 * clicks on a node in this tree.
		 */
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (getGotoSelectedElementOnClick()) {
				//gotoSelectedElement();
				TreePath newPath = e.getNewLeadSelectionPath();
				if (newPath!=null) {
					gotoElementAtPath(newPath);
				}
			}
		}

	}


}
