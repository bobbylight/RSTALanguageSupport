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


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PackageNameCompletion) &&
			((PackageNameCompletion)obj).getReplacementText().equals(getReplacementText());
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.PACKAGE_ICON);
	}


	@Override
	public int hashCode() {
		return getReplacementText().hashCode();
	}


	@Override
	public void rendererText(Graphics g, int x, int y, boolean selected) {
		g.drawString(getInputText(), x, y);
	}


}