/*
 * 02/17/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SelectRegionLinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;

import sun.reflect.Reflection;

/**
 * Checks for hyperlink-able tokens under the mouse position when Ctrl is
 * pressed (Cmd on OS X). Currently this class only checks for accessible
 * members in the current file only (e.g. no members in super classes, no other
 * classes on the classpath, etc.). So naturally, there is a lot of room for
 * improvement. IDE-style applications, for example, would want to check for
 * members in super-classes, and open their source on click events.
 *
 * @author Robert Futrell
 * @version 1.0
 */
// TODO: Anonymous inner classes probably aren't handled well.
class JavaLinkGenerator implements LinkGenerator {

	private static final Logger log = Logger.getLogger(Reflection.getCallerClass(1));

	private JavaLanguageSupport jls;
	private JavaCompletionProvider javaCompletionProvider;
	private SourceCompletionProvider sourceCompletionProvider;

	JavaLinkGenerator(JavaLanguageSupport jls, JavaCompletionProvider p) {
		this.jls = jls;
		javaCompletionProvider = p;
		sourceCompletionProvider = (SourceCompletionProvider)javaCompletionProvider.
				getDefaultCompletionProvider();
	}

	/**
	 * Checks if the token at the specified offset is possibly a "click-able"
	 * region.
	 *
	 * @param textArea
	 *            The text area.
	 * @param offs
	 *            The offset, presumably at the mouse position.
	 * @return A result object.
	 */
	private IsLinkableCheckResult checkForLinkableToken(RSyntaxTextArea textArea, int offs) {
		// log.info(offs);
		IsLinkableCheckResult result = null;

		if (offs >= 0) {

			try {

				int line = textArea.getLineOfOffset(offs);
				Token first = textArea.getTokenListForLine(line);
				RSyntaxDocument doc = (RSyntaxDocument) textArea.getDocument();
				Token prev = null;

				for (Token t = first; t != null && t.isPaintable(); t = t.getNextToken()) {

					if (t.containsPosition(offs)) {
//						log.info(t);
						// RSTA's tokens are pooled and re-used, so we must
						// defensively make a copy of the one we want to keep!
						Token token = new TokenImpl(t);
						boolean isMethod = false;

//						if (prev == null) {
//							prev = RSyntaxUtilities.getPreviousImportantToken(doc, line - 1);
//						}
//						log.info(prev);
						Token firstE = token;
//						ArrayList<Token> prevTockens = new ArrayList();
//						prevTockens.add(prev);
						int start=firstE.getOffset();
						String alreadyEnteredTextS2 = SourceCompletionProvider.getAlreadyEnteredTextS2(textArea,firstE.getOffset());
						log.info(alreadyEnteredTextS2);
						start = start - alreadyEnteredTextS2.length();
//						while (true) {
//							Token prevprev = RSyntaxUtilities.getPreviousImportantTokenFromOffs(doc,
//									first≈.getTextOffset());
//							// log.info(prevprev);
//							if (prevprev != null && (prevprev.isSingleChar('=') || prevprev.isSingleChar(';')
//									|| prevprev.isSingleChar('{')) || prevprev.isSingleChar('}')) {
//								break;
//							}
//							if (prevprev != null) {
////								prevTockens.add(prevprev);
//								first≈ = prevprev;
//								start = prevprev.getOffset();
//							}else {
//								 break;
//
//							}
//						}
						int end = token.getEndOffset();
						Token last= token;
//						Token next = RSyntaxUtilities.getNextImportantToken(
//								token.getNextToken(), textArea, line);
						 int maxLength = textArea.getText().length();
						 String text2 = textArea.getText(end, Math.min(maxLength, 10));
						 if(text2.trim().startsWith("(")) {
							 int closeBracket = text2.indexOf(')');
							 end+=closeBracket+1;
							 log.info("params added");
						 }
//						Token next =
//								token.getNextToken();
//						if (next!=null && next.isSingleChar(Token.SEPARATOR, '(')) {
//							// looking for close bracket
//							while(true){
//
//								next = // RSyntaxUtilities.getNextImportantToken(
//										next.getNextToken(); //, textArea, line);
//								if(next ==null){
//									break;
//								}else{
//									if (next!=null && next.isSingleChar(Token.SEPARATOR, ')')){
//										last = next;
//										end = next.getEndOffset();
//										break;
//									}
//
//								}
//							}
//						}
						log.info(firstE);
						log.info(last);
						IsLinkableCheckResult aa = new IsLinkableCheckResult(token, isMethod);
						aa.start = start; // first≈.getOffset();
						aa.end = end;
						int length = aa.end - aa.start;
						log.info("pos:  "+aa.start+" "+aa.end);
						if(length<1) {
							log.info("lengt is negative "+aa.start+" "+aa.end);
						}else {
							String text = textArea.getText(aa.start, length);
							log.info(text);
							aa.text = text;
							return aa;
						}
						// log.info(first);
						// if (prev != null && prev.isSingleChar('.')) {
						// // Not a field or method defined in this class.
						// break;
						// }
						//
						// Token next =
						// RSyntaxUtilities.getNextImportantToken(t.getNextToken(),
						// textArea, line);
						// if (next != null &&
						// next.isSingleChar(Token.SEPARATOR, '(')) {
						// isMethod = true;
						// }
						//
						// log.info(token + " " + isMethod);
						// result = new IsLinkableCheckResult(token, isMethod);
						// break;

					}

					else if (!t.isCommentOrWhitespace()) {
						prev = t;
					}

				}

			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Never happens
			}

		}

		return result;

	}

