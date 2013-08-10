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

import org.fife.rsta.ac.js.ast.type.TypeDeclaration;


/**
 * Cache Local and System scope variables Local scope variables are cleared each
 * time the <code>SourceCompletionProvider</code> finishes parsing the script
 * System scope variables will not be cleared
 */
public class VariableResolver {

	// HashMap of local variables mapped Name --> JSVariableDeclaration
	private HashMap<String, JavaScriptVariableDeclaration> localVariables =
			new HashMap<String, JavaScriptVariableDeclaration>();
	// pre processing variables - these are set when pre-processing
	private HashMap<String, JavaScriptVariableDeclaration> preProcessedVariables =
			new HashMap<String, JavaScriptVariableDeclaration>();
	// HashMap of system variables mapped Name --> JSVariableDeclaration
	// system variables do not get cleared as they are always available to the
	// system
	private HashMap<String, JavaScriptVariableDeclaration> systemVariables =
			new HashMap<String, JavaScriptVariableDeclaration>();
	
	private HashMap<String, JavaScriptFunctionDeclaration> localFunctions =
			new HashMap<String, JavaScriptFunctionDeclaration>();
	private HashMap<String, JavaScriptFunctionDeclaration> preProcessedFunctions =
			new HashMap<String, JavaScriptFunctionDeclaration>();


	/**
	 * Add local scope variable to cache.
	 * 
	 * @param declaration variable to add
	 */
	public void addLocalVariable(JavaScriptVariableDeclaration declaration) {
		localVariables.put(declaration.getName(), declaration);
	}


	/**
	 * Add pre-processing scope variable to cache.
	 * 
	 * @param declaration variable to add
	 */
	public void addPreProcessingVariable(JavaScriptVariableDeclaration declaration) {
		preProcessedVariables.put(declaration.getName(), declaration);
	}


	/**
	 * Add system scope variable to cache
	 * 
	 * @param declaration variable to add
	 */
	public void addSystemVariable(JavaScriptVariableDeclaration declaration) {
		systemVariables.put(declaration.getName(), declaration);
	}


	/**
	 * remove pre-processing variable from system variable cache
	 * 
	 * @param name of the system variable to remove
	 */
	public void removePreProcessingVariable(String name) {
		preProcessedVariables.remove(name);
	}


	/**
	 * remove system variable from system variable cache
	 * 
	 * @param name of the system variable to remove
	 */
	public void removeSystemVariable(String name) {
		systemVariables.remove(name);
	}


	/**
	 * Find JSVariableDeclaration for name against all variable types and check is in scope of caret position
	 * 
	 * @param name
	 * @param dot
	 * @return JSVariableDeclaration from the name
	 */
	public JavaScriptVariableDeclaration findDeclaration(String name, int dot) {
		JavaScriptVariableDeclaration findDeclaration = findDeclaration(
				localVariables, name, dot);
		// test whether this was found and then try pre-processing variable
		findDeclaration = findDeclaration == null ? findDeclaration(
				preProcessedVariables, name, dot) : findDeclaration;
		// last chance... look in system variables
		return findDeclaration == null ? findDeclaration(systemVariables, name,
				dot) : findDeclaration;
	}
	
	public JavaScriptVariableDeclaration findDeclaration(String name, int dot, boolean local, boolean preProcessed, boolean system) {
		JavaScriptVariableDeclaration findDeclaration = local ? findDeclaration(localVariables, name, dot) : null;
		// test whether this was found and then try pre-processing variable
		findDeclaration = findDeclaration == null ? preProcessed ? findDeclaration(preProcessedVariables, name, dot) : null : findDeclaration;
		// last chance... look in system variables
		return findDeclaration == null ? system ? findDeclaration(systemVariables, name, dot) : null : findDeclaration;
	}
	
	/**
	 * Find JSVariableDeclaration within pre-processed and system variable only. Also check is in scope of caret position
	 * 
	 * @param name of variable to resolve
	 * @param dot position in text document
	 * @return JSVariableDeclaration from the name
	 */
	
	public JavaScriptVariableDeclaration findNonLocalDeclaration(String name, int dot) {
		//try pre-processing variable
		JavaScriptVariableDeclaration findDeclaration = findDeclaration(preProcessedVariables, name, dot);
		// last chance... look in system variables
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
	private JavaScriptVariableDeclaration findDeclaration(
			HashMap<String, JavaScriptVariableDeclaration> variables,
			String name, int dot) {
		JavaScriptVariableDeclaration dec = variables.get(name);

		if (dec != null) {
			if (dec.getCodeBlock() == null || dec.getCodeBlock().contains(dot)) {
				int decOffs = dec.getOffset();
				if (dot <= decOffs) {
					return dec;
				}
			}
		}
		return null;
	}


	/**
	 * Find the <code>TypeDeclaration</code> for the variable and check the
	 * scope of the caret position
	 * 
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
		localFunctions.clear();
	}


	public void resetPreProcessingVariables(boolean clear) {
		if (clear) {
			preProcessedVariables.clear();
			preProcessedFunctions.clear();
		}
		else {
			for (JavaScriptVariableDeclaration dec : preProcessedVariables.values()) {
				dec.resetVariableToOriginalType();
			}
		}
	}


	public void resetSystemVariables() {
		systemVariables.clear();
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
	
	
	public void addLocalFunction(JavaScriptFunctionDeclaration func)
	{
		localFunctions.put(func.getName(), func);
	}
	
	public JavaScriptFunctionDeclaration findFunctionDeclaration(String name)
	{
		JavaScriptFunctionDeclaration dec = localFunctions.get(name);
		if(dec == null) {
			dec = preProcessedFunctions.get(name);
		}
		return dec;	
	}
	
	public JavaScriptFunctionDeclaration findFunctionDeclaration(String name, boolean local, boolean preProcessed)
	{
		JavaScriptFunctionDeclaration dec = local ? (JavaScriptFunctionDeclaration) localFunctions.get(name) : null;
		if(dec == null) {
			dec = preProcessed ? (JavaScriptFunctionDeclaration)  preProcessedFunctions.get(name) : null;
		}
		return dec;	
	}
	
	public JavaScriptFunctionDeclaration findFunctionDeclarationByFunctionName(String name, boolean local, boolean preprocessed) {
		JavaScriptFunctionDeclaration func = local ? findFirstFunction(name, localFunctions) : null;
		if(func == null) {
			func = preprocessed ? findFirstFunction(name, preProcessedFunctions) : null;
		}
		return func;
	}
	
	private JavaScriptFunctionDeclaration findFirstFunction(String name,
			HashMap<String, JavaScriptFunctionDeclaration> functions) {
		for (JavaScriptFunctionDeclaration func : functions.values()) {
			if(name.equals(func.getFunctionName())) {
				return func;
			}
		}
		return null;
	}
	
	/**
	 * Add pre-processing scope function to cache.
	 * 
	 * @param function variable to add
	 */
	public void addPreProcessingFunction(
			JavaScriptFunctionDeclaration func) {
		preProcessedFunctions.put(func.getName(), func);
	}

}
