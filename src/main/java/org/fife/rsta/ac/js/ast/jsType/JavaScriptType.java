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
package org.fife.rsta.ac.js.ast.jsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSCompletion;


/**
 * Cached Type Tree Node with pointer to a list of super classes to make it easy
 * to walk through Completion hierarchy Contains a HashMap of lookup keys to
 * <code>JSCompletion</code>
 * 
 * @author steveup
 * 
 */
public class JavaScriptType {

	// base type
	protected TypeDeclaration type;
	// completions for base type String-->JSCompletion
	protected HashMap<String, JSCompletion> methodFieldCompletions;
	//constructor completions
	protected HashMap<String, JSCompletion> constructors;
	//class type
	protected JSCompletion classType;

	// extended cached types
	private ArrayList<JavaScriptType> extended;


	public JavaScriptType(TypeDeclaration type) {
		this.type = type;
		methodFieldCompletions = new HashMap<>();
		constructors = new HashMap<>();
		extended = new ArrayList<>();
	}


	/**
	 * Add method or field completion to CachedType
	 * 
	 * @param completion
	 * @see JSCompletion
	 */
	public void addCompletion(JSCompletion completion) {
		methodFieldCompletions.put(completion.getLookupName(), completion);
	}
	
	public JSCompletion removeCompletion(String completionLookup, SourceCompletionProvider provider)
	{
		JSCompletion completion = getCompletion(completionLookup, provider);
		if(completion != null) {
			removeCompletion(this, completion);
		}
		return completion;
	}
	
	/**
	 * Recursively walk through completions for this and extended classes to remove completion for this lookup name
	 * @param type
	 * @param completion The completion to remove.
	 */
	private void removeCompletion(JavaScriptType type, JSCompletion completion) {
		
		if(type.methodFieldCompletions.containsKey(completion.getLookupName())) {
			type.methodFieldCompletions.remove(completion.getLookupName());
		}
		//get extended classes and recursively remove method from them
		for (JavaScriptType extendedType : type.extended) {
			removeCompletion(extendedType, completion);
		}
	}
	
	/**
	 * Adds a constructor completion to CachedType object type
	 * @param completion 
	 */
	public void addConstructor(JSCompletion completion) {
		constructors.put(completion.getLookupName(), completion);
	}
	
	public void removeConstructor(JSCompletion completion)
	{
		constructors.remove(completion.getLookupName());
	}

	/**
	 * Set the class type completion e.g String, Number
	 * @param classType Completion to format the class
	 */
	public void setClassTypeCompletion(JSCompletion classType) {
		this.classType = classType;
	}
	
	/**
	 * @return the class type completion for the javascript type
	 */
	public JSCompletion getClassTypeCompletion() {
		return classType;
	}

	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	public JSCompletion getCompletion(String completionLookup,
			SourceCompletionProvider provider) {
		return getCompletion(this, completionLookup, provider);
	}


	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	protected JSCompletion _getCompletion(String completionLookup, SourceCompletionProvider provider) {
		return methodFieldCompletions.get(completionLookup);
	}


	/**
	 * @param cachedType
	 * @param completionLookup
	 * @return completion searches this typCompletions first and if not found,
	 *         then tries the extended type. Recursive routine to drill down for
	 *         JSCompletion
	 * @see JSCompletion
	 */
	private static JSCompletion getCompletion(JavaScriptType cachedType,
			String completionLookup, SourceCompletionProvider provider) {
		JSCompletion completion = cachedType._getCompletion(completionLookup,
				provider);
		if (completion == null) {
			// try the extended types
			for (Iterator<JavaScriptType> i = cachedType.getExtendedClasses().iterator(); i
					.hasNext();) {
				completion = getCompletion(i.next(), completionLookup, provider);
				if (completion != null)
					break;
			}
		}
		return completion;
	}


	/**
	 * @return A map of completion names to completions.
	 * @see JSCompletion
	 */
	public HashMap<String, JSCompletion> getMethodFieldCompletions() {
		return methodFieldCompletions;
	}
	
	public HashMap<String, JSCompletion> getConstructorCompletions() {
		return constructors;
	}


	/**
	 * 
	 * @return Get type declaration for CachedType
	 * @see TypeDeclaration
	 */
	public TypeDeclaration getType() {
		return type;
	}


	/**
	 * Add Cached Type extension
	 * 
	 * @param type
	 * @see JavaScriptType
	 */
	public void addExtension(JavaScriptType type) {
		extended.add(type);
	}


	/**
	 * 
	 * @return list of CachedType extended classes
	 */
	public List<JavaScriptType> getExtendedClasses() {
		return extended;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o instanceof JavaScriptType) {
			JavaScriptType ct = (JavaScriptType) o;
			return ct.getType().equals(getType());
		}

		if (o instanceof TypeDeclaration) {
			TypeDeclaration dec = (TypeDeclaration) o;
			return dec.equals(getType());
		}

		return false;
	}


	/**
	 * Overridden since {@link #equals(Object)} is overridden.
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return getType().hashCode();
	}

}
