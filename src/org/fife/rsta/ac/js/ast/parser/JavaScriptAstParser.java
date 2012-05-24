package org.fife.rsta.ac.js.ast.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.rsta.ac.js.completion.JavaScriptInScriptFunctionCompletion;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
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
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;


public class JavaScriptAstParser extends JavaScriptParser {

	public JavaScriptAstParser(SourceCompletionProvider provider, int dot,
			boolean preProcessingMode) {
		super(provider, dot, preProcessingMode);
	}


	public CodeBlock convertAstNodeToCodeBlock(AstRoot root, Set set,
			String entered) {
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
			iterateNode((AstNode) child, set, entered, childBlock, offset);

			child = child.getNext();

		}
	}


	protected void iterateNode(AstNode child, Set set, String entered,
			CodeBlock block, int offset) {

		if (child == null)
			return;

		Logger.log(child.toSource());
		Logger.log(child.shortName());

		if (JavaScriptHelper.isInfixOnly(child)) {
			// Will need to look into it.
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
					reassignVariable(child);
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

				// ignore
				case Token.BREAK:
				case Token.CONTINUE:
				case Token.CALL:
				case Token.EMPTY:
				case Token.NAME:
				case Token.CATCH:
				case Token.ERROR:
					break;
				case Token.EXPR_RESULT:
					processExpressionStatement(child, block, set, entered,
							offset);
					break;
				default:
					Logger.log("Unhandled: " + child.getClass() + " (\""
							+ child.toString() + "\":" + child.getLineno());
					break;
			}
		}

	}


	private void processExpressionStatement(Node child, CodeBlock block,
			Set set, String entered, int offset) {
		ExpressionStatement exp = (ExpressionStatement) child;

		AstNode expNode = exp.getExpression();
		iterateNode(expNode, set, entered, block, offset);
	}


	private void reassignVariable(AstNode assign) {
		Assignment assignNode = (Assignment) assign;
		// maybe a variable
		AstNode leftNode = assignNode.getLeft();
		// assign the variable to
		AstNode rightNode = assignNode.getRight();

		String name = leftNode.getType() == Token.NAME ? ((Name) leftNode)
				.getIdentifier() : null;
		if (name != null) {
			int start = assignNode.getAbsolutePosition();
			int offset = start + assignNode.getLength();
			// check that the caret position is below the dot before looking for
			// the variable
			if (offset <= dot) {
				JavaScriptVariableDeclaration dec = provider
						.getVariableResolver().findDeclaration(name, dot);
				if (dec != null) {
					// Set reference to new type
					dec.setTypeDeclaration(rightNode, preProcessingMode);
				}
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
				iterateNode(node, set, entered, block, offset);
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
				iterateNode((AstNode) o, set, entered, block, offset);
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

			JavaScriptVariableDeclaration dec = extractVariableFromNode(target,
					catchBlock, offset);
			if (dec != null) {
				dec.setTypeDeclaration(clause);
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
			iterateNode(elseNode, set, entered, childBlock, offset);
			childBlock.setEndOffset(offset);
		}

	}


	/**
	 * Extract completions from expression node
	 */
	private void processExpressionNode(Node child, CodeBlock block, Set set,
			String entered, int offset) {
		ExpressionStatement expr = (ExpressionStatement) child;
		iterateNode(expr.getExpression(), set, entered, block, offset);
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
		TypeDeclaration returnType = getFunctionType(fn);
		JavaScriptInScriptFunctionCompletion fc = new JavaScriptInScriptFunctionCompletion(
				provider, fn.getName(), returnType); 
		fc.setShortDescription(jsdoc);
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

				if (!preProcessingMode) {
					extractVariableFromNode(node, block, offset);
				}
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
	
	private TypeDeclaration getFunctionType(FunctionNode fn) {
		FunctionReturnVisitor visitor = new FunctionReturnVisitor();
		fn.visit(visitor);
		return visitor.getCommonReturnType();
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
			iterateNode(loop.getInitializer(), set, entered, block, offset);
			addCodeBlock(loop.getBody(), set, entered, block, offset);
		}
		else if (child instanceof ForInLoop) {
			ForInLoop loop = (ForInLoop) child;
			offset = loop.getAbsolutePosition() + loop.getLength();
			AstNode iteratedObject = loop.getIteratedObject();
			AstNode iterator = loop.getIterator();
			if (iterator != null) {
				if (iterator.getType() == Token.VAR) // expected
				{
					VariableDeclaration vd = (VariableDeclaration) iterator;
					List variables = vd.getVariables();
					if (variables.size() == 1) // expected
					{
						VariableInitializer vi = (VariableInitializer) variables
								.get(0);
						if (loop.isForEach()) {
							extractVariableForForEach(vi, block, offset,
									iteratedObject);
						}
						else {
							extractVariableForForIn(vi, block, offset,
									iteratedObject);
						}
					}
				}
			}
			addCodeBlock(loop.getBody(), set, entered, block, offset);
		}
	}


	/**
	 * Extract the variable from the Variable initializer and set the Type
	 * 
	 * @param initializer AstNode from which to extract the variable
	 * @param block code block to add the variable too
	 * @param offset position of the variable in code
	 */
	private void extractVariableFromNode(VariableInitializer initializer,
			CodeBlock block, int offset) {
		AstNode target = initializer.getTarget();

		if (target != null) {
			JavaScriptVariableDeclaration dec = extractVariableFromNode(target,
					block, offset);
			if (dec != null
					&& initializer.getInitializer() != null
					&& JavaScriptHelper.canResolveVariable(target, initializer
							.getInitializer())) {
				dec.setTypeDeclaration(initializer.getInitializer());
			}
			if (dec != null) {
				if (canAddVariable(block)) {
					// add declaration to resolver if one is found
					if (preProcessingMode) {
						block.setStartOffSet(0);
					}

					if (preProcessingMode) {
						provider.getVariableResolver()
								.addPreProcessingVariable(dec);
					}
					else {
						provider.getVariableResolver().addLocalVariable(dec);
					}
				}
			}

		}
	}


	/**
	 * Extract variable for each in loop. If the iteratedObject is an Array,
	 * then resolve the variable to the array type otherwise set to iterator
	 * object type
	 * 
	 * @param initializer
	 * @param block
	 * @param offset
	 * @param iteratedObject
	 */
	private void extractVariableForForEach(VariableInitializer initializer,
			CodeBlock block, int offset, AstNode iteratedObject) {
		AstNode target = initializer.getTarget();
		if (target != null) {
			JavaScriptVariableDeclaration dec = extractVariableFromNode(target,
					block, offset);
			if (dec != null
					&& iteratedObject != null
					&& JavaScriptHelper.canResolveVariable(target,
							iteratedObject)) {

				// resolve the iterated object
				JavaScriptResolver resolver = provider.getJavaScriptEngine()
						.getJavaScriptResolver(provider);
				if (resolver != null) {
					TypeDeclaration iteratorDec = resolver
							.resolveNode(iteratedObject);
					if (iteratorDec instanceof ArrayTypeDeclaration) {
						// set type to array type dec
						dec
								.setTypeDeclaration(((ArrayTypeDeclaration) iteratorDec)
										.getArrayType());
					}
					else {
						dec.setTypeDeclaration(iteratorDec);
					}

				}

				if (canAddVariable(block)) {
					provider.getVariableResolver().addLocalVariable(dec);
				}
			}
		}
	}


	/**
	 * Extract variable for in loop. If the iteratedObject is an Array, then
	 * assume the variable to be a number otherwise do not attempt to resolve
	 * 
	 * @param initializer
	 * @param block
	 * @param offset
	 * @param iteratedObject
	 */
	private void extractVariableForForIn(VariableInitializer initializer,
			CodeBlock block, int offset, AstNode iteratedObject) {
		AstNode target = initializer.getTarget();
		if (target != null) {
			JavaScriptVariableDeclaration dec = extractVariableFromNode(target,
					block, offset);
			if (dec != null
					&& iteratedObject != null
					&& JavaScriptHelper.canResolveVariable(target,
							iteratedObject)) {

				// resolve the iterated object
				JavaScriptResolver resolver = provider.getJavaScriptEngine()
						.getJavaScriptResolver(provider);
				if (resolver != null) {
					TypeDeclaration iteratorDec = resolver
							.resolveNode(iteratedObject);
					if (iteratorDec instanceof ArrayTypeDeclaration) {
						// always assume a number for arrays
						dec.setTypeDeclaration(TypeDeclarationFactory
								.Instance().getTypeDeclaration(
										TypeDeclarationFactory.ECMA_NUMBER));
					}
					else {
						dec.setTypeDeclaration(TypeDeclarationFactory
								.getDefaultTypeDeclaration());
					}

				}

				if (canAddVariable(block)) {
					provider.getVariableResolver().addLocalVariable(dec);
				}
			}
		}
	}


	private boolean canAddVariable(CodeBlock block) {
		if (!preProcessingMode)
			return true;
		// else check parent is base block
		CodeBlock parent = block.getParent();
		return parent != null && parent.getStartOffset() == 0;
	}


	public void setPreProcessingMode(boolean preProcessingMode) {
		this.preProcessingMode = preProcessingMode;
	}


	/**
	 * Extract the variable from the Rhino node and add to the CodeBlock
	 * 
	 * @param node AstNode node from which to extract the variable
	 * @param block code block to add the variable too
	 * @param offset position of the variable in code
	 */
	private JavaScriptVariableDeclaration extractVariableFromNode(AstNode node,
			CodeBlock block, int offset) {
		JavaScriptVariableDeclaration dec = null;
		if (node != null) {

			switch (node.getType()) {
				case Token.NAME:
					Name name = (Name) node;
					dec = new JavaScriptVariableDeclaration(name
							.getIdentifier(), offset, provider);
					block.addVariable(dec);
					break;
				default:
					Logger.log("... Unknown var target type: "
							+ node.getClass());
					break;
			}
		}
		return dec;
	}


	public SourceCompletionProvider getProvider() {
		return provider;
	}


	public int getDot() {
		return dot;
	}


	public boolean isPreProcessingMode() {
		return preProcessingMode;
	}
	
	private class FunctionReturnVisitor implements NodeVisitor
	{
		private ArrayList returnStatements = new ArrayList();
		public boolean visit(AstNode node) {
			switch(node.getType()) {
				case Token.RETURN : returnStatements.add(node);
				break;
			}
			return true;
		}
		
		/**
		 * Iterate through all the return types and check they are all the same, otherwise 
		 * return no type
		 * @return
		 */
		public TypeDeclaration getCommonReturnType() {
			TypeDeclaration commonType = null;
			for(Iterator i = returnStatements.iterator(); i.hasNext();) {
				ReturnStatement rs = (ReturnStatement) i.next();
				AstNode returnValue = rs.getReturnValue();
				//resolve the node
				TypeDeclaration type = provider.getJavaScriptEngine().getJavaScriptResolver(provider).resolveNode(returnValue);
				if(commonType == null) {
					commonType = type;
				}
				else {
					if(!commonType.equals(type)) {
						commonType = TypeDeclarationFactory.getDefaultTypeDeclaration();
						break; //not matching
					}
						
				}
			}
			return commonType;
		}
		
	}
}
