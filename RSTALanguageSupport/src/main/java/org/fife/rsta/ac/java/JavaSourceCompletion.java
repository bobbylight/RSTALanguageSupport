/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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
	boolean equals(Object obj);


	/**
	 * Used by {@link JavaCellRenderer} to render this completion choice.
	 *
	 * @param g The graphics context to render to.
	 * @param x The x-offset at which to render.
	 * @param y The y-offset at which to render.
	 * @param selected Whether this completion is currently selected/active.
	 */
	void rendererText(Graphics g, int x, int y, boolean selected);


}
