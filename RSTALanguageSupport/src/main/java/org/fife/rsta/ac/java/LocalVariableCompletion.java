/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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


	LocalVariableCompletion(CompletionProvider provider,
									LocalVariable localVar) {
		super(provider, localVar.getName());
		this.localVar = localVar;
		setRelevance(RELEVANCE);
	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof LocalVariableCompletion) &&
			((LocalVariableCompletion)obj).getReplacementText().
												equals(getReplacementText());
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON);
	}


	@Override
	public String getToolTipText() {
		return localVar.getType() + " " + localVar.getName();
	}


	@Override
	public int hashCode() {
		return getReplacementText().hashCode(); // Match equals()
	}


	@Override
	public void rendererText(Graphics g, int x, int y, boolean selected) {
		StringBuilder sb = new StringBuilder();
		sb.append(localVar.getName());
		sb.append(" : ");
		sb.append(localVar.getType());
		g.drawString(sb.toString(), x, y);
	}


}
