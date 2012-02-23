/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.common.CodeBlock;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.VariableCompletion;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;

/**
 * Completion provider for JavaScript source code (not comments or strings).
 *
 * @author Steve Upton
 * @author Robert Futrell
 * @version 1.0
 */
public class SourceCompletionProvider extends DefaultCompletionProvider {

	private JavaScriptCompletionProvider parent;


	public SourceCompletionProvider() {
		setParameterizedCompletionParams('(', ", ", ')');
	}


	/**
	 * {@inheritDoc}
	 */
	protected List getCompletionsImpl(JTextComponent comp) {

		comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {

			completions.clear();

			int dot = comp.getCaretPosition();

			AstRoot astRoot = parent.getASTRoot();
			if (astRoot==null) {
				return completions; // empty
			}

			Set set = new TreeSet();

			// Cut down the list to just those matching what we've typed.
			// Note: getAlreadyEnteredText() never returns null
			String text = getAlreadyEnteredText(comp);
			if (text==null) {
				return completions; // empty
			}

			if (text.indexOf('.')==-1) {
				CodeBlock block = addAllCompletions(astRoot, set, text, dot);
				//TODO: remove
				//debugCodeBlock(block, 0);
				recursivelyAddLocalVars(set, block, dot);
			}

			// Do a final sort of all of our completions and we're good to go!
			completions.addAll(set);
			Collections.sort(completions);

			// Only match based on stuff after the final '.', since that's what
			// is
			// displayed for all of our completions.
			text = text.substring(text.lastIndexOf('.') + 1);

			int start = Collections.binarySearch(completions, text, comparator);
			if (start < 0) {
				start = -(start + 1);
			}
			else {
				// There might be multiple entries with the same input text.
				while (start > 0 && comparator.compare(completions.get(start - 1), text) == 0) {
					start--;
				}
			}

			int end = Collections.binarySearch(completions, text + '{', comparator);
			end = -(end + 1);

			return completions.subList(start, end);

		} finally {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}


	protected CodeBlock addAllCompletions(AstRoot root, Set set, String entered, int dot) {
		System.out.println(root.getFunctions() + ", " + root.getFunctionCount());
		CodeBlock block = new CodeBlock(0);
		addCodeBlock(root, set, entered, block, Integer.MAX_VALUE);
		return block;
	}


	/**
	 * For each child of parent AstNode add a new code block and add
	 * completions for each block of code.
	 *
	 * @param parent AstNode whose children to iterate through.
	 * @param set The set of completions to add to.
	 * @param entered The text entered.
	 * @param codeBlock The parent CodeBlock. 
	 * @param offset The end offset of <code>codeBlock</code>.
	 */
	private void addCodeBlock(Node parent, Set set, String entered,
								CodeBlock codeBlock, int offset) {
		Node child = parent.getFirstChild();

		while (child != null) {
			CodeBlock childBlock = codeBlock;
			if (child instanceof AstNode) {
				AstNode node = (AstNode) child;
				int start = node.getAbsolutePosition();
				childBlock = codeBlock.addChildCodeBlock(start);
				childBlock.setEndOffset(offset);
			}
			addCompletions(child, set, entered, childBlock, offset);

			child = child.getNext();

		}
	}

	private void addCompletions(Node child, Set set, String entered,
								CodeBlock block, int offset) {

		if (child == null) {
			return;
		}

		if (child instanceof InfixExpression) {
			//TODO not sure this is needed, processes any node with ==, >, < etc...
			processInfix(child, block, set, entered, offset);
		}
		else {
			switch (child.getType()) {
				case Token.FUNCTION:
					processFunctionNode(child, block, set, entered, offset);
					break;
				case Token.VAR:
					processVariableNode(child, block, set, entered, offset);
					break;
				case Token.FOR:
					processForNode(child, block, set, entered, offset);
					break;
				case Token.WHILE:
					processWhileNode(child, block, set, entered, offset);
					break;
				case Token.BLOCK:
					System.out.println("Scope");
					addCodeBlock(child, set, entered, block, offset);
					break;
				case Token.ASSIGN:
					processAssignNode(child, block, set, offset);
					break;
				case Token.EXPR_VOID:
					processExpressionNode(child, block, set, entered, offset);
					break;
				case Token.IF:
					processIfThenElse(child, block, set, entered, offset);
					break;
				case Token.TRY:
					processTryCatchNode(child, block, set, entered, offset);
					break;
				case Token.CATCH:
					System.out.println("DEBUG; CATCH node found in addCompletions");
					break; //do nothing
				case Token.DO:
					processDoNode(child, block, set, entered, offset);
					break;
				case Token.ERROR:
					// TODO
					System.out.println("ERROR: " + child.getClass());
					break;
				default:
					System.out.println("Unhandled: " + child.getClass());
					break;
			}
		}

	}

	/**
	 * Extract variables from try/catch node(s)
	 */
	private void processTryCatchNode(Node child, CodeBlock block, Set set,
									String entered, int offset) {
		System.out.println("Try statement");
		TryStatement tryStatement = (TryStatement) child;
		offset = tryStatement.getTryBlock().getAbsolutePosition() +
									tryStatement.getTryBlock().getLength();
		addCodeBlock(tryStatement.getTryBlock(), set, entered, block, offset);

		// Iterate through each catch block
		List catchClauses = tryStatement.getCatchClauses();
		for (int i=0; i<catchClauses.size(); i++) {

			CatchClause clause = (CatchClause) tryStatement.getCatchClauses().get(i);
			offset = clause.getAbsolutePosition() + clause.getLength();
			CodeBlock catchBlock = block.getParent().
					addChildCodeBlock(clause.getAbsolutePosition());
			catchBlock.setEndOffset(offset);
			AstNode target = clause.getVarName();

			extractVariableFromNode(target, catchBlock, offset);

			addCodeBlock(clause.getBody(), set, entered, catchBlock, offset);
		}

		// Possible finally block
		AstNode finallyNode = tryStatement.getFinallyBlock();
		if (finallyNode!=null) {
			offset = finallyNode.getAbsolutePosition() + finallyNode.getLength();
			CodeBlock finallyBlock = block.getParent().addChildCodeBlock(tryStatement.getFinallyBlock().getAbsolutePosition());
			addCodeBlock(finallyNode, set, entered, finallyBlock, offset);
			finallyBlock.setEndOffset(offset);
		}

	}


	/**
	 * Extract variables from if/else node(s)
	 */
	private void processIfThenElse(Node child, CodeBlock block, Set set,
							String entered, int offset) {
		System.out.println("If statement");
		IfStatement ifStatement = (IfStatement) child;
		offset = ifStatement.getAbsolutePosition() + ifStatement.getLength();
		addCodeBlock(ifStatement.getThenPart(), set, entered, block, offset);
		AstNode elseNode = ifStatement.getElsePart();
		if(elseNode != null) {
			System.out.println("Else Node");
			int start = elseNode.getAbsolutePosition();
			CodeBlock childBlock = block.addChildCodeBlock(start);
			offset = start + elseNode.getLength();
			addCompletions(elseNode, set, entered, childBlock, offset);
			childBlock.setEndOffset(offset);
		}
		
	}


	/**
	 * Extract completions from expression node
	 */
	private void processExpressionNode(Node child, CodeBlock block, Set set,
										String entered, int offset) {
		System.out.println("Expression void");
		ExpressionStatement expr = (ExpressionStatement) child;
		addCompletions(expr.getExpression(), set, entered, block, offset);
	}


	/**
	 * Extract variable from assign node
	 */
	private void processAssignNode(Node child, CodeBlock block, Set set, int offset) {
		System.out.println("Assign");
		Assignment ass = (Assignment) child;
		AstNode target = ass.getLeft();
		extractVariableFromNode(target, block, offset);
	}


	/**
	 * Extract while loop from node and add new code block
	 */
	private void processWhileNode(Node child, CodeBlock block, Set set,
									String entered, int offset) {
		System.out.println("While loop");
		WhileLoop loop = (WhileLoop) child;
		offset = loop.getAbsolutePosition() + loop.getLength();
		addCodeBlock(loop.getBody(), set, entered, block, offset);
	}


	/**
	 * Extract while loop from node and add new code block
	 */
	private void processDoNode(Node child, CodeBlock block, Set set,
							String entered, int offset) {
		System.out.println("Do loop");
		DoLoop loop = (DoLoop) child;
		offset = loop.getAbsolutePosition() + loop.getLength();
		addCodeBlock(loop.getBody(), set, entered, block, offset);
	}


	/**
	 * Extract variable from binary operator e.g <, >, = etc...
	 */
	private void processInfix(Node child, CodeBlock block, Set set,
								String entered, int offset) {
		InfixExpression epre = (InfixExpression) child;
		AstNode target = epre.getLeft();
		extractVariableFromNode(target, block, offset);
		addCodeBlock(epre, set, entered, block, offset);
	}


	/**
	 * Add function to completions set and extract local variables to add to code block 
	 * TODO: functions can have local scope, so add function to it's own codeblock when applicable
	 */
	private void processFunctionNode(Node child, CodeBlock block, Set set,
									String entered, int offset) {
		FunctionNode fn = (FunctionNode) child;
		System.out.println("Function: " + fn.getName());
		String jsdoc = fn.getJsDoc();
		FunctionCompletion fc = new FunctionCompletion(this, fn.getName(), null);
		fc.setShortDescription(Util.jsDocToHtml(jsdoc));
		offset = fn.getAbsolutePosition() + fn.getLength();
		if (fn.getParamCount() > 0) {
			List fnParams = fn.getParams();
			List params = new ArrayList();
			for (int i = 0; i < fn.getParamCount(); i++) {
				String paramName = null;
				AstNode node = (AstNode) fnParams.get(i);
				switch (node.getType()) {
				case Token.NAME:
					paramName = ((Name) node).getIdentifier();
					break;
				default:
					System.out.println("Unhandled class for param: " + node.getClass());
					break;
				}
				Parameter param = new Parameter(null, paramName);
				params.add(param);

				extractVariableFromNode(node, block, offset);
			}
			fc.setParams(params);
		}
		set.add(fc);
		// get body
		addCodeBlock(fn.getBody(), set, entered, block, offset);
	}


	/**
	 * Extract variable from node and add to code block
	 */
	private void processVariableNode(Node child, CodeBlock block, Set set,
									String entered, int offset) {
		VariableDeclaration varDec = (VariableDeclaration) child;
		List vars = varDec.getVariables();
		for (Iterator i = vars.iterator(); i.hasNext();) {
			VariableInitializer var = (VariableInitializer) i.next();
			AstNode target = var.getTarget();

			extractVariableFromNode(target, block, offset);

			if (var.getInitializer() != null) {
				addCompletions(var.getInitializer(), set, entered, block, offset);
			}
		}
	}


	/**
	 * Extract code from Token.FOR and add completions, then parse body of for loop
	 */
	private void processForNode(Node child, CodeBlock block, Set set,
								String entered, int offset) {
		System.out.println("For loop");
		ForLoop loop = (ForLoop) child;
		offset = loop.getAbsolutePosition() + loop.getLength();
		addCompletions(loop.getInitializer(), set, entered, block, offset);
		addCodeBlock(loop.getBody(), set, entered, block, offset);
	}


	/**
	 * Extract the variable from the Rhino node and add to the CodeBlock
	 * 
	 * @param node The Rhino node from which to extract the variable
	 * @param block The code block to add the variable to.
	 * @param offset The position of the variable in code
	 */
	private void extractVariableFromNode(AstNode node, CodeBlock block, int offset) {
		if (node != null) {
			switch (node.getType()) {
				case Token.NAME:
					Name name = (Name) node;
					System.out.println("... Variable: " + name.getIdentifier());
					block.addVariable(new org.fife.rsta.ac.common.VariableDeclaration(name.getIdentifier(), offset));
					break;
				default:
					System.out.println("... Unknown var target type: " + node.getClass());
					break;
				}
			}
	}


	void setParent(JavaScriptCompletionProvider parent) {
		this.parent = parent;
	}


	private void recursivelyAddLocalVars(Set completions, CodeBlock block, int dot) {

		if (!block.contains(dot)) {
			return;
		}

		// Add local variables declared in this code block
		for (int i = 0; i < block.getVariableDeclarationCount(); i++) {
			org.fife.rsta.ac.common.VariableDeclaration dec = block.getVariableDeclaration(i);
			int decOffs = dec.getOffset();
			if (dot <= decOffs)
				completions.add(new VariableCompletion(this, dec.getName(), null));
			else
				break;
		}

		// add functions
		// TODO
		/*
		 * for (int i = 0; i < block.getFunctionCount(); i++) {
		 * FunctionDeclaration fc = block.getFunctionAt(i);
		 * 
		 * int decOffs = fc.getOffset(); if (dot <= decOffs)
		 * completions.add(fc.getFunction()); else break; }
		 */

		// Add any local variables declared in a child code block
		for (int i = 0; i < block.getChildCodeBlockCount(); i++) {
			CodeBlock child = block.getChildCodeBlock(i);
			if (child.contains(dot)) {
				recursivelyAddLocalVars(completions, child, dot);
			}
		}
	}


	private void debugCodeBlock(CodeBlock block, int tab) {
		System.out.println();
		tab++;
		if (block != null) {
			for (int i = 0; i < tab; i++) {
				System.out.print("\t");
			}
			System.out.print("Start: " + block.getStartOffset() + " end:" + block.getEndOffset());
			for(int ii = 0; ii<block.getVariableDeclarationCount(); ii++)
			{
				org.fife.rsta.ac.common.VariableDeclaration vd = block.getVariableDeclaration(ii);
				System.out.print(" " + vd.getName() + " ");
			}
			for (int i = 0; i < block.getChildCodeBlockCount(); i++) {
				debugCodeBlock(block.getChildCodeBlock(i), tab);
			}
		}
	}


}