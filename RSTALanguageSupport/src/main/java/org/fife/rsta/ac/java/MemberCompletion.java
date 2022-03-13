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

import org.fife.rsta.ac.java.IconFactory.IconData;


/**
 * Extra methods defined by a completion for a Java member (fields and methods).
 *
 * @author Robert Futrell
 * @version 1.0
 */
interface MemberCompletion extends JavaSourceCompletion {


	/**
	 * Returns the name of the enclosing class.
	 *
	 * @param fullyQualified Whether the name returned should be fully
	 *        qualified.
	 * @return The class name.
	 */
	String getEnclosingClassName(boolean fullyQualified);


	/**
	 * Returns the signature of this member.
	 *
	 * @return The signature.
	 */
	String getSignature();


	/**
	 * Returns the type of this member (the return type for methods).
	 *
	 * @return The type of this member.
	 */
	String getType();


	/**
	 * Returns whether this member is deprecated.
	 *
	 * @return Whether this member is deprecated.
	 */
	boolean isDeprecated();


	/**
	 * Metadata about the member.  Member completions will be constructed
	 * from a concrete instance of this interface.  This is because there are
	 * two sources that member completions come from - parsing Java source
	 * files and parsing compiled class files (in libraries).
	 */
	interface Data extends IconData {

		/**
		 * Returns the name of the enclosing class.
		 *
		 * @param fullyQualified Whether the name returned should be fully
		 *        qualified.
		 * @return The class name.
		 */
		String getEnclosingClassName(boolean fullyQualified);

		/**
		 * Returns the signature of this member.
		 *
		 * @return The signature.
		 * @see MemberCompletion#getSignature()
		 */
		String getSignature();

		/**
		 * Returns the summary description (should be HTML) for this member.
		 *
		 * @return The summary description, or <code>null</code> if there is
		 *         none.
		 * @see MemberCompletion#getSummary()
		 */
		String getSummary();

		/**
		 * Returns the type of this member (the return type for methods).
		 *
		 * @return The type of this member.
		 * @see MemberCompletion#getType()
		 */
		String getType();

		/**
		 * Returns whether this member is a constructor.
		 *
		 * @return Whether this member is a constructor.
		 */
		boolean isConstructor();

	}


}
