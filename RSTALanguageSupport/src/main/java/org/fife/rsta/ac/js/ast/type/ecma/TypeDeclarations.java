package org.fife.rsta.ac.js.ast.type.ecma;

import java.util.*;

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

	// xml support
	public static final String ECMA_NAMESPACE = "E4XNamespace";
	public static final String ECMA_QNAME = "E4XQName";
	public static final String ECMA_XML = "E4XXML";
	public static final String ECMA_XMLLIST = "E4XXMLList";

	public static final String FUNCTION_CALL = "FC";
	// Default - Any type cannot be resolved as any javascript type
	public static final String ANY = "any";

	public static String NULL_TYPE = "void";

	private final Map<String, TypeDeclaration> types = new HashMap<>();

	// reverse lookup for Java types to Javascript types
	private final Map<String, String> javascriptReverseLookup = new HashMap<>();
	private final HashSet<JavaScriptObject> ecmaObjects = new HashSet<>();


	public TypeDeclarations() {
		loadTypes();
		loadExtensions();
		loadReverseLookup();
		loadJavaScriptConstructors();
	}


	private void loadExtensions() {
		addTypeDeclaration(FUNCTION_CALL, new TypeDeclaration(null,
				FUNCTION_CALL, FUNCTION_CALL, false, false));
		addTypeDeclaration(ANY, new TypeDeclaration(null, "any", "any"));
	}


	protected void loadJavaScriptConstructors() {
		addECMAObject(ECMA_STRING, true);
		addECMAObject(ECMA_DATE, true);
		addECMAObject(ECMA_NUMBER, true);
		addECMAObject(ECMA_MATH, false);
		addECMAObject(ECMA_OBJECT, true);
		addECMAObject(ECMA_FUNCTION, true);
		addECMAObject(ECMA_BOOLEAN, true);
		addECMAObject(ECMA_REGEXP, true);
		addECMAObject(ECMA_ARRAY, true);
		addECMAObject(ECMA_ERROR, true);
		addECMAObject(ECMA_JSON, false);
	}


	public void addECMAObject(String type, boolean canBeInstantiated) {

		ecmaObjects.add(new JavaScriptObject(type, canBeInstantiated));
	}


	protected void loadReverseLookup() {
		// need to add lookup for Javascript Objects such as new Date(), String
		// etc..
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
		// xml support
		addJavaScriptLookup("Namespace", ECMA_NAMESPACE);
		addJavaScriptLookup("QName", ECMA_QNAME);
		addJavaScriptLookup("XML", ECMA_XML);
		addJavaScriptLookup("XMLList", ECMA_XMLLIST);
	}


	protected abstract void loadTypes();


	public void addTypeDeclaration(String name, TypeDeclaration dec) {
		types.put(name, dec);
		// add the reverse lookup for the class
		addJavaScriptLookup(dec.getQualifiedName(), name);
	}


	public String getClassName(String lookupType) {
		TypeDeclaration dec = types.get(lookupType);
		return dec != null ? dec.getQualifiedName() : null;
	}


	public List<String> getAllClasses() {
		List<String> classes = new ArrayList<>();

		for (String name : types.keySet()) {
			TypeDeclaration dec = types.get(name);
			if (dec != null) {
				classes.add(dec.getQualifiedName());
			}
		}
		return classes;
	}


	public List<TypeDeclaration> getAllJavaScriptTypeDeclarations() {
		List<TypeDeclaration> jsTypes = new ArrayList<>();

		for (String name : types.keySet()) {
			TypeDeclaration dec = types.get(name);
			if (isJavaScriptType(dec)) {
				jsTypes.add(dec);
			}
		}
		return jsTypes;
	}


	/**
	 * Add Javascript reverse lookup
	 *
	 * @param apiName Java API name
	 * @param jsName Javascript name
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
	public void removeType(String name) {
		types.remove(name);
	}


	/**
	 * Returns whether the qualified name is a built-in JavaScript type
	 *
	 * @param td
	 * @return
	 */
	public boolean isJavaScriptType(TypeDeclaration td) {
		return td != null && td.getPackageName() != null
				&& td.getPackageName().startsWith(ECMA_DEFAULT_PACKAGE);
	}


	/**
	 * Returns the type declaration.
	 *
	 * @param name The type to look up.
	 * @return Lookup type declaration from name. If the
	 *         <code>TypeDeclaration</code> cannot be found, then lookup using
	 *         reserve lookup
	 */
	public TypeDeclaration getTypeDeclaration(String name) {
		// nothing to resolve
		if (name == null)
			return null;

		TypeDeclaration typeDeclaration = types.get(name);
		if (typeDeclaration == null) {
			typeDeclaration = getJSType(name);
		}
		return typeDeclaration;
	}


	/**
	 * Lookup the JavaScript name for a given name
	 *
	 * @param lookupName
	 * @return check whether the name is wrapped in [] then return
	 *         ArrayTypeDeclaration otherwise lookup from JavaScript Name cache
	 * @see ArrayTypeDeclaration
	 */
	private TypeDeclaration getJSType(String lookupName) {
		// first check whether this is an array
		if (lookupName.indexOf('[') > -1 && lookupName.indexOf(']') > -1) {
			TypeDeclaration arrayType = getTypeDeclaration(ECMA_ARRAY);
			ArrayTypeDeclaration arrayDec = new ArrayTypeDeclaration(arrayType
					.getPackageName(), arrayType.getAPITypeName(), arrayType
					.getJSName());

			// trim last index of [
			String arrayTypeName = lookupName.substring(0, lookupName
					.indexOf('['));
			TypeDeclaration containerType = JavaScriptHelper
					.createNewTypeDeclaration(arrayTypeName);
			arrayDec.setArrayType(containerType);
			return arrayDec;
		}
		else {
			String name = javascriptReverseLookup.get(lookupName);
			if (name != null) {
				return types.get(name);
			}
		}

		return null;
	}


	public Set<JavaScriptObject> getJavaScriptObjects() {
		return ecmaObjects;
	}

	/**
	 * Answers the question whether an object can be instantiated (i.e. has a constructor)
	 * Note, only tests ECMA objects
	 * @param name name of class to test
	 *
	 */
	public boolean canECMAObjectBeInstantiated(String name) {
		String tempName = javascriptReverseLookup.get(name);
		if (tempName != null) {
			name = tempName;
		}
		for (JavaScriptObject jo : ecmaObjects) {
			if (jo.getName().equals(name)) {
				return jo.canBeInstantiated();
			}
		}

		return false;
	}


	/**
	 * Simple class holder to hold the name of ECMA object and whether it can be instantiated
	 */
	public static class JavaScriptObject {

		private String name;
		private boolean canBeInstantiated;


		public JavaScriptObject(String name, boolean canBeInstantiated) {
			this.name = name;
			this.canBeInstantiated = canBeInstantiated;
		}


		public String getName() {
			return name;
		}


		public boolean canBeInstantiated() {
			return canBeInstantiated;
		}

		@Override
		public boolean equals(Object jsObj) {
			if (jsObj == this)
				return true;

			if (jsObj instanceof JavaScriptObject) {
				return ((JavaScriptObject) jsObj).getName().equals(getName());
			}

			return false;
		}


		@Override
		public int hashCode() {
			return name.hashCode();
		}


	}
}
