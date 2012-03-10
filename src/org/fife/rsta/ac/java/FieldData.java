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
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;


/**
 * Metadata about a field as read from a Java source file.  This class is
 * used by instances of {@link FieldCompletion}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class FieldData implements Data {

	private Field field;


	public FieldData(Field field) {
		this.field = field;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getDefinedIn() {
		// NOTE: This check isn't really necessary, but is here just in case
		// there's a bug in the parsing code.
		TypeDeclaration td = field.getParentTypeDeclaration();
		if (td==null) {
			new Exception("No parent type declaration for: " + getSignature()).
							printStackTrace();
			return "";
		}
		return field.getParentTypeDeclaration().getName();
	}


	/**
	 * {@inheritDoc}
	 */
	public String getIcon() {

		String key = null;

		Modifiers mod = field.getModifiers();
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


	/**
	 * {@inheritDoc}
	 */
	public String getSignature() {
		return field.getName();
	}


	/**
	 * {@inheritDoc}
	 */
	public String getSummary() {
		String docComment = field.getDocComment();
		return docComment!=null ? docComment : field.toString();
	}


	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return field.getType().toString();
	}


	public boolean isAbstract() {
		return field.getModifiers().isAbstract();
	}


	/**
	 * Always returns <code>false</code>, fields cannot be constructors.
	 *
	 * @return <code>false</code> always.
	 */
	public boolean isConstructor() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isDeprecated() {
		return field.isDeprecated();
	}


	public boolean isFinal() {
		return field.getModifiers().isFinal();
	}


	public boolean isStatic() {
		return field.getModifiers().isStatic();
	}


}