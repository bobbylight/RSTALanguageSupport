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

import org.fife.rsta.ac.java.MemberCompletion.Data;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;


/**
 * Metadata about a method as read from a Java source file.  This class is
 * used by instances of {@link MethodCompletion}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MethodData implements Data {

	private Method method;


	public MethodData(Method method) {
		this.method = method;
	}


	public String getIcon() {

		String key = null;

		Modifiers mod = method.getModifiers();
		if (mod==null) {
			key = IconFactory.METHOD_DEFAULT_ICON;
		}
		else if (mod.isPrivate()) {
			key = IconFactory.METHOD_PRIVATE_ICON;
		}
		else if (mod.isProtected()) {
			key = IconFactory.METHOD_PROTECTED_ICON;
		}
		else if (mod.isPublic()) {
			key = IconFactory.METHOD_PUBLIC_ICON;
		}
		else {
			key = IconFactory.METHOD_DEFAULT_ICON;
		}

		return key;

	}


	public String getSignature() {
		return method.getNameAndParameters();
	}


	public String getSummary() {
		String docComment = method.getDocComment();
		return docComment!=null ? docComment : method.toString();
	}


	public String getType() {
		Type type = method.getType();
		return type==null ? "void" : type.toString();
	}


	public boolean isAbstract() {
		return method.getModifiers().isAbstract();
	}


	public boolean isDeprecated() {
		return method.isDeprecated();
	}


	public boolean isFinal() {
		return method.getModifiers().isFinal();
	}


	public boolean isStatic() {
		return method.getModifiers().isStatic();
	}


}