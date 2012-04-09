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

public class TypeDeclaration {

	private String pkg;
	private String apiName;
	private String jsName;


	public TypeDeclaration(String pkg, String apiName, String jsName) {
		this.pkg = pkg;
		this.apiName = apiName;
		this.jsName = jsName;
	}


	public String getPackageName() {
		return pkg;
	}


	public String getAPITypeName() {
		return apiName;
	}


	public String getJSName() {
		return jsName;
	}


	public String getQualifiedName() {
		return pkg != null ? (pkg + '.' + apiName) : apiName;
	}


	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj instanceof TypeDeclaration) {
			TypeDeclaration dec = (TypeDeclaration) obj;
			return getQualifiedName().equals(dec.getQualifiedName());
		}

		return super.equals(obj);
	}


	/**
	 * Overridden since {@link #equals(Object)} is overridden.
	 *
	 * @return The hash code.
	 */
	public int hashCode() {
		return getQualifiedName().hashCode();
	}


}