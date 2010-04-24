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
package org.fife.rsta.ac.java.rjc.lexer;


/**
 * Scanner for the Java programming language.<p>
 *
 * @author Robert Futrell
 * @version 0.1
 */
%%

%class SourceCodeScanner
%implements org.fife.rsta.ac.java.rjc.lexer.TokenTypes
%unicode
%line
%column
%char
%type org.fife.rsta.ac.java.rjc.lexer.Token


%{

	/**
	 * Whether comments should be returned as tokens.
	 */
	private boolean returnComments;

	/**
	 * Whether whitespace should be returned as tokens.
	 */
	private boolean returnWhitespace;

	/**
	 * Whether the last documentation comment parsed should be kept.
	 */
	private boolean keepLastDocComment;

	/**
	 * The last documentation comment parsed, if that feature is enabled.
	 */
	private String lastDocComment;


	private Token createToken(int type) {
		return createToken(type, false);
	}


	private Token createToken(int type, boolean invalid) {
		return new TokenImpl(type, yytext(), yyline, yycolumn, yychar, invalid);
	}


	/**
	 * Returns the current column into the current line.
	 *
	 * @return The current column.
	 */
	public int getColumn() {
		return yycolumn;
	}


	/**
	 * Returns the last documentation comment parsed, if this feature is
	 * enabled.
	 *
	 * @return The last documentation comment parsed, or <code>null</code>
	 *         if the feature is disabled.
	 * @see #setKeepLastDocComment(boolean)
	 */
	public String getLastDocComment() {
		return lastDocComment;
	}


	/**
	 * Returns the current line into the document.
	 *
	 * @return The current line.
	 */
	public int getLine() {
		return yyline;
	}


	/**
	 * Returns the current offset into the document.
	 *
	 * @return The offset.
	 */
	public int getOffset() {
		return yychar;
	}


	/**
	 * Returns whether comments are returned as tokens.
	 *
	 * @return Whether comments are returned as tokens.
	 * @see #getReturnWhitespace()
	 */
	public boolean getReturnComments() {
		return returnComments;
	}


	/**
	 * Returns whether whitespace is returned as tokens.
	 *
	 * @return Whether whitespace is returned as tokens.
	 * @see #getReturnComments()
	 */
	public boolean getReturnWhitespace() {
		return returnWhitespace;
	}


	/**
	 * Sets whether the last documentation comment should be kept.
	 *
	 * @param keep Whether to keep the last documentation comment.
	 * @see #getLastDocComment()
	 */
	public void setKeepLastDocComment(boolean keep) {
		keepLastDocComment = keep;
	}


	/**
	 * Sets whether comments are returned as tokens.
	 *
	 * @param returnComments Whether comments should be returned as tokens.
	 * @see #getReturnComments()
	 * @see #setReturnWhitespace(boolean)
	 */
	public void setReturnComments(boolean returnComments) {
		this.returnComments = returnComments;
	}


	/**
	 * Sets whether whitespace is returned as tokens.
	 *
	 * @param returnWhitespace Whether whitespace should be returned as tokens.
	 * @see #getReturnWhitespace()
	 * @see #setReturnComments(boolean)
	 */
	public void setReturnWhitespace(boolean returnWhitespace) {
		this.returnWhitespace = returnWhitespace;
	}


%}

/* JLS 3.3 - Unicode Escapes */
UnicodeInputCharacter				= ({UnicodeEscape}|{RawInputCharacter})
UnicodeEscape						= ([\\]{UnicodeMarker}{HexDigit}{4})
UnicodeMarker						= ("u"|{UnicodeMarker}"u")
RawInputCharacter					= (.)

/* JLS 3.4 - Line Terminators */
LineTerminator						= (\r|\n|\r\n)
// JFlex has some trouble compiling InputCharacter...
//InputCharacter						= ({UnicodeInputCharacter}|[^\r\n])
InputCharacter						= ([\\][u]+{HexDigit}{4}|[^\r\n])

