/*
 * 01/16/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java.classreader.attributes;

import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.classreader.ClassFile;


/**
 * The Signature attribute is an optional fixed-length attribute in the
 * attribute table of the ClassFile, field_info(§4.6) and method_info
 * structures.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Signature extends AttributeInfo {

	private String signature;


	public Signature(ClassFile cf, String signature) {
		super(cf);
		this.signature = signature;
	}


	public List getClassParamTypes() {

		List types = null;
		
		if (signature!=null && signature.startsWith("<")) {

			types = new ArrayList(1); // Usually a small number
			int ltCount = 1;
			int offs = 1;

			while (offs<signature.length() && ltCount>0) {
				char ch = signature.charAt(offs++);
				switch (ch) {
					case '<':
						ltCount++;
						break;
					case '>':
						ltCount--;
						break;
				}
			}

			// We're assuming we don't come across corrupt signatures...

			String temp = signature.substring(1, offs-1);
			offs = 0;
			int colon = temp.indexOf(':', offs);
			while (offs<temp.length() && colon>-1) {
				String ident = temp.substring(offs, colon);
				char ch = temp.charAt(colon+1);
				if (ch=='L') { // A ClassTypeSignature
					int semicolon = temp.indexOf(';', colon+2);
					if (semicolon>-1) {
						String type = temp.substring(colon+2, semicolon);
						// TODO: ...
						types.add(ident);
						offs = semicolon + 1;
					}
					else {
						System.err.println("WARN: Can't parse signature (1): " + signature);
						break;
					}
				}
				else {
					System.err.println("WARN: Can't parse signature (2): " + signature);
					break;
				}
			}
		}

		return types;

	}


	public String getMethodReturnType() {

		String sig = null;

		if (signature!=null) {

			// TODO: Handle "<...>"
			if (signature.charAt(0)=='(') {
				int rparen = signature.indexOf(')', 1);
				if (rparen>-1 && rparen<signature.length()-3) { // Should always be true
					if (signature.charAt(rparen+1)=='T' &&
							signature.charAt(rparen+3)==';') {
						String typeChar = signature.substring(rparen+2, rparen+3);
						return typeChar;
					}
				}
			}

		}

		return sig;

	}


	public String getSignature() {
		return signature;
	}


	public String toString() {
		return "[Signature: signature=" + getSignature() + "]";
	}


}