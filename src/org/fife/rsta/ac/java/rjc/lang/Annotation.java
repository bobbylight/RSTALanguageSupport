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
package org.fife.rsta.ac.java.rjc.lang;


public class Annotation {

	private Type type;


	public Annotation(Type type) {
		this.type = type;
	}


	public String toString() {
		return "@" + type.toString();
	}


}