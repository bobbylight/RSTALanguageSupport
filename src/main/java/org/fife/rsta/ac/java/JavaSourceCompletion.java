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

import org.fife.ui.autocomplete.Completion;


/**
 * Interface for Java source code completions.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface JavaSourceCompletion extends Completion {


	/**
	 * Force subclasses to override equals().
	 * TODO: Remove me
	 */
	@Override
	public boolean equals(Object obj);


	/**
	 * Used by {@link JavaCellRenderer} to render this completion choice.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param selected
	 */
	public void rendererText(Graphics g, int x, int y, boolean selected);


}