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
package org.fife.rsta.ac.java.classreader;

import java.io.DataInputStream;
import java.io.IOException;


/**
 * Utility methods for this package.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Util implements AccessFlags {


	/**
	 * Private constructor to prevent instantiation.
	 */
	private Util() {
	}


	/**
	 * Returns whether an object has default scope.
	 *
	 * @return Whether an object has default scope.
	 * @see #isPrivate()
	 * @see #isProtected()
	 * @see #isPublic()
	 */
	public static boolean isDefault(int accessFlags) {
		int access = ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE;
		return (accessFlags&access)==0;
	}


	/**
	 * Returns whether an object has private scope.
	 *
	 * @return Whether an object has private scope.
	 * @see #isDefault()
	 * @see #isProtected()
	 * @see #isPublic()
	 */
	public static boolean isPrivate(int accessFlags) {
		return (accessFlags&ACC_PRIVATE)>0;
	}


	/**
	 * Returns whether an object has protected scope.
	 *
	 * @return Whether an object has protected scope.
	 * @see #isDefault()
	 * @see #isPrivate()
	 * @see #isPublic()
	 */
	public static boolean isProtected(int accessFlags) {
		return (accessFlags&ACC_PROTECTED)>0;
	}


	/**
	 * Returns whether an object has public scope.
	 *
	 * @return Whether an object has public scope.
	 * @see #isDefault()
	 * @see #isPrivate()
	 * @see #isProtected()
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