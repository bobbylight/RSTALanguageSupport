/*
 * 01/11/2010
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.groovy;

import java.util.Collections;
import java.util.List;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.common.CodeBlock;
import org.fife.rsta.ac.common.TokenScanner;
import org.fife.rsta.ac.common.VariableDeclaration;
import org.fife.rsta.ac.java.JarManager;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * The completion provider used for Groovy source code.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class GroovySourceCompletionProvider extends DefaultCompletionProvider {

	private JarManager jarManager;

	private static final char[] KEYWORD_DEF = { 'd', 'e', 'f' };


	/**
	 * Constructor.
	 */
	public GroovySourceCompletionProvider() {
		this(null);
	}


	/**
	 * Constructor.
	 *
	 * @param jarManager The jar manager for this provider.
	 */
	public GroovySourceCompletionProvider(JarManager jarManager) {
		if (jarManager==null) {
			jarManager = new JarManager();
		}
		this.jarManager = jarManager;
		setParameterizedCompletionParams('(', ", ", ')');
		setAutoActivationRules(false, "."); // Default - only activate after '.'
	}



	private CodeBlock createAst(JTextComponent comp) {

		CodeBlock ast = new CodeBlock(0);

		RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
		TokenScanner scanner = new TokenScanner(textArea);
		parseCodeBlock(scanner, ast);

		return ast;

	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		completions.clear();

		CodeBlock ast = createAst(comp);

		int dot = comp.getCaretPosition();
		recursivelyAddLocalVars(completions, ast, dot);

		Collections.sort(completions);

		// Cut down the list to just those matching what we've typed.
		String text = getAlreadyEnteredText(comp);

		int start = Collections.binarySearch(completions, text, comparator);
		if (start<0) {
			start = -(start+1);
		}
		else {
			// There might be multiple entries with the same input text.
			while (start>0 &&
					comparator.compare(completions.get(start-1), text)==0) {
				start--;
			}
		}

		int end = Collections.binarySearch(completions, text+'{', comparator);
		end = -(end+1);

		return completions.subList(start, end);

	}


	/**
	 * {@inheritDoc}
	 */
	protected boolean isValidChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch=='.';
	}


	private void parseCodeBlock(TokenScanner scanner, CodeBlock block) {

		Token t = scanner.next();
		while (t != null) {
			if (t.isRightCurly()) {
				block.setEndOffset(t.getOffset());
				return;
			} else if (t.isLeftCurly()) {
				CodeBlock child = block.addChildCodeBlock(t.getOffset());
				parseCodeBlock(scanner, child);
			} else if (t.is(Token.RESERVED_WORD, KEYWORD_DEF)) {
				t = scanner.next();
				if (t != null) {
					VariableDeclaration varDec = new VariableDeclaration(t
							.getLexeme(), t.getOffset());
					block.addVariable(varDec);
				}
			}
			t = scanner.next();
		}

	}


	private void recursivelyAddLocalVars(List completions, CodeBlock block,
										int dot) {

		if (!block.contains(dot)) {
			return;
		}

		// Add local variables declared in this code block
		for (int i=0; i<block.getVariableDeclarationCount(); i++) {
			VariableDeclaration dec = block.getVariableDeclaration(i);
			int decOffs = dec.getOffset();
			if (decOffs<dot) {
				BasicCompletion c = new BasicCompletion(this, dec.getName());
				completions.add(c);
			}
			else {
				break;
			}
		}

		// Add any local variables declared in a child code block
		for (int i=0; i<block.getChildCodeBlockCount(); i++) {
			CodeBlock child = block.getChildCodeBlock(i);
			if (child.contains(dot)) {
				recursivelyAddLocalVars(completions, child, dot);
				return; // No other child blocks can contain the dot
			}
		}

	}


}