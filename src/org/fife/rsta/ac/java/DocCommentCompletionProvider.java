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
package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * Completion provider for documentation comments.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class DocCommentCompletionProvider extends DefaultCompletionProvider {


	public DocCommentCompletionProvider() {

		// Block tags
		addCompletion(new JavadocCompletion(this, "@author"));
		addCompletion(new JavadocCompletion(this, "@deprecated"));
		addCompletion(new JavadocCompletion(this, "@exception"));
		addCompletion(new JavadocCompletion(this, "@param"));
		addCompletion(new JavadocCompletion(this, "@return"));
		addCompletion(new JavadocCompletion(this, "@see"));
		addCompletion(new JavadocCompletion(this, "@serial"));
		addCompletion(new JavadocCompletion(this, "@serialData"));
		addCompletion(new JavadocCompletion(this, "@serialField"));
		addCompletion(new JavadocCompletion(this, "@since"));
		addCompletion(new JavadocCompletion(this, "@throws"));
		addCompletion(new JavadocCompletion(this, "@version"));

		// Proposed block tags
		addCompletion(new JavadocCompletion(this, "@category"));
		addCompletion(new JavadocCompletion(this, "@example"));
		addCompletion(new JavadocCompletion(this, "@tutorial"));
		addCompletion(new JavadocCompletion(this, "@index"));
		addCompletion(new JavadocCompletion(this, "@exclude"));
		addCompletion(new JavadocCompletion(this, "@todo"));
		addCompletion(new JavadocCompletion(this, "@internal"));
		addCompletion(new JavadocCompletion(this, "@obsolete"));
		addCompletion(new JavadocCompletion(this, "@threadsafety"));

		// Inline tags
		addCompletion(new JavadocTemplateCompletion(this, "{@code", "{@code}", "{@code ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@docRoot", "{@docRoot}", "{@docRoot ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@inheritDoc", "{@inheritDoc}", "{@inheritDoc ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@link", "{@link}", "{@link ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@linkplain", "{@linkplain}", "{@linkplain ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@literal", "{@literal}", "{@literal ${}}${cursor}"));
		addCompletion(new JavadocTemplateCompletion(this, "{@value", "{@value}", "{@value ${}}${cursor}"));

		// Other common stuff
		addCompletion(new JavaShorthandCompletion(this, "null", "<code>null</code>", "<code>null</code>"));
		addCompletion(new JavaShorthandCompletion(this, "true", "<code>true</code>", "<code>true</code>"));
		addCompletion(new JavaShorthandCompletion(this, "false", "<code>false</code>", "<code>false</code>"));

		setAutoActivationRules(false, "@");

	}


	/**{@link something};
	 * {@inheritDoc}
	 */
	protected boolean isValidChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch=='_' || ch=='@' ||
					ch=='{' || ch=='}';
	}


	/**
	 * A Javadoc completion.
	 */
	private static class JavadocCompletion extends BasicCompletion
								implements JavaSourceCompletion {

		public JavadocCompletion(CompletionProvider provider,
									String replacementText) {
			super(provider, replacementText);
		}

		public Icon getIcon() {
			return IconFactory.get().getIcon(IconFactory.JAVADOC_ITEM_ICON);
		}

		public void rendererText(Graphics g, int x, int y, boolean selected) {
			g.drawString(getReplacementText(), x, y);
		}
		
	}


	private static class JavadocTemplateCompletion
						extends JavaTemplateCompletion {

		public JavadocTemplateCompletion(CompletionProvider provider,
				String inputText, String definitionString, String template) {
			super(provider, inputText, definitionString, template);
			setIcon(IconFactory.JAVADOC_ITEM_ICON);
		}

	}


}