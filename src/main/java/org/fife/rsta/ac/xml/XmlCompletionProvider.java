/*
 * 10/02/2014
 *
 * XmlCompletionProvider.java - Returns XML tag names or attributes that are
 * seen elsewhere in the document.
 * 
 * This library is distributed under a modified BSD license.  See the included
 * RSyntaxTextArea.License.txt file for details.
 */
package org.fife.rsta.ac.xml;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenImpl;
import org.fife.ui.rsyntaxtextarea.TokenTypes;


/**
 * A completion provider that, in the absence of a DTD or XML schema, makes a
 * best guess at what completion choices the user might want for XML.  It does
 * this by looking at what XML element names and attributes have been used
 * elsewhere in the document:<p>
 * 
 * <ul>
 *    <li>If the caret is not in an XML tag, no completion choices are
 *        suggested.
 *    <li>If the caret is in an XML tag name, other tag names in the document
 *        that start with that prefix are suggested.
 *    <li>If the caret is in an XML tag, but not in the tag's name, the
 *        document is parsed for other instances of that tag.  If any are
 *        found, all attributes used for that tag elsewhere are suggested.
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
class XmlCompletionProvider extends DefaultCompletionProvider {

	private static final char[] TAG_SELF_CLOSE = { '/', '>' };


	public XmlCompletionProvider() {
		setAutoActivationRules(false, "<");
	}


	/**
	 * Creates a <code>Completion</code> of the proper type and adds it
	 * to the completions list.
	 *
	 * @param word The text of the completion.
	 * @param desiredType The completion type we're collecting.
	 */
	private void addCompletionImpl(String word, int desiredType) {
		Completion c;
		if (desiredType==TokenTypes.MARKUP_TAG_NAME) {
			c = new MarkupTagCompletion(this, word);
		}
		else {
			ParameterizedCompletion.Parameter param =
					new ParameterizedCompletion.Parameter(null, word);
			c = new AttributeCompletion(this, param);
		}
		completions.add(c);
	}


	/**
	 * Returns the list of attribute names to offer for completion choices for
	 * a given tag.
	 *
	 * @param doc The document being parsed.
	 * @param inTag The XML tag whose attribute is being completed.
	 * @param currentWordStart The start of the current word.
	 * @return The set of completion choices.  This will never be
	 *         <code>null</code>.
	 */
	private Set<String> collectCompletionWordsAttribute(RSyntaxDocument doc,
			Token inTag, int currentWordStart) {

		Set<String> possibleAttrs = new HashSet<>();
		Set<String> attrs = new HashSet<>();
		Set<String> attrsAlreadySpecified = new HashSet<>();
		String desiredTagName = inTag.getLexeme();
		boolean collectAttrs = false;
		boolean inCurTag = false;

		for (Token t2 : doc) {
			int type = t2.getType();
			if (type==TokenTypes.MARKUP_TAG_NAME) {
				collectAttrs = desiredTagName.equals(t2.getLexeme());
				inCurTag = t2.getOffset()==inTag.getOffset();
				if (!attrs.isEmpty()) {
					possibleAttrs.addAll(attrs);
					attrs.clear();
				}
			}
			else if (type==TokenTypes.MARKUP_TAG_ATTRIBUTE && collectAttrs) {
				if (t2.getOffset()!=currentWordStart) {
					String word = t2.getLexeme();
					if (inCurTag) {
						if (word.indexOf('<')>-1) {
							collectAttrs = false;
							attrs.clear();
							// Keep attrs already specified up to the element
							// start
							//attrsAlreadySpecified.clear();
						}
						else {
							attrsAlreadySpecified.add(word);
						}
					}
					else {
						// This is a hack to work around the fact that RSTA will
						// identify e.g. "<book" as a single attribute token if the
						// user has entered an unclosed tag above it.  We don't
						// want "<book" offered as an attribute possibility in this
						// case.
						if (word.indexOf('<')>-1) {
							// Prevent other attributes from being added
							collectAttrs = false;
							attrs.clear();
							attrsAlreadySpecified.clear();
						}
						else {
							attrs.add(word);
						}
					}
				}
			}
		}

		if (!attrs.isEmpty()) {
			possibleAttrs.addAll(attrs);
		}
		possibleAttrs.removeAll(attrsAlreadySpecified);
		return possibleAttrs;

	}


	/**
	 * Returns the list of tag names to offer for completion choices.
	 *
	 * @param doc The document being parsed.
	 * @param currentWordStart The start of the current word.
	 * @return The set of completion choices.  This will never be
	 *         <code>null</code>.
	 */
	private Set<String> collectCompletionWordsTag(RSyntaxDocument doc,
			int currentWordStart) {
		Set<String> words = new HashSet<>();
		for (Token t2 : doc) {
			if (t2.getType()==TokenTypes.MARKUP_TAG_NAME &&
					t2.getOffset()!=currentWordStart) {
				words.add(t2.getLexeme());
			}
		}
		return words;
	}


	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {

		completions.clear();

		String text = getAlreadyEnteredText(comp);
		if (text==null) {
			return completions;
		}

		RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
		int dot = textArea.getCaretPosition();
		RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
		Token t = RSyntaxUtilities.getPreviousImportantTokenFromOffs(doc, dot);
		if (t==null) {
			UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			return completions;
		}

		int desiredType = getDesiredTokenType(t, dot);
		if (desiredType==TokenTypes.NULL) {
			UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			return completions;
		}

		int currentWordStart = dot - text.length();
		Set<String> words;
		if (desiredType==TokenTypes.MARKUP_TAG_NAME) {
			words = collectCompletionWordsTag(doc, currentWordStart);
		}
		else {
			Token tagNameToken = getTagNameTokenForCaretOffset(textArea);
			if (tagNameToken!=null) {
				tagNameToken = new TokenImpl(tagNameToken);
				words = collectCompletionWordsAttribute(doc,
						tagNameToken, currentWordStart);
			}
			else {
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
				return completions;
			}
		}

		for (String word : words) {
			addCompletionImpl(word, desiredType);
		}
		Collections.sort(completions);

		return super.getCompletionsImpl(comp);

	}


	/**
	 * Returns the type of token to return as completion choices, based on
	 * the current caret position.
	 *
	 * @param t The previous "important" (e.g., non-whitespace) token.
	 * @param dot The caret position.
	 * @return The token type, or {@link TokenTypes#NULL} if no completion
	 *         choices should be suggested for the current caret position.
	 */
	private static int getDesiredTokenType(Token t, int dot) {
		switch (t.getType()) {
			case TokenTypes.MARKUP_TAG_NAME:
				if (t.containsPosition(dot-1)) {
					return t.getType();
				}
				return TokenTypes.MARKUP_TAG_ATTRIBUTE;
			case TokenTypes.MARKUP_TAG_ATTRIBUTE:
				return t.getType();
			case TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE:
				if (t.containsPosition(dot)) {
					return TokenTypes.NULL; // In the attribute itself
				}
				return TokenTypes.MARKUP_TAG_ATTRIBUTE;
			case TokenTypes.MARKUP_TAG_DELIMITER:
				if (t.isSingleChar('<')) {
					return TokenTypes.MARKUP_TAG_NAME;
				}
				return TokenTypes.NULL;
			default:
				return TokenTypes.NULL;
		}
	}


	/**
	 * If the caret is inside a tag, this method returns the token
	 * representing the tag name; otherwise, <code>null</code> is returned.
	 *
	 * @param textArea The text area.
	 * @return The token representing the tag name, or <code>null</code> if it
	 *         could not be found.
	 */
	public static final Token getTagNameTokenForCaretOffset(
			RSyntaxTextArea textArea) {

		int dot = textArea.getCaretPosition();
		int line = textArea.getCaretLineNumber();
		Token toMark = null;

		do {

			Token t = textArea.getTokenListForLine(line);

			while (t!=null && t.isPaintable()) {
				if (t.getType()==Token.MARKUP_TAG_NAME) {
					toMark = t;
				}
				if (t.getEndOffset()==dot || t.containsPosition(dot)) {
					break;
				}
				if (t.getType()==Token.MARKUP_TAG_DELIMITER) {
					if (t.isSingleChar('>') || t.is(TAG_SELF_CLOSE)) {
						toMark = null;
					}
				}
				t = t.getNextToken();
			}

		} while (toMark==null && --line>=0);

		return toMark;

	}


}
