/*
 * 04/23/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;


/**
 * A base class for language support implementations.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public abstract class AbstractLanguageSupport implements LanguageSupport {

	/**
	 * The default renderer for the completion list.
	 */
	private ListCellRenderer renderer;


	/**
	 * Constructor.
	 */
	protected AbstractLanguageSupport() {
		setDefaultCompletionCellRenderer(null); // Force default
	}


	/**
	 * Creates the default cell renderer to use when none is specified.
	 * Subclasses can override this method if there is a "better" default
	 * renderer for a specific language.
	 *
	 * @return The default renderer for the completion list.
	 */
	protected ListCellRenderer createDefaultCompletionCellRenderer() {
		return new DefaultListCellRenderer();
	}


	/**
	 * {@inheritDoc}
	 */
	public ListCellRenderer getDefaultCompletionCellRenderer() {
		return renderer;
	}


	/**
	 * {@inheritDoc}
	 */
	public void setDefaultCompletionCellRenderer(ListCellRenderer r) {
		if (r==null) {
			r = createDefaultCompletionCellRenderer();
		}
		renderer = r;
	}


}