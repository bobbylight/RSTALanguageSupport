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

import org.fife.rsta.ac.java.MemberCompletion.Data;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEnclosingClassName(boolean fullyQualified) {
		// NOTE: This check isn't really necessary, but is here just in case
		// there's a bug in the parsing code.
		TypeDeclaration td = method.getParentTypeDeclaration();
		if (td==null) {
			new Exception("No parent type declaration for: " + getSignature()).
							printStackTrace();
			return "";
		}
		return td.getName(fullyQualified);
	}


	@Override
	public String getIcon() {

		String key = null;

		Modifiers mod = method.getModifiers();
        if (method.getType() == null) {
            key = IconFactory.CONSTRUCTOR_ICON;
        }
		else if (mod==null) {
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


	@Override
	public String getSignature() {
		return method.getNameAndParameters();
	}


	@Override
	public String getSummary() {
		String docComment = method.getDocComment();
		return docComment!=null ? docComment : method.toString();
	}


	@Override
	public String getType() {
		Type type = method.getType();
		return type==null ? "void" : type.toString();
	}


	@Override
	public boolean isAbstract() {
		return method.getModifiers().isAbstract();
	}


	@Override
	public boolean isConstructor() {
		return method.isConstructor();
	}


	@Override
	public boolean isDeprecated() {
		return method.isDeprecated();
	}


	@Override
	public boolean isFinal() {
		return method.getModifiers().isFinal();
	}


	@Override
	public boolean isStatic() {
		return method.getModifiers().isStatic();
	}


}