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

import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.VariableCompletion;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;


/**
 * Completion provider for JavaScript source code (not comments or strings).
 *
 * @author Robert Futrell
 * @vesrion 1.0
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
			addFunctionCompletions(astRoot, set, text);
		}

		// Do a final sort of all of our completions and we're good to go!
		completions = new ArrayList(set);
		Collections.sort(completions);

		// Only match based on stuff after the final '.', since that's what is
		// displayed for all of our completions.
		text = text.substring(text.lastIndexOf('.')+1);

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

		} finally {
			comp.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}

	}


	private void addFunctionCompletions(AstRoot root, Set set, String entered) {

		System.out.println(root.getFunctions() + ", " + root.getFunctionCount());

		Node child = root.getFirstChild();
		while (child!=null) {

			switch (child.getType()) {
				case Token.FUNCTION:
					FunctionNode fn = (FunctionNode)child;
					System.out.println("Function: " + fn.getName());
					String jsdoc = fn.getJsDoc();
					FunctionCompletion fc = new FunctionCompletion(this, fn.getName(), null);
					fc.setShortDescription(Util.jsDocToHtml(jsdoc));
					if (fn.getParamCount()>0) {
						List fnParams = fn.getParams();
						List params = new ArrayList();
						for (int i=0; i<fn.getParamCount(); i++) {
							String paramName = null;
							AstNode node = (AstNode)fnParams.get(i);
							switch (node.getType()) {
								case Token.NAME:
									paramName = ((Name)node).getIdentifier();
									break;
								default:
									System.out.println("Unhandled class for param: " + node.getClass());
									break;
							}
							Parameter param = new Parameter(null, paramName);
							params.add(param);
						}
						fc.setParams(params);
					}
					set.add(fc);
					break;
				case Token.VAR:
					VariableDeclaration varDec = (VariableDeclaration)child;
					List vars = varDec.getVariables();
					for (Iterator i=vars.iterator(); i.hasNext(); ) {
						VariableInitializer var = (VariableInitializer)i.next();
						AstNode target = var.getTarget();
						switch (target.getType()) {
							case Token.NAME:
								Name name = (Name)target;
								System.out.println("... Variable: " + name.getIdentifier());
								set.add(new VariableCompletion(this, name.getIdentifier(), null));
								break;
							default:
								System.out.println("... Unknown var target type: " + target.getClass());
								break;
						}
					}
					break;
				default:
					System.out.println("Unhandled: " + child.getClass());
					break;
			}

			child = child.getNext();

		}

	}


	void setParent(JavaScriptCompletionProvider parent) {
		this.parent = parent;
	}


}