/* JLS 3.6 - White Space */
WhiteSpace							= (([ \t\f]|{LineTerminator})+)

/* JLS 3.7 - Comments (made non-recursive for JFlex) */
DocumentationComment				= ("/*" "*"+ [^/*] ~"*/")
Comment								= ({TraditionalComment}|{EndOfLineComment})
TraditionalComment					= ("/*" [^*] ~"*/" | "/*" "*"+ "/")
EndOfLineComment					= ("//" {CharactersInLine}?)
CharactersInLine					= ({InputCharacter}+)

/* JLS 3.8 - Identifiers (made non-recursive for JFlex) */
Identifier							= ({IdentifierChars}) /* but not Keyword, BooleanLiteral, NullLiteral */
IdentifierChars						= ({JavaLetter}{JavaLetterOrDigit}*)
JavaLetter							= ([:jletter:])
JavaLetterOrDigit					= ([:jletterdigit:])

/* JLS 3.10.1 - Integer Literals */
IntegerLiteral						= ({DecimalIntegerLiteral}|{HexIntegerLiteral}|{OctalIntegerLiteral})
DecimalIntegerLiteral				= ({DecimalNumeral}{IntegerTypeSuffix}?)
HexIntegerLiteral					= ({HexNumeral}{IntegerTypeSuffix}?)
OctalIntegerLiteral					= ({OctalNumeral}{IntegerTypeSuffix}?)
IntegerTypeSuffix					= ([lL])
DecimalNumeral						= ("0"|{NonZeroDigit}{Digits}?)
Digits								= ({Digit}+)
Digit								= ("0"|{NonZeroDigit})
NonZeroDigit						= ([1-9])
HexNumeral							= ("0"[xX]{HexDigits})
HexDigits							= ({HexDigit}+)
HexDigit							= ([0-9a-fA-F])
OctalNumeral						= ("0"{OctalDigits})
OctalDigits							= ({OctalDigit}+)
OctalDigit							= ([0-7])

/* JLS 3.10.2 - Floating Point Literals */
/* TODO*/
FloatingPointLiteral				= ([0-9]+[\.][0-9]+[fF])

/* JLS 3.10.3 - Boolean Literals */
BooleanLiteral						= ("true"|"false")

/* JLS 3.10.4 - Character Literals */
CharacterLiteral					= ([\']({SingleCharacter}|{EscapeSequence})[\'])
SingleCharacter						= ([\\][u]+{HexDigit}{4}|[^\r\n\'\\])
InvalidCharLiteral					= ([\'][^\']*[\']?)

/* JLS 3.10.5 - String Literals */
StringLiteral						= ([\"]{StringCharacters}*[\"])
StringCharacters					= ({StringCharacter}+)
StringCharacter						= ([\\][u]+{HexDigit}{4}|[^\r\n\"\\]|{EscapeSequence})
//StringCharacter						= ([^\r\n\"\\]|{EscapeSequence})
InvalidStringLiteral				= ([\"][^\"]*[\"]?)

/* JLS 3.10.6 - Escape Sequences for Character and String Literals */
EscapeSequence						= ([\\][btnfr\"\'\\]|{OctalEscape})
OctalEscape							= ([\\]({OctalDigit}{OctalDigit}?|{ZeroToThree}{OctalDigit}{OctalDigit}))
OctalDigit							= ([0-7])
ZeroToThree							= ([0-3])

/* JLS 3.10.7 - The Null Literal */
NullLiteral							= ("null")

/* ??? - Stuff not in JLS */
AnnotationStart						= ([\@])
Elipsis								= ("...")


%%

<YYINITIAL> {

	{WhiteSpace}			{
								if (returnWhitespace) {
									return createToken(Token.WHITESPACE);
								}
							}

	{DocumentationComment}	{
								if (keepLastDocComment) {
									lastDocComment = yytext();
								}
								if (returnComments) {
									return createToken(Token.DOC_COMMENT);
								}
							}

	{Comment}				{
								if (returnComments) {
									return createToken(Token.COMMENT);
								}
							}

	/* Keywords */
	"abstract"				{ return createToken(KEYWORD_ABSTRACT); }
	"assert"				{ return createToken(KEYWORD_ASSERT); }
	"break"					{ return createToken(KEYWORD_BREAK); }
	"case"					{ return createToken(KEYWORD_CASE); }
	"catch"					{ return createToken(KEYWORD_CATCH); }
	"class"					{ return createToken(KEYWORD_CLASS); }
	"const"					{ return createToken(KEYWORD_CONST); }
	"continue"				{ return createToken(KEYWORD_CONTINUE); }
	"default"				{ return createToken(KEYWORD_DEFAULT); }
	"do"					{ return createToken(KEYWORD_DO); }
	"else"					{ return createToken(KEYWORD_ELSE); }
	"enum"					{ return createToken(KEYWORD_ENUM); }
	"extends"				{ return createToken(KEYWORD_EXTENDS); }
	"final"					{ return createToken(KEYWORD_FINAL); }
	"finally"				{ return createToken(KEYWORD_FINALLY); }
	"for"					{ return createToken(KEYWORD_FOR); }
	"goto"					{ return createToken(KEYWORD_GOTO); }
	"if"					{ return createToken(KEYWORD_IF); }
	"implements"			{ return createToken(KEYWORD_IMPLEMENTS); }
	"import"				{ return createToken(KEYWORD_IMPORT); }
	"instanceof"			{ return createToken(KEYWORD_INSTANCEOF); }
	"interface"				{ return createToken(KEYWORD_INTERFACE); }
	"native"				{ return createToken(KEYWORD_NATIVE); }
	"new"					{ return createToken(KEYWORD_NEW); }
	"package"				{ return createToken(KEYWORD_PACKAGE); }
	"private"				{ return createToken(KEYWORD_PRIVATE); }
	"protected"				{ return createToken(KEYWORD_PROTECTED); }
	"public"				{ return createToken(KEYWORD_PUBLIC); }
	"return"				{ return createToken(KEYWORD_RETURN); }
	"static"				{ return createToken(KEYWORD_STATIC); }
	"strictfp"				{ return createToken(KEYWORD_STRICTFP); }
	"super"					{ return createToken(KEYWORD_SUPER); }
	"switch"				{ return createToken(KEYWORD_SWITCH); }
	"synchronized"			{ return createToken(KEYWORD_SYNCHRONIZED); }
	"this"					{ return createToken(KEYWORD_THIS); }
	"throw"					{ return createToken(KEYWORD_THROW); }
	"throws"				{ return createToken(KEYWORD_THROWS); }
	"transient"				{ return createToken(KEYWORD_TRANSIENT); }
	"try"					{ return createToken(KEYWORD_TRY); }
	"void"					{ return createToken(KEYWORD_VOID); }
	"volatile"				{ return createToken(KEYWORD_VOLATILE); }
	"while"					{ return createToken(KEYWORD_WHILE); }

	/* Data types */
	"boolean"				{ return createToken(KEYWORD_BOOLEAN); }
	"byte"					{ return createToken(KEYWORD_BYTE); }
	"char"					{ return createToken(KEYWORD_CHAR); }
	"double"				{ return createToken(KEYWORD_DOUBLE); }
	"float"					{ return createToken(KEYWORD_FLOAT); }
	"int"					{ return createToken(KEYWORD_INT); }
	"long"					{ return createToken(KEYWORD_LONG); }
	"short"					{ return createToken(KEYWORD_SHORT); }

	/* Literals */
	{IntegerLiteral}		{ return createToken(LITERAL_INT); }
	{FloatingPointLiteral}	{ return createToken(LITERAL_FP); }
	{BooleanLiteral}		{ return createToken(LITERAL_BOOLEAN); }
	{CharacterLiteral}		{ return createToken(LITERAL_CHAR); }
	{StringLiteral}			{ return createToken(LITERAL_STRING); }
	{NullLiteral}			{ return createToken(LITERAL_NULL); }
	{InvalidCharLiteral}	{ return createToken(LITERAL_CHAR, true); }
	{InvalidStringLiteral}	{ return createToken(LITERAL_STRING, true); }

	{Identifier}			{ return createToken(IDENTIFIER); }

	{AnnotationStart}		{ return createToken(ANNOTATION_START); }

	{Elipsis}				{ return createToken(ELIPSIS); }

	/* Separators  (JLS 3.11) */
	"("						{ return createToken(SEPARATOR_LPAREN); }
	")"						{ return createToken(SEPARATOR_RPAREN); }
	"{"						{ return createToken(SEPARATOR_LBRACE); }
	"}"						{ return createToken(SEPARATOR_RBRACE); }
	"["						{ return createToken(SEPARATOR_LBRACKET); }
	"]"						{ return createToken(SEPARATOR_RBRACKET); }
	";"						{ return createToken(SEPARATOR_SEMICOLON); }
	","						{ return createToken(SEPARATOR_COMMA); }
	"."						{ return createToken(SEPARATOR_DOT); }

	/* Operators (JLS 3.12) */
	"="						{ return createToken(OPERATOR_EQUALS); }
	">"						{ return createToken(OPERATOR_GT); }
	"<"						{ return createToken(OPERATOR_LT); }
	"!"						{ return createToken(OPERATOR_LOGICAL_NOT); }
	"~"						{ return createToken(OPERATOR_BITWISE_NOT); }
	"?"						{ return createToken(OPERATOR_QUESTION); }
	":"						{ return createToken(OPERATOR_COLON); }
	"=="					{ return createToken(OPERATOR_EQUALS_EQUALS); }
	"<="					{ return createToken(OPERATOR_LTE); }
	">="					{ return createToken(OPERATOR_GTE); }
	"!="					{ return createToken(OPERATOR_NE); }
	"&&"					{ return createToken(OPERATOR_LOGICAL_AND); }
	"||"					{ return createToken(OPERATOR_LOGICAL_OR); }
	"++"					{ return createToken(OPERATOR_INCREMENT); }
	"--"					{ return createToken(OPERATOR_DECREMENT); }
	"+"						{ return createToken(OPERATOR_PLUS); }
	"-"						{ return createToken(OPERATOR_MINUS); }
	"*"						{ return createToken(OPERATOR_TIMES); }
	"/"						{ return createToken(OPERATOR_DIVIDE); }
	"&"						{ return createToken(OPERATOR_BITWISE_AND); }
	"|"						{ return createToken(OPERATOR_BITWISE_OR); }
	"^"						{ return createToken(OPERATOR_BITWISE_XOR); }
	"%"						{ return createToken(OPERATOR_MOD); }
	"<<"					{ return createToken(OPERATOR_LSHIFT); }
	">>"					{ return createToken(OPERATOR_RSHIFT); }
	">>>"					{ return createToken(OPERATOR_RSHIFT2); }
	"+="					{ return createToken(OPERATOR_PLUS_EQUALS); }
	"-="					{ return createToken(OPERATOR_MINUS_EQUALS); }
	"*="					{ return createToken(OPERATOR_TIMES_EQUALS); }
	"/="					{ return createToken(OPERATOR_DIVIDE_EQUALS); }
	"&="					{ return createToken(OPERATOR_BITWISE_AND_EQUALS); }
	"|="					{ return createToken(OPERATOR_BITWISE_OR_EQUALS); }
	"^="					{ return createToken(OPERATOR_BITWISE_XOR_EQUALS); }
	"%="					{ return createToken(OPERATOR_MOD_EQUALS); }
	"<<="					{ return createToken(OPERATOR_LSHIFT_EQUALS); }
	">>="					{ return createToken(OPERATOR_RSHIFT_EQUALS); }
	">>>="					{ return createToken(OPERATOR_RSHIFT2_EQUALS); }


	/* Unhandled stuff. */
	.						{ return createToken(IDENTIFIER, true); }

}
