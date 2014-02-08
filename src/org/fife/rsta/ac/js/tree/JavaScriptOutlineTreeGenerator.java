/*
 * 01/30/2014
 *
 * Copyright (C) 2014 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;


/**
 * Generates the root node for a {@link JavaScriptOutlineTree} based on a
 * Rhino AST of {@link RSyntaxTextArea} code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaScriptOutlineTreeGenerator implements NodeVisitor {

	private JavaScriptTreeNode root;
	private RSyntaxTextArea textArea;

//	private Scope curScopeNode;
	private JavaScriptTreeNode curScopeTreeNode;

	private Map<String, List<JavaScriptTreeNode>> prototypeAdditions = null;


	public JavaScriptOutlineTreeGenerator(RSyntaxTextArea textArea,
			AstRoot ast) {
		this.textArea = textArea;
		root = new JavaScriptTreeNode(null);
		if (ast!=null) {
			ast.visit(this);
		}
	}


	private void addPrototypeAdditionsToRoot() {

		if (prototypeAdditions!=null) {

			root.refresh();

			for (Map.Entry<String, List<JavaScriptTreeNode>> entry : prototypeAdditions.entrySet()) {

				String clazz = entry.getKey();
				for (int i=0; i<root.getChildCount(); i++) {
					JavaScriptTreeNode childNode = (JavaScriptTreeNode)root.getChildAt(i);
					if (childNode.getText(true).startsWith(clazz + "(")) {
						for (JavaScriptTreeNode memberNode : entry.getValue()) {
							childNode.add(memberNode);
						}
						childNode.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
						break;
					}
				}
			}

		}

	}


	private String getFunctionArgsString(FunctionNode fn) {
		StringBuilder sb = new StringBuilder("(");
		int paramCount = fn.getParamCount();
		if (paramCount>0) {
			List<AstNode> fnParams = fn.getParams();
			for (int i=0; i<paramCount; i++) {
				String paramName = null;
				AstNode paramNode = fnParams.get(i);
				switch (paramNode.getType()) {
					case Token.NAME:
						paramName = ((Name)paramNode).getIdentifier();
						break;
					default:
						System.out.println("Unhandled class for param: " +
								paramNode.getClass());
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
		return sb.toString();
	}


	public JavaScriptTreeNode getTreeRoot() {
		addPrototypeAdditionsToRoot();
		return root;
	}


	public boolean visit(AstNode node) {

		if (node==null) {
			return false;
		}

		int nodeType = node.getType();
		switch (nodeType) {

			case Token.SCRIPT: // AstRoot
//				curScopeNode = (ScriptNode)node;
				curScopeTreeNode = root;
				return true;

			case Token.FUNCTION:
				FunctionNode fn = (FunctionNode)node;
				return visitFunction(fn);

			case Token.VAR:
				VariableDeclaration varDec = (VariableDeclaration)node;
				return visitVariableDeclaration(varDec);

			case Token.BLOCK:
				return true;

case Token.EXPR_RESULT:
	// Check for "foo.prototype.xyz = ..."
	ExpressionStatement exprStmt = (ExpressionStatement)node;
	AstNode expr = exprStmt.getExpression();
	if (expr instanceof Assignment) {
		Assignment assignment = (Assignment)expr;
		AstNode left = assignment.getLeft();
		if (left instanceof PropertyGet) {
			PropertyGet pg = (PropertyGet)left;
			if (pg.getLeft() instanceof PropertyGet) {
				PropertyGet pg2 = (PropertyGet)pg.getLeft();
				if (pg2.getLeft() instanceof Name && pg2.getRight() instanceof Name) {
					Name temp = (Name)pg2.getRight();
					if (temp.getIdentifier().equals("prototype")) {
						
						String clazz = ((Name)pg2.getLeft()).getIdentifier();
						String member = ((Name)pg.getRight()).getIdentifier();

						JavaScriptTreeNode tn = new JavaScriptTreeNode(pg.getRight());
						try {
							int offs = pg.getRight().getAbsolutePosition();
							tn.setOffset(textArea.getDocument().createPosition(offs));
						} catch (BadLocationException ble) { // Never happens
							ble.printStackTrace();
						}

						boolean isFunction = assignment.getRight() instanceof FunctionNode;
						String text = member;
						if (isFunction) {
							FunctionNode func = (FunctionNode)assignment.getRight();
							text += getFunctionArgsString(func);
						}
						tn.setText(text);
						tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
						tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
						if (prototypeAdditions==null) {
							prototypeAdditions = new HashMap<String,
												List<JavaScriptTreeNode>>();
						}
						List<JavaScriptTreeNode> list = prototypeAdditions.get(clazz);
						if (list==null) {
							list = new ArrayList<JavaScriptTreeNode>();
							prototypeAdditions.put(clazz, list);
						}
						list.add(tn);
						if (isFunction) {
//							curScopeNode = func;
							JavaScriptTreeNode prevScopeTreeNode = curScopeTreeNode;
							curScopeTreeNode = tn;
							FunctionNode func = (FunctionNode)assignment.getRight();
							func.getBody().visit(this);
//							curScopeNode = curScopeNode.getParentScope();
							curScopeTreeNode = prevScopeTreeNode;
						}
					}
				}
			}
		}
	}
	break;
default:
	System.out.println("Unhandled node: " + node);
	break;
		}

		return false; // Unhandled node type

	}


	private boolean visitFunction(FunctionNode fn) {

		Name funcName = fn.getFunctionName();

		// Happens with certain syntax errors, such as
		// "function function foo() {".
		if (funcName!=null) {
	
			String text = fn.getName() + getFunctionArgsString(fn);

			JavaScriptTreeNode tn = new JavaScriptTreeNode(funcName);
			try {
				int offs = funcName.getAbsolutePosition();
				tn.setOffset(textArea.getDocument().createPosition(offs));
			} catch (BadLocationException ble) { // Never happens
				ble.printStackTrace();
			}
			tn.setText(text);
			tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
			tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);

			curScopeTreeNode.add(tn);

//			curScopeNode = fn;
			curScopeTreeNode = tn;
			fn.getBody().visit(this);
//			curScopeNode = curScopeNode.getParentScope();
			curScopeTreeNode = (JavaScriptTreeNode)curScopeTreeNode.getParent();

		}

		// Never visit children; we do this manually so we know when scope ends
		return false;

	}


	private boolean visitVariableDeclaration(VariableDeclaration varDec) {

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

			boolean isFunction = var.getInitializer() instanceof FunctionNode;
			JavaScriptTreeNode tn = new JavaScriptTreeNode(varNameNode);
			try {
				int offs = varNameNode.getAbsolutePosition();
				tn.setOffset(textArea.getDocument().createPosition(offs));
			} catch (BadLocationException ble) { // Never happens
				ble.printStackTrace();
			}
			if (isFunction) {

				FunctionNode func = (FunctionNode)var.getInitializer();
				tn.setText(varName + getFunctionArgsString(func));
				tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
				tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
				curScopeTreeNode.add(tn);

//				curScopeNode = func;
				curScopeTreeNode = tn;
				func.getBody().visit(this);
//				curScopeNode = curScopeNode.getParentScope();
				curScopeTreeNode = (JavaScriptTreeNode)curScopeTreeNode.getParent();

			}
			else {
				tn.setText(varName);
				tn.setIcon(IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
				tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_VARIABLE);
				curScopeTreeNode.add(tn);
			}

		}

		return false;

	}


}
