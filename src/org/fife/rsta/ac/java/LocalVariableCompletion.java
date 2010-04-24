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

import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.ui.autocomplete.CompletionProvider;

class LocalVariableCompletion extends AbstractJavaSourceCompletion {

	private LocalVariable localVar;


	public LocalVariableCompletion(CompletionProvider provider,
									LocalVariable localVar) {
			super(provider, localVar.getName());
			this.localVar = localVar;
	}


	public boolean equals(Object obj) {
		return (obj instanceof LocalVariableCompletion) &&
			((LocalVariableCompletion)obj).getReplacementText().equals(getReplacementText());
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.LOCAL_VARIABLE_ICON);
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {

		StringBuffer sb = new StringBuffer();
		sb.append(localVar.getName());
		sb.append(" : ");
		sb.append(localVar.getType());
		g.drawString(sb.toString(), x, y);

	}


}