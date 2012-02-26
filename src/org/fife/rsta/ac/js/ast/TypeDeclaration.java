/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
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


}