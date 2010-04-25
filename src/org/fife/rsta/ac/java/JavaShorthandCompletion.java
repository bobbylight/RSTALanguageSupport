/*
 * 04/08/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;


/**
 * A completion for shorthand items that mimics the style seen in Eclipse.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JavaShorthandCompletion extends ShorthandCompletion implements
		JavaSourceCompletion {


	/**
	 * Constructor.
	 *
	 * @param provider
	 * @param inputText
	 * @param replacementText
	 */
	public JavaShorthandCompletion(CompletionProvider provider,
			String inputText, String replacementText) {
		super(provider, inputText, replacementText);
	}


	/**
	 * Constructor.
	 *
	 * @param provider
	 * @param inputText
	 * @param replacementText
	 * @param shortDesc
	 */
	public JavaShorthandCompletion(CompletionProvider provider,
			String inputText, String replacementText, String shortDesc) {
		super(provider, inputText, replacementText, shortDesc);
	}


	/**
	 * {@inheritDoc}
	 */
	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.TEMPLATE_ICON);
	}


	/**f
	 * {@inheritDoc}
	 */
	public void rendererText(Graphics g, int x, int y, boolean selected) {
//		g.drawString(toString(), x, y);
//		if (shortDesc==null) {
//			return getInputText();
//		}
//		return getInputText() + " - " + shortDesc;
		Color old = g.getColor();
		if (!selected) {
			g.setColor(new Color(0, 127, 174));//114, 157, 219));
		}
		String temp = getInputText() + " - ";
		g.drawString(temp, x, y);
		x += g.getFontMetrics().stringWidth(temp);
		if (!selected) {
			g.setColor(Color.GRAY);
		}
		g.drawString(getReplacementText(), x, y);
	}


}