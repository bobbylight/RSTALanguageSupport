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
package org.fife.rsta.ac.js.ast.type;

import java.util.HashMap;

import org.fife.rsta.ac.js.JavaScriptHelper;



/**
 * TypeDeclarationFactory contains cache of TypeDeclarations for to make the
 * lookup of JavaScript types as efficient as possible.
 */
public class TypeDeclarationFactory {

	// list of supported JavaScript Types
	public static final String ECMA_ARRAY = "org.fife.rsta.ac.js.ecma.api.JSArray";
	public static final String ECMA_BOOLEAN = "org.fife.rsta.ac.js.ecma.api.JSBoolean";
	public static final String ECMA_DATE = "org.fife.rsta.ac.js.ecma.api.JSDate";
	public static final String ECMA_ERROR = "org.fife.rsta.ac.js.ecma.api.JSError";
	public static final String ECMA_FUNCTION = "org.fife.rsta.ac.js.ecma.api.JSFunction";
	public static final String ECMA_MATH = "org.fife.rsta.ac.js.ecma.api.JSMath";
	public static final String ECMA_NUMBER = "org.fife.rsta.ac.js.ecma.api.JSNumber";
	public static final String ECMA_OBJECT = "org.fife.rsta.ac.js.ecma.api.JSObject";
	public static final String ECMA_REGEXP = "org.fife.rsta.ac.js.ecma.api.JSRegExp";
	public static final String ECMA_STRING = "org.fife.rsta.ac.js.ecma.api.JSString";

	public static final String FUNCTION_CALL = "FC";

	// Default - Any type cannot be resolved as any javascript type
	public static final String ANY = "any";
	private static String NULL_TYPE = "void";

	// cache of type declarations
	private final HashMap typeDeclarations = new HashMap();
	// reverse lookup for Java types to Javascript types
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

