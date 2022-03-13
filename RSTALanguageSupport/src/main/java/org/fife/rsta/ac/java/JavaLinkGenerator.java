/*
 * 02/17/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.BadLocationException;

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


/**
 * Checks for hyperlink-able tokens under the mouse position when Ctrl is
 * pressed (Cmd on OS X).  Currently this class only checks for accessible
 * members in the current file only (e.g. no members in super classes, no other
 * classes on the classpath, etc.).  So naturally, there is a lot of room for
 * improvement. IDE-style applications, for example, would want to check
 * for members in super-classes, and open their source on click events.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
// TODO: Anonymous inner classes probably aren't handled well.
class JavaLinkGenerator implements LinkGenerator {

	private JavaLanguageSupport jls;


	JavaLinkGenerator(JavaLanguageSupport jls) {
		this.jls = jls;
	}


	/**
	 * Checks if the token at the specified offset is possibly a "click-able"
	 * region.
	 *
	 * @param textArea The text area.
	 * @param offs The offset, presumably at the mouse position.
	 * @return A result object.
	 */
	private IsLinkableCheckResult checkForLinkableToken(
			RSyntaxTextArea textArea, int offs) {

		IsLinkableCheckResult result = null;

		if (offs>=0) {

			try {

				int line = textArea.getLineOfOffset(offs);
				Token first = textArea.getTokenListForLine(line);
				RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
				Token prev = null;

				for (Token t=first; t!=null && t.isPaintable(); t=t.getNextToken()) {

					if (t.containsPosition(offs)) {

						// RSTA's tokens are pooled and re-used, so we must
						// defensively make a copy of the one we want to keep!
						Token token = new TokenImpl(t);
						boolean isMethod = false;

						if (prev==null) {
							prev = RSyntaxUtilities.getPreviousImportantToken(
									doc, line-1);
						}
						if (prev!=null && prev.isSingleChar('.')) {
							// Not a field or method defined in this class.
							break;
						}

						Token next = RSyntaxUtilities.getNextImportantToken(
								t.getNextToken(), textArea, line);
						if (next!=null && next.isSingleChar(Token.SEPARATOR, '(')) {
							isMethod = true;
						}

						result = new IsLinkableCheckResult(token, isMethod);
						break;

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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea,
			int offs) {

		int start = -1;
		int end = -1;

		IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
		if (result!=null) {

			JavaParser parser = jls.getParser(textArea);
			CompilationUnit cu = parser.getCompilationUnit();
			Token t = result.token;
			boolean method = result.method;

			if (cu!=null) {

				TypeDeclaration td = cu.getDeepestTypeDeclarationAtOffset(offs);
				boolean staticFieldsOnly = false;
				boolean deepestTypeDec = true;
				boolean deepestContainingMemberStatic = false;
				while (td!=null && start==-1) {

					// First, check for a local variable in methods/static blocks
					if (!method && deepestTypeDec) {

						Iterator<Member> i = td.getMemberIterator();
						while (i.hasNext()) {
	
							Method m = null; // Nasty!  Clean this code up
							Member member = i.next();
							CodeBlock block = null;

							// Check if a method or static block contains offs
							if (member instanceof Method) {
								m = (Method)member;
								if (m.getBodyContainsOffset(offs) && m.getBody()!=null) {
									deepestContainingMemberStatic = m.isStatic();
									block = m.getBody().getDeepestCodeBlockContaining(offs);
								}
							}
							else if (member instanceof CodeBlock) {
								block = (CodeBlock)member;
								deepestContainingMemberStatic = block.isStatic();
								block = block.getDeepestCodeBlockContaining(offs);
							}

							// If so, scan its locals
							if (block!=null) {
								String varName = t.getLexeme();
								// Local variables first, in reverse order
								List<LocalVariable> locals = block.getLocalVarsBefore(offs);
								Collections.reverse(locals);
								for (LocalVariable local : locals) {
									if (varName.equals(local.getName())) {
										start = local.getNameStartOffset();
										end = local.getNameEndOffset();
									}
								}
								// Then arguments, if any.
								if (start==-1 && m!=null) {
									for (int j=0; j<m.getParameterCount(); j++) {
										FormalParameter p = m.getParameter(j);
										if (varName.equals(p.getName())) {
											start = p.getNameStartOffset();
											end = p.getNameEndOffset();
										}
									}
								}
								break; // No other code block will contain offs
							}
	
						}
					}

					// If no local var match, check fields or methods.
					if (start==-1) {
						String varName = t.getLexeme();
						Iterator<? extends Member> i = method ?
								td.getMethodIterator() : td.getFieldIterator();
						while (i.hasNext()) {
							Member member = i.next();
							if (((!deepestContainingMemberStatic && !staticFieldsOnly) || member.isStatic()) &&
									varName.equals(member.getName())) {
								start = member.getNameStartOffset();
								end = member.getNameEndOffset();
								break;
							}
						}
					}

					// If still no match found, check parent type
					if (start==-1) {
						staticFieldsOnly |= td.isStatic();
						//td = td.isStatic() ? null : td.getParentType();
						td = td.getParentType();
						// Don't check for local vars in parent type methods.
						deepestTypeDec = false;
					}

				}

			}

			if (start>-1) {
				return new SelectRegionLinkGeneratorResult(textArea, t.getOffset(),
						start, end);
			}

		}

		return null;

	}


	/**
	 * The result of checking whether a region of code under the mouse is
	 * <em>possibly</em> link-able.
	 */
	private static class IsLinkableCheckResult {

		/**
		 * The token under the mouse position.
		 */
		private Token token;

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
