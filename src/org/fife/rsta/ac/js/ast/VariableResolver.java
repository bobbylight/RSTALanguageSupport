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
import java.util.Iterator;


/**
 * Cache Local and System scope variables
 * Local scope variables are cleared each time the <code>SourceCompletionProvider</code> finishes parsing the script
 * System scope variables will not be cleared 
 */
public class VariableResolver {

	// HashMap of local variables mapped Name --> JSVariableDeclaration
	private HashMap localVariables = new HashMap();
	//pre processing variables - these are set when pre-processing
	private HashMap preProcessedVariables = new HashMap();
	// HashMap of system variables mapped Name --> JSVariableDeclaration
	// system variables do not get cleared as they are always available to the
	// system
	private HashMap systemVariables = new HashMap();

	/**
	 * Add local scope variable to cache.
	 * @param declaration variable to add
	 */
	public void addLocalVariable(JavaScriptVariableDeclaration declaration) {
		localVariables.put(declaration.getName(), declaration);
	}
	
	/**
	 * Add pre-processing scope variable to cache.
	 * @param declaration variable to add
	 */
	public void addPreProcessingVariable(JavaScriptVariableDeclaration declaration) {
		preProcessedVariables.put(declaration.getName(), declaration);
	}


	/**
	 * Add system scope variable to cache
	 * @param declaration variable to add
	 */
	public void addSystemVariable(JavaScriptVariableDeclaration declaration) {
		systemVariables.put(declaration.getName(), declaration);
	}


	/**
	 * remove pre-processing variable from system variable cache
	 * @param name of the system variable to remove
	 */
	public void removePreProcessingVariable(String name) {
		systemVariables.remove(name);
	}
	
	/**
	 * remove system variable from system variable cache
	 * @param name of the system variable to remove
	 */
	public void removeSystemVariable(String name) {
		systemVariables.remove(name);
	}


	/**
	 * Find JSVariableDeclaration and check is in scope of caret position
	 * 
	 * @param name
	 * @param dot
	 * @return JSVariableDeclaration from the name
	 */
	public JavaScriptVariableDeclaration findDeclaration(String name, int dot) {
		JavaScriptVariableDeclaration findDeclaration = findDeclaration(localVariables,
				name, dot);
		//test whether this was found and then try pre-processing variable
		findDeclaration = findDeclaration == null ? findDeclaration(preProcessedVariables, name,
				dot) : findDeclaration;
		//last chance... look in system variables
		return findDeclaration == null ? findDeclaration(systemVariables, name,
				dot) : findDeclaration;
	}


	/**
	 * Find JSVariableDeclaration and check the scope of the caret position
	 * 
	 * @param name
	 * @param dot
	 * @return JSVariableDeclaration from the name
	 */
	private JavaScriptVariableDeclaration findDeclaration(HashMap variables,
			String name, int dot) {
		JavaScriptVariableDeclaration dec = (JavaScriptVariableDeclaration) variables.get(name);
		if (dec != null) {
			int decOffs = dec.getOffset();
			if (dot <= decOffs) {
				return dec;
			}
		}
		return null;
	}


	/**
	 * Find the <code>TypeDeclaration</code> for the variable and check the scope of the caret position
	 * @param name name of variable
	 * @param dot position
	 * @return TypeDeclaration from the name
	 */
	public TypeDeclaration getTypeDeclarationForVariable(String name, int dot) {
		JavaScriptVariableDeclaration dec = findDeclaration(name, dot);
		return dec != null ? dec.getTypeDeclaration() : null;
	}


	/**
	 * Clear all local scope variables
	 */
	public void resetLocalVariables() {
		localVariables.clear();
	}
	
	public void resetPreProcessingVariables(boolean clear) {
		if(clear) {
			preProcessedVariables.clear();
		}
		else {
			for(Iterator i = preProcessedVariables.values().iterator(); i.hasNext();) {
				JavaScriptVariableDeclaration dec = (JavaScriptVariableDeclaration) i.next();
				dec.resetVariableToOriginalType();
			}
		}
	}
	
	
	/**
	 * Resolve the entered text by chopping up the text and working from left to
	 * right, resolving each type in turn
	 * 
	 * @param entered
	 * @param provider
	 * @param dot
	 * @return TypeDeclaration for variable name
	 */
	public TypeDeclaration resolveType(String varName, int dot) {

		// just look up variable
		return getTypeDeclarationForVariable(varName, dot);
	}	

}
