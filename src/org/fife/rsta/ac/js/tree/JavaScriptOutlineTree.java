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
package org.fife.rsta.ac.js.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.JavaScriptParser;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;


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

	private static final int PRIORITY_FUNCTION = 1;
	private static final int PRIORITY_VARIABLE = 2;


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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void expandInitialNodes() {

		// First, collapse all rows.
		int j=0;
		while (j<getRowCount()) {
			collapseRow(j++);
		}

		// Expand only the root node
		expandRow(0);

	}


	private void gotoElementAtPath(TreePath path) {
		Object node = path.getLastPathComponent();
		if (node instanceof JavaScriptTreeNode) {
			JavaScriptTreeNode jstn = (JavaScriptTreeNode)node;
			int len = jstn.getLength();
			if (len>-1) { // Should always be true
				int offs = jstn.getOffset();
				textArea.select(offs, offs+len);
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean gotoSelectedElement() {
		TreePath path = getLeadSelectionPath();//e.getNewLeadSelectionPath();
		if (path != null) {
			gotoElementAtPath(path);
			return true;
		}
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
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


	/**
	 * {@inheritDoc}
	 */
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

		JavaScriptTreeNode root = new JavaScriptTreeNode(null);
		if (ast==null) {
			model.setRoot(root);
			return;
		}

		// Loop through all children and add tree nodes for functions and
		// variables
		Node child = ast.getFirstChild();
		while (child!=null) {

			switch (child.getType()) {

				case Token.FUNCTION:
					FunctionNode fn = (FunctionNode)child;
					Name funcName = fn.getFunctionName();
					// Happens with certain syntax errors, such as
					// "function function foo() {".
					if (funcName!=null) {
						System.out.println("--- name: " + fn.getName());
						StringBuilder sb = new StringBuilder(fn.getName()).append('(');
						int paramCount = fn.getParamCount();
						if (paramCount>0) {
							List<AstNode> fnParams = fn.getParams();
							for (int i=0; i<paramCount; i++) {
								String paramName = null;
								AstNode node = fnParams.get(i);
								switch (node.getType()) {
									case Token.NAME:
										paramName = ((Name)node).getIdentifier();
										break;
									default:
										System.out.println("Unhandled class for param: " +
												node.getClass());
										paramName = "?";
										break;
								}
								sb.append(paramName);
								if (i<paramCount-1) {
									sb.append(", ");
								}
							}
						}
						sb.append(')');
						JavaScriptTreeNode tn = new JavaScriptTreeNode(funcName);
						try {
							int offs = funcName.getAbsolutePosition();
							tn.setOffset(textArea.getDocument().createPosition(offs));
						} catch (BadLocationException ble) { // Never happens
							ble.printStackTrace();
						}
						tn.setText(sb.toString());
						tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
						tn.setSortPriority(PRIORITY_FUNCTION);
						root.add(tn);
					}
					break;

				case Token.VAR:
					VariableDeclaration varDec = (VariableDeclaration)child;
					List<VariableInitializer> vars = varDec.getVariables();
					for (VariableInitializer var : vars) {
						Name varNameNode = null;
						String varName = null;
						AstNode target = var.getTarget();
						switch (target.getType()) {
							case Token.NAME:
								varNameNode = (Name)target;
								//System.out.println("... Variable: " + name.getIdentifier());
								varName = varNameNode.getIdentifier();
								break;
							default:
								System.out.println("... Unknown var target type: " + target.getClass());
								varName = "?";
								break;
						}
						JavaScriptTreeNode tn = new JavaScriptTreeNode(varNameNode);
						try {
							int offs = varNameNode.getAbsolutePosition();
							tn.setOffset(textArea.getDocument().createPosition(offs));
						} catch (BadLocationException ble) { // Never happens
							ble.printStackTrace();
						}
						tn.setText(varName);
						tn.setIcon(IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
						tn.setSortPriority(PRIORITY_VARIABLE);
						root.add(tn);
					}
					break;

			}

			child = child.getNext();

		}

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