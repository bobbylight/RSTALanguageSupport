/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lexer.Token;


/**
 * A TypeParameter.
 *
 * <pre>
 * TypeParameter:
 *    Identifier ['extends' Bound]
 * 
 * Bound:
 *    Type { '&' Type }
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class TypeParameter {

	private Token name;
	private List bounds;


	public TypeParameter(Token name) {
		this.name = name;
	}


	public void addBound(Type bound) {
		if (bounds==null) {
			bounds = new ArrayList(1); // Usually just 1
		}
		bounds.add(bound);
	}


	public String getName() {
		return name.getLexeme();
	}


}