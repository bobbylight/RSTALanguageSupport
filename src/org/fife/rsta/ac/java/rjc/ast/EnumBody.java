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
package org.fife.rsta.ac.java.rjc.ast;

import org.fife.rsta.ac.java.rjc.lexer.Offset;


/**
 * An EnumBody.
 *
 * <pre>
 * EnumBody:
 *    '{' [EnumConstants] [,] [EnumBodyDeclarations] '}'
 *
 *
 * EnumConstants:
 *    EnumConstant
 *    EnumConstants , EnumConstant
 *
 * EnumConstant:
 *    Annotations Identifier [Arguments] [ClassBody]
 *
 * EnumBodyDeclarations:
 *    ; {ClassBodyDeclaration}
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class EnumBody extends AbstractASTNode {


	public EnumBody(String name, Offset start) {
		super(name, start);
	}


}