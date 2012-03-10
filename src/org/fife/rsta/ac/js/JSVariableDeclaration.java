/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import org.fife.rsta.ac.common.VariableDeclaration;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;


public class JSVariableDeclaration extends VariableDeclaration {

	private AstNode type;


	public JSVariableDeclaration(String name, int offset) {
		super(name, offset);
	}


	public void setType(AstNode type) {
		this.type = type;
	}


	public AstNode getType() {
		return type;
	}


	public String getTypeName() {
		return tokenToName(type);
	}


	public static final String tokenToName(AstNode type) {
		if (type != null) {
			switch (type.getType()) {
				case Token.STRING:
					return "String";
				case Token.NUMBER:
					return "Number";
				case Token.NAME:
					return ((Name) type).getIdentifier();
				case Token.CATCH:
					return "Error";
			}
		}
		return "any";
	}


}