		// need to add lookup for Javascript Objects such as new Date(), String
		// etc...
		factory.addJavaScriptLookup("String",
				TypeDeclarationFactory.ECMA_STRING);
		factory.addJavaScriptLookup("Date", TypeDeclarationFactory.ECMA_DATE);
		factory.addJavaScriptLookup("RegExp",
				TypeDeclarationFactory.ECMA_REGEXP);
		factory.addJavaScriptLookup("Number",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("Math", TypeDeclarationFactory.ECMA_MATH);
		factory.addJavaScriptLookup("Object",
				TypeDeclarationFactory.ECMA_OBJECT);
		factory.addJavaScriptLookup("Array", TypeDeclarationFactory.ECMA_ARRAY);
		factory.addJavaScriptLookup("Boolean",
				TypeDeclarationFactory.ECMA_BOOLEAN);
		factory.addJavaScriptLookup("Error", TypeDeclarationFactory.ECMA_ERROR);
		factory.addJavaScriptLookup("java.lang.String",
				TypeDeclarationFactory.ECMA_STRING);
		factory.addJavaScriptLookup("java.lang.Number",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Short",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Long",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Float",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Byte",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Double",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("java.lang.Boolean",
				TypeDeclarationFactory.ECMA_BOOLEAN);
		factory
				.addJavaScriptLookup("short",
						TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("long", TypeDeclarationFactory.ECMA_NUMBER);
		factory
				.addJavaScriptLookup("float",
						TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("byte", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("double",
				TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("int", TypeDeclarationFactory.ECMA_NUMBER);
		factory.addJavaScriptLookup("boolean",
				TypeDeclarationFactory.ECMA_BOOLEAN);
	}


	/**
	 * @return Instance of TypeDeclarationFactory
	 */
	public static TypeDeclarationFactory Instance() {
		if (instance == null)
			instance = new TypeDeclarationFactory();

		return instance;
	}


	/**
	 * Add Javascript reverse lookup
	 * 
	 * @param apiName Java API name
	 * @param jsName Javascript name e.g java.lang.String --> String
	 */
	public void addJavaScriptLookup(String apiName, String jsName) {
		typeDeclarationsLookup.put(apiName, jsName);
	}


	/**
	 * Adds declaration type to type cache
	 * 
	 * @param name name of type declaration
	 * @param td type declaration to cache
	 */
	public void addType(String name, TypeDeclaration td) {
		typeDeclarations.put(name, td);
	}
	
	/**
	 * Removes declaration type from type cache
	 * 
	 * @param name name of type declaration
	 * 
	 */
	public void removeType(String name)
	{
		typeDeclarations.remove(name);
	}


	/**
	 * 
	 * @param name
	 * @return Lookup type declaration from name. If the
	 *         <code>TypeDeclaration</code> cannot be found, then lookup using
	 *         reserve lookup
	 */
	public TypeDeclaration getTypeDeclaration(String name) {
		// nothing to resolve
		if (name == null)
			return null;

		TypeDeclaration typeDeclation = (TypeDeclaration) typeDeclarations
				.get(name);
		if (typeDeclation == null) {
			typeDeclation = getJSType(name);
		}
		/*if (typeDeclation == null) {
			name = getJSTypeName(name);
			if (name != null) {
				typeDeclation = (TypeDeclaration) typeDeclarations.get(name);
			}
		}*/
		
		return typeDeclation;
	}
	
	
	/**
	 * @param name of TypeDeclaration to lookup
	 * @return lookup <code>TypeDeclaration</code> and return the JavaScript name
	 */
	private String getJSTypeDeclarationAsString(String name) {
		TypeDeclaration dec = getTypeDeclaration(name);
		return dec != null ? dec.getJSName() : null;
	}


	/**
	 * Lookup the JavaScript name for a given name 
	 * @param lookupName 
	 * @return check whether the name is wrapped in [] then return ArrayTypeDeclaration otherwise lookup from JavaScript Name cache
	 * @see ArrayTypeDeclaration
	 */
	private TypeDeclaration getJSType(String lookupName) {
		// first check whether this is an array
		if (lookupName.indexOf('[') > -1 && lookupName.indexOf(']') > -1) {
			TypeDeclaration arrayType = getTypeDeclaration(ECMA_ARRAY);
			ArrayTypeDeclaration arrayDec = new ArrayTypeDeclaration(arrayType.getPackageName(), arrayType.getAPITypeName(), arrayType.getJSName());
			
			//trim last index of [
			String arrayTypeName = lookupName.substring(0, lookupName.indexOf('['));
			TypeDeclaration containerType = JavaScriptHelper.createNewTypeDeclaration(arrayTypeName);
			arrayDec.setArrayType(containerType);
			return arrayDec;
		}
		else {
			String name = (String) typeDeclarationsLookup.get(lookupName);
			if(name != null) {
				return (TypeDeclaration) typeDeclarations.get(name);
			}
		}
		
		return null;
	}


	/**
	 * The API may have it's own types, so these need converting back to
	 * JavaScript types e.g JSString == String, JSNumber == Number
	 */

	public static String lookupJSType(String lookupName, boolean qualified) {
		if (lookupName != null) {
			if (NULL_TYPE.equals(lookupName)) { // void has no type
				return null;
			}

			String lookup = TypeDeclarationFactory.Instance()
					.getJSTypeDeclarationAsString(lookupName);
			lookupName = lookup != null ? lookup : lookupName;
			if (!qualified) {
				if (lookupName != null && lookupName.indexOf(".") > -1) {
					return lookupName.substring(
							lookupName.lastIndexOf(".") + 1, lookupName
									.length());
				}
			}
		}
		return lookupName;
	}


	/**
	 * @return default type declaration - ANY
	 */
	public static TypeDeclaration getDefaultTypeDeclaration() {
		return TypeDeclarationFactory.Instance().getTypeDeclaration(
				TypeDeclarationFactory.ANY);
	}
}