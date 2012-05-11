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
	private boolean staticsOnly;


	public TypeDeclaration(String pkg, String apiName, String jsName, boolean staticsOnly) {
		this.staticsOnly = staticsOnly;
		this.pkg = pkg;
		this.apiName = apiName;
		this.jsName = jsName;
	}
	
	public TypeDeclaration(String pkg, String apiName, String jsName) {
		this(pkg, apiName, jsName, false);
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
	
	public boolean isStaticsOnly() {
		return staticsOnly;
	}
	
	public void setStaticsOnly(boolean staticsOnly) {
		this.staticsOnly = staticsOnly;
	}


	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		
		if (obj instanceof TypeDeclaration) {
			TypeDeclaration dec = (TypeDeclaration) obj;
			return getQualifiedName().equals(dec.getQualifiedName()) &&
			isStaticsOnly() == dec.isStaticsOnly();
		}

		return super.equals(obj);
	}


	/**
	 * Overridden since {@link #equals(Object)} is overridden.
	 *
	 * @return The hash code.
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * new Boolean(staticsOnly).hashCode();
		hash = 31 * hash + getQualifiedName().hashCode();
		return hash;
	}


}