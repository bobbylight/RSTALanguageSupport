/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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
	int getOffset();


}
