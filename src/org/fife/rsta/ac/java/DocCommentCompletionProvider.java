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
		addCompletion(new JavadocCompletion(this, "{@code}"));
		addCompletion(new JavadocCompletion(this, "{@docRoot}"));
		addCompletion(new JavadocCompletion(this, "{@inheritDoc}"));
		addCompletion(new JavadocCompletion(this, "{@link}"));
		addCompletion(new JavadocCompletion(this, "{@linkplain}"));
		addCompletion(new JavadocCompletion(this, "{@literal}"));
		addCompletion(new JavadocCompletion(this, "{@value}"));

		// Other common stuff
		addCompletion(new JavaShorthandCompletion(this, "null", "<code>null</code>", "<code>null</code>"));
		addCompletion(new JavaShorthandCompletion(this, "true", "<code>true</code>", "<code>true</code>"));
		addCompletion(new JavaShorthandCompletion(this, "false", "<code>false</code>", "<code>false</code>"));

		setAutoActivationRules(false, "@");

	}


	/**
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


}