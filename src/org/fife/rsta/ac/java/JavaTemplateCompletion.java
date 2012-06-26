/*
 * 06/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Graphics;
import javax.swing.Icon;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;


/**
 * A template completion for Java.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaTemplateCompletion extends TemplateCompletion
		implements JavaSourceCompletion {

	private String shortDesc;
	private String icon;


	public JavaTemplateCompletion(CompletionProvider provider,
			String inputText, String definitionString, String template) {
		this(provider, inputText, definitionString, template, null);
	}


	public JavaTemplateCompletion(CompletionProvider provider,
			String inputText, String definitionString, String template,
			String shortDesc) {
		super(provider, inputText, definitionString, template);
		setShortDescription(shortDesc);
		setIcon(IconFactory.TEMPLATE_ICON);
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(icon);
	}


	public String getShortDescription() {
		return shortDesc;
	}

	public void rendererText(Graphics g, int x, int y, boolean selected) {
		JavaShorthandCompletion.renderText(g, getDefinitionString(),
				getShortDescription(), x, y, selected);
	}


	public void setIcon(String iconId) {
		this.icon = iconId;
	}


	public void setShortDescription(String shortDesc) {
		this.shortDesc = shortDesc;
	}


}