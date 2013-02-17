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

import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.ui.autocomplete.CompletionProvider;

class LocalVariableCompletion extends AbstractJavaSourceCompletion {

	private LocalVariable localVar;

	/**
	 * The relevance of local variables.  This allows local variables to be
	 * "higher" in the completion list than other types.
	 */
	private static final int RELEVANCE		= 4;


	public LocalVariableCompletion(CompletionProvider provider,
									LocalVariable localVar) {
		super(provider, localVar.getName());
		this.localVar = localVar;
		setRelevance(RELEVANCE);
	}


	public boolean equals(Object obj) {
		return (obj instanceof LocalVariableCompletion) &&
			((LocalVariableCompletion)obj).getReplacementText().
												equals(getReplacementText());
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON);
	}


	public String getToolTipText() {
		return localVar.getType() + " " + localVar.getName();
	}


	public int hashCode() {
		return getReplacementText().hashCode(); // Match equals()
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {

		StringBuffer sb = new StringBuffer();
		sb.append(localVar.getName());
		sb.append(" : ");
		sb.append(localVar.getType());
		g.drawString(sb.toString(), x, y);

	}


}