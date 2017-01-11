/*
 * 01/29/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac;

import java.awt.event.ActionEvent;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.TextAction;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;


/**
 * Base class for language supports for markup languages, such as HTML, PHP,
 * and JSP.  This class facilitates support for automatically adding a closing
 * tag (e.g. "<code>&lt;/foo&gt;</code>") when the user types an opening tag
 * (e.g. "<code>&lt;foo attr='val'&gt;</code>").
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractMarkupLanguageSupport
		extends AbstractLanguageSupport {

	protected static final String INSERT_CLOSING_TAG_ACTION =
			"HtmlLanguageSupport.InsertClosingTag";

	/**
	 * Whether closing tags are automatically added when the user types opening
	 * tags.  For HTML, this will only occur for tags that are allowed to have
	 * closing tags, based on the doctype.  For XML, this will always occur if
	 * this property is set to <code>true</code>.
	 */
	private boolean autoAddClosingTags;


	protected AbstractMarkupLanguageSupport() {
		setAutoAddClosingTags(true);
	}


	/**
	 * Returns whether closing tags should be automatically added when the user
	 * types a (non-self-closing) start tag.  This will only be done for tags
	 * where closing tags are accepted and valid, based on the doctype.
	 *
	 * @return Whether to automatically add closing tags.
	 * @see #setAutoAddClosingTags(boolean)
	 */
	public boolean getAutoAddClosingTags() {
		return autoAddClosingTags;
	}


	/**
	 * Installs extra keyboard shortcuts supported by this language support.
	 * The default implementation maps an action to automatically add closing
	 * tags when '&gt;' is pressed; subclasses can override and add additional
	 * shortcuts if desired.<p>
	 *
	 * Subclasses should call this method in their
	 * {@link #install(RSyntaxTextArea)} methods.
	 *
	 * @param textArea The text area to install the shortcuts into.
	 * @see #uninstallKeyboardShortcuts(RSyntaxTextArea)
	 */
	protected void installKeyboardShortcuts(RSyntaxTextArea textArea) {

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();

		im.put(KeyStroke.getKeyStroke('>'), INSERT_CLOSING_TAG_ACTION);
		am.put(INSERT_CLOSING_TAG_ACTION, new InsertClosingTagAction());

	}


	/**
	 * Subclasses should override this method to return whether a specified
	 * tag should have its closing tag auto-inserted.
	 *
	 * @param tag The name of the tag to check.
	 * @return Whether the tag should have its closing tag auto-inserted.
	 */
	protected abstract boolean shouldAutoCloseTag(String tag);


	/**
	 * Sets whether closing tags should be automatically added when the user
	 * types a (non-self-closing) start tag.  This will only be done for tags
	 * where closing tags are accepted and valid, based on the doctype.
	 *
	 * @param autoAdd Whether to automatically add closing tags.
	 * @see #getAutoAddClosingTags()
	 */
	public void setAutoAddClosingTags(boolean autoAdd) {
		autoAddClosingTags = autoAdd;
	}


	/**
	 * Uninstalls any keyboard shortcuts specific to this language support.<p>
	 *
	 * Subclasses should call this method in their
	 * {@link #uninstall(RSyntaxTextArea)} methods.
	 *
	 * @param textArea The text area to uninstall the actions from.
	 * @see #installKeyboardShortcuts(RSyntaxTextArea)
	 */
	protected void uninstallKeyboardShortcuts(RSyntaxTextArea textArea) {

		InputMap im = textArea.getInputMap();
		ActionMap am = textArea.getActionMap();

		im.remove(KeyStroke.getKeyStroke('>'));
		am.remove(INSERT_CLOSING_TAG_ACTION);

	}


	/**
	 * Action that checks whether a closing tag should be auto-inserted into
	 * the document.  Subclasses should map this action to the '&gt;'
	 * key-typed event.
	 */
	private class InsertClosingTagAction extends TextAction {

		InsertClosingTagAction() {
			super(INSERT_CLOSING_TAG_ACTION);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			RSyntaxTextArea textArea = (RSyntaxTextArea)getTextComponent(e);
			RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();
			Caret c = textArea.getCaret();

			int dot = c.getDot(); // Get before "<" insertion
			boolean selection = dot!=c.getMark(); // Me too
			textArea.replaceSelection(">");

			// Don't automatically complete a tag if there was a selection
			if (!selection && getAutoAddClosingTags()) {

				Token t = doc.getTokenListForLine(textArea.getCaretLineNumber());
				t = RSyntaxUtilities.getTokenAtOffset(t, dot);
				if (t!=null && t.isSingleChar(Token.MARKUP_TAG_DELIMITER, '>')) {
					String tagName = discoverTagName(doc, dot);
					if (tagName!=null) {
						textArea.replaceSelection("</" + tagName + ">");
						textArea.setCaretPosition(dot+1);
					}
				}

			}

		}

		/**
		 * Discovers the name of the tag just opened, if any.  Assumes standard
		 * SGML-style markup tags.
		 *
		 * @param doc The document to parse.
		 * @param dot The location of the caret.  This should be right at a
		 *        "<code>&gt;</code>" character closing an HTML tag.
		 * @return The name of the tag to close, or <code>null</code> if it
		 *         could not be determined.
		 */
		private String discoverTagName(RSyntaxDocument doc, int dot) {

			String candidate = null;

			Element root = doc.getDefaultRootElement();
			int curLine = root.getElementIndex(dot);

			// For now, we only check for tags on the current line, for
			// simplicity.  Tags spanning multiple lines aren't common anyway.
			Token t = doc.getTokenListForLine(curLine);
			while (t!=null && t.isPaintable()) {
				if (t.getType()==Token.MARKUP_TAG_DELIMITER) {
					if (t.isSingleChar('<')) {
						t = t.getNextToken();
						if (t!=null && t.isPaintable()) {
							candidate = t.getLexeme();
						}
					}
					else if (t.isSingleChar('>')) {
						if (t.getOffset()==dot) {
							if (candidate==null ||
									shouldAutoCloseTag(candidate)) {
								return candidate;
							}
							return null;
						}
					}
					else if (t.is(Token.MARKUP_TAG_DELIMITER, "</")) {
						candidate = null;
					}
				}

				t = t.getNextToken();

			}

			return null; // No match found

		}

	}


}