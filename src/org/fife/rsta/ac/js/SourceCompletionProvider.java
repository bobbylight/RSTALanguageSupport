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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptType;
import org.fife.rsta.ac.js.ast.jsType.JavaScriptTypesFactory;
import org.fife.rsta.ac.js.ast.parser.JavaScriptParser;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSVariableCompletion;
import org.fife.rsta.ac.js.engine.JavaScriptEngine;
import org.fife.rsta.ac.js.engine.JavaScriptEngineFactory;
import org.fife.rsta.ac.js.resolver.JavaScriptResolver;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

/**
 * Completion provider for JavaScript source code (not comments or strings).
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class SourceCompletionProvider extends DefaultCompletionProvider {

	private JavaScriptCompletionProvider parent;
	private JarManager jarManager;
	private int dot;
	private JavaScriptEngine engine;
	private JavaScriptTypesFactory javaScriptTypesFactory;

	private VariableResolver variableResolver;
	
	private PreProcesssingScripts preProcessing;

	public SourceCompletionProvider() {
		this(null);
	}
	
	public SourceCompletionProvider(String javaScriptEngine) {
		variableResolver = new VariableResolver();
		setParameterizedCompletionParams('(', ", ", ')');
		setAutoActivationRules(false, "."); // Default - only activate after '.'
		engine = JavaScriptEngineFactory.Instance().getEngineFromCache(javaScriptEngine);
		javaScriptTypesFactory = engine.getJavaScriptTypesFactory(this);
	}


	/**
	 * {@inheritDoc}
	 */
	protected List getCompletionsImpl(JTextComponent comp) {

		comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {

			completions.clear();

			dot = comp.getCaretPosition();

			AstRoot astRoot = parent.getASTRoot();

			if (astRoot == null) {
				return completions; // empty
			}

			Set set = new TreeSet();

			// Cut down the list to just those matching what we've typed.
			// Note: getAlreadyEnteredText() never returns null
			String text = getAlreadyEnteredText(comp);

			if (supportsPreProcessingScripts()) {
				variableResolver.resetPreProcessingVariables(false);
			}

			if (text == null) {
				return completions; // empty
			}

			// trim any whitespace as " " are needed to evaluate inputted data
			text = text.trim();

			boolean noDotInText = (text == null || text.indexOf('.') == -1);

			// need to populate completions to work out all variables available
			CodeBlock block = iterateAstRoot(astRoot, set, text, dot, false);

			if (noDotInText) {
				if (text.length() > 0) { // try to convert text by removing
					// any if, while etc...
					text = JavaScriptHelper.parseEnteredText(text);
				}
				recursivelyAddLocalVars(set, block, dot, null, false, false);
			}
			else {
				// Compile the entered text and resolve the variables/function
				// 
				JavaScriptResolver compiler = engine.getJavaScriptResolver(this);
				try {
					JavaScriptType type = compiler.compileText(text);
					if (type != null) {
						javaScriptTypesFactory.populateCompletionsForType(type,
								set);
					}
				} catch (IOException io) {
					// TODO
					io.printStackTrace();
				}

			}

			if (noDotInText && supportsPreProcessingScripts())
				preProcessing.addPreProcessedCompletions(completions);

			completions.addAll(set);

			// Do a final sort of all of our completions and we're good to go!
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
			// do not need locally (RSTATextArea only) resolved variables
			// anymore, so clear them
			variableResolver.resetLocalVariables();
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}
	
	


	public String getAlreadyEnteredText(JTextComponent comp) {
		String text = super.getAlreadyEnteredText(comp);
		if(text != null) {
			int charIndex = JavaScriptHelper.findIndexOfFirstOpeningBracket(text);
			text = text.substring(charIndex, text.length());
			int sqIndex = JavaScriptHelper.findIndexOfFirstOpeningSquareBracket(text);
			text = text.substring(sqIndex).trim();
		}
		return text;
	}


	/**
	 * Iterates through AstRoot to extract all code blocks, functions, variables
	 * etc.... e.g functions, if statements, variables
	 * 
	 * @param root AstRoot to iterate
	 * @param set add add completions to set (functions only TODO remove this
	 *        and do elsewhere)
	 * @param entered already entered text
	 * @param dot position in code
	 * @param preProcessingMode flag to state whether the parsing is before the
	 *        RSTA parsing
	 * @return
	 */
	protected CodeBlock iterateAstRoot(AstRoot root, Set set, String entered,
			int dot, boolean isPreProcessingMode) {
		JavaScriptParser parser = engine.getParser(this, dot, isPreProcessingMode);
		return parser.convertAstNodeToCodeBlock(root, set, entered);
	}


	/**
	 * Convenience method to call variable resolver
	 * 
	 * @param name
	 * @return
	 */
	public TypeDeclaration resolveTypeDeclation(String name) {
		return variableResolver.resolveType(name, dot);
	}


	/**
	 * Convenience method to call variable resolver
	 * 
	 * @param name
	 * @return JavaScript variable declaration
	 */
	public JavaScriptVariableDeclaration findDeclaration(String name) {
		return variableResolver.findDeclaration(name, dot);
	}


	/**
	 * Get the source of the node and try to resolve function node:
	 * 
	 * @param functionNode
	 * @return a.toString().getCharAt(1); returns String TypeDeclaration
	 */
	public TypeDeclaration resolveTypeFromFunctionNode(AstNode functionNode) {
		String functionText = functionNode.toSource();

		// resolve the TypeDeclaration and set on the variable
		return resolveTypeDeclation(functionText);
	}


	void setParent(JavaScriptCompletionProvider parent) {
		this.parent = parent;
	}


	public void setJavaScriptTypesFactory(JavaScriptTypesFactory factory) {
		this.javaScriptTypesFactory = factory;
	}


	public JavaScriptTypesFactory getJavaScriptTypesFactory() {
		return javaScriptTypesFactory;
	}


	/**
	 * Iterate though the CodeBlock and extract all variables within scope
	 * 
	 * @param completions
	 * @param block
	 * @param dot
	 * @param text
	 * @param findMatch
	 */
	protected void recursivelyAddLocalVars(Set completions, CodeBlock block,
			int dot, String text, boolean findMatch, boolean isPreprocessing) {

		if (!block.contains(dot)) {
			return;
		}

		// Add local variables declared in this code block
		for (int i = 0; i < block.getVariableDeclarationCount(); i++) {
			JavaScriptVariableDeclaration dec = block.getVariableDeclaration(i);
			int decOffs = dec.getOffset();
			if (dot <= decOffs) {

				if (!findMatch || dec.getName().equals(text)) {
					JSVariableCompletion completion = new JSVariableCompletion(
							this, dec, !isPreprocessing);
					// check whether the variable exists and replace as the
					// scope may be local
					if (completions.contains(completion)) {
						completions.remove(completion);
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
						findMatch, isPreprocessing);
			}
		}
	}


	protected boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch == ',' || ch == '.'
				|| ch == getParameterListStart() || ch == getParameterListEnd()
				|| ch == ' ' || ch == '"' || ch == '[' || ch == ']';

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


	public VariableResolver getVariableResolver() {
		return variableResolver;
	}


	public JavaScriptLanguageSupport getLanguageSupport() {
		return parent.getLanguageSupport();
	}


	public void setPreProcessingScripts(PreProcesssingScripts preProcessing) {
		this.preProcessing = preProcessing;
	}


	public PreProcesssingScripts getPreProcessingScripts() {
		return preProcessing;
	}


	private boolean supportsPreProcessingScripts() {
		return preProcessing != null;
	}
	
	public JavaScriptEngine getJavaScriptEngine()
	{
		return engine;
	}
	
	public void setJavaScriptEngine(JavaScriptEngine engine)
	{
		this.engine = engine;
	}
	
	public SourceLocation  getSourceLocForClass(String className) {
		return jarManager.getSourceLocForClass(className);
	}
	
	
	// TODO remove
	public void debugCodeBlock(CodeBlock block, int tab) {
		System.out.println();
		tab++;
		if (block != null) {
			for (int i = 0; i < tab; i++) {
				System.out.print("\t");
			}
			System.out.print("Start: " + block.getStartOffset() + " end:"
					+ block.getEndOffset());
			for (int ii = 0; ii < block.getVariableDeclarationCount(); ii++) {
				JavaScriptVariableDeclaration vd = block
						.getVariableDeclaration(ii);
				System.out.print(" " + vd.getName() + " ");
			}
			for (int i = 0; i < block.getChildCodeBlockCount(); i++) {
				debugCodeBlock(block.getChildCodeBlock(i), tab);
			}
		}
	}

}
