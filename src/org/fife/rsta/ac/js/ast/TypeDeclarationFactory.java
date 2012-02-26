/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js.ast;

import java.util.HashMap;


public class TypeDeclarationFactory {

	public static final String ECMA_ARRAY = "Array";
	public static final String ECMA_BOOLEAN = "Boolean";
	public static final String ECMA_DATE = "Date";
	public static final String ECMA_ERROR = "Error";
	public static final String ECMA_NUMBER = "Number";
	public static final String ECMA_OBJECT = "Object";
	public static final String ECMA_REGEXP = "RegExp";
	public static final String ECMA_STRING = "String";

	public static final String ANY = "any";

	private static final HashMap typeDeclarations = new HashMap();
	private static final HashMap typeDeclarationsLookup = new HashMap();

	private static TypeDeclarationFactory instance;

	static {
		TypeDeclarationFactory factory = Instance();
		factory.addType(ECMA_ARRAY, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSArray", "Array"));
		factory.addType(ECMA_BOOLEAN, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSBoolean", "Boolean"));
		factory.addType(ECMA_DATE, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSDate", "Date"));
		factory.addType(ECMA_ERROR, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSError", "Error"));
		factory.addType(ECMA_NUMBER, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSNumber", "Number"));
		factory.addType(ECMA_OBJECT, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSObject", "Object"));
		factory.addType(ECMA_REGEXP, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSRegExp", "RegExp"));
		factory.addType(ECMA_STRING, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSString", "String"));
		factory.addType(ANY, new TypeDeclaration(null, "any", "any"));
	}


	private TypeDeclarationFactory() {
	}


	public static TypeDeclarationFactory Instance() {
		if (instance == null)
			instance = new TypeDeclarationFactory();

		return instance;
	}


	public void addType(String name, TypeDeclaration td) {
		typeDeclarations.put(name, td);
		typeDeclarationsLookup.put(td.getAPITypeName(), td.getJSName());
	}


	public TypeDeclaration getTypeDeclaration(String name) {
		return (TypeDeclaration) typeDeclarations.get(name);
	}


	public String getJSTypeName(String lookupName) {
		return (String) typeDeclarationsLookup.get(lookupName);
	}


}