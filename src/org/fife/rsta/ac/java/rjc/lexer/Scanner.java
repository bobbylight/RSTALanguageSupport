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

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Stack;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;


/**
 * A scanner that allows the user to "push back" tokens.  This scanner
 * allows arbitrary lookahead.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Scanner {

	private static final boolean DEBUG = false;

	/**
	 * The scanner we delegate to.
	 */
	private SourceCodeScanner s;

	/**
	 * Stack of tokens that have been "pushed back."
	 */
	private Stack stack;

	/**
	 * The depth in which we're in TypeArguments or TypeParameters.
	 */
	private int typeArgLevel;

	/**
	 * If we are parsing text in a Swing <code>JTextComponent</code>, this
	 * should be the document of that component.
	 */
	private Document doc;

	/**
	 * The most recently lexed token, or <code>null</code> if EOS was
	 * reached.
	 */
	private Token mostRecentToken;


	/**
	 * Constructor.  This scanner will return no tokens unless some are pushed
	 * onto it via {@link #yyPushback(Token)}.
	 */
	public Scanner() {
		this((Reader)null);
	}


	/**
	 * Constructor.  This scanner will only return those tokens pushed onto it.
	 *
	 * @param tokens Tokens to return.
	 */
	public Scanner(List tokens) {
		stack = new Stack();
		for (int i=tokens.size()-1; i>=0; i--) {
			stack.push(tokens.get(i));
		}
	}


	/**
	 * Constructor.
	 *
	 * @param r The stream to read from.
	 */
	public Scanner(Reader r) {
		s = r!=null ? new SourceCodeScanner(r) : null;
		s.setKeepLastDocComment(true);
		stack = new Stack();
	}


/**
 * This method is just here for debugging purposes to make sure
 * our parser is sound.
 *
 * @param t A token to push onto the stack (non-<code>null</code>).
 */
