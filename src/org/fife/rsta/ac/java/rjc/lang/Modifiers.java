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
package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.java.rjc.lexer.TokenTypes;


/**
 * Wrapper around modifiers to a member.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Modifiers {

	public static final Integer ABSTRACT			= new Integer(1024);
	public static final Integer FINAL				= new Integer(16);
	public static final Integer INTERFACE			= new Integer(512);
	public static final Integer NATIVE				= new Integer(256);
	public static final Integer PRIVATE				= new Integer(2);
	public static final Integer PROTECTED			= new Integer(4);
	public static final Integer PUBLIC				= new Integer(1);
	public static final Integer STATIC				= new Integer(8);
	public static final Integer STRICTFP			= new Integer(2048);
	public static final Integer SYNCHRONIZED		= new Integer(32);
	public static final Integer TRANSIENT			= new Integer(128);
	public static final Integer VOLATILE			= new Integer(64);

	private List modifiers;
	private List annotations;


	private static final Map MODIFIER_TEXT = new HashMap() {

		private static final long serialVersionUID = 1L;

		{ 
			put(ABSTRACT, "abstract");
			put(FINAL, "final");
			put(INTERFACE, "interface");
			put(NATIVE, "native");
			put(PRIVATE, "private");
			put(PROTECTED, "protected");
			put(PUBLIC, "public");
			put(STATIC, "static");
			put(STRICTFP, "strictfp");
			put(SYNCHRONIZED, "synchronized");
			put(TRANSIENT, "transient");
			put(VOLATILE, "volatile");
		}
	};


	public Modifiers() {
		modifiers = new ArrayList(1); // Usually not many.
		annotations = new ArrayList(0); // Often 0 or 1 (@Deprecated)
	}


	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}


	public boolean addModifier(int tokenType) {

		Integer key = null;

		switch (tokenType) {
			case TokenTypes.KEYWORD_ABSTRACT:
				key = ABSTRACT;
				break;
			case TokenTypes.KEYWORD_FINAL:
				key = FINAL;
				break;
			case TokenTypes.KEYWORD_INTERFACE:
				key = INTERFACE;
				break;
			case TokenTypes.KEYWORD_NATIVE:
				key = NATIVE;
				break;
			case TokenTypes.KEYWORD_PRIVATE:
				key = PRIVATE;
				break;
			case TokenTypes.KEYWORD_PROTECTED:
				key = PROTECTED;
				break;
			case TokenTypes.KEYWORD_PUBLIC:
				key = PUBLIC;
				break;
			case TokenTypes.KEYWORD_STATIC:
				key = STATIC;
				break;
			case TokenTypes.KEYWORD_STRICTFP:
				key = STRICTFP;
				break;
			case TokenTypes.KEYWORD_SYNCHRONIZED:
				key = SYNCHRONIZED;
				break;
			case TokenTypes.KEYWORD_TRANSIENT:
				key = TRANSIENT;
				break;
			case TokenTypes.KEYWORD_VOLATILE:
				key = VOLATILE;
				break;
			default:
				throw new IllegalArgumentException("Invalid tokenType: " +
												tokenType);
		}

		int pos = Collections.binarySearch(modifiers, key);
		if (pos<0) {
			// pos = -insertionPoint - 1
			int insertionPoint = -(pos+1);
			modifiers.add(insertionPoint, key);
		}

		return pos<0;

	}


	private boolean containsModifier(Integer modifierKey) {
		return Collections.binarySearch(modifiers, modifierKey)>=0;
	}


	public boolean isAbstract() {
		return containsModifier(ABSTRACT);
	}


	public boolean isFinal() {
		return containsModifier(FINAL);
	}


	public boolean isPrivate() {
		return containsModifier(PRIVATE);
	}


	public boolean isProtected() {
		return containsModifier(PROTECTED);
	}


	public boolean isPublic() {
		return containsModifier(PUBLIC); 
	}


	public boolean isStatic() {
		return containsModifier(STATIC);
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<annotations.size(); i++) {
			sb.append(annotations.get(i).toString());
			if (i<annotations.size()-1 || modifiers.size()>0) {
				sb.append(' ');
			}
		}
		for (int i=0; i<modifiers.size(); i++) {
			Integer modifier = (Integer)modifiers.get(i);
			sb.append(MODIFIER_TEXT.get(modifier));
			if (i<modifiers.size()-1) {
				sb.append(' ');
			}
		}
		return sb.toString();
	}


}