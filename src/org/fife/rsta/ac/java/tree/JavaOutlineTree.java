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
package org.fife.rsta.ac.java.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.rjc.ast.ASTNode;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;


/**
 * A tree view showing the outline of Java source, similar to the "Outline"
 * view in the Eclipse JDT.  It also uses Eclipse's icons, just like the rest
 * of this code completion library.<p>
 *
 * You can get this tree automatically updating in response to edits in an
 * <tt>RSyntaxTextArea</tt> with {@link JavaLanguageSupport} installed by
 * calling {@link #listenTo(RSyntaxTextArea)}.  Note that, if you have an
 * application with multiple RSTA editors, you would want to call this method
 * each time a new editor is focused.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaOutlineTree extends /*org.fife.ui.ToolTipTree */JTree {

	private DefaultTreeModel model;
	private RSyntaxTextArea textArea;
	private JavaParser parser;
	private Listener listener;


	/**
	 * Constructor.
	 */
	public JavaOutlineTree() {
		setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
		setRootVisible(false);
		setCellRenderer(new AstTreeCellRenderer());
		model = new DefaultTreeModel(new DefaultMutableTreeNode("Nothing"));
		setModel(model);
		listener = new Listener();
		addTreeSelectionListener(listener);
	}


	/**
	 * Refreshes this tree.
	 *
	 * @param cu The parsed compilation unit.  If this is <code>null</code>
	 *        then the tree is cleared.
	 */
	private void update(CompilationUnit cu) {

		JavaTreeNode root = new JavaTreeNode("Remove me!",
												IconFactory.SOURCE_FILE_ICON);
		if (cu==null) {
			model.setRoot(root);
			return;
		}

		Package pkg = cu.getPackage();
		if (pkg!=null) {
			String iconName = IconFactory.PACKAGE_ICON;
			root.add(new JavaTreeNode(pkg, iconName));
		}

		JavaTreeNode importNode = new JavaTreeNode("Imports",
											IconFactory.IMPORT_ROOT_ICON);
		for (Iterator i=cu.getImportIterator(); i.hasNext(); ) {
			ImportDeclaration idec = (ImportDeclaration)i.next();
			JavaTreeNode iNode = new JavaTreeNode(idec,IconFactory.IMPORT_ICON);
			importNode.add(iNode);
		}
		root.add(importNode);

		for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); ) {
			TypeDeclaration td = (TypeDeclaration)i.next();
			TypeDeclarationTreeNode dmtn = createTypeDeclarationNode(td);
			root.add(dmtn);
		}

		model.setRoot(root);
		expandInitialNodes();

	}


	/**
	 * Refreshes listeners on the text area when its syntax style changes.
	 */
	private void checkForJavaParsing() {

		// Remove possible listener on old Java parser (in case they're just
		// changing syntax style AWAY from Java)
		if (parser!=null) {
			parser.removePropertyChangeListener(
						JavaParser.PROPERTY_COMPILATION_UNIT, listener);
			parser = null;
		}

		// Get the Java language support (shared by all RSTA instances editing
		// Java that were registered with the LanguageSupportFactory).
		LanguageSupportFactory lsf = LanguageSupportFactory.get();
		LanguageSupport support = lsf.getSupportFor(SyntaxConstants.
													SYNTAX_STYLE_JAVA);
		JavaLanguageSupport jls = (JavaLanguageSupport)support;

		// Listen for re-parsing of the editor, and update the tree accordingly
		parser = jls.getParser(textArea);
		if (parser!=null) { // Should always be true
			parser.addPropertyChangeListener(
					JavaParser.PROPERTY_COMPILATION_UNIT, listener);
			// Populate with any already-existing CompilationUnit
			CompilationUnit cu = parser.getCompilationUnit();
			update(cu);
		}
		else {
			update((CompilationUnit)null); // Clear the tree
		}

	}


	private MemberTreeNode createMemberNode(Member member) {

		MemberTreeNode node = null;
		if (member instanceof CodeBlock) {
			node = new MemberTreeNode((CodeBlock)member);
		}
		else if (member instanceof Field) {
			node = new MemberTreeNode((Field)member);
		}
		else {
			node = new MemberTreeNode((Method)member);
		}

		CodeBlock body = null;
		if (member instanceof CodeBlock) {
			body = (CodeBlock)member;
		}
		else if (member instanceof Method) {
			body = ((Method)member).getBody();
		}

		if (body!=null) {
			for (int i=0; i<body.getLocalVarCount(); i++) {
				LocalVariable var = body.getLocalVar(i);
				LocalVarTreeNode varNode = new LocalVarTreeNode(var);
				node.add(varNode);
			}
		}

		return node;

	}


	private TypeDeclarationTreeNode createTypeDeclarationNode(
											TypeDeclaration td) {

		TypeDeclarationTreeNode dmtn = new TypeDeclarationTreeNode(td);

		if (td instanceof NormalClassDeclaration) {
			NormalClassDeclaration ncd = (NormalClassDeclaration)td;
			for (int j=0; j<ncd.getChildTypeCount(); j++) {
				TypeDeclaration td2 = ncd.getChildType(j);
				dmtn.add(createTypeDeclarationNode(td2));
			}
			for (Iterator j=ncd.getMemberIterator(); j.hasNext(); ) {
				dmtn.add(createMemberNode((Member)j.next()));
			}
		}

		else if (td instanceof NormalInterfaceDeclaration) {
			NormalInterfaceDeclaration nid = (NormalInterfaceDeclaration)td;
			for (int j=0; j<nid.getChildTypeCount(); j++) {
				TypeDeclaration td2 = nid.getChildType(j);
				dmtn.add(createTypeDeclarationNode(td2));
			}
			for (Iterator j=nid.getMemberIterator(); j.hasNext(); ) {
				dmtn.add(createMemberNode((Member)j.next()));
			}
		}

		return dmtn;

	}


	/**
	 * Expands all nodes in the specified tree.
	 *
	 * @param tree The tree.
	 */
	private void expandInitialNodes() {

		// First, collapse all rows.
		int j=0;
		while (j<getRowCount()) {
			collapseRow(j++);
		}

		// Expand only type declarations
		expandRow(0);
		j = 1;
		while (j<getRowCount()) {
			TreePath path = getPathForRow(j);
			Object comp = path.getLastPathComponent();
			if (comp instanceof TypeDeclarationTreeNode) {
				expandPath(path);
			}
			j++;
		}

	}


	/**
	 * Causes this outline tree to reflect the source code in the specified
	 * text area.
	 *
	 * @param textArea The text area.  This should have been registered with
	 *        the {@link LanguageSupportFactory}, and be editing Java.
	 * @see #uninstall()
	 */
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

		// Check whether we're currently editing Java
		checkForJavaParsing();

	}


	/**
	 * Makes this outline tree stop listening to its current text area.
	 *
	 * @see #listenTo(RSyntaxTextArea)
	 */
	public void uninstall() {

		if (parser!=null) {
			parser.removePropertyChangeListener(
					JavaParser.PROPERTY_COMPILATION_UNIT, listener);
			parser = null;
		}

		if (textArea!=null) {
			textArea.removePropertyChangeListener(
					RSyntaxTextArea.SYNTAX_STYLE_PROPERTY, listener);
			textArea = null;
		}

	}


	/**
	 * Overridden to also update the UI of the child cell renderer.
	 */
	public void updateUI() {
		super.updateUI();
		// Might not be so the first time through
		TreeCellRenderer renderer = getCellRenderer();
		if (renderer instanceof JComponent) {
			((JComponent)renderer).updateUI();
		}
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
				checkForJavaParsing();
			}

			if (JavaParser.PROPERTY_COMPILATION_UNIT.equals(name)) {
				CompilationUnit cu = (CompilationUnit)e.getNewValue();
				update(cu);
			}

		}

		/**
		 * Selects the corresponding element in the text editor when a user
		 * clicks on a node in this tree.
		 */
		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getNewLeadSelectionPath();
			if (path != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.
												getLastPathComponent();
				Object obj = node.getUserObject();
				if (obj instanceof ASTNode) {
					ASTNode astNode = (ASTNode)obj;
					int start = astNode.getNameStartOffset();
					int end = astNode.getNameEndOffset();
					textArea.select(start, end);
				}
			}
		}

	}


}