private void pushOntoStack(Token t) {
	if (t!=null && !stack.isEmpty() && t.equals(stack.peek())) {
		System.err.println("ERROR: Token being duplicated: " + t);
		Thread.dumpStack();
		System.exit(5);
	}
	else if (t==null) {
		System.err.println("ERROR: null token pushed onto stack");
		Thread.dumpStack();
		System.exit(6);
	}
	stack.push(t);
}


	/**
	 * Decreases the depth in which we're in TypeArguments or TypeParameters.
	 *
	 * @see #increaseTypeArgumentsLevel()
	 * @see #getTypeArgumentsLevel()
	 */
	public void decreaseTypeArgumentsLevel() {
		if (--typeArgLevel<0) {
			throw new InternalError("typeArgLevel dipped below 0");
		}
	}


	/**
	 * Returns an offset into the source being parsed.  This offset will be
	 * tracked if we are parsing code from a Swing <code>JTextComponent</code>.
	 *
	 * @param offs The offset.
	 * @return An object representing the offset.
	 * @see #setDocument(Document)
	 */
	public Offset createOffset(final int offs) {
		if (doc!=null) {
			try {
				return new DocumentOffset(doc.createPosition(offs));
			} catch (BadLocationException ble) { // Should never happen
				ble.printStackTrace();
			}
		}
		return new Offset() {
			public int getOffset() {
				return offs;
			}
		};
	}


	private void debugPrintToken(Token t) {
		if (DEBUG) {
			if (t==null) {
				System.out.println("... null");
			}
			else {
				System.out.println("... " + t);
			}
		}
	}


	/**
	 * Returns the current column into the current line.
	 *
	 * @return The current column.
	 * @see #getLine()
	 */
	public int getColumn() {
		return s.getColumn();
	}


	/**
	 * Returns the last documentation comment parsed.  The "last documentation
	 * comment" is cleared when this method returns.
	 *
	 * @return The last documentation comment parsed, or <code>null</code>
	 *         if there was none.
	 */
	public String getLastDocComment() {
		return s.getLastDocComment();
	}


	/**
	 * Returns the current line into the document.
	 *
	 * @return The current line.
	 * @see #getColumn()
	 */
	public int getLine() {
		return s.getLine();
	}


	/**
	 * Returns the most recently-lexed token.
	 *
	 * @return The token, or <code>null</code> if EOS was reached.
	 */
	public Token getMostRecentToken() {
		return mostRecentToken;
	}


	/**
	 * Returns the current offset into the document.
	 *
	 * @return The offset.
	 */
	public int getOffset() {
		return s.getOffset();
	}


	/**
	 * Eats through (possibly nested) paren pairs, e.g.:
	 * <pre>(int i=0; i&lt;getFoo(getParam()); i++)</pre>.
	 * Blocks nested inside the paren pairs are also skipped.
	 *
	 * @throws IOException If an IO error occurs.
	 * @throws InternalError If the next token is not a '('.
	 */
	public void eatParenPairs() throws IOException {

		Token t = yylex();
		if (t==null || t.getType()!=TokenTypes.SEPARATOR_LPAREN) {
			throw new InternalError("'(' expected, found: " + t);
		}

		int blockDepth = 0;
		int parenDepth = 1;

		while ((t=yylex())!=null) {
			int type = t.getType();
			switch (type) {
				case TokenTypes.SEPARATOR_LBRACE:
					blockDepth++;
					break;
				case TokenTypes.SEPARATOR_RBRACE:
					blockDepth--;
					break;
				case TokenTypes.SEPARATOR_LPAREN:
					parenDepth++;
					break;
				case TokenTypes.SEPARATOR_RPAREN:
					if (--parenDepth == 0) {
						return;
					}
					break;
			}
		}

	}


	/**
	 * Eats all tokens up to (and including) the next token of the specified
	 * type.  This is useful, for example, to eat until the next semicolon.
	 *
	 * @param tokenType The type of token to eat through.
	 * @throws IOException If an IO error occurs.
	 */
	public void eatThroughNext(int tokenType) throws IOException {
		Token t = null;
		while ((t=yylex())!=null && t.getType()!=tokenType);
	}


	/**
	 * Eats all tokens up to (and including) the next token of the specified
	 * type.  This is useful, for example, to eat until the next semicolon.
	 *
	 * @param tokenType The type of token to eat through.
	 * @throws IOException If an IO error occurs.
	 * @see #eatThroughNextSkippingBlocks(int, int)
	 * @see #eatThroughNextSkippingBlocksAndStuffInParens(int, int)
	 */
	public void eatThroughNextSkippingBlocks(int tokenType) throws IOException {
		Token t = null;
		int blockDepth = 0;
		while ((t=yylex())!=null) {
			int type = t.getType();
			if (type==TokenTypes.SEPARATOR_LBRACE) {
				blockDepth++;
			}
			else if (type==TokenTypes.SEPARATOR_RBRACE) {
				blockDepth--;
			}
			else if (type==tokenType) {
				if (blockDepth<=0) {
					return;
				}
			}
		}
	}


	/**
	 * Eats all tokens up to (and including) the next token of one of the
	 * specified types.  This is useful, for example, to eat until the next
	 * equal sign or semicolon.
	 *
	 * @param tokenType1 The type of token to eat through.
	 * @param tokenType2 Another type of token to eat through.
	 * @return The last token read.  This will either be one of the two token
	 *         types passed in, or <code>null</code> if the end of the stream
	 *         is reached.
	 * @throws IOException If an IO error occurs.
	 * @see #eatThroughNextSkippingBlocksAndStuffInParens(int, int)
	 */
	public Token eatThroughNextSkippingBlocks(int tokenType1,
									int tokenType2) throws IOException {
		Token t = null;
		int blockDepth = 0;
		while ((t=yylex())!=null) {
			int type = t.getType();
			if (type==TokenTypes.SEPARATOR_LBRACE) {
				blockDepth++;
			}
			else if (type==TokenTypes.SEPARATOR_RBRACE) {
				blockDepth--;
			}
			else if (type==tokenType1 || type==tokenType2) {
				if (blockDepth<=0) {
					return t;
				}
			}
		}
		return null;
	}


	/**
	 * Eats all tokens up to (and including) the next token of one of the
	 * specified types.  This is useful, for example, to eat until the next
	 * equal sign or semicolon.
	 *
	 * @param tokenType1 The type of token to eat through.
	 * @param tokenType2 Another type of token to eat through.
	 * @return The last token read.  This will either be one of the two token
	 *         types passed in, or <code>null</code> if the end of the stream
	 *         is reached.
	 * @throws IOException If an IO error occurs.
	 * @see #eatThroughNextSkippingBlocks(int, int)
	 */
	public Token eatThroughNextSkippingBlocksAndStuffInParens(int tokenType1,
									int tokenType2) throws IOException {

		Token t = null;
		int blockDepth = 0;
		int parenDepth = 0;

		while ((t=yylex())!=null) {
			int type = t.getType();
			switch (type) {
				case TokenTypes.SEPARATOR_LBRACE:
					blockDepth++;
					break;
				case TokenTypes.SEPARATOR_RBRACE:
					blockDepth--;
					break;
				case TokenTypes.SEPARATOR_LPAREN:
					parenDepth++;
					break;
				case TokenTypes.SEPARATOR_RPAREN:
					parenDepth--;
					break;
				default:
					if (type==tokenType1 || type==tokenType2) {
						if (blockDepth<=0 && parenDepth<=0) {
							return t;
						}
					}
			}
		}

		return null;

	}


	public void eatUntilNext(int type1, int type2) throws IOException {
		Token t = null;
		while ((t=yylex())!=null) {
			int type = t.getType();
			if (type==type1 || type==type2) {
				yyPushback(t);
				break;
			}
		}
	}


	public void eatUntilNext(int type1, int type2, int type3) throws IOException {
		Token t = null;
		while ((t=yylex())!=null) {
			int type = t.getType();
			if (type==type1 || type==type2 || type==type3) {
				yyPushback(t);
				break;
			}
		}
	}


	/**
	 * Returns the current TypeArgument/TypeParameter level.
	 *
	 * @return The current level.
	 * @see #increaseTypeArgumentsLevel()
	 * @see #decreaseTypeArgumentsLevel()
	 */
	public int getTypeArgumentsLevel() {
		return typeArgLevel;
	}


	/**
	 * Increases the depth in which we're in TypeArguments or TypeParameters.
	 *
	 * @see #decreaseTypeArgumentsLevel()
	 * @see #getTypeArgumentsLevel()
	 */
	public void increaseTypeArgumentsLevel() {
		typeArgLevel++;
	}


