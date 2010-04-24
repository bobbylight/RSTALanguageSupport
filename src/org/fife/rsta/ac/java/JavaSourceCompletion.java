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

import org.fife.ui.autocomplete.Completion;


/**
 * Interface for Java source code completions.
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface JavaSourceCompletion extends Completion {


	/**
	 * Force subclasses to override equals().
	 * TODO: Remove me
	 */
	public abstract boolean equals(Object obj);


	/**
	 * Returns the icon to use when rendering this completion.
	 *
	 * @return The icon.
	 */
	public abstract Icon getIcon();


	/**
	 * Used by {@link JavaCellRenderer} to render this completion choice.
	 *
	 * @param g
	 * @param x
	 * @param y
	 * @param selected
	 */
	public abstract void rendererText(Graphics g, int x, int y,
										boolean selected);


}