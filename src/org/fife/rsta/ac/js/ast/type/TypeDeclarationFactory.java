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

import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;
import org.fife.rsta.ac.js.ast.type.ecma.v3.TypeDeclarationsECMAv3;
import org.fife.rsta.ac.js.ast.type.ecma.v5.TypeDeclarationsECMAv5;



/**
 * TypeDeclarationFactory contains cache of TypeDeclarations for to make the
 * lookup of JavaScript types as efficient as possible.
 */
public class TypeDeclarationFactory {

	
	private static TypeDeclarationFactory instance;
	
	private TypeDeclarations ecma;
	
	private TypeDeclarationFactory()
	{
		setTypeDeclarationVersion(null);
	}

	public List setTypeDeclarationVersion(String ecmaVersion) {
		if(TypeDeclarationsECMAv5.ECMA_VERSION.equals(ecmaVersion)) {
			ecma = new TypeDeclarationsECMAv5();
		}
		else {
			ecma = new TypeDeclarationsECMAv3();
		}
		return ecma.getAllClasses();
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
	 * Removes declaration type from type cache
	 * 
	 * @param name name of type declaration
	 * 
	 */
	public void removeType(String name)
	{
		ecma.removeType(name);
	}
	
	/**
	 * Returns whether the qualified name is a built in JavaScript type
	 * @param name
	 * @return
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

	public static String convertJavaScriptType(String lookupName, boolean qualified) {
		if (lookupName != null) {
			if (TypeDeclarations.NULL_TYPE.equals(lookupName)) { // void has no type
				return null;
			}
			
			//remove param descriptor type from type e.g java.util.Iterator<Object> --> java.util.Iterator
			//as JavaScript does not support this
			if(lookupName.indexOf('<') > -1) {
				lookupName = lookupName.substring(0, lookupName.indexOf('<'));
			}
			
			String lookup = !qualified ? TypeDeclarationFactory.Instance()
					.getJSTypeDeclarationAsString(lookupName) : lookupName;
			
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
		return TypeDeclarationFactory.Instance().getTypeDeclaration(TypeDeclarations.ANY);
	}
	
	public void addType(String name, TypeDeclaration dec) {
		ecma.addTypeDeclaration(name, dec);
	}
	
	public static String getClassName(String lookup) throws RuntimeException
	{
		TypeDeclaration td = Instance().getTypeDeclaration(lookup);
		if(td != null) {
			return td.getQualifiedName();
		}
		//else
		throw new RuntimeException("Error finding TypeDeclaration for: " + lookup);
	}
	
}