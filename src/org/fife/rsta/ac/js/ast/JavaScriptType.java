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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	private TypeDeclaration type;
	// completions for base type String-->JSCompletion
	private HashMap typeCompletions;

	// extended cached types
	private ArrayList extended;


	public JavaScriptType(TypeDeclaration type) {
		this.type = type;
		typeCompletions = new HashMap();
		extended = new ArrayList();
	}


	/**
	 * Add completion to CachedType
	 * 
	 * @param completion
	 * @see JSCompletion
	 */
	public void addCompletion(JSCompletion completion) {
		typeCompletions.put(completion.getLookupName(), completion);
	}


	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	public JSCompletion getCompletion(String completionLookup) {
		return getCompletion(this, completionLookup);
	}


	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	private JSCompletion _getCompletion(String completionLookup) {
		return (JSCompletion) typeCompletions.get(completionLookup);
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
			String completionLookup) {
		JSCompletion completion = cachedType._getCompletion(completionLookup);
		if (completion == null) {
			// try the extended types
			for (Iterator i = cachedType.getExtendedClasses().iterator(); i
					.hasNext();) {
				completion = getCompletion((JavaScriptType) i.next(),
						completionLookup);
				if (completion != null)
					break;
			}
		}
		return completion;
	}


	/**
	 * @return true if completion exists for lookupname
	 */
	public boolean containsCompletion(String completionLookup) {
		return typeCompletions.containsKey(completionLookup);
	}


	/**
	 * @return Map of completions String --> JSCompletion
	 * @see JSCompletion
	 */
	public HashMap getCompletions() {
		return typeCompletions;
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
	public List getExtendedClasses() {
		return extended;
	}


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

}
