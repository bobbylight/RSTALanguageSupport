/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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