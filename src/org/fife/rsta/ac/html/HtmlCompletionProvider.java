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
package org.fife.rsta.ac.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * Completion provider for HTML documents.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class HtmlCompletionProvider extends DefaultCompletionProvider {

	/**
	 * A mapping of tag names to their legal attributes.
	 */
	private Map tagToAttrs;

	/**
	 * Whether the text last grabbed via
	 * {@link #getAlreadyEnteredText(JTextComponent)} was an HTML tag name.
	 *
	 * @see #lastTagName
	 */
	private boolean isTagName;

	/**
	 * Returns the last tag name grabbed via
	 * {@link #getAlreadyEnteredText(JTextComponent)}.  This value is only
	 * valid if {@link #isTagName} is <code>true</code>.
	 */
	private String lastTagName;


	/**
	 * Constructor.
	 */
	public HtmlCompletionProvider() {

		try {
			loadFromXML("data/html.xml");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		tagToAttrs = new HashMap();
		for (Iterator i=completions.iterator(); i.hasNext(); ) {
			MarkupTagCompletion c = (MarkupTagCompletion)i.next();
			String tag = c.getInputText();
			ArrayList attrs = new ArrayList();
			tagToAttrs.put(tag, attrs);
			for (int j=0; j<c.getAttributeCount(); j++) {
				final Parameter param = c.getAttribute(j);
				attrs.add(new AttributeCompletion(this, param));
			}
		}

		setAutoActivationRules(false, "<");

	}


	/**
	 * This nasty hack is just a hook for subclasses (e.g.
	 * <code>PhpCompletionProvider</code>) to be able to get at the
	 * <code>DefaultCompletionProvider</code> implementation.
	 *
	 * @param comp The text component.
	 * @return The text, or <code>null</code> if none.
	 */
	protected String defaultGetAlreadyEnteredText(JTextComponent comp) {
		return super.getAlreadyEnteredText(comp);
	}


	/**
	 * Locates the name of the tag a given offset is in.  This method assumes
	 * that the caller has already concluded that <code>offs</code> is in
	 * fact inside a tag, and that there is a little "text" just before it.
	 *
	 * @param doc The document being parsed.
	 * @param tokenList The token list for the current line.
	 * @param offs The offset into the document to check.
	 * @return Whether a tag name was found.
	 */
	private final boolean findLastTagNameBefore(RSyntaxDocument doc,
												Token tokenList, int offs) {

		lastTagName = null;
		boolean foundOpenTag = false;

		for (Token t=tokenList; t!=null; t=t.getNextToken()) {
			if (t.containsPosition(offs)) {
				break;
			}
			else if (t.type==Token.MARKUP_TAG_NAME) {
				lastTagName = t.getLexeme();
			}
			else if (t.type==Token.MARKUP_TAG_DELIMITER) {
				lastTagName = null;
				foundOpenTag = t.isSingleChar('<');
			}
		}

		if (lastTagName==null && !foundOpenTag) {

			Element root = doc.getDefaultRootElement();
			int prevLine = root.getElementIndex(offs) - 1;
			while (prevLine>=0) {
				tokenList = doc.getTokenListForLine(prevLine);
				for (Token t=tokenList; t!=null; t=t.getNextToken()) {
					if (t.type==Token.MARKUP_TAG_NAME) {
						lastTagName = t.getLexeme();
					}
					else if (t.type==Token.MARKUP_TAG_DELIMITER) {
						lastTagName = null;
						foundOpenTag = t.isSingleChar('<');
					}
				}
				if (lastTagName!=null || foundOpenTag) {
					break;
				}
				prevLine--;
			}

		}

		return lastTagName!=null;

	}


	/**
	 * {@inheritDoc}
	 */
	public String getAlreadyEnteredText(JTextComponent comp) {

		isTagName = true;
		lastTagName = null;

		String text = super.getAlreadyEnteredText(comp);
		if (text!=null) {

			// Check token just before caret (i.e., what we're typing after).
			int dot = comp.getCaretPosition();
			if (dot>0) { // Must go back 1

				RSyntaxTextArea textArea = (RSyntaxTextArea)comp;

				try {

					int line = textArea.getLineOfOffset(dot-1);
					Token list = textArea.getTokenListForLine(line);

					if (list!=null) { // Always true?

						Token t = RSyntaxUtilities.getTokenAtOffset(list,dot-1);

						if (t==null) { // Not sure this ever happens...
							text = null;
						}

						// If we're completing just after a tag delimiter,
						// only offer suggestions for the "inside" of tags,
						// e.g. after "<" and "</".
						else if (t.type==Token.MARKUP_TAG_DELIMITER) {
							if (!isTagOpeningToken(t)) {
								text = null;
							}
						}

						// If we're completing after whitespace, we must
						// determine whether we're "inside" a tag.
						else if (t.type==Token.WHITESPACE) {
							if (!insideMarkupTag(textArea, list, line, dot)) {
								text = null;
							}
						}

						// Otherwise, only auto-complete if we're appending
						// to text already recognized as a markup tag name or
						// attribute (e.g. we know we're in a tag).
						else if (t.type!=Token.MARKUP_TAG_ATTRIBUTE &&
								t.type!=Token.MARKUP_TAG_NAME) {

							// We also have the case where "dot" was the start
							// offset of the line, so the token list we got was
							// actually for the previous line.  So here we must
							// also check for an EOL token that means "we're in
							// a tag."
							// HACK: Using knowledge of HTML/JSP/PHPTokenMaker!
							if (t.type>-1 || t.type<-4) {
								text = null;
							}

						}

						if (text!=null) { // We're going to auto-complete
							t = getTokenBeforeOffset(list, dot-text.length());
							isTagName = t!=null && isTagOpeningToken(t);
							if (!isTagName) {
								RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
								findLastTagNameBefore(doc, list, dot);
							}
						}

					}

				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}

			}

			else {
				text = null; // No completions for offset 0
			}

		}

		return text;

	}


	/**
	 * {@inheritDoc}
	 */
	protected List getCompletionsImpl(JTextComponent comp) {

		List retVal = new ArrayList();
		String text = getAlreadyEnteredText(comp);
		List completions = this.completions;
		if (lastTagName!=null) {
			lastTagName = lastTagName.toLowerCase();
			completions = (List)tagToAttrs.get(lastTagName);
			//System.out.println(completions);
		}

		if (text!=null && completions!=null) {

			int index = Collections.binarySearch(completions, text, comparator);
			if (index<0) {
				index = -index - 1;
			}

			while (index<completions.size()) {
				Completion c = (Completion)completions.get(index);
				if (startsWithIgnoreCase(c.getInputText(), text)) {
					retVal.add(c);
					index++;
				}
				else {
					break;
				}
			}

		}

		return retVal;

	}


	/**
	 * Returns the token before the specified offset.
	 *
	 * @param tokenList A list of tokens containing the offset.
	 * @param offs The offset.
	 * @return The token before the offset, or <code>null</code> if the
	 *         offset was the first offset in the token list (or not in the
	 *         token list at all, which would be an error).
	 */
	private static final Token getTokenBeforeOffset(Token tokenList, int offs) {
		if (tokenList!=null) {
			Token prev = tokenList;
			for (Token t=tokenList.getNextToken(); t!=null; t=t.getNextToken()) {
				if (t.containsPosition(offs)) {
					return prev;
				}
				prev = t;
			}
		}
		return null;
	}


	/**
	 * Returns whether the given offset is inside a markup tag (and not in
	 * string content, such as an attribute value).
	 *
	 * @param textArea The text area being parsed.
	 * @param list The list of tokens for the current line (the line containing
	 *        <code>offs</code>.
	 * @param line The index of the line containing <code>offs</code>.
	 * @param offs The offset into the text area's content to check.
	 * @return Whether the offset is inside a markup tag.
	 */
	private static final boolean insideMarkupTag(RSyntaxTextArea textArea,
								Token list, int line, int offs) {

		int inside = -1; // -1 => not determined, 0 => false, 1 => true

		for (Token t=list; t!=null; t=t.getNextToken()) {
			if (t.containsPosition(offs)) {
				break;
			}
			switch (t.type) {
				case Token.MARKUP_TAG_NAME:
				case Token.MARKUP_TAG_ATTRIBUTE:
					inside = 1;
					break;
				case Token.MARKUP_TAG_DELIMITER:
					inside = t.isSingleChar('>') ? 0 : 1;
					break;
			}
		}

		// Still not determined - check how previous line ended.
		if (inside==-1) {
			if (line==0) {
				inside = 0;
			}
			else {
				RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
				int prevLastToken = doc.getLastTokenTypeOnLine(line-1);
				// HACK: This code uses the insider knowledge that token types
				// -1 through -4 mean "something inside a tag" for all
				// applicable markup languages (HTML, JSP, and PHP)!
				// TODO: Remove knowledge of internal toktn types.
				if (prevLastToken<=-1 && prevLastToken>=-4) {
					inside = 1;
				}
				else {
					inside = 0;
				}
			}
		}

		return inside==1;

	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isAutoActivateOkay(JTextComponent tc) {

		boolean okay = super.isAutoActivateOkay(tc);

		if (okay) {

			RSyntaxTextArea textArea = (RSyntaxTextArea)tc;
			int dot = textArea.getCaretPosition();

			try {

				int line = textArea.getLineOfOffset(dot);
				Token list = textArea.getTokenListForLine(line);

				if (list!=null) { // Always true?
					return !insideMarkupTag(textArea, list, line, dot);
				}

			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}

		}

		return okay;
	}


	/**
	 * Returns whether this token's text is "<" or "</".  It is assumed that
	 * whether this is a markup delimiter token is checked elsewhere.
	 *
	 * @param t The token to check.
	 * @return Whether it is a tag opening token.
	 */
	private static final boolean isTagOpeningToken(Token t) {
		return t.isSingleChar('<') ||
			(t.textCount==2 && t.text[t.textOffset]=='<' &&
					t.text[t.textOffset+1]=='/');
	}


}