	long lastAccess = -1;

	/**
	 * {@inheritDoc}
	 */
	public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
		log.info(offs);
		int start = -1;
		int end = -1;

		IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
		if (result != null) {

			JavaParser parser = jls.getParser(textArea);
			CompilationUnit cu = parser.getCompilationUnit();
//			log.info(cu);
			Token t = result.token;
			boolean method = result.method;

			if (cu != null) {

				TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(offs);
//				log.info(td);
				boolean staticFieldsOnly = false;
				boolean deepestTypeDec = true;
				boolean deepestContainingMemberStatic = false;
				if(td != null && start == -1) {
					Method findCurrencMethod = SourceCompletionProvider.findCurrencMethod(cu, textArea, td, offs);
					if (findCurrencMethod != null) {
						if(System.currentTimeMillis()-lastAccess < 2000) {
							log.info("too often");
							return null;
						}
						return new LinkGeneratorResult() {

							@Override
							public int getSourceOffset() {
//								log.info(1);
								return 0;
							}

							@Override
							public HyperlinkEvent execute() {
								log.info(result.text);
								String text2 = result.text.replace("@", "");
								sourceCompletionProvider.open(cu, result.text, td, findCurrencMethod, text2, offs);
//								log.info(2);
								return null;
							}
						};
					} else {
						log.info("can't find method");
					}
					// First, check for a local variable in methods/static
					// blocks
				}

			}

			if (start > -1) {
				return new SelectRegionLinkGeneratorResult(textArea, t.getOffset(), start, end);
			}

		}

		return null;

	}

	private void openClassDeclartion(RSyntaxTextArea textArea, int offs, IsLinkableCheckResult result) {

	}

	/**
	 * The result of checking whether a region of code under the mouse is
	 * <em>possibly</em> link-able.
	 */
	private static class IsLinkableCheckResult {

		public String text;

		/**
		 * The token under the mouse position.
		 */
		private Token token;

		private int start;
		private int end;

		/**
		 * Whether the token is a method invocation (as opposed to a local
		 * variable or field).
		 */
		private boolean method;

		private IsLinkableCheckResult(Token token, boolean method) {
			this.token = token;
			this.method = method;
		}

	}

}