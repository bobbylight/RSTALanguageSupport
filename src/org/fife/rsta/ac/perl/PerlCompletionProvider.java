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
package org.fife.rsta.ac.perl;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * A completion provider for Perl.  It provides:
 * 
 * <ul>
 *    <li>Auto-completion for standard Perl 5.10 functions (read from an
 *        XML file).</li>
 *    <li>Crude auto-completion for variables.  Any variables declared or used
 *        up to the caret position are offered, regardless of whether or not
 *        they are in scope.</li>
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


	/**
	 * {@inheritDoc}
	 */
	protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
		// Add nothing for now.
	}


	/**
	 * {@inheritDoc}
	 */
	protected CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		return cp;
	}


	/**
	 * {@inheritDoc}
	 */
	protected List getCompletionsImpl(JTextComponent comp) {

		List completions = super.getCompletionsImpl(comp);

		Set varCompletions = getVariableCompletions(comp);
		if (varCompletions!=null) {
			completions.addAll(varCompletions);
		}

		return completions;

	}


	/**
	 * Overridden to return the null char (meaning "no start character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The end character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	public char getParameterListEnd() {
System.out.println("Returning: " + (getUseParensWithFunctions() ? super.getParameterListEnd() : '\0'));
		return getUseParensWithFunctions() ? super.getParameterListEnd() : '\0';
	}


	/**
	 * Overridden to return the null char (meaning "no start character") if the
	 * user doesn't want to use parens around their functions.
	 *
	 * @return The start character for parameters list, or the null char if
	 *         none.
	 * @see #getUseParensWithFunctions()
	 */
	public char getParameterListStart() {
		return getUseParensWithFunctions() ? super.getParameterListEnd() : '\0';
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
	private Set getVariableCompletions(JTextComponent comp) {

		RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
		int dot = textArea.getCaretPosition();
		int lastLine = 0;
		try {
			lastLine = textArea.getLineOfOffset(dot);
		} catch (BadLocationException ble) { // Never happens
			ble.printStackTrace();
			return null;
		}
		RSyntaxDocument doc = (RSyntaxDocument)comp.getDocument();
		Set varCompletions = null;

		for (int i=0; i<=lastLine; i++) {
			Token t = doc.getTokenListForLine(i);
			while (t!=null && t.offset<dot && t.isPaintable()) {
				if (t.type==Token.VARIABLE) {
					if (varCompletions==null) {
						varCompletions = new TreeSet();
					}
					String name = t.getLexeme();
					VariableCompletion vc = new VariableCompletion(this,
																name, null);
					varCompletions.add(vc);
				}
				t = t.getNextToken();
			}
		}

		return varCompletions;

	}


	/**
	 * {@inheritDoc}
	 */
	protected String getXmlResource() {
		return "data/perl5.xml";
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