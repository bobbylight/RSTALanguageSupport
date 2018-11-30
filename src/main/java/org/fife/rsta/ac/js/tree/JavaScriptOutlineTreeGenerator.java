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
import org.fife.rsta.ac.js.util.RhinoUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
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

	private JavaScriptTreeNode curScopeTreeNode;

	private Map<String, List<JavaScriptTreeNode>> prototypeAdditions = null;


	JavaScriptOutlineTreeGenerator(RSyntaxTextArea textArea,
			AstRoot ast) {
		this.textArea = textArea;
		root = new JavaScriptTreeNode((AstNode)null);
		if (ast!=null) {
			ast.visit(this);
		}
	}


	/**
	 * While parsing JS code, this class identifies members added to prototypes.
	 * After all parsing is done, this method is called to create tree nodes
	 * for those prototype members identified.
	 */
	private void addPrototypeAdditionsToRoot() {

		if (prototypeAdditions!=null) {

			root.refresh();

			for (Map.Entry<String, List<JavaScriptTreeNode>> entry : prototypeAdditions.entrySet()) {
				String clazz = entry.getKey();
				for (int i=0; i<root.getChildCount(); i++) {
					JavaScriptTreeNode childNode = (JavaScriptTreeNode)root.getChildAt(i);
					String text = childNode.getText(true);
					if (text!=null && text.startsWith(clazz + "(")) {
						for (JavaScriptTreeNode memberNode : entry.getValue()) {
							childNode.add(memberNode);
						}
						childNode.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
						break;
					}
					/*
					 * Not sure why this happens, but it happens in *some* instances like this:
					 * 
					 * ... js code ...
					 * Foo = function() {
					 *   ... foo body ...
					 * };
					 * Foo.prototype = Object.create(Bar.prototype, {
					 *    ...
					 * });
					 * The "Foo" global variable is complained about.  Note that this does not
					 * occur if there is not JS code *before* and *after* the Foo stuff (?)...
					else {
						System.out.println("Node with null text: " + ((AstNode)((java.util.List)childNode.getUserObject()).get(0)).toSource());
					}
					*/
				}
			}

		}

	}


	/**
	 * Creates and returns a node for the outline tree that corresponds to
	 * a specific AST node.
	 *
	 * @param node The AST node.
	 * @return The outline tree node.
	 */
	private JavaScriptTreeNode createTreeNode(AstNode node) {
		JavaScriptTreeNode tn = new JavaScriptTreeNode(node);
		try {
			int offs = node.getAbsolutePosition();
			tn.setOffset(textArea.getDocument().createPosition(offs));
		} catch (BadLocationException ble) { // Never happens
			ble.printStackTrace();
		}
		return tn;
	}


	/**
	 * Creates and returns a node for the outline tree that corresponds to
	 * specific AST nodes.
	 *
	 * @param nodes The AST nodes.
	 * @return The outline tree node.
	 */
	private JavaScriptTreeNode createTreeNode(List<AstNode> nodes) {
		JavaScriptTreeNode tn = new JavaScriptTreeNode(nodes);
		try {
			int offs = nodes.get(0).getAbsolutePosition();
			tn.setOffset(textArea.getDocument().createPosition(offs));
		} catch (BadLocationException ble) { // Never happens
			ble.printStackTrace();
		}
		return tn;
	}


	private List<AstNode> getChainedPropertyGetNodes(PropertyGet pg) {
		List<AstNode> nodes = new ArrayList<>();
		getChainedPropertyGetNodesImpl(pg, nodes);
		return nodes;
	}


	private void getChainedPropertyGetNodesImpl(PropertyGet pg, List<AstNode> nodes){
		if (pg.getLeft() instanceof PropertyGet) {
			getChainedPropertyGetNodesImpl((PropertyGet)pg.getLeft(), nodes);
		}
		else {
			nodes.add(pg.getLeft());
		}
		nodes.add(pg.getRight());
	}


	public JavaScriptTreeNode getTreeRoot() {
		addPrototypeAdditionsToRoot();
		return root;
	}


	@Override
	public boolean visit(AstNode node) {

		if (node==null) {
			return false;
		}

		int nodeType = node.getType();
		switch (nodeType) {

			case Token.SCRIPT: // AstRoot
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
				ExpressionStatement exprStmt = (ExpressionStatement)node;
				return visitExpressionStatement(exprStmt);

		}

		return false; // Unhandled node type

	}


	private boolean visitExpressionStatement(ExpressionStatement exprStmt) {

		// NOTE: We currently only check for expressions of the following forms:
		//    * "Foo.prototype.xyz = ..."
		//    * "Foo.prototype = { ...  };"
		//    * "Foo.prototype = Object.create(...);"

		AstNode expr = exprStmt.getExpression();

		// "<something> = ..."
		if (expr instanceof Assignment) {

			Assignment assignment = (Assignment)expr;
			AstNode left = assignment.getLeft();

			// e.g. "x.y(.z)* = ..."
			if (left instanceof PropertyGet) {

				PropertyGet pg = (PropertyGet)left;
				List<AstNode> chainedPropertyGetNodes =
						getChainedPropertyGetNodes(pg);
				int count = chainedPropertyGetNodes.size();
				
				// Check for "Foo.prototype.xyz = ..."
				if (count>=3 &&
						RhinoUtil.isPrototypeNameNode(chainedPropertyGetNodes.get(count-2))) {

					String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count-2);
					AstNode propNode = chainedPropertyGetNodes.get(count-1);
					String member = ((Name)propNode).getIdentifier();

					JavaScriptTreeNode tn = createTreeNode(propNode);
					AstNode propertyValue = assignment.getRight();
					visitPrototypeMember(tn, clazz, member, propertyValue);

				}

				// Check for "Foo.prototype = ..."
				else if (RhinoUtil.isPrototypeNameNode(chainedPropertyGetNodes.get(count-1))) {

					JavaScriptTreeNode tn = createTreeNode(chainedPropertyGetNodes);
					tn.setIcon(IconFactory.getIcon(IconFactory.LOCAL_VARIABLE_ICON));
					tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_VARIABLE);
					curScopeTreeNode.add(tn);

					String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count-1);
					AstNode rhs = assignment.getRight();

					// "Foo.prototype = { ... };"
					if (rhs instanceof ObjectLiteral) {
						tn.setText(clazz + "()");
						ObjectLiteral value = (ObjectLiteral)rhs;
						visitPrototypeMembers(value, clazz);
					}

					// Check for "Foo.prototype = Object.create(...)"
					else if (rhs instanceof FunctionCall) {

						FunctionCall rhsFunc = (FunctionCall)rhs;
						AstNode target = rhsFunc.getTarget();
						if (target instanceof PropertyGet) {

							pg = (PropertyGet)target;
							if (RhinoUtil.isSimplePropertyGet(pg, "Object", "create")) {
								tn.setText(clazz + "()");
								List<AstNode> args = rhsFunc.getArguments();
								// TODO: Also include fields in extended
								// prototype!
								if (args.size()>=2) {
									AstNode arg2 = args.get(1);
									if (arg2 instanceof ObjectLiteral) {
										ObjectLiteral descriptorObjLit = (ObjectLiteral)arg2;
										visitPropertyDescriptors(descriptorObjLit, clazz);
									}
								}
							}

							else {
								tn.setText(clazz + "(???)");
							}

						}

					}

					else {
						tn.setText(clazz + "(???)");
					}

				}

				else {

					JavaScriptTreeNode tn = createTreeNode(chainedPropertyGetNodes);
					tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
					tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
					//curScopeTreeNode.add(tn); // Don't add until we know how to handle it

					String clazz = RhinoUtil.getPrototypeClazz(chainedPropertyGetNodes, count);
					AstNode rhs = assignment.getRight();

					// "Foo.bar.bas = { ... };"
					if (rhs instanceof ObjectLiteral) {
						
						curScopeTreeNode.add(tn);
						tn.setText(clazz + "()");

						ObjectLiteral value = (ObjectLiteral)rhs;
						List<ObjectProperty> properties = value.getElements();
						for (ObjectProperty property : properties) {

							AstNode propertyKey = property.getLeft();
							tn = createTreeNode(propertyKey);

							String memberName = RhinoUtil.getPropertyName(propertyKey);
							AstNode propertyValue = property.getRight();
							visitPrototypeMember(tn, clazz,
								memberName, propertyValue);

						}
						
					}

					// Check for "Foo.bar.bas = Object.create(...)"
					else if (rhs instanceof FunctionCall) {

						FunctionCall rhsFunc = (FunctionCall)rhs;
						AstNode target = rhsFunc.getTarget();
						if (target instanceof PropertyGet) {

							pg = (PropertyGet)target;
							if (RhinoUtil.isSimplePropertyGet(pg, "Object", "create")) {
								curScopeTreeNode.add(tn);
								tn.setText(clazz + "()");
								List<AstNode> args = rhsFunc.getArguments();
								// TODO: Also include fields in extended
								// prototype!
								if (args.size()>=2) {
									AstNode arg2 = args.get(1);
									if (arg2 instanceof ObjectLiteral) {
										ObjectLiteral descriptorObjLit = (ObjectLiteral)arg2;
										visitPropertyDescriptors(descriptorObjLit, clazz);
									}
								}
							}

							else if (RhinoUtil.isSimplePropertyGet(pg, "Object", "freeze")) {
								curScopeTreeNode.add(tn);
								tn.setText(clazz + "()");
								List<AstNode> args = rhsFunc.getArguments();
								if (args.size()==1) {
									AstNode arg = args.get(0);
									if (arg instanceof ObjectLiteral) {
										tn.setText(clazz + "()");
										ObjectLiteral value = (ObjectLiteral)arg;
										visitPrototypeMembers(value, clazz);
									}
								}
							}

						}
						else {
							tn.setText(clazz + "(???)");
						}
					}

					else if (rhs instanceof FunctionNode) {
						String text = clazz;
						curScopeTreeNode.add(tn);
						tn.setText(text);

						curScopeTreeNode = tn;
						((FunctionNode)rhs).getBody().visit(this);
						curScopeTreeNode = (JavaScriptTreeNode)curScopeTreeNode.getParent();
					}

					else {
						curScopeTreeNode.add(tn);
						tn.setText(clazz + "(???)");
					}

				}

			}

		}

		return false;

	}


	/**
	 * It is assumed that <code>descriptorObjectLit</code> has been
	 * identified as an object literal containing property descriptors.  Any
	 * property descriptors found as properties of that literal are parsed
	 * and tree nodes are created for them.
	 *
	 * @param descriptorObjLit The object literal containing property
	 *        descriptors (for example, the object parameter to
	 *        <code>Object.create()</code>).
	 * @param clazz The class that the properties belong to.
	 */
	private void visitPropertyDescriptors(ObjectLiteral descriptorObjLit,
			String clazz) {

		List<ObjectProperty> descriptors = descriptorObjLit.getElements();
		for (ObjectProperty prop : descriptors) {

			AstNode propertyKey = prop.getLeft();
			AstNode propertyValue = prop.getRight();

			// Should always be true, as this should be a property descriptor
			if (propertyValue instanceof ObjectLiteral) {

				JavaScriptTreeNode tn = createTreeNode(propertyKey);

				String memberName = RhinoUtil.getPropertyName(propertyKey);
				visitPropertyDescriptor(tn, clazz,
					memberName, (ObjectLiteral)propertyValue);

			}

		}

	}


	/**
	 * Parses an AST node that represents a property descriptor.
	 *
	 * @param tn The tree node representing the property defined by the
	 *        property descriptor.  This is configured by this method, and
	 *        added to the list of nodes to ultimately add to the tree.
	 * @param clazz The class that the property belongs to.
	 * @param memberName The name of the property.
	 * @param propDesc The node representing the property descriptor.
	 */
	private void visitPropertyDescriptor(JavaScriptTreeNode tn, String clazz,
			String memberName, ObjectLiteral propDesc) {

		// TODO: Glean more information than just the value, for a more
		// detailed icon.

		List<ObjectProperty> propDescProperties = propDesc.getElements();
		for (ObjectProperty propDescProperty : propDescProperties) {

			AstNode propertyKey = propDescProperty.getLeft();
			String propName = RhinoUtil.getPropertyName(propertyKey);
			if ("value".equals(propName)) {

				AstNode propertyValue = propDescProperty.getRight();
				boolean isFunction = propertyValue instanceof FunctionNode;
				String text = memberName;
				if (isFunction) {
					FunctionNode func = (FunctionNode)propertyValue;
					text += RhinoUtil.getFunctionArgsString(func);
					tn.setIcon(IconFactory.getIcon(IconFactory.PUBLIC_METHOD_ICON));
					tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
				}
				else {
					tn.setIcon(IconFactory.getIcon(IconFactory.PUBLIC_FIELD_ICON));
					tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_VARIABLE);
				}

				tn.setText(text);
				if (prototypeAdditions==null) {
					prototypeAdditions = new HashMap<>();
				}
				List<JavaScriptTreeNode> list = prototypeAdditions.get(clazz);
				if (list==null) {
					list = new ArrayList<>();
					prototypeAdditions.put(clazz, list);
				}

				list.add(tn);

				if (isFunction) {
					JavaScriptTreeNode prevScopeTreeNode = curScopeTreeNode;
					curScopeTreeNode = tn;
					FunctionNode func = (FunctionNode)propertyValue;
					func.getBody().visit(this);
					curScopeTreeNode = prevScopeTreeNode;
				}

			}

		}

	}


	private void visitPrototypeMembers(ObjectLiteral objLiteral,
			String clazz) {

		List<ObjectProperty> properties = objLiteral.getElements();
		for (ObjectProperty property : properties) {

			AstNode propertyKey = property.getLeft();
			JavaScriptTreeNode tn = createTreeNode(propertyKey);

			String memberName = RhinoUtil.getPropertyName(propertyKey);
			AstNode propertyValue = property.getRight();
			visitPrototypeMember(tn, clazz,
				memberName, propertyValue);

		}

	}


	/**
	 * Visits a node representing a member being defined on a prototype.
	 *
	 * @param tn The tree node to extend, representing this member.
	 * @param clazz The class that the property belongs to.
	 * @param memberName The name of the member.
	 * @param memberValue The AST node representing the value of the member.
	 */
	private void visitPrototypeMember(JavaScriptTreeNode tn, String clazz,
			String memberName, AstNode memberValue) {

		boolean isFunction = memberValue instanceof FunctionNode;
		String text = memberName;
		if (isFunction) {
			FunctionNode func = (FunctionNode)memberValue;
			text += RhinoUtil.getFunctionArgsString(func);
			tn.setIcon(IconFactory.getIcon(IconFactory.PUBLIC_METHOD_ICON));
			tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
		}
		else {
			tn.setIcon(IconFactory.getIcon(IconFactory.PUBLIC_FIELD_ICON));
			tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_VARIABLE);
		}

		tn.setText(text);
		if (prototypeAdditions==null) {
			prototypeAdditions = new HashMap<>();
		}
		List<JavaScriptTreeNode> list = prototypeAdditions.get(clazz);
		if (list==null) {
			list = new ArrayList<>();
			prototypeAdditions.put(clazz, list);
		}

		list.add(tn);

		if (isFunction) {
			JavaScriptTreeNode prevScopeTreeNode = curScopeTreeNode;
			curScopeTreeNode = tn;
			FunctionNode func = (FunctionNode)memberValue;
			func.getBody().visit(this);
			curScopeTreeNode = prevScopeTreeNode;
		}

	}


	/**
	 * Visits a function AST node.  This will create a tree node for the
	 * function, and also visit the AST nodes in the function's body, to
	 * identify local variables, nested functions, etc.
	 *
	 * @param fn The function node.
	 * @return <code>false</code> always, as this method manually visits
	 *         the function AST node's child nodes.
	 */
	private boolean visitFunction(FunctionNode fn) {

		Name funcName = fn.getFunctionName();

		// Happens with certain syntax errors, such as
		// "function function foo() {".
		if (funcName!=null) {
	
			String text = fn.getName() + RhinoUtil.getFunctionArgsString(fn);

			JavaScriptTreeNode tn = createTreeNode(funcName);
			tn.setText(text);
			tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON));
			tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);

			curScopeTreeNode.add(tn);

			curScopeTreeNode = tn;
			fn.getBody().visit(this);
			curScopeTreeNode = (JavaScriptTreeNode)curScopeTreeNode.getParent();

		}

		// Never visit children; we do this manually so we know when scope ends
		return false;

	}


	/**
	 * Visits a variable AST node.  This will create a tree node for the
	 * variable, and if it also has an initializer, it is parsed.
	 *
	 * @param varDec The variable declaration AST node.
	 * @return <code>false</code> always, as this method manually visits
	 *         the variable AST node's child nodes.
	 */
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
			JavaScriptTreeNode tn = createTreeNode(varNameNode);
			if (isFunction) {

				FunctionNode func = (FunctionNode)var.getInitializer();
				tn.setText(varName + RhinoUtil.getFunctionArgsString(func));
				tn.setIcon(IconFactory.getIcon(IconFactory.DEFAULT_CLASS_ICON));
				tn.setSortPriority(JavaScriptOutlineTree.PRIORITY_FUNCTION);
				curScopeTreeNode.add(tn);

				curScopeTreeNode = tn;
				func.getBody().visit(this);
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
