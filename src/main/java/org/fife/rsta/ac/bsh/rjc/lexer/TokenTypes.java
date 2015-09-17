/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.bsh.rjc.lexer;


/**
 * All possible token types returned by this lexer.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface TokenTypes {

	public static final int KEYWORD					= (0x01)<<16;
	public static final int DATA_TYPE				= (0x02|KEYWORD)<<16;
	public static final int IDENTIFIER				= (0x04)<<16;
	public static final int COMMENT					= (0x08)<<16;
	public static final int DOC_COMMENT				= (0x10|COMMENT)<<16;
	public static final int WHITESPACE				= (0x20)<<16;
	public static final int LITERAL					= (0x40)<<16;
	public static final int SEPARATOR				= (0x80)<<16;
	public static final int OPERATOR				= (0x100)<<16;
	public static final int ASSIGNMENT_OPERATOR		= (0x200|OPERATOR)<<16;
	public static final int ANNOTATION_START		= (0x400)<<16;
	public static final int ELIPSIS					= (0x800)<<16;

	public static final int KEYWORD_ABSTRACT		= KEYWORD|1;
	public static final int KEYWORD_ASSERT			= KEYWORD|2;
	public static final int KEYWORD_BOOLEAN			= DATA_TYPE|3;
	public static final int KEYWORD_BREAK			= KEYWORD|4;
	public static final int KEYWORD_BYTE			= DATA_TYPE|5;
	public static final int KEYWORD_CASE			= KEYWORD|6;
	public static final int KEYWORD_CATCH			= KEYWORD|7;
	public static final int KEYWORD_CHAR			= DATA_TYPE|8;
	public static final int KEYWORD_CLASS			= KEYWORD|9;
	public static final int KEYWORD_CONST			= KEYWORD|10;
	public static final int KEYWORD_CONTINUE		= KEYWORD|11;
	public static final int KEYWORD_DEFAULT			= KEYWORD|12;
	public static final int KEYWORD_DO				= KEYWORD|13;
	public static final int KEYWORD_DOUBLE			= DATA_TYPE|14;
	public static final int KEYWORD_ELSE			= KEYWORD|15;
	public static final int KEYWORD_ENUM			= KEYWORD|16;
	public static final int KEYWORD_EXTENDS			= KEYWORD|17;
	public static final int KEYWORD_FINAL			= KEYWORD|18;
	public static final int KEYWORD_FINALLY			= KEYWORD|19;
	public static final int KEYWORD_FLOAT			= DATA_TYPE|20;
	public static final int KEYWORD_FOR				= KEYWORD|21;
	public static final int KEYWORD_GOTO			= KEYWORD|22;
	public static final int KEYWORD_IF				= KEYWORD|23;
	public static final int KEYWORD_IMPLEMENTS		= KEYWORD|24;
	public static final int KEYWORD_IMPORT			= KEYWORD|25;
	public static final int KEYWORD_INSTANCEOF		= KEYWORD|26;
	public static final int KEYWORD_INT				= DATA_TYPE|27;
	public static final int KEYWORD_INTERFACE		= KEYWORD|28;
	public static final int KEYWORD_LONG			= DATA_TYPE|29;
	public static final int KEYWORD_NATIVE			= KEYWORD|30;
	public static final int KEYWORD_NEW				= KEYWORD|31;
	public static final int KEYWORD_PACKAGE			= KEYWORD|32;
	public static final int KEYWORD_PRIVATE			= KEYWORD|33;
	public static final int KEYWORD_PROTECTED		= KEYWORD|34;
	public static final int KEYWORD_PUBLIC			= KEYWORD|35;
	public static final int KEYWORD_RETURN			= KEYWORD|36;
	public static final int KEYWORD_SHORT			= DATA_TYPE|37;
	public static final int KEYWORD_STATIC			= KEYWORD|38;
	public static final int KEYWORD_STRICTFP		= KEYWORD|39;
	public static final int KEYWORD_SUPER			= KEYWORD|40;
	public static final int KEYWORD_SWITCH			= KEYWORD|41;
	public static final int KEYWORD_SYNCHRONIZED	= KEYWORD|42;
	public static final int KEYWORD_THIS			= KEYWORD|43;
	public static final int KEYWORD_THROW			= KEYWORD|44;
	public static final int KEYWORD_THROWS			= KEYWORD|45;
	public static final int KEYWORD_TRANSIENT		= KEYWORD|46;
	public static final int KEYWORD_TRY				= KEYWORD|47;
	public static final int KEYWORD_VOID			= KEYWORD|48;
	public static final int KEYWORD_VOLATILE		= KEYWORD|49;
	public static final int KEYWORD_WHILE			= KEYWORD|50;

	public static final int LITERAL_INT				= LITERAL|1;
	public static final int LITERAL_FP				= LITERAL|2;
	public static final int LITERAL_BOOLEAN			= LITERAL|3;
	public static final int LITERAL_CHAR			= LITERAL|4;
	public static final int LITERAL_STRING			= LITERAL|5;
	public static final int LITERAL_NULL			= LITERAL|6;

	public static final int SEPARATOR_LPAREN		= SEPARATOR|1;
	public static final int SEPARATOR_RPAREN		= SEPARATOR|2;
	public static final int SEPARATOR_LBRACE		= SEPARATOR|3;
	public static final int SEPARATOR_RBRACE		= SEPARATOR|4;
	public static final int SEPARATOR_LBRACKET		= SEPARATOR|5;
	public static final int SEPARATOR_RBRACKET		= SEPARATOR|6;
	public static final int SEPARATOR_SEMICOLON		= SEPARATOR|7;
	public static final int SEPARATOR_COMMA			= SEPARATOR|8;
	public static final int SEPARATOR_DOT			= SEPARATOR|9;

	public static final int OPERATOR_EQUALS				= ASSIGNMENT_OPERATOR|1;
	public static final int OPERATOR_GT					= OPERATOR|2;
	public static final int OPERATOR_LT					= OPERATOR|3;
	public static final int OPERATOR_LOGICAL_NOT		= OPERATOR|4;
	public static final int OPERATOR_BITWISE_NOT		= OPERATOR|5;
	public static final int OPERATOR_QUESTION			= OPERATOR|6;
	public static final int OPERATOR_COLON				= OPERATOR|7;
	public static final int OPERATOR_EQUALS_EQUALS		= OPERATOR|8;
	public static final int OPERATOR_LTE				= OPERATOR|9;
	public static final int OPERATOR_GTE				= OPERATOR|10;
	public static final int OPERATOR_NE					= OPERATOR|11;
	public static final int OPERATOR_LOGICAL_AND		= OPERATOR|12;
	public static final int OPERATOR_LOGICAL_OR			= OPERATOR|13;
	public static final int OPERATOR_INCREMENT			= OPERATOR|14;
	public static final int OPERATOR_DECREMENT			= OPERATOR|15;
	public static final int OPERATOR_PLUS				= OPERATOR|16;
	public static final int OPERATOR_MINUS				= OPERATOR|17;
	public static final int OPERATOR_TIMES				= OPERATOR|18;
	public static final int OPERATOR_DIVIDE				= OPERATOR|19;
	public static final int OPERATOR_BITWISE_AND		= OPERATOR|20;
	public static final int OPERATOR_BITWISE_OR			= OPERATOR|21;
	public static final int OPERATOR_BITWISE_XOR		= OPERATOR|22;
	public static final int OPERATOR_MOD				= OPERATOR|23;
	public static final int OPERATOR_LSHIFT				= OPERATOR|24;
	public static final int OPERATOR_RSHIFT				= OPERATOR|25;
	public static final int OPERATOR_RSHIFT2			= OPERATOR|26;
	public static final int OPERATOR_PLUS_EQUALS		= ASSIGNMENT_OPERATOR|27;
	public static final int OPERATOR_MINUS_EQUALS		= ASSIGNMENT_OPERATOR|28;
	public static final int OPERATOR_TIMES_EQUALS		= ASSIGNMENT_OPERATOR|29;
	public static final int OPERATOR_DIVIDE_EQUALS		= ASSIGNMENT_OPERATOR|30;
	public static final int OPERATOR_BITWISE_AND_EQUALS	= ASSIGNMENT_OPERATOR|31;
	public static final int OPERATOR_BITWISE_OR_EQUALS	= ASSIGNMENT_OPERATOR|32;
	public static final int OPERATOR_BITWISE_XOR_EQUALS	= ASSIGNMENT_OPERATOR|33;
	public static final int OPERATOR_MOD_EQUALS			= ASSIGNMENT_OPERATOR|34;
	public static final int OPERATOR_LSHIFT_EQUALS		= ASSIGNMENT_OPERATOR|35;
	public static final int OPERATOR_RSHIFT_EQUALS		= ASSIGNMENT_OPERATOR|36;
	public static final int OPERATOR_RSHIFT2_EQUALS		= ASSIGNMENT_OPERATOR|37;

}