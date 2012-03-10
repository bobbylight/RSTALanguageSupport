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

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.JSTypeFunctionsHelper;
import org.fife.rsta.ac.js.ast.JSVariableDeclaration;
import org.fife.rsta.ac.js.ast.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSVariableCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;


/**
 * Completion provider for JavaScript source code (not comments or strings).
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class SourceCompletionProvider extends DefaultCompletionProvider {

	private JavaScriptCompletionProvider parent;
	private JarManager jarManager;


	public SourceCompletionProvider() {
		setParameterizedCompletionParams('(', ", ", ')');
		setAutoActivationRules(false, "."); // Default - only activate after '.'
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

			if (astRoot == null) {
				return completions; // empty
			}

			Set set = new TreeSet();

			// Cut down the list to just those matching what we've typed.
			// Note: getAlreadyEnteredText() never returns null
			String text = getAlreadyEnteredText(comp);
			if (text == null) {
				return completions; // empty
			}

			// need to populate completions to work out all variables available
			CodeBlock block = addAllCompletions(astRoot, set, text, dot);
			if (text.indexOf('.') == -1) {
				recursivelyAddLocalVars(set, block, dot, null, false);
			}
			else {
				// search for variable in the set and add completions for Type
				recursivelyAddAutoCompletionForQualifiedName(set, text, block,
						dot);
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
				while (start > 0
						&& comparator.compare(completions.get(start - 1), text) == 0) {
					start--;
				}
			}

			int end = Collections.binarySearch(completions, text + '{',
					comparator);
			end = -(end + 1);

			return completions.subList(start, end);

		} finally {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}


	protected CodeBlock addAllCompletions(AstRoot root, Set set,
			String entered, int dot) {
		CodeBlock block = new CodeBlock(0);
		addCodeBlock(root, set, entered, block, Integer.MAX_VALUE);
		return block;
	}


	/**
	 * for each child of parent AstNode add a new code block and add completions
	 * for each block of code
	 * 
	 * @param parent AstNode to iterate children
	 * @param set completions set to add to
	 * @param entered Text entered
	 * @param codeBlock parent CodeBlock
	 * @param offset codeblock offset
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

		if (child == null)
			return;

		if (child instanceof InfixExpression) {
			// TODO not sure this is needed, processes any node with ==, >, <
			// etc...
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
				case Token.FOR: {
					processForNode(child, block, set, entered, offset);
					break;
				}
				case Token.WHILE: {
					processWhileNode(child, block, set, entered, offset);
					break;
				}
				case Token.BLOCK: {
					addCodeBlock(child, set, entered, block, offset);
					break;
				}
				case Token.ASSIGN: {
					processAssignNode(child, block, set, offset);
					break;
				}
				case Token.EXPR_VOID: {
					processExpressionNode(child, block, set, entered, offset);
					break;
				}
				case Token.IF: {
					processIfThenElse(child, block, set, entered, offset);
					break;
				}
				case Token.TRY: {
					processTryCatchNode(child, block, set, entered, offset);
					break;
				}
				case Token.CATCH: {
					break; // do nothing
				}
				case Token.DO: {
					processDoNode(child, block, set, entered, offset);
					break;
				}
				case Token.SWITCH:
					processSwitchNode(child, block, set, entered, offset);
					break;
				case Token.CASE:
					// TODO
					processCaseNode(child, block, set, entered, offset);
					break;

				case Token.ERROR:
					// TODO
					System.out.println("ERROR: " + child.getClass());
					break;
				// ignore
				case Token.BREAK:
				case Token.CONTINUE:
				case Token.CALL:
				case Token.EXPR_RESULT:
					break;
				default:
					System.out.println("Unhandled: " + child.getClass());
					break;
			}
		}

	}


	private void processCaseNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		SwitchCase switchCase = (SwitchCase) child;
		List statements = switchCase.getStatements();
		int start = switchCase.getAbsolutePosition();
		offset = start + switchCase.getLength();
		block = block.addChildCodeBlock(start);
		block.setEndOffset(offset);
		for (Iterator i = statements.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof AstNode) {
				AstNode node = (AstNode) o;
				addCompletions(node, set, entered, block, offset);
			}
		}
	}


	/** Extract local variables from switch node* */
	private void processSwitchNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		SwitchStatement switchStatement = (SwitchStatement) child;
		List cases = switchStatement.getCases();
		for (Iterator i = cases.iterator(); i.hasNext();) {
			Object o = i.next();
			if (o instanceof AstNode) {
				addCompletions((AstNode) o, set, entered, block, offset);
			}
		}
	}


	/**
	 * Extract variables from try/catch node(s)
	 */
	private void processTryCatchNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		TryStatement tryStatement = (TryStatement) child;
		offset = tryStatement.getTryBlock().getAbsolutePosition()
				+ tryStatement.getTryBlock().getLength();
		addCodeBlock(tryStatement.getTryBlock(), set, entered, block, offset);
		// iterate catch
		for (int i = 0; i < tryStatement.getCatchClauses().size(); i++) {

			CatchClause clause = (CatchClause) tryStatement.getCatchClauses()
					.get(i);
			offset = clause.getAbsolutePosition() + clause.getLength();
			CodeBlock catchBlock = block.getParent().addChildCodeBlock(
					clause.getAbsolutePosition());
			catchBlock.setEndOffset(offset);
			AstNode target = clause.getVarName();

			JSVariableDeclaration dec = extractVariableFromNode(target,
					catchBlock, offset);
			if (dec != null) {
				dec.setTypeNode(clause);
			}

			addCodeBlock(clause.getBody(), set, entered, catchBlock, offset);
		}
		// now sort out finally block
		if (tryStatement.getFinallyBlock() != null) {
			AstNode finallyNode = tryStatement.getFinallyBlock();
			offset = finallyNode.getAbsolutePosition()
					+ finallyNode.getLength();
			CodeBlock finallyBlock = block.getParent().addChildCodeBlock(
					tryStatement.getFinallyBlock().getAbsolutePosition());
			addCodeBlock(finallyNode, set, entered, finallyBlock, offset);
			finallyBlock.setEndOffset(offset);
		}
	}


	/**
	 * Extract variables from if/else node(s)
	 */
	private void processIfThenElse(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		IfStatement ifStatement = (IfStatement) child;
		offset = ifStatement.getAbsolutePosition() + ifStatement.getLength();
		addCodeBlock(ifStatement.getThenPart(), set, entered, block, offset);
		AstNode elseNode = ifStatement.getElsePart();
		if (elseNode != null) {
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
		ExpressionStatement expr = (ExpressionStatement) child;
		addCompletions(expr.getExpression(), set, entered, block, offset);
	}


	/**
	 * Extract variable from assign node
	 */
	private void processAssignNode(Node child, CodeBlock block, Set set,
			int offset) {
		Assignment ass = (Assignment) child;
		AstNode target = ass.getLeft();
		extractVariableFromNode(target, block, offset);
	}


	/**
	 * Extract while loop from node and add new code block
	 */
	private void processWhileNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		WhileLoop loop = (WhileLoop) child;
		offset = loop.getAbsolutePosition() + loop.getLength();
		addCodeBlock(loop.getBody(), set, entered, block, offset);
	}


	/**
	 * Extract while loop from node and add new code block
	 */
	private void processDoNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
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
	 * Add function to completions set and extract local variables to add to
	 * code block TODO: functions can have local scope, so add function to it's
	 * own codeblock when applicable
	 */
	private void processFunctionNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		FunctionNode fn = (FunctionNode) child;
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
						break;
				}
				Parameter param = new Parameter(null, paramName);
				params.add(param);

				extractVariableFromNode(node, block, offset);
			}
			fc.setParams(params);
		}
		// TODO need to add functions elsewhere for autocomplete
		if (entered.indexOf('.') == -1) {
			set.add(fc);
		}
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
			extractVariableFromNode(var, block, offset);
		}
	}


	/**
	 * Extract code from Token.FOR and add completions, then parse body of for
	 * loop
	 */
	private void processForNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		if (child instanceof ForLoop) {
			ForLoop loop = (ForLoop) child;
			offset = loop.getAbsolutePosition() + loop.getLength();
			addCompletions(loop.getInitializer(), set, entered, block, offset);
			addCodeBlock(loop.getBody(), set, entered, block, offset);
		}
		else if (child instanceof ForInLoop) {
			ForInLoop loop = (ForInLoop) child;
			offset = loop.getAbsolutePosition() + loop.getLength();
			addCompletions(loop.getIterator(), set, entered, block, offset);
			addCodeBlock(loop.getBody(), set, entered, block, offset);
		}
	}


	/**
	 * Extract the variable from the Variable initializer and set the Type
	 * 
	 * @param node Rhino node from which to extract the variable
	 * @param block code block to add the variable too
	 * @param offset position of the variable in code
	 */
	private void extractVariableFromNode(VariableInitializer initializer,
			CodeBlock block, int offset) {
		AstNode target = initializer.getTarget();

		if (target != null) {
			JSVariableDeclaration dec = extractVariableFromNode(target, block,
					offset);
			if (dec != null && initializer.getInitializer() != null) {
				dec.setTypeNode(initializer.getInitializer());
			}
		}
	}


	/**
	 * Extract the variable from the Rhino node and add to the CodeBlock
	 * 
	 * @param node Rhino node from which to extract the variable
	 * @param block code block to add the variable too
	 * @param offset position of the variable in code
	 */
	private JSVariableDeclaration extractVariableFromNode(AstNode node,
			CodeBlock block, int offset) {
		JSVariableDeclaration dec = null;
		if (node != null) {

			switch (node.getType()) {
				case Token.NAME:
					Name name = (Name) node;
					dec = new JSVariableDeclaration(name.getIdentifier(), offset);
					block.addVariable(dec);
					break;
				default:
					System.out.println("... Unknown var target type: " + node.getClass());
					break;
			}
		}
		return dec;
	}


	void setParent(JavaScriptCompletionProvider parent) {
		this.parent = parent;
	}


	private void recursivelyAddAutoCompletionForQualifiedName(Set completions,
			String enteredText, CodeBlock block, int dot) {

		// TODO will only work one level deep

		// need to slit the entered text using .
		String[] enteredSplit = enteredText.split("\\.");

		if (enteredSplit.length > 0) {
			// get variable name and search completions to find it
			Set findCompletions = new TreeSet();
			recursivelyAddLocalVars(findCompletions, block, dot,
					enteredSplit[0], true);

			// hopefully just have one completion
			if (findCompletions.size() > 0) {
				Object completionObj = findCompletions.iterator().next();
				if (completionObj instanceof JSVariableCompletion) {
					// found the completion for the variable, now create a new
					// completion adding the type
					JSVariableCompletion var = (JSVariableCompletion) completionObj;
					TypeDeclaration typeDec = var.getVariableDeclaration()
							.getTypeDeclaration();
					JSTypeFunctionsHelper.addFunctionCompletionsForJSType(
							completions, typeDec, jarManager, this);
				}
			}
		}

	}


	private void recursivelyAddLocalVars(Set completions, CodeBlock block,
			int dot, String text, boolean findMatch) {

		if (!block.contains(dot)) {
			return;
		}

		// Add local variables declared in this code block
		for (int i = 0; i < block.getVariableDeclarationCount(); i++) {
			JSVariableDeclaration dec = block.getVariableDeclaration(i);
			int decOffs = dec.getOffset();
			if (dot <= decOffs) {
				if (!findMatch || dec.getName().equals(text)) {
					JSVariableCompletion completion = new JSVariableCompletion(
							this, dec.getName(), dec);
					// check whether the variable exists and replace as the
					// scope may be local
					if (completions.contains(completion)) {
						completions.remove(completion);
						completions.add(completion);
					}
					completions.add(completion);
				}
			}
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
				recursivelyAddLocalVars(completions, child, dot, text,
						findMatch);
			}
		}
	}


	protected boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch == '.';
	}


	/**
	 * The jar manager is used to parse the JS API for function completions
	 * 
	 * @param jarManager
	 */
	public void setJarManager(JarManager jarManager) {
		this.jarManager = jarManager;
	}


	public JarManager getJarManager() {
		return jarManager;
	}


	private void debugCodeBlock(CodeBlock block, int tab) {
		System.out.println();
		tab++;
		if (block != null) {
			for (int i = 0; i < tab; i++) {
				System.out.print("\t");
			}
			System.out.print("Start: " + block.getStartOffset() + " end:"
					+ block.getEndOffset());
			for (int ii = 0; ii < block.getVariableDeclarationCount(); ii++) {
				JSVariableDeclaration vd = block.getVariableDeclaration(ii);
				System.out.print(" " + vd.getName() + " ");
			}
			for (int i = 0; i < block.getChildCodeBlockCount(); i++) {
				debugCodeBlock(block.getChildCodeBlock(i), tab);
			}
		}
	}

}
