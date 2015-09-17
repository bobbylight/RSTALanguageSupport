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
package org.fife.rsta.ac.bsh.rjc.lexer;


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