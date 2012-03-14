/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.ast;

import java.util.HashMap;


public class TypeDeclarationFactory {

	public static final String ECMA_ARRAY = "Array";
	public static final String ECMA_BOOLEAN = "Boolean";
	public static final String ECMA_DATE = "Date";
	public static final String ECMA_ERROR = "Error";
	public static final String ECMA_FUNCTION = "Function";
	public static final String ECMA_MATH = "Math";
	public static final String ECMA_NUMBER = "Number";
	public static final String ECMA_OBJECT = "Object";
	public static final String ECMA_REGEXP = "RegExp";
	public static final String ECMA_STRING = "String";

	public static final String FUNCTION_CALL = "FC";

	public static final String ANY = "any";

	private final HashMap typeDeclarations = new HashMap();
	private final HashMap typeDeclarationsLookup = new HashMap();

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
		factory.addType(ECMA_FUNCTION, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSFunction", "Function"));
		factory.addType(ECMA_MATH, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSMath", "Math"));
		factory.addType(ECMA_NUMBER, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSNumber", "Number"));
		factory.addType(ECMA_OBJECT, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSObject", "Object"));
		factory.addType(ECMA_REGEXP, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSRegExp", "RegExp"));
		factory.addType(ECMA_STRING, new TypeDeclaration(
				"org.fife.rsta.ac.js.ecma.api", "JSString", "String"));
		factory.addType(FUNCTION_CALL, new TypeDeclaration(null, FUNCTION_CALL,
				FUNCTION_CALL));
		factory.addType(ANY, new TypeDeclaration(null, "any", "any"));

		// as we are converting Java API, need reserve lookup for java native
		// types
		// add native Java Types lookup
		factory.addReverseLookup("Integer", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("Float", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("Double", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("Short", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("int", TypeDeclarationFactory.ECMA_NUMBER);
		factory
				.addReverseLookup("boolean",
						TypeDeclarationFactory.ECMA_BOOLEAN);
		factory
				.addReverseLookup("Boolean",
						TypeDeclarationFactory.ECMA_BOOLEAN);
		factory.addReverseLookup("double", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("float", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("short", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addReverseLookup("String", TypeDeclarationFactory.ECMA_STRING);

	}


	public static TypeDeclarationFactory Instance() {
		if (instance == null)
			instance = new TypeDeclarationFactory();

		return instance;
	}


	public void addReverseLookup(String apiName, String jsName) {
		typeDeclarationsLookup.put(apiName, jsName);
	}


	public void addType(String name, TypeDeclaration td) {
		typeDeclarations.put(name, td);
		addReverseLookup(td.getAPITypeName(), td.getJSName());
	}


	public TypeDeclaration getTypeDeclaration(String name,
			boolean tryReverseLookup) {
		// nothing to resolve
		if (name == null)
			return null;

		TypeDeclaration typeDeclation = (TypeDeclaration) typeDeclarations
				.get(name);
		if (tryReverseLookup) {
			if (typeDeclation == null) {
				name = getJSTypeName(name);
				if (name != null) {
					typeDeclation = (TypeDeclaration) typeDeclarations
							.get(name);
				}
			}
		}
		return typeDeclation;
	}


	public TypeDeclaration getTypeDeclaration(String name) {
		return getTypeDeclaration(name, false);
	}


	public String getJSTypeName(String lookupName) {
		// first check whether this is an array
		if (lookupName.indexOf('[') > -1 && lookupName.indexOf(']') > -1) {
			return ECMA_ARRAY;
		}
		return (String) typeDeclarationsLookup.get(lookupName);
	}

}