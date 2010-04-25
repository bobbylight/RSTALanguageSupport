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
package org.fife.rsta.ac.java.rjc.lexer;


/**
 * An offset into Java source.  This is an interface so we can wrap
 * <code>javax.swing.text.Position</code> instances when parsing code in a
 * <code>JTextComponent</code>, so these offsets can get tracked.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface Offset {


	/**
	 * Returns the offset into the source.
	 *
	 * @return The offset.
	 */
	public int getOffset();


}