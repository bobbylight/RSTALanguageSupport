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

import javax.swing.Icon;

import org.fife.rsta.ac.java.IconFactory.IconData;


/**
 * Extra methods defined by a completion for a Java member (fields and methods).
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface MemberCompletion extends JavaSourceCompletion {


	public String getDefinedIn();


	/**
	 * {@inheritDoc}
	 */
	public Icon getIcon();


	/**
	 * Returns the signature of this member.
	 *
	 * @return The signature.
	 */
	public String getSignature();


	/**
	 * {@inheritDoc}
	 */
	public String getSummary();


	/**
	 * Returns the type of this member (the return type for methods).
	 *
	 * @return The type of this member.
	 */
	public String getType();


	/**
	 * Returns whether this member is deprecated.
	 *
	 * @return Whether this member is deprecated.
	 */
	public boolean isDeprecated();


	/**
	 * Meta data about the member.  Member completions will be constructed
	 * from a concrete instance of this interface.  This is because there are
	 * two sources that member completions come from - parsing Java source
	 * files and parsing compiled class files (in libraries).
	 */
	public static interface Data extends IconData {

		/**
		 * Returns the name of the parent type declaration that this member is
		 * defined in.
		 *
		 * @return The type declaration this member is defined in.
		 */
		public String getDefinedIn();

		/**
		 * Returns the icon to use when rendering this member's completion.
		 *
		 * @return The icon to use.
		 * @see MemberCompletion#getIcon()
		 */
		public String getIcon();

		/**
		 * Returns the signature of this member.
		 *
		 * @return The signature.
		 * @see MemberCompletion#getSignature()
		 */
		public String getSignature();

		/**
		 * Returns the summary description (should be HTML) for this member.
		 *
		 * @return The summary description, or <code>null</code> if there is
		 *         none.
		 * @see MemberCompletion#getSummary()
		 */
		public String getSummary();

		/**
		 * Returns the type of this member (the return type for methods).
		 *
		 * @return The type of this member.
		 * @see MemberCompletion#getType()
		 */
		public String getType();

		/**
		 * Returns whether this member is deprecated.
		 *
		 * @return Whether this member is deprecated.
		 * @see MemberCompletion#isDeprecated()
		 */
		public boolean isDeprecated();

	}


}