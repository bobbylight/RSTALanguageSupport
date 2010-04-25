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

import org.fife.ui.autocomplete.CompletionProvider;


/**
 * A completion that represents a package name.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PackageNameCompletion extends AbstractJavaSourceCompletion {


	public PackageNameCompletion(CompletionProvider provider, String text,
								String alreadyEntered) {
		super(provider, text.substring(text.lastIndexOf('.')+1));
	}


	public boolean equals(Object obj) {
		return (obj instanceof PackageNameCompletion) &&
			((PackageNameCompletion)obj).getReplacementText().equals(getReplacementText());
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.PACKAGE_ICON);
	}


	public int hashCode() {
		return getReplacementText().hashCode();
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {
		g.drawString(getInputText(), x, y);
	}


}