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

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.c.CCompletionProvider;
import org.fife.ui.autocomplete.Completion;
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
	protected CompletionProvider createCodeCompletionProvider() {
		DefaultCompletionProvider cp = new PerlCodeCompletionProvider();
		loadCodeCompletionsFromXml(cp);
		addShorthandCompletions(cp);
		return cp;

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

		SortedSet varCompletions = getVariableCompletions(comp);
		if (varCompletions!=null) {
			completions.addAll(varCompletions);
			Collections.sort(completions);
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
	private SortedSet getVariableCompletions(JTextComponent comp) {

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
		SortedSet varCompletions = null;

		String text = getDefaultCompletionProvider().getAlreadyEnteredText(comp);
		char firstChar = text.length()==0 ? 0 : text.charAt(0);
		if (firstChar!='$' && firstChar!='@' && firstChar!='%') {
			System.out.println("DEBUG: No use matching variables, exiting");
			return null;
		}

		for (int i=0; i<=lastLine; i++) {
			Token t = doc.getTokenListForLine(i);
			while (t!=null && (t.offset+t.textCount)<dot && t.isPaintable()) {
				if (t.type==Token.VARIABLE) {
					String name = t.getLexeme();
					char ch = name.charAt(0);
					if (firstChar<=ch) { // '$' comes before '@'/'%' in ascii
						if (varCompletions==null) { // Lazy creation
							varCompletions = new TreeSet(new CaseInsensitiveComparator());
						}
						if (firstChar<ch) { // Use first char they entered
							name = firstChar + name.substring(1);
						}
						VariableCompletion vc = new VariableCompletion(this,
																name, null);
						varCompletions.add(vc);
					}
				}
				t = t.getNextToken();
			}
		}

		// Get only those that match what's typed
		if (varCompletions!=null) {
			varCompletions = varCompletions.subSet(text, text+'{');
		}

		return varCompletions;

	}

private CaseInsensitiveComparator comparator = new CaseInsensitiveComparator();
	/**
	 * A comparator that compares the input text of a {@link Completion}
	 * against a String lexicographically, ignoring case.
	 *
	 * @author Robert Futrell
	 * @version 1.0
	 */
	private static class CaseInsensitiveComparator implements Comparator,
														Serializable {

		public int compare(Object o1, Object o2) {
			String s1 = o1 instanceof String ? (String)o1 :
							((Completion)o1).getInputText();
			String s2 = o2 instanceof String ? (String)o2 :
							((Completion)o2).getInputText();
			return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
		}

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