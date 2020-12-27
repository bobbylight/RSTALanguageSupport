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

import java.util.List;
import java.util.Set;

import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;
import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations.JavaScriptObject;
import org.fife.rsta.ac.js.ast.type.ecma.client.ClientBrowserAdditions;
import org.fife.rsta.ac.js.ast.type.ecma.client.DOMAddtions;
import org.fife.rsta.ac.js.ast.type.ecma.client.HTMLDOMAdditions;
import org.fife.rsta.ac.js.ast.type.ecma.e4x.ECMAvE4xAdditions;
import org.fife.rsta.ac.js.ast.type.ecma.v5.TypeDeclarationsECMAv5;



/**
 * TypeDeclarationFactory contains cache of TypeDeclarations for to make the
 * lookup of JavaScript types as efficient as possible.
 */
public class TypeDeclarationFactory {

	
	private TypeDeclarations ecma;
	
	public TypeDeclarationFactory()
	{
		setTypeDeclarationVersion(null, false, false);
	}

	
	public List<String> setTypeDeclarationVersion(String ecmaVersion,
			boolean xmlSupported, boolean client) {
		
		try
		{
			ecmaVersion = ecmaVersion == null ? getDefaultECMAVersion() : ecmaVersion;
			//try to instantiate classes
			Class<?> ecmaClass = TypeDeclarationFactory.class.getClassLoader().loadClass(ecmaVersion);
		 	ecma = (TypeDeclarations) ecmaClass.newInstance();
		}
		catch(Exception e)
		{
			//TODO log error?
			//ignore this
			ecma = new TypeDeclarationsECMAv5();
		}
		
		if(xmlSupported) { //add E4X API
			new ECMAvE4xAdditions().addAdditionalTypes(ecma);
		}
		
		if(client) {
			//for client we are going to add DOM, HTML DOM and Browser attributes/methods
			new ClientBrowserAdditions().addAdditionalTypes(ecma);
			new DOMAddtions().addAdditionalTypes(ecma);
			new HTMLDOMAdditions().addAdditionalTypes(ecma);
		}
			
		
		return ecma.getAllClasses();
	}
	
	/**
	 * @return Default base ECMA implementation
	 */
	protected String getDefaultECMAVersion()
	{
		return TypeDeclarationsECMAv5.class.getName();
	}
	
	public List<TypeDeclaration> getAllJavaScriptTypes() {
		return ecma.getAllJavaScriptTypeDeclarations();
	}

	/**
	 * Removes declaration type from type cache
	 * 
	 * @param name name of type declaration
	 * 
	 */
	public void removeType(String name) {
		ecma.removeType(name);
	}
	
	/**
	 * Returns whether the qualified name is a built in JavaScript type
	 * @param td The type declaration to check.
	 * @return Whether it is a built-in JS type.
	 */
	public boolean isJavaScriptType(TypeDeclaration td)
	{
		return ecma.isJavaScriptType(td);
	}


	/**
	 * 
	 * @param name
	 * @return Lookup type declaration from name. If the
	 *         <code>TypeDeclaration</code> cannot be found, then lookup using
	 *         reserve lookup
	 */
	public TypeDeclaration getTypeDeclaration(String name) {
		return ecma.getTypeDeclaration(name);
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
	 * The API may have it's own types, so these need converting back to
	 * JavaScript types e.g JSString == String, JSNumber == Number
	 */

	public String convertJavaScriptType(String lookupName, boolean qualified) {
		if (lookupName != null) {
			if (TypeDeclarations.NULL_TYPE.equals(lookupName)) { // void has no type
				return null;
			}
			
			//remove param descriptor type from type e.g java.util.Iterator<Object> --> java.util.Iterator
			//as JavaScript does not support this
			if(lookupName.indexOf('<') > -1) {
				lookupName = lookupName.substring(0, lookupName.indexOf('<'));
			}
			
			String lookup = !qualified ? getJSTypeDeclarationAsString(lookupName) : lookupName;
			
			lookupName = lookup != null ? lookup : lookupName;
			if (!qualified) {
				if (lookupName != null && lookupName.contains(".")) {
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
	public TypeDeclaration getDefaultTypeDeclaration() {
		return getTypeDeclaration(TypeDeclarations.ANY);
	}
	
	public void addType(String name, TypeDeclaration dec) {
		ecma.addTypeDeclaration(name, dec);
	}
	
	public String getClassName(String lookup) throws RuntimeException {
		TypeDeclaration td = getTypeDeclaration(lookup);
		if(td != null) {
			return td.getQualifiedName();
		}
		//else
		throw new RuntimeException("Error finding TypeDeclaration for: " + lookup);
	}
	
	/**
	 * 
	 * @return a list of ECMA JavaScriptObjects
	 */
	public Set<JavaScriptObject> getECMAScriptObjects() {
		return ecma.getJavaScriptObjects();
	}
	
	/**
	 * Answers the question whether an object can be instantiated (i.e has a constructor) 
	 * @param name name of class to test
	 * 
	 */
	public boolean canJavaScriptBeInstantiated(String name) {
		return ecma.canECMAObjectBeInstantiated(name);
	}
	
}
