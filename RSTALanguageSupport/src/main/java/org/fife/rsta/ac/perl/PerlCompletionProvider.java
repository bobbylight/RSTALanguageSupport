/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.perl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.rsta.ac.common.CodeBlock;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.rsta.ac.common.VariableDeclaration;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * A completion provider for Perl.  It provides:
 *
 * <ul>
 *    <li>Auto-completion for standard Perl 5.10 functions (read from an
 *        XML file).</li>
 *    <li>Crude auto-completion for variables.  Only variables in scope at the
 *        current caret position are suggested, but there may still be issues
 *        with variable types, etc.</li>
 * </ul>
 *
 * To toggle whether parameter assistance wraps your parameters in parens,
 * use the {@link #setUseParensWithFunctions(boolean)} method.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PerlCompletionProvider extends CCompletionProvider {

	private boolean useParensWithFunctions;


	@Override
	protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
		// Add nothing for now.
	}


	/**
	 * Creates an "AST" for Perl code, representing code blocks and variables
	 * inside those blocks.
	 *
	 * @param textArea The text area.
	 * @return A "code block" representing the entire Perl source file.
	 */
	private CodeBlock createAst(RSyntaxTextArea textArea) {
		CodeBlock ast = new CodeBlock(0);
		TokenScanner scanner = new TokenScanner(textArea);
		parseCodeBlock(scanner, ast);
		return ast;
	}


	@Override
	protected CompletionProvider createCodeCompletionProvider() {
		DefaultCompletionProvider cp = new PerlCodeCompletionProvider(this);
		loadCodeCompletionsFromXml(cp);
		addShorthandCompletions(cp);
		return cp;

	}


	@Override
	protected CompletionProvider createStringCompletionProvider() {
        return new DefaultCompletionProvider();
	}


	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		List<Completion> completions = super.getCompletionsImpl(comp);

		SortedSet<Completion> varCompletions = getVariableCompletions(comp);
		if (varCompletions!=null) {
			completions.addAll(varCompletions);
			Collections.sort(completions);
		}

		return completions;

	}


	/**
	 * Overridden to return the null char (meaning "no end character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The end character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	@Override
	public char getParameterListEnd() {
		return getUseParensWithFunctions() ? ')' : 0;
	}


	/**
	 * Overridden to return the null char (meaning "no start character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The start character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	@Override
	public char getParameterListStart() {
		return getUseParensWithFunctions() ? '(' : ' ';
	}


	/**
	 * Returns whether the user wants to use parens around parameters to
	 * functions.
	 *
	 * @return Whether to use parens around parameters to functions.
	 * @see #setUseParensWithFunctions(boolean)
	 */
	public boolean getUseParensWithFunctions() {
		return useParensWithFunctions;
	}


	/**
	 * Does a crude search for variables up to the caret position.  This
	 * method does not care whether the variables are in scope at the caret
	 * position.
	 *
	 * @param comp The text area.
	 * @return The completions for variables, or <code>null</code> if there
	 *         were none.
	 */
	private SortedSet<Completion> getVariableCompletions(JTextComponent comp) {

		RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
		int dot = textArea.getCaretPosition();
		SortedSet<Completion> varCompletions = new TreeSet<>(comparator);

		CompletionProvider p = getDefaultCompletionProvider();
		String text = p.getAlreadyEnteredText(comp);
		char firstChar = text.length()==0 ? 0 : text.charAt(0);
		if (firstChar!='$' && firstChar!='@' && firstChar!='%') {
			return null;
		}

		// Go through all code blocks in scope and look for variables
		// declared before the caret.
		CodeBlock block = createAst(textArea);
		recursivelyAddLocalVars(varCompletions, block, dot, firstChar);

		// Get only those that match what's typed
		if (varCompletions.size()>0) {
			Completion from = new BasicCompletion(p, text);
			Completion to = new BasicCompletion(p, text + '{');
			varCompletions = varCompletions.subSet(from, to);
		}

		return varCompletions;

	}

private CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();
	/**
	 * A comparator that compares the input text of two {@link Completion}s
	 * lexicographically, ignoring case.
	 */
	private static final class CaseInsensitiveComparator
			implements Comparator<Completion>, Serializable {

		@Override
		public int compare(Completion c1, Completion c2) {
			String s1 = c1.getInputText();
			String s2 = c2.getInputText();
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}

	}


	@Override
	protected String getXmlResource() {
		return "data/perl5.xml";
	}


	/**
	 * Recursively adds code blocks, remembering variables in them.
	 *
	 * @param scanner The scanner to read from.
	 * @param block The code block update.
	 */
	private void parseCodeBlock(TokenScanner scanner, CodeBlock block) {
		Token t = scanner.next();
		while (t != null) {
			if (t.isRightCurly()) {
				block.setEndOffset(t.getOffset());
				return;
			}
			else if (t.isLeftCurly()) {
				CodeBlock child = block.addChildCodeBlock(t.getOffset());
				parseCodeBlock(scanner, child);
			}
			else if (t.getType()==Token.VARIABLE) {
				VariableDeclaration varDec = new VariableDeclaration(
						t.getLexeme(), t.getOffset());
				block.addVariable(varDec);
			}
			t = scanner.next();
		}
	}


	/**
	 * Recursively adds any local variables defined before the given caret
	 * offset, and in the given code block (and any nested children the caret
	 * is in).
	 *
	 * @param completions The list to add to.
	 * @param block The code block to search through.
	 * @param dot The caret position.
	 */
	private void recursivelyAddLocalVars(SortedSet<Completion> completions,
			CodeBlock block, int dot, int firstChar) {

		if (!block.contains(dot)) {
			return;
		}

		// Add local variables declared in this code block
		for (int i = 0; i < block.getVariableDeclarationCount(); i++) {
			VariableDeclaration dec = block.getVariableDeclaration(i);
			int decOffs = dec.getOffset();
			if (decOffs < dot) {
				String name = dec.getName();
				char ch = name.charAt(0);
				if (firstChar<=ch) { // '$' comes before '@'/'%' in ascii
					if (firstChar<ch) { // Use first char they entered
						name = firstChar + name.substring(1);
					}
					BasicCompletion c = new BasicCompletion(this, name);
					completions.add(c);
				}
			}
			else { // A variable declared past the caret -> nothing more to add
				break;
			}
		}

		// Add any local variables declared in a child code block
		for (int i = 0; i < block.getChildCodeBlockCount(); i++) {
			CodeBlock child = block.getChildCodeBlock(i);
			if (child.contains(dot)) {
				recursivelyAddLocalVars(completions, child, dot, firstChar);
				return; // No other child blocks can contain the dot
			}
		}

	}

	/**
	 * Sets whether the user wants to use parens around parameters to
	 * functions.
	 *
	 * @param use Whether to use parens around parameters to functions.
	 * @see #getUseParensWithFunctions()
	 */
	public void setUseParensWithFunctions(boolean use) {
		useParensWithFunctions = use;
	}


}
