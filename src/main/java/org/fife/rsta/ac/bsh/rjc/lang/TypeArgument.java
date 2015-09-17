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
package org.fife.rsta.ac.bsh.rjc.lang;


public class TypeArgument {

	public static final int NOTHING			= 0;
	public static final int EXTENDS			= 1;
	public static final int SUPER			= 2;

	private Type type;
	private int doesExtend;
	private Type otherType;


	public TypeArgument(Type type) {
		this.type = type;
	}


	public TypeArgument(Type type, int doesExtend, Type otherType) {
		if (doesExtend<0 || doesExtend>2) {
			throw new IllegalArgumentException("Illegal doesExtend: " + doesExtend);
		}
		this.type = type;
		this.doesExtend = doesExtend;
		this.otherType = otherType;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (type==null) {
			sb.append('?');
		}
		else {
			sb.append(type.toString());
		}
		if (doesExtend==EXTENDS) {
			sb.append(" extends ");
			sb.append(otherType.toString());
		}
		else if (doesExtend==SUPER) {
			sb.append(" super ");
			sb.append(otherType.toString());
		}
		return sb.toString();
	}


}