private Stack resetPositions;
private Stack currentResetTokenStack;
private int currentResetStartOffset;
	public void markResetPosition() {
		if (s!=null) { // Hack!  We should really do something for token-only scanners
			if (resetPositions==null) {
				resetPositions = new Stack();
			}
			currentResetTokenStack = new Stack();
			resetPositions.push(currentResetTokenStack);
			currentResetStartOffset = s.getOffset();
		}
	}
	public void resetToLastMarkedPosition() {
		if (s!=null) { // Hack!  We should really do something for token-only scanners
			if (currentResetTokenStack==null) {
				throw new InternalError("No resetTokenStack!");
			}
			// Remove tokens off the standard stack within the "marked" range
			while (!stack.isEmpty()) {
				Token t = (Token)stack.peek();
				if (t.getOffset()>=currentResetStartOffset) {
					stack.pop();
				}
				else {
					break;
				}
			}
			// Add all tokens in the "marked" range to our stack
			while (!currentResetTokenStack.isEmpty()) {
				Token t = (Token)currentResetTokenStack.pop();
				stack.push(t);
			}
			resetPositions.pop(); // Remote currentResetTokenStack
			currentResetTokenStack = resetPositions.isEmpty() ? null : (Stack)resetPositions.peek();
			currentResetStartOffset = -1;
		}
	}
	public void clearResetPosition() {
		if (s!=null) { // Hack!  We should really do something for token-only scanners
			if (currentResetTokenStack==null) {
				throw new InternalError("No resetTokenStack!");
			}
			resetPositions.pop(); // Remote currentResetTokenStack
			currentResetTokenStack = resetPositions.isEmpty() ? null : (Stack)resetPositions.peek();
			currentResetStartOffset = -1;
		}
	}

	/**
	 * Sets the Swing <code>Document</code> whose content is being parsed.
	 * This method should be called if we are parsing code inside a
	 * <code>JTextComponent</code>, as it will help our parsed code to track
	 * changes when the document is modified.  If we are parsing source from a
	 * flat file, this method shouldn't be called.
	 *
	 * @param doc The document being parsed.
	 */
	public void setDocument(Document doc) {
		this.doc = doc;
	}


	/**
	 * Skips all bracket pairs ('[' followed by ']') in the stream.
	 *
	 * @return The number of bracket pairs skipped.
	 * @throws IOException If an IO error occurs.
	 */
	public int skipBracketPairs() throws IOException {

		int count = 0;

		while (yyPeekCheckType()==TokenTypes.SEPARATOR_LBRACKET &&
				yyPeekCheckType(2)==TokenTypes.SEPARATOR_RBRACKET) {
			yylex();
			yylex();
			count++;
		}

		return count;

	}


	/**
	 * Returns the next token from the input stream.
	 *
	 * @return The next token.
	 * @throws IOException If an IO error occurs.
	 */
	/*
	 * NOTE: All other lex'ing methods should call into this one.
	 */
	public Token yylex() throws IOException {

		Token t = null;
		if (stack.isEmpty()) {
			t = s!=null ? s.yylex() : null;
		}
		else {
			t = (Token)stack.pop();
		}

		// If we have nested TypeArguments ("Set<Map.Entry<String,String>>"),
		// Prevent the ">>" from coming across as a single token.
		if (typeArgLevel>0 && t!=null && t.isOperator()) {
			String lexeme = t.getLexeme();
			if (lexeme.length()>1) {
				char ch = lexeme.charAt(0);
				if (ch=='<') {
					Token rest = null;
					switch (t.getType()) {
						case TokenTypes.OPERATOR_LTE:
							rest = new TokenImpl(Token.OPERATOR_EQUALS, "=",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_LSHIFT:
							rest = new TokenImpl(Token.OPERATOR_LT, "<",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_LSHIFT_EQUALS:
							rest = new TokenImpl(Token.OPERATOR_LTE, "<=",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
					}
					stack.push(rest);
					t = new TokenImpl(Token.OPERATOR_LT, "<",
							t.getLine(), t.getColumn(), t.getOffset());
				}
				else if (ch=='>') {
					Token rest = null;
					switch (t.getType()) {
						case TokenTypes.OPERATOR_GTE:
							rest = new TokenImpl(Token.OPERATOR_EQUALS, "=",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_RSHIFT:
							rest = new TokenImpl(Token.OPERATOR_GT, ">",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_RSHIFT2:
							rest = new TokenImpl(Token.OPERATOR_RSHIFT, ">>",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_RSHIFT_EQUALS:
							rest = new TokenImpl(Token.OPERATOR_GTE, ">=",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
						case TokenTypes.OPERATOR_RSHIFT2_EQUALS:
							rest = new TokenImpl(Token.OPERATOR_RSHIFT_EQUALS, ">>=",
									t.getLine(), t.getColumn()+1, t.getOffset()+1);
							break;
					}
					stack.push(rest);
					t = new TokenImpl(Token.OPERATOR_GT, ">",
							t.getLine(), t.getColumn(), t.getOffset());
				}
			}
		}

		debugPrintToken(t);
		if (currentResetTokenStack!=null) {
			currentResetTokenStack.push(t);
		}
		if (t!=null) { // Don't let EOS corrupt most recent token
			mostRecentToken = t;
		}
		return t;

	}


	/**
	 * Returns the next token from the input stream, or throws an exception
	 * if the end of stream is reached.
	 *
	 * @param error The error description for the exception if the end of
	 *        stream is reached.
	 * @return The token.
	 * @throws IOException If an IO error occurs or the end of stream is
	 *         reached.
	 */
	public Token yylexNonNull(String error) throws IOException {
		Token t = yylex();
		if (t==null) {
			throw new EOFException(error);
		}
		return t;
	}


	/**
	 * Returns the next token from the input stream, or throws an exception
	 * if the end of stream is reached or if the token is not of a given
	 * type.
	 *
	 * @param type The type the token must be.
	 * @param error The error description for the exception if the end of
	 *        stream is reached, or if the token is of an unexpected type.
	 * @return The token.
	 * @throws IOException If an IO error occurs or the end of stream is
	 *         reached, or if the token is of the wrong type.
	 */
	public Token yylexNonNull(int type, String error) throws IOException {
		return yylexNonNull(type, -1, error);
	}


	/**
	 * Returns the next token from the input stream, or throws an exception
	 * if the end of stream is reached or if the token is not of two given
	 * types.
	 *
	 * @param type1 One type the token can be.
	 * @param type2 Another type the token can be, or <tt>-1</tt> if we
	 *        should only check against <tt>type1</tt>.
	 * @param error The error description for the exception if the end of
	 *        stream is reached, or if the token is of an unexpected type.
	 * @return The token.
	 * @throws IOException If an IO error occurs or the end of stream is
	 *         reached, or if the token is of a wrong type.
	 */
	public Token yylexNonNull(int type1, int type2, String error)
								throws IOException {
		return yylexNonNull(type1, type2, -1, error);
	}


	/**
	 * Returns the next token from the input stream, or throws an exception
	 * if the end of stream is reached or if the token is not of three given
	 * types.
	 *
	 * @param type1 One type the token can be.
	 * @param type2 Another type the token can be, or <tt>-1</tt> if we
	 *        should only check against <tt>type1</tt>.
	 * @param type3 Another type the token can be, or <tt>-1</tt> if we
	 *        should only check against <tt>type1</tt> and <tt>type2</tt>.
	 * @param error The error description for the exception if the end of
	 *        stream is reached, or if the token is of an unexpected type.
	 * @return The token.
	 * @throws IOException If an IO error occurs or the end of stream is
	 *         reached, or if the token is of a wrong type.
	 */
	public Token yylexNonNull(int type1, int type2, int type3, String error)
								throws IOException {
		Token t = yylex();
		if (t==null) {
			throw new IOException(error);
		}
		if (t.getType()!=type1 && (type2==-1 || t.getType()!=type2) &&
				(type3==-1 || t.getType()!=type3)) {
			throw new IOException(error + ", found '" + t.getLexeme() + "'");
		}
		return t;
	}


	/**
	 * Returns the next token, but does not take it off of the stream.  This
	 * is useful for lookahead.
	 *
	 * @return The next token.
	 * @throws IOException If an IO error occurs.
	 */
	public Token yyPeek() throws IOException {
		Token t = yylex();
		if (t!=null) {
			pushOntoStack(t);
		}
		return t;
	}


	/**
	 * Returns the <tt>depth</tt>-th token, but does not anything off of the
	 * stream.  This is useful for lookahead.
	 *
	 * @param depth The token to peek at, from <tt>1</tt> forward.
	 * @return The token, or <code>null</code> if that token index is past the
	 *         end of the stream.
	 * @throws IOException If an IO error occurs.
	 */
	public Token yyPeek(int depth) throws IOException {
		if (depth<1) {
			throw new IllegalArgumentException("depth must be >= 1");
		}
		Stack read = new Stack();
		for (int i=0; i<depth; i++) {
			Token t = yylex();
			if (t!=null) {
				read.push(t);
			}
			else {
				while (!read.isEmpty()) {
					yyPushback((Token)read.pop());
				}
				return null;
			}
		}
		Token t = (Token)read.peek();
		while (!read.isEmpty()) {
			yyPushback((Token)read.pop());
		}
		return t;
	}


	/**
	 * Peeks at and returns the type of the next token on the stream.
	 *
	 * @return The type of the next token, or <tt>-1</tt> if the end of stream
	 *         has been reached.
	 * @throws IOException If an IO error occurs.
	 */
	public int yyPeekCheckType() throws IOException {
		Token t = yyPeek();
		return t!=null ? t.getType() : -1;
	}


	/**
	 * Peeks at and returns the type of the specified token on the stream.
	 *
	 * @param index The index of the token to retrieve.
	 * @return The type of the token, or <tt>-1</tt> if the end of stream
	 *         was reached first.
	 * @throws IOException If an IO error occurs.
	 */
	public int yyPeekCheckType(int index) throws IOException {
		Token t = yyPeek(index);
		return t!=null ? t.getType() : -1;
	}


	/**
	 * Returns the next token, but does not take it off of the stream.  This
	 * is useful for lookahead.
	 *
	 * @return The next token.
	 * @throws IOException If an IO error occurs.
	 */
	public Token yyPeekNonNull(String error) throws IOException {
		Token t = yyPeek();
		if (t==null) {
			throw new IOException(error);
		}
		return t;
	}


	/**
	 * Returns the next token, but does not take it off of the stream.  This
	 * is useful for lookahead.
	 *
	 * @param type The type the token must be.
	 * @return The next token.
	 * @throws IOException If an IO error occurs, or if EOS is reached, or
	 *         if the token is not of the specified type.
	 */
	public Token yyPeekNonNull(int type, String error) throws IOException {
		return yyPeekNonNull(type, -1, error);
	}


	/**
	 * Returns the next token, but does not take it off of the stream.  This
	 * is useful for lookahead.
	 *
	 * @param type1 One of the two types the token must be.
	 * @param type2 The other of the two types the token must be.
	 * @return The next token.
	 * @throws IOException If an IO error occurs, or if EOS is reached, or
	 *         if the token is not of the specified type.
	 */
	public Token yyPeekNonNull(int type1, int type2, String error)
												throws IOException {
		return yyPeekNonNull(type1, type2, -1, error);
	}


	/**
	 * Returns the next token, but does not take it off of the stream.  This
	 * is useful for lookahead.
	 *
	 * @param type1 One of the three types the token must be.
	 * @param type2 Another of the three types the token must be.
	 * @param type3 The third of the types the token must be.
	 * @return The next token.
	 * @throws IOException If an IO error occurs, or if EOS is reached, or
	 *         if the token is not of the specified type.
	 */
	public Token yyPeekNonNull(int type1, int type2, int type3, String error)
												throws IOException {
		Token t = yyPeek();
		if (t==null) {
			throw new IOException(error);
		}
		if (t.getType()!=type1 && (type2==-1 || t.getType()!=type2) &&
				(type3==-1 || t.getType()!=type3)) {
			throw new IOException(error + ", found '" + t.getLexeme() + "'");
		}
		return t;
	}


	/**
	 * Pushes a token back onto the stream.
	 *
	 * @param t The token.
	 */
	public void yyPushback(Token t) {
		if (t!=null) {
			pushOntoStack(t);
		}
	}


	private class DocumentOffset implements Offset {

		public Position pos;

		public DocumentOffset(Position pos) {
			this.pos = pos;
		}

		public int getOffset() {
			return pos.getOffset();
		}

	}


}