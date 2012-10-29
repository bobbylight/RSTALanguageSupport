package org.fife.rsta.ac.js.ast.type.ecma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.type.ArrayTypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


public abstract class TypeDeclarations {
	
	
	private static final String ECMA_DEFAULT_PACKAGE = "org.fife.rsta.ac.js.ecma.api";
	// list of supported JavaScript Types
	public static final String ECMA_ARRAY = "JSArray";
	public static final String ECMA_BOOLEAN = "JSBoolean";
	public static final String ECMA_DATE = "JSDate";
	public static final String ECMA_ERROR = "JSError";
	public static final String ECMA_FUNCTION = "JSFunction";
	public static final String ECMA_MATH = "JSMath";
	public static final String ECMA_NUMBER = "JSNumber";
	public static final String ECMA_OBJECT = "JSObject";
	public static final String ECMA_REGEXP = "JSRegExp";
	public static final String ECMA_STRING = "JSString";
	public static final String ECMA_GLOBAL = "JSGlobal";
	public static final String ECMA_JSON = "JSJSON";
	
	public static final String FUNCTION_CALL = "FC";
	// Default - Any type cannot be resolved as any javascript type
	public static final String ANY = "any";
	
	public static String NULL_TYPE = "void";

	private final HashMap types = new HashMap();
	
	// reverse lookup for Java types to Javascript types
	private final HashMap javascriptReverseLookup = new HashMap();
	
	public TypeDeclarations()
	{
		loadTypes();
		loadExtensions();
		loadReverseLookup();
	}
	
	private void loadExtensions()
	{
		addTypeDeclaration(FUNCTION_CALL, new TypeDeclaration(null, FUNCTION_CALL,
				FUNCTION_CALL, false, false));
		addTypeDeclaration(ANY, new TypeDeclaration(null, "any", "any"));
	}
	
	protected void loadReverseLookup()
	{
		// need to add lookup for Javascript Objects such as new Date(), String
		// etc...
		addJavaScriptLookup("String", ECMA_STRING);
		addJavaScriptLookup("Date", ECMA_DATE);
		addJavaScriptLookup("RegExp", ECMA_REGEXP);
		addJavaScriptLookup("Number", ECMA_NUMBER);
		addJavaScriptLookup("Math", ECMA_MATH);
		addJavaScriptLookup("Function", ECMA_FUNCTION);
		addJavaScriptLookup("Object", ECMA_OBJECT);
		addJavaScriptLookup("Array", ECMA_ARRAY);
		addJavaScriptLookup("Boolean", ECMA_BOOLEAN);
		addJavaScriptLookup("Error", ECMA_ERROR);
		addJavaScriptLookup("java.lang.String", ECMA_STRING);
		addJavaScriptLookup("java.lang.Number", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Short", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Long", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Float", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Byte", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Double", ECMA_NUMBER);
		addJavaScriptLookup("java.lang.Boolean", ECMA_BOOLEAN);
		addJavaScriptLookup("short", ECMA_NUMBER);
		addJavaScriptLookup("long", ECMA_NUMBER);
		addJavaScriptLookup("float", ECMA_NUMBER);
		addJavaScriptLookup("byte", ECMA_NUMBER);
		addJavaScriptLookup("double", ECMA_NUMBER);
		addJavaScriptLookup("int", ECMA_NUMBER);
		addJavaScriptLookup("boolean", ECMA_BOOLEAN);
		addJavaScriptLookup("JSON", ECMA_JSON);
	}
	
	protected abstract void loadTypes();
	
	public void addTypeDeclaration(String name, TypeDeclaration dec){
		types.put(name, dec);
		//add the reverse lookup for the class
		addJavaScriptLookup(dec.getQualifiedName(), name);
	}
	
	public String getClassName(String lookupType) {
		TypeDeclaration dec = (TypeDeclaration) types.get(lookupType);
		return dec != null ? dec.getQualifiedName() : null;
	}
	
	public List getAllClasses() {
		ArrayList classes = new ArrayList();
		
		for(Iterator i = types.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			TypeDeclaration dec = (TypeDeclaration) types.get(name);
			if(dec != null) {
				classes.add(dec.getQualifiedName());
			}
		}
		return classes;
	}
	
	/**
	 * Add Javascript reverse lookup
	 * 
	 * @param apiName Java API name
	 * @param jsName Javascript name e.g java.lang.String --> String
	 */
	public void addJavaScriptLookup(String apiName, String jsName) {
		javascriptReverseLookup.put(apiName, jsName);
	}
	
	/**
	 * Removes declaration type from type cache
	 * 
	 * @param name name of type declaration
	 * 
	 */
	public void removeType(String name)
	{
		types.remove(name);
	}
	
	
	/**
	 * Returns whether the qualified name is a built in JavaScript type
	 * @param name
	 * @return
	 */
	public boolean isJavaScriptType(TypeDeclaration td)
	{
		return td != null && td.getPackageName() != null && td.getPackageName().startsWith(ECMA_DEFAULT_PACKAGE);
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

		TypeDeclaration typeDeclation = (TypeDeclaration) types.get(name);
		if (typeDeclation == null) {
			typeDeclation = getJSType(name);
		}
		return typeDeclation;
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
			String name = (String) javascriptReverseLookup.get(lookupName);
			if(name != null) {
				return (TypeDeclaration) types.get(name);
			}
		}
		
		return null;
	}
}
