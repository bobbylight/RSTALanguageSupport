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
package org.fife.rsta.ac.java.classreader;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * Utility methods for this package.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class Util implements AccessFlags {


	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}


	/**
	 * Returns whether an object has default scope.
	 *
	 * @param accessFlags The access flags to check.
	 * @return Whether an object has default scope.
	 * @see #isDefault(int)
	 * @see #isPrivate(int)
	 * @see #isPublic(int)
	 */
	public static boolean isDefault(int accessFlags) {
		int access = ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE;
		return (accessFlags&access)==0;
	}


	/**
	 * Returns whether an object has private scope.
	 *
	 * @param accessFlags The access flags to check.
	 * @return Whether an object has private scope.
	 * @see #isDefault(int)
	 * @see #isPrivate(int)
	 * @see #isPublic(int)
	 */
	public static boolean isPrivate(int accessFlags) {
		return (accessFlags&ACC_PRIVATE)>0;
	}


	/**
	 * Returns whether an object has protected scope.
	 *
	 * @param accessFlags The access flags to check.
	 * @return Whether an object has protected scope.
	 * @see #isDefault(int)
	 * @see #isPrivate(int)
	 * @see #isPublic(int)
	 */
	public static boolean isProtected(int accessFlags) {
		return (accessFlags&ACC_PROTECTED)>0;
	}


	/**
	 * Returns whether an object has public scope.
	 *
	 * @param accessFlags The access flags to check.
	 * @return Whether an object has public scope.
	 * @see #isDefault(int)
	 * @see #isPrivate(int)
	 * @see #isPublic(int)
	 */
	public static boolean isPublic(int accessFlags) {
		return (accessFlags&ACC_PUBLIC)>0;
	}


	/**
	 * Fully skips a given number of bytes in an input stream.
	 *
	 * @param in The input stream.
	 * @param count The number of bytes to skip.
	 * @throws IOException If an IO error occurs.
	 */
	public static void skipBytes(DataInputStream in, int count)
												throws IOException {
		int skipped = 0;
		while (skipped<count) {
			skipped += in.skipBytes(count-skipped);
		}
	}


}
