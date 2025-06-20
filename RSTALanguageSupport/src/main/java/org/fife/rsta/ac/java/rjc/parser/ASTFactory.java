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
package org.fife.rsta.ac.java.rjc.parser;

import java.io.EOFException;
import java.io.IOException;
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.rjc.ast.AbstractTypeDeclarationNode;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.EnumBody;
import org.fife.rsta.ac.java.rjc.ast.EnumDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Package;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclarationContainer;
import org.fife.rsta.ac.java.rjc.lang.Annotation;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lang.TypeArgument;
import org.fife.rsta.ac.java.rjc.lang.TypeParameter;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.lexer.Token;
import org.fife.rsta.ac.java.rjc.lexer.TokenTypes;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;


/**
 * Generates an abstract syntax tree for a Java source file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ASTFactory implements TokenTypes {

	private static final Logger LOG =
		System.getLogger(ASTFactory.class.getName());

	private static final boolean DEBUG = false;

	/**
	 * Whether the next member (or class, interface or enum) is deprecated.
	 */
	private boolean nextMemberDeprecated;


	public ASTFactory() {
	}


	private boolean checkDeprecated() {
		boolean deprecated = nextMemberDeprecated;
		nextMemberDeprecated = false;
		return deprecated;
	}


	/**
	 * Checks whether a local variable's name collides with a local variable
	 * defined earlier.  Note that this method assumes that it is called
	 * immediately whenever a variable is parsed, thus any other variables
	 * declared in a code block were declared before the one being checked.
	 *
	 * @param cu The compilation unit.
	 * @param lVar The just-scanned local variable.
	 * @param block The code block the variable is in.
	 * @param m The method the (possibly nested) code block <code>block</code>
	 *        is in, or <code>null</code> for none.
	 */
	private void checkForDuplicateLocalVarNames(CompilationUnit cu,
							Token lVar, CodeBlock block, Method m) {

		String name = lVar.getLexeme();
		boolean found = false;

		// See if a local variable defined previously in this block has the
		// same name.
		for (int i=0; i<block.getLocalVarCount(); i++) {
			LocalVariable otherLocal = block.getLocalVar(i);
			if (name.equals(otherLocal.getName())) {
				cu.addParserNotice(lVar, "Duplicate local variable: " + name);
				found = true;
				break;
			}
		}

		// If not...
		if (!found) {

			// If this was a nested code block, check previously-defined
			// variables in the parent block.
			if (block.getParent()!=null) {
				checkForDuplicateLocalVarNames(cu, lVar, block.getParent(), m);
			}

			// If this was the highest-level code block, if we're in the body
			// of a method, check the method's parameters.
			else if (m!=null) {
				for (int i=0; i<m.getParameterCount(); i++) {
					FormalParameter param = m.getParameter(i);
					if (name.equals(param.getName())) {
						cu.addParserNotice(lVar, "Duplicate local variable: " + name);
						break;
					}
				}
			}

		}

	}


	/**
	 * Assumes <tt>t</tt> is the actual '<tt>@foobar</tt>' annotation token.
	 *
	 * @param cu  The compilation unit.
	 * @param s The scanner.
	 * @return The annotation.
	 * @throws IOException If an IO error occurs.
	 */
	private Annotation getAnnotation(CompilationUnit cu, Scanner s)
									throws IOException {

		s.yylexNonNull(ANNOTATION_START, "Annotation expected");
		Type type = getType(cu, s);

		if ("Deprecated".equals(type.toString())) {
			nextMemberDeprecated = true;
		}

		if (s.yyPeekCheckType()==SEPARATOR_LPAREN) {
			s.yylex();
			// TODO: Read rest of Annotation stuff
			s.eatThroughNextSkippingBlocks(SEPARATOR_RPAREN);
		}

        return new Annotation(type);

	}


	private CodeBlock getBlock(CompilationUnit cu, CodeBlock parent, Method m,
                               Scanner s, boolean isStatic) throws IOException {
		return getBlock(cu, parent, m, s, isStatic, 1);
	}


	/**
	 * Parses a block of code.  This should not be called.
	 *
	 * @param parent The parent code block, or <code>null</code> if none (i.e.
	 *        this is the body of a method, a static initializer block, etc.).
	 * @param m The method containing this block, or <code>null</code> if this
	 *        block is not part of a method.
	 * @param s The scanner.
	 * @param isStatic Whether this is a static code block.
	 * @param depth The nested depth of this code block.
	 */
	private CodeBlock getBlock(CompilationUnit cu, CodeBlock parent,
                               Method m, Scanner s,
                               boolean isStatic, int depth) throws IOException {

		log("Entering getBlock() (" + depth + ")");

		// TODO: Implement me to get variable declarations.

		Token t = s.yylexNonNull(SEPARATOR_LBRACE, "'{' expected");
		CodeBlock block = new CodeBlock(isStatic, s.createOffset(t.getOffset()));
		block.setParent(parent);
		boolean atStatementStart =true;

OUTER:
		while (true) {

			// Don't bail if they have unmatched parens (for example), just
			// return the current status of the block.
			//t = s.yylexNonNull("Unexpected end of input");
			if ((t = s.yylex())==null) {
				log("Exiting getBlock() - eos (" + depth + ")");
				block.setDeclarationEndOffset(s.createOffset(s.getOffset()));
				return block;
			}

			int type = t.getType();
			boolean isFinal = false;

			switch (type) {

				case SEPARATOR_LBRACE:
					s.yyPushback(t);
					CodeBlock child = getBlock(cu, block, m, s, isStatic, depth+1);
					block.add(child);
					atStatementStart = true;
					break;

				case SEPARATOR_RBRACE:
					block.setDeclarationEndOffset(s.createOffset(t.getOffset()));
					break OUTER;

				case KEYWORD_TRY:
					t = s.yyPeekNonNull(SEPARATOR_LBRACE, SEPARATOR_LPAREN, "'{' or '(' expected");
					if (t.getType()==SEPARATOR_LPAREN) { // Auto-closeable stuff
						// TODO: Get block-scoped var(s)
						s.eatParenPairs();
					}
					s.yyPeekNonNull(SEPARATOR_LBRACE, "'{' expected");
					CodeBlock tryBlock = getBlock(cu, block, m, s, isStatic, depth+1);
					block.add(tryBlock);
					while (s.yyPeekCheckType()==KEYWORD_CATCH &&
							s.yyPeekCheckType(2)==SEPARATOR_LPAREN) {
						s.yylex(); // catch
						s.yylex(); // lparen
						Type exType;
						Token var;
						boolean multiCatch = false;
						do {
							isFinal = false;
							Token temp = s.yyPeekNonNull(IDENTIFIER, KEYWORD_FINAL, "Throwable type expected");
							if (temp.isType(KEYWORD_FINAL)) {
								isFinal = true;
								s.yylex();
							}
							s.yyPeekNonNull(IDENTIFIER, "Variable declarator expected");
							exType = getType(cu, s); // Not good for multi-catch!
							var = s.yylexNonNull(IDENTIFIER, OPERATOR_BITWISE_OR, "Variable declarator expected");
							multiCatch |= var.isType(OPERATOR_BITWISE_OR);
						} while (var.isType(OPERATOR_BITWISE_OR));
						s.yylexNonNull(SEPARATOR_RPAREN, "')' expected");
						s.yyPeekNonNull(SEPARATOR_LBRACE, "'{' expected");
						CodeBlock catchBlock = getBlock(cu, block, m, s, false, depth);
						int offs = var.getOffset(); // Not actually in block!
						if (multiCatch) {
							// TODO: With Java 7's multi-catch, calculate least upper bound for exception type.
							// java.lang.Throwable is always an upper bound, but not the least.
							// https://docs.oracle.com/javase/specs/jls/se17/html/jls-4.html#jls-4.10.4
							exType = new Type("java");
							exType.addIdentifier("lang", null);
							exType.addIdentifier("Throwable", null);
						}
						LocalVariable localVar = new LocalVariable(s, isFinal, exType, offs, var.getLexeme());
						checkForDuplicateLocalVarNames(cu, var, block, m);
						catchBlock.addLocalVariable(localVar);
						block.add(catchBlock);
					}
					break;

				case KEYWORD_FOR:
					// TODO: Get local var (e.g. "int i", "Iterator i", etc.)
					// Fall through
				case KEYWORD_WHILE:
					int nextType = s.yyPeekCheckType();
					while (nextType!=-1 && nextType!=SEPARATOR_LPAREN) {
						t = s.yylex(); // Grab the (unexpected) token
						if (t!=null) { // Should always be true
							ParserNotice pn = new ParserNotice(t, "Unexpected token");
							cu.addParserNotice(pn);
						}
						nextType = s.yyPeekCheckType();
					}
					if (nextType==SEPARATOR_LPAREN) {
						s.eatParenPairs();
					}
					nextType = s.yyPeekCheckType();
					if (nextType==SEPARATOR_LBRACE) {
						child = getBlock(cu, block, m, s, isStatic, depth+1);
						block.add(child);
						atStatementStart = true;
					}
					break;

				// NOTE: The code below is supposed to try to parse code blocks and identify
				// variable declarations.  This does work somewhat, but the problem is that
				// our parsing of type parameters isn't good enough, and lines like:
				//    for (int i=0; i<foo; i++) {
				// get incorrectly parsed as a type "i<foo>" (even though the closing '>' isn't
				// there, but that's not the big problem really!).  We should be able to check
				// whether our type parameters are well-formed, and if they aren't, then assume
				// push all tokens back, and assume that the '<' was a less-than operator.
				// It's the "push all tokens back" that's tough for us at the moment, as we've
				// lost most of the tokens (there may be an arbitrary number that have been
				// parsed and discarded).
				case KEYWORD_FINAL:
					isFinal = true;
					t = s.yylexNonNull("Unexpected end of file");
					// Fall through

				default:
					if (t.isType(SEPARATOR_SEMICOLON)) {
						atStatementStart = true;
						break;
					}
					else if (atStatementStart && (t.isBasicType() || (t.isIdentifier()))) {
						s.yyPushback(t);
						// TODO: This is very inefficient
						Type varType;
						try {
							varType = getType(cu, s, true);
						} catch (IOException ioe) { // Not a var declaration
							s.eatUntilNext(SEPARATOR_SEMICOLON, SEPARATOR_LBRACE, SEPARATOR_RBRACE);
							// Only needed if ended on ';' or '}', but...
							atStatementStart = true;
							break;
						}
						if (s.yyPeekCheckType()==IDENTIFIER) {
							while ((t=s.yylexNonNull(IDENTIFIER,
									"Variable name expected (type==" + varType + ")"))!=null) {
								int arrayDepth = s.skipBracketPairs();
								varType.incrementBracketPairCount(arrayDepth);
								String varDec = varType + " " + t.getLexeme();
								log(">>> Variable -- " + varDec + " (line " + t.getLine() + ")");
								int offs = t.getOffset();
								String name = t.getLexeme();
								LocalVariable lVar = new LocalVariable(s, isFinal, varType, offs, name);
								checkForDuplicateLocalVarNames(cu, t, block, m);
								block.addLocalVariable(lVar);
								nextType = s.yyPeekCheckType();
								// A "valid" nextType would be '=', ',' or ';'.
								// If it's an '=', skip past the assignment.
								if (nextType==OPERATOR_EQUALS) {
									Token temp = s.eatThroughNextSkippingBlocksAndStuffInParens(SEPARATOR_COMMA,
										SEPARATOR_SEMICOLON);
									if (temp!=null) {
										s.yyPushback(temp);
									}
									nextType = s.yyPeekCheckType();
								}
								// If next is a comma, loop to read the next local
								// var.  Otherwise, whether it's valid,
								// eat until the end of the statement.
								if (nextType!=SEPARATOR_COMMA) {
									s.eatThroughNextSkippingBlocks(SEPARATOR_SEMICOLON);
									break;
								}
								s.yylex(); // Eat the comma (does nothing if EOS)
							}
						}
					}
					else {
						atStatementStart = false;
					}
					break;

			}

		}

		log("Exiting Block() (" + depth + ")");
		return block;

	}


	private void getClassBody(CompilationUnit cu, Scanner s,
                              NormalClassDeclaration classDec) throws IOException {

		log("Entering getClassBody");

		Token t = s.yylexNonNull(SEPARATOR_LBRACE, "'{' expected");
		classDec.setBodyStartOffset(s.createOffset(t.getOffset()));

		t = s.yylexNonNull("ClassBody expected");

		while (t.getType() != SEPARATOR_RBRACE) {

			switch (t.getType()) {

				case SEPARATOR_SEMICOLON:
					break; // Do nothing

				case KEYWORD_STATIC:
					Token t2 = s.yyPeekNonNull("'{' or modifier expected");
					if (t2.isType(SEPARATOR_LBRACE)) {
						CodeBlock block = getBlock(cu, null, null, s, true);
						classDec.addMember(block);
						break;
					}
					else { // Not "static {" => must be a member.
						s.yyPushback(t); // Put back "static"
						Modifiers modList = getModifierList(cu, s);
						getMemberDecl(cu, s, classDec, modList);
					}
					break;

				case SEPARATOR_LBRACE:
					s.yyPushback(t);
					CodeBlock block = getBlock(cu, null, null, s, false);
					classDec.addMember(block);
					break;

				default:
					s.yyPushback(t);
					Modifiers modList = getModifierList(cu, s);
					getMemberDecl(cu, s, classDec, modList);
					break;

			}

			try {
				t = s.yylexNonNull("'}' expected (one)");
				classDec.setBodyEndOffset(s.createOffset(t.getOffset()));
			} catch (IOException ioe) {
				classDec.setBodyEndOffset(s.createOffset(s.getOffset()));
				int line = s.getLine();
				int col = s.getColumn();
				ParserNotice pn = new ParserNotice(line, col, 1, "'}' expected (two)");
				cu.addParserNotice(pn);
				break; // No more content in file
			}

		}

		log("Exiting getClassBody");

	}


	private TypeDeclaration getClassOrInterfaceDeclaration(CompilationUnit cu,
                   Scanner s, TypeDeclarationContainer addTo, Modifiers modList)
					throws IOException {

		log("Entering getClassOrInterfaceDeclaration");
		Token t = s.yyPeekNonNull(
						"class, enum, interface or @interface expected");

		if (modList==null) { // Not yet read in
			modList = getModifierList(cu, s);
		}
		t = s.yylexNonNull("class, enum, interface or @interface expected");

		AbstractTypeDeclarationNode td;

		switch (t.getType()) {

			case KEYWORD_CLASS:
				td = getNormalClassDeclaration(cu, s, addTo);
				break;

			case KEYWORD_ENUM:
				td = getEnumDeclaration(cu, s, addTo);
				break;

			case KEYWORD_INTERFACE:
				td = getNormalInterfaceDeclaration(cu, s, addTo);
				break;

			case ANNOTATION_START:
				// TODO: AnnotationTypeDeclaration, implement me.
				throw new IOException(
						"AnnotationTypeDeclaration not implemented");

			default:
				ParserNotice notice = new ParserNotice(t,
										"class, interface or enum expected");
				cu.addParserNotice(notice);
				//return td;
				// Assume we're a class to get more problems.
				td = getNormalClassDeclaration(cu, s, addTo);
				break;

		}

		td.setModifiers(modList);
		td.setDeprecated(checkDeprecated());

		log("Exiting getClassOrInterfaceDeclaration");
		return td;

	}


	/**
	 * Reads tokens for a Java source file from the specified lexer and returns
	 * the structure of the source as an AST.
	 *
     * @param name The name of the compilation unit.
	 * @param scanner The scanner to read from.
	 * @return The root node of the AST.
	 */
	public CompilationUnit getCompilationUnit(String name, Scanner scanner) {

		CompilationUnit cu = new CompilationUnit(name);

try {

		// Get annotations.
		List<Annotation> initialAnnotations = null; // Usually none
		while (scanner.yyPeekCheckType()==ANNOTATION_START) {
			if (initialAnnotations==null) {
				initialAnnotations = new ArrayList<>(1);
			}
			initialAnnotations.add(getAnnotation(cu, scanner));
		}

		// Get possible "package" line.
		Token t = scanner.yylex();
		if (t==null) {
			return cu;
		}
		if (t.isType(KEYWORD_PACKAGE)) {
			t = scanner.yyPeekNonNull("Identifier expected");
			int offs = t.getOffset();
			String qualifiedID = getQualifiedIdentifier(scanner);
			Package pkg = new Package(scanner, offs, qualifiedID);
			if (initialAnnotations!=null) {
				//pkg.setAnnotations(initialAnnotations);
				initialAnnotations = null;
			}
			cu.setPackage(pkg);
			scanner.yylexNonNull(SEPARATOR_SEMICOLON, "Semicolon expected");
			t = scanner.yylex();
		}

		// Go through any import statements.
		OUTER:
		while (t!=null && t.isType(KEYWORD_IMPORT)) {

			boolean isStatic = false;
			StringBuilder buf = new StringBuilder();
			t = scanner.yylexNonNull("Incomplete import statement");
			Token temp = null;
			int offs = 0;

			if (t.isType(KEYWORD_STATIC)) {
				isStatic = true;
				t = scanner.yylexNonNull("Incomplete import statement");
			}

			if (!t.isIdentifier()) {
				cu.addParserNotice(t, "Expected identifier, found: \"" +
									t.getLexeme() + "\"");
				scanner.eatThroughNextSkippingBlocks(SEPARATOR_SEMICOLON);
				// We expect "t" to be the semicolon below
				t = scanner.getMostRecentToken();
			}
			else {
				offs = t.getOffset();
				buf.append(t.getLexeme());
				temp = scanner.yylexNonNull(SEPARATOR_DOT, SEPARATOR_SEMICOLON,
											"'.' or ';' expected");
				while (temp.isType(SEPARATOR_DOT)) {
					temp = scanner.yylexNonNull(IDENTIFIER, OPERATOR_TIMES,
												"Identifier or '*' expected");
					if (temp.isIdentifier()) {
						buf.append('.').append(temp.getLexeme());
					}
					else {//if (temp.getLexeme().equals("*")) {
						buf.append(".*");
						temp = scanner.yylex(); // We're bailing, so scan here
						break;
					}
					temp = scanner.yylexNonNull(KEYWORD_IMPORT, SEPARATOR_DOT,
									SEPARATOR_SEMICOLON, "'.' or ';' expected");
					if (temp.isType(KEYWORD_IMPORT)) {
						cu.addParserNotice(temp, "';' expected");
						t = temp;
						continue OUTER;
					}
				}
				t = temp;
			}

			if (temp==null || !t.isType(SEPARATOR_SEMICOLON)) {
				throw new IOException("Semicolon expected, found " + t);
			}

			ImportDeclaration id = new ImportDeclaration(scanner, offs,
											buf.toString(), isStatic);
			cu.addImportDeclaration(id);
			t = scanner.yylex();

		}

		// class files aren't required to have TypeDeclarations.
		if (t==null) {
			return cu;
		}

		scanner.yyPushback(t);
		//TypeDeclaration td = null;
		while ((/*td = */getTypeDeclaration(cu, scanner)) != null) {
			if (initialAnnotations!=null) {
				//td.addAnnotations(initialAnnotations);
				initialAnnotations = null;
			}
			//cu.addTypeDeclaration(td);
			// Done when the type declarations are created.
		}

} catch (IOException ioe) {
	if (isDebug() && !(ioe instanceof EOFException)) { // Not just "end of file"
		ioe.printStackTrace();
	}
	ParserNotice notice;
	Token lastTokenLexed = scanner.getMostRecentToken();
	if (lastTokenLexed==null) {
		notice = new ParserNotice(0,0,5, ioe.getMessage());
	}
	else {
		notice = new ParserNotice(lastTokenLexed, ioe.getMessage());
	}
	cu.addParserNotice(notice);
	//throw ioe; // Un-comment me to get the AnnotationTypeDeclaration error count in "Main" test
}

return cu;

	}


	private EnumBody getEnumBody(CompilationUnit cu, Scanner s,
                                 EnumDeclaration enumDec) throws IOException {
		// TODO: Implement me
		CodeBlock block = getBlock(cu, null, null, s, false);
		enumDec.setBodyEndOffset(s.createOffset(block.getNameEndOffset()));
		return null;
	}


	private EnumDeclaration getEnumDeclaration(CompilationUnit cu,
                                               Scanner s, TypeDeclarationContainer addTo) throws IOException {

		Token t = s.yylexNonNull(IDENTIFIER, "Identifier expected");
		String enumName = t.getLexeme();
		EnumDeclaration enumDec = new EnumDeclaration(s,t.getOffset(),enumName);
		enumDec.setPackage(cu.getPackage());
		addTo.addTypeDeclaration(enumDec);

		t = s.yylexNonNull("implements or '{' expected");

		if (t.isType(KEYWORD_IMPLEMENTS)) {
			List<Type> implemented = new ArrayList<>(1); // Usually small
			do {
				implemented.add(getType(cu, s));
				t = s.yylex();
			} while (t != null && t.isType(SEPARATOR_COMMA));
			// enumDesc.setImplementedInterfaces(implemented);
			if (t != null) {
				s.yyPushback(t);
			}
		}
		else if (t.isType(SEPARATOR_LBRACE)) {
			s.yyPushback(t);
		}

		getEnumBody(cu, s, enumDec);
		//EnumBody enumBody = getEnumBody(cu, s);
		//enumDec.setEnumBody(enumBody);

		return enumDec;

	}


	private List<FormalParameter> getFormalParameters(CompilationUnit cu,
                                                      List<Token> tokenList) throws IOException {

		List<FormalParameter> list = new ArrayList<>(0);

		Scanner s = new Scanner(tokenList);
		Token t = s.yylex();
		if (t==null) { // No parameters
			return list;
		}

		while (true) {

			boolean isFinal = false;
			if (t.isType(KEYWORD_FINAL)) {
				isFinal = true;
				t = s.yylexNonNull("Type expected");
			}

			List<Annotation> annotations = null;
			while (t.getType() == ANNOTATION_START) {
				s.yyPushback(t);
				if (annotations == null) {
					annotations = new ArrayList<>(1); // Usually just 1
				}
				annotations.add(getAnnotation(cu, s));
				t = s.yylexNonNull("Type expected");
			}

			s.yyPushback(t);
			Type type = getType(cu, s);
			Token temp = s.yylexNonNull("Argument name expected");
			boolean elipsis = false;
			if (temp.isType(ELIPSIS)) {
				elipsis = true;
				temp = s.yylexNonNull(IDENTIFIER, "Argument name expected");
			}
			type.incrementBracketPairCount(s.skipBracketPairs());
			int offs = temp.getOffset();
			String name = temp.getLexeme();
			FormalParameter param = new FormalParameter(s, isFinal, type,
												offs, name, annotations);
			list.add(param);
			if (elipsis) {
				break; // Must be last parameter
			}
			t = s.yylex();
			if (t==null) {
				break;
			}
			else if (t.getType()!=SEPARATOR_COMMA) {
				throw new IOException("Comma expected");
			}
			t = s.yylexNonNull("Parameter or ')' expected");
		}

		return list;

	}


	private void getInterfaceBody(CompilationUnit cu, Scanner s,
                                  NormalInterfaceDeclaration iDec) throws IOException {

		log("Entering getInterfaceBody");

		Token t = s.yylexNonNull(SEPARATOR_LBRACE, "'{' expected");
		iDec.setBodyStartOffset(s.createOffset(t.getOffset()));

		t = s.yylexNonNull("InterfaceBody expected");

		while (t.getType() != SEPARATOR_RBRACE) {

			switch (t.getType()) {

				case SEPARATOR_SEMICOLON:
					break; // Do nothing

				case SEPARATOR_LBRACE:
					s.yyPushback(t);
					// TODO: What is this?
					getBlock(cu, null, null, s, false);
					break;

				default:
					s.yyPushback(t);
					Modifiers modList = getModifierList(cu, s);
					getInterfaceMemberDecl(cu, s, iDec, modList);
					break;

			}

			try {
				t = s.yylexNonNull("'}' expected (one)");
				iDec.setBodyEndOffset(s.createOffset(t.getOffset()));
			} catch (IOException ioe) {
				iDec.setBodyEndOffset(s.createOffset(s.getOffset()));
				int line = s.getLine();
				int col = s.getColumn();
				ParserNotice pn = new ParserNotice(line, col, 1, "'}' expected (two)");
				cu.addParserNotice(pn);
			}

		}

		log("Exiting getInterfaceBody");

	}


	/*
	 * InterfaceMemberDecl:
	 *    InterfaceMethodOrFieldDecl
	 *    InterfaceGenericMethodDecl
	 *    void Identifier VoidInterfaceMethodDeclaratorRest
	 *    InterfaceDeclaration
	 *    ClassDeclaration
	 */
	private void getInterfaceMemberDecl(CompilationUnit cu, Scanner s,
                                        NormalInterfaceDeclaration iDec, Modifiers modList)
			throws IOException {

		log("Entering getInterfaceMemberDecl");

		List<Token> tokenList = new ArrayList<>(1);
		List<Token> methodNameAndTypeTokenList = null;
		List<Token> methodParamsList = null;
		int bracketPairCount;
		boolean methodDecl = false;
		boolean blockDecl = false;
		boolean varDecl = false;
		Token t;

OUTER:
		while (true) {

			t = s.yylexNonNull("Unexpected end of input");

			switch (t.getType()) {
				case SEPARATOR_LPAREN:
					methodNameAndTypeTokenList = tokenList;
					methodParamsList = new ArrayList<>(1);
					methodDecl = true;
					break OUTER;
				case SEPARATOR_LBRACE:
					blockDecl = true;
					break OUTER;
				case OPERATOR_EQUALS:
					varDecl = true;
					// can be "= 4;", "= new foo();" or even "= new foo() { ... };".
					s.eatThroughNextSkippingBlocks(SEPARATOR_SEMICOLON);
					break OUTER;
				case SEPARATOR_SEMICOLON:
					varDecl = true;
					break OUTER;
				default:
					tokenList.add(t);
					break;
			}

		}

		if (varDecl) {
			log("*** Variable declaration:");
			Scanner tempScanner = new Scanner(tokenList);
			Type type = getType(cu, tempScanner);
			Token fieldNameToken = tempScanner.yylexNonNull(IDENTIFIER, "Identifier (field name) expected");
			bracketPairCount = tempScanner.skipBracketPairs();
			type.incrementBracketPairCount(bracketPairCount);
			Field field = new Field(s, modList, type, fieldNameToken);
			field.setDeprecated(checkDeprecated());
			field.setDocComment(s.getLastDocComment());
			log(field.toString());
			iDec.addMember(field);
			// TODO: Parse and grab the "value" after the '=' sign.
		}
		else if (methodDecl) {
			log("*** Method declaration:");
			Scanner tempScanner = new Scanner(methodNameAndTypeTokenList);
			Type type = null;
			if (methodNameAndTypeTokenList.size()>1) { // InterfaceMethodOrFieldDecl or InterfaceGenericMethodDecl
				if (tempScanner.yyPeekCheckType()==OPERATOR_LT) { // InterfaceGenericMethodDecl
					getTypeParameters(cu, tempScanner);
					type = getType(cu, tempScanner);
				}
				else { // InterfaceMethodOrFieldDecl (really just an InterfaceMethod)
					type = getType(cu, tempScanner);
				}
			}
			Token methodNameToken = tempScanner.yylexNonNull(IDENTIFIER, "Identifier (method name) expected");
			while (true) {
				t = s.yylexNonNull("Unexpected end of input");
				if (t.isType(SEPARATOR_RPAREN)) {
					break;
				}
				methodParamsList.add(t);
			}
			List<FormalParameter> formalParams = getFormalParameters(cu, methodParamsList);
			if (s.yyPeekCheckType()==SEPARATOR_LBRACKET) {
				if (type==null) {
					throw new IOException("Constructors cannot return array types");
				}
				type.incrementBracketPairCount(s.skipBracketPairs());
			}
			List<String> thrownTypeNames = getThrownTypeNames(cu, s);
			t = s.yylexNonNull("'{' or ';' expected");
			if (t.getType() != SEPARATOR_SEMICOLON) {
				throw new IOException("';' expected");
			}
			Method m = new Method(s, modList, type, methodNameToken, formalParams,
									thrownTypeNames);
			m.setDeprecated(checkDeprecated());
			m.setDocComment(s.getLastDocComment());
			iDec.addMember(m);
		}
		else if (blockDecl) {
			//s.yyPushback(t);
			//_getBlock(cu, s, false);
			//// TODO: Add the member for a block...
			// Could be a code block (static or not), or an inner class, enum,
			// or interface...
			if (tokenList.size()<2) {
				for (int i=tokenList.size()-1; i>=0; i--) {
					s.yyPushback(tokenList.get(i));
				}
				CodeBlock block = getBlock(cu, null, null, s, false);
				iDec.addMember(block);
			}
			else { // inner class, enum, or interface (?)
				s.yyPushback(t); // The '{' token
				for (int i=tokenList.size()-1; i>=0; i--) {
					s.yyPushback(tokenList.get(i));
				}
				/*TypeDeclaration type = */
				getClassOrInterfaceDeclaration(cu, s, iDec, modList);
			}
		}

		log("Exiting getInterfaceMemberDecl");

	}


	/*
	 * MemberDecl:
	 *    GenericMethodOrConstructorDecl
	 *    MethodOrFieldDecl
	 *    void Identifier VoidMethodDeclaratorRest
	 *    Identifier ConstructorDeclaratorRest
	 *    InterfaceDeclaration
	 *    ClassDeclaration
	 */
	private void getMemberDecl(CompilationUnit cu, Scanner s,
                               NormalClassDeclaration classDec, Modifiers modList)
			throws IOException {

		log("Entering getMemberDecl");

		List<Token> tokenList = new ArrayList<>(1);
		List<Token> methodNameAndTypeTokenList = null;
		List<Token> methodParamsList = null;
		int bracketPairCount;
		boolean methodDecl = false;
		boolean blockDecl = false;
		boolean varDecl = false;
		Token t;

OUTER:
		while (true) {

			t = s.yylexNonNull("Unexpected end of input");

			switch (t.getType()) {
				case SEPARATOR_LPAREN:
					methodNameAndTypeTokenList = tokenList;
					methodParamsList = new ArrayList<>(1);
					methodDecl = true;
					break OUTER;
				case SEPARATOR_LBRACE:
					blockDecl = true;
					break OUTER;
				case OPERATOR_EQUALS:
					varDecl = true;
					// can be "= 4;", "= new foo();" or even "= new foo() { ... };".
					s.eatThroughNextSkippingBlocks(SEPARATOR_SEMICOLON);
					break OUTER;
				case SEPARATOR_SEMICOLON:
					varDecl = true;
					break OUTER;
				default:
					tokenList.add(t);
					break;
			}

		}

		if (varDecl) {
			log("*** Variable declaration:");
			Scanner tempScanner = new Scanner(tokenList);
			Type type = getType(cu, tempScanner);
			Token fieldNameToken = tempScanner.yylexNonNull(IDENTIFIER, "Identifier (field name) expected");
			bracketPairCount = tempScanner.skipBracketPairs();
			type.incrementBracketPairCount(bracketPairCount);
			Field field = new Field(s, modList, type, fieldNameToken);
			field.setDeprecated(checkDeprecated());
			field.setDocComment(s.getLastDocComment());
			log(field.toString());
			classDec.addMember(field);
		}
		else if (methodDecl) {
			log("*** Method declaration:");
			CodeBlock block = null; // Method body
			Scanner tempScanner = new Scanner(methodNameAndTypeTokenList);
			Type type = null;
			if (methodNameAndTypeTokenList.size()>1) { // GenericMethodOrConstructorDecl or method (not reg constructor)
				if (tempScanner.yyPeekCheckType()==OPERATOR_LT) { // GenericMethodOrConstructorDecl
					getTypeParameters(cu, tempScanner);
					if (tempScanner.yyPeekCheckType(2)==-1) { // GenericConstructor
						// Do nothing; we're good to keep going
					}
					else { // A generic method declaration.
						type = getType(cu, tempScanner);
					}
				}
				else {
					type = getType(cu, tempScanner); // Method (not a constructor)
				}
			}
			Token methodNameToken = tempScanner.yylexNonNull(IDENTIFIER,
									"Identifier (method name) expected");
			while (true) {
				t = s.yylexNonNull("Unexpected end of input");
				if (t.isType(ANNOTATION_START)) {
					// we have an annotation, we need to check if it has
					// parenthesis
					methodParamsList.add(t);
					// first token should be annotation name
					t = s.yylexNonNull("Unexpected end of input");
					methodParamsList.add(t);
					t = s.yylexNonNull("Unexpected end of input");
					if (t.isType(SEPARATOR_LPAREN)) {
						// add opening parenthesis
						methodParamsList.add(t);
						// read through next RPAREN
						while (true) {
							t = s.yylexNonNull("Unexpected end of input");
							methodParamsList.add(t);
							if (t.isType(SEPARATOR_RPAREN)) {
								break;
							}
						}
					}
					// a new annotation starts right away, we push back onto
					// stack, so we read at the next cycle again
					else if (t.isType(ANNOTATION_START)) {
						s.yyPushback(t);
					}
					else if (!t.isType(SEPARATOR_RPAREN)) {
						// if not a brace, we add to the method param list
						methodParamsList.add(t);
					}
					else {
						// contains only 1 annotation without any parameters?
						// (like public void someMethod(@Param) ?)
						break;
					}
				}
				else if (t.isType(SEPARATOR_RPAREN)) {
					break;
				}
				else {
					methodParamsList.add(t);
				}
			}
			List<FormalParameter> formalParams = getFormalParameters(cu, methodParamsList);
			if (s.yyPeekCheckType()==SEPARATOR_LBRACKET) {
				if (type==null) {
					throw new IOException("Constructors cannot return array types");
				}
				type.incrementBracketPairCount(s.skipBracketPairs());
			}
			List<String> thrownTypeNames = getThrownTypeNames(cu, s);
			Method m = new Method(s, modList, type, methodNameToken, formalParams,
					thrownTypeNames);
			m.setDeprecated(checkDeprecated());
			m.setDocComment(s.getLastDocComment());
			classDec.addMember(m);
			t = s.yylexNonNull("'{' or ';' expected");
			if (t.isType(SEPARATOR_SEMICOLON)) {
				// Just a method declaration (such as in an interface)
			}
			else if (t.isType(SEPARATOR_LBRACE)) {
				s.yyPushback(t);
				block = getBlock(cu, null, m, s, false);
			}
			else {
				throw new IOException("'{' or ';' expected");
			}
			m.setBody(block);
		}
		else if (blockDecl) {
			// Could be a code block (static or not), or an inner class, enum,
			// or interface...
			nextMemberDeprecated = false;
			if (tokenList.size()<2) {
				for (int i=tokenList.size()-1; i>=0; i--) {
					s.yyPushback(tokenList.get(i));
				}
				CodeBlock block = getBlock(cu, null, null, s, false);
				classDec.addMember(block);
			}
			else { // inner class, enum, or interface (?)
				s.yyPushback(t); // The '{' token
				for (int i=tokenList.size()-1; i>=0; i--) {
					s.yyPushback(tokenList.get(i));
				}
				/*TypeDeclaration type = */
				getClassOrInterfaceDeclaration(cu, s, classDec, modList);
			}
		}

		log("Exiting getMemberDecl (next== " + s.yyPeek() + ")");

	}


	private Modifiers getModifierList(CompilationUnit cu, Scanner s)
										throws IOException {

		Modifiers modList = null;
		Token t = s.yylexNonNull("Unexpected end of input");

		while (true) {

			int modifier = isModifier(t);
			if (modifier != -1) {
				if (modList==null) {
					modList = new Modifiers();
				}
				if (!modList.addModifier(modifier)) {
					cu.addParserNotice(t, "Duplicate modifier");
				}
			}
			else if (t.isType(ANNOTATION_START)) {
				Token next = s.yyPeekNonNull("Annotation expected");
				s.yyPushback(t); // Put '@' back
				// TODO: Handle at a higher level (even at Scanner?)
				if (next.isType(KEYWORD_INTERFACE)) {
					return modList;
				}
				if (modList==null) {
					modList = new Modifiers();
				}
				modList.addAnnotation(getAnnotation(cu, s));
			}
			else {
				s.yyPushback(t);
				return modList;
			}

			t = s.yylexNonNull("Unexpected end of input");

		}

	}


	private NormalClassDeclaration getNormalClassDeclaration(
			CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo)
			throws IOException {

		log("Entering getNormalClassDeclaration");
		String className;

		Token t = s.yylexNonNull("Identifier expected");
		if (t.isType(IDENTIFIER)) {
			className = t.getLexeme();
		}
		else {
			className = "Unknown";
			cu.addParserNotice(new ParserNotice(t, "Class name expected"));
			s.eatUntilNext(KEYWORD_EXTENDS, KEYWORD_IMPLEMENTS, SEPARATOR_LBRACE);
		}

		NormalClassDeclaration classDec = new NormalClassDeclaration(s,
													t.getOffset(), className);
		classDec.setPackage(cu.getPackage());
		addTo.addTypeDeclaration(classDec);

		t = s.yylexNonNull("TypeParameters, extends, implements or '{' expected");
		if (t.isType(OPERATOR_LT)) {
			s.yyPushback(t);
			List<TypeParameter> typeParams = getTypeParameters(cu, s);
			classDec.setTypeParameters(typeParams);
			t = s.yylexNonNull("extends, implements or '{' expected");
		}

		if (t.isType(KEYWORD_EXTENDS)) {
			classDec.setExtendedType(getType(cu, s));
			t = s.yylexNonNull("implements or '{' expected");
		}

		if (t.isType(KEYWORD_IMPLEMENTS)) {
			do {
				classDec.addImplemented(getType(cu, s));
				t = s.yylex();
			} while (t != null && t.isType(SEPARATOR_COMMA));
			if (t != null) {
				s.yyPushback(t);
			}
		}
		else if (t.isType(SEPARATOR_LBRACE)) {
			s.yyPushback(t);
		}

		getClassBody(cu, s, classDec);

		log("Exiting getNormalClassDeclaration");
		return classDec;

	}


	private NormalInterfaceDeclaration getNormalInterfaceDeclaration(
			CompilationUnit cu, Scanner s, TypeDeclarationContainer addTo)
			throws IOException {

		String iName;

		Token t = s.yylexNonNull("Identifier expected");
		if (t.isType(IDENTIFIER)) {
			iName = t.getLexeme();
		}
		else {
			iName = "Unknown";
			cu.addParserNotice(new ParserNotice(t, "Interface name expected"));
			s.eatUntilNext(KEYWORD_EXTENDS, SEPARATOR_LBRACE);
		}

		NormalInterfaceDeclaration iDec = new NormalInterfaceDeclaration(
													s, t.getOffset(), iName);
		iDec.setPackage(cu.getPackage());
		addTo.addTypeDeclaration(iDec);

		t = s.yylexNonNull("TypeParameters, extends or '{' expected");
		if (t.isType(OPERATOR_LT)) {
			s.yyPushback(t);
			getTypeParameters(cu, s);
			t = s.yylexNonNull("Interface body expected");
		}

		if (t.isType(KEYWORD_EXTENDS)) {
			do {
				iDec.addExtended(getType(cu, s));
				t = s.yylex();
			} while (t != null && t.isType(SEPARATOR_COMMA));
			if (t != null) {
				s.yyPushback(t);
			}
		}
		else if (t.isType(SEPARATOR_LBRACE)) {
			s.yyPushback(t);
		}

		getInterfaceBody(cu, s, iDec);

		return iDec;

	}


	private String getQualifiedIdentifier(Scanner scanner)
			throws IOException {

		Token t;
		StringBuilder sb = new StringBuilder();

		while ((t = scanner.yylex()).isIdentifier()) {
			sb.append(t.getLexeme());
			t = scanner.yylex();
			if (t.isType(SEPARATOR_DOT)) {
				sb.append('.');
			}
			else {
				break;
			}
		}

		// QualifiedIdentifier has ended.
		scanner.yyPushback(t);

		return sb.toString();

	}


	private List<String> getThrownTypeNames(CompilationUnit cu, Scanner s)
									throws IOException {

		if (s.yyPeekCheckType()!=KEYWORD_THROWS) {
			return null;
		}
		s.yylex();

		List<String> list = new ArrayList<>(1); // Usually small

		list.add(getQualifiedIdentifier(s));
		while (s.yyPeekCheckType()==SEPARATOR_COMMA) {
			s.yylex();
			list.add(getQualifiedIdentifier(s));
		}

		return list;

	}


	// For "backwards compatibility," don't know if "false" is usually
	// correct or not
	private Type getType(CompilationUnit cu, Scanner s) throws IOException {
		return getType(cu, s, false);
	}


	private Type getType(CompilationUnit cu, Scanner s,
                         boolean pushbackOnUnexpected) throws IOException {

		log("Entering getType()");
		Type type = new Type();

		Token t = s.yylexNonNull("Type expected");

		// TODO: "void" checking is NOT in the JLS for type!  Remove me
		if (t.isType(KEYWORD_VOID)) {
			type.addIdentifier(t.getLexeme(), null);
			log("Exiting getType(): " + type);
			return type;
		}
		else if (t.isBasicType()) {
			int arrayDepth = s.skipBracketPairs();
			type.addIdentifier(t.getLexeme(), null);
			type.setBracketPairCount(arrayDepth);
			log("Exiting getType(): " + type);
			return type;
		}

OUTER:
		while (true) {
			switch (t.getType()) {
				case IDENTIFIER:
					List<TypeArgument> typeArgs = null;
					if (s.yyPeekCheckType()==OPERATOR_LT) {
						typeArgs = getTypeArguments(cu, s);
					}
					type.addIdentifier(t.getLexeme(), typeArgs);
					t = s.yylexNonNull("Unexpected end of input");
					if (t.isType(SEPARATOR_DOT)) {
						t = s.yylexNonNull("Unexpected end of input");
						continue;
					}
					else if (t.isType(SEPARATOR_LBRACKET)) {
						s.yyPushback(t);
						type.setBracketPairCount(s.skipBracketPairs());
						break OUTER;
					}
					else {
						s.yyPushback(t);
						break OUTER;
					}
				default:
					if (pushbackOnUnexpected) {
						s.yyPushback(t);
					}
					throw new IOException("Expected identifier, found: " + t);
			}
		}

		log("Exiting getType(): " + type);
		return type;

	}


	private TypeArgument getTypeArgument(CompilationUnit cu, Scanner s)
											throws IOException {

		log("Entering getTypeArgument()");

		TypeArgument typeArg;

		Token t = s.yyPeekNonNull("Type or '?' expected");

		if (t.isType(OPERATOR_QUESTION)) {
			s.yylex(); // Pop the '?' off the stream.
			t = s.yyPeek();
			if (t.getType()!=OPERATOR_GT) {
				t = s.yylexNonNull(SEPARATOR_COMMA, KEYWORD_EXTENDS,
								KEYWORD_SUPER,
								"',', super or extends expected");
				switch (t.getType()) {
					case SEPARATOR_COMMA:
						typeArg = new TypeArgument(null, 0, null);
						s.yyPushback(t);
						break;
					case KEYWORD_EXTENDS:
						Type otherType = getType(cu, s);
						typeArg = new TypeArgument(null, TypeArgument.EXTENDS, otherType);
						break;
					default: // KEYWORD_SUPER:
						otherType = getType(cu, s);
						typeArg = new TypeArgument(null, TypeArgument.SUPER, otherType);
						break;
				}
			}
			else {
				typeArg = new TypeArgument(null, 0, null);
			}
		}
		else {
			Type type = getType(cu, s);
			typeArg = new TypeArgument(type);
		}

		log("Exiting getTypeArgument() : " + typeArg);
		return typeArg;

	}


	private List<TypeArgument> getTypeArguments(CompilationUnit cu, Scanner s)
									throws IOException {

		s.increaseTypeArgumentsLevel();
		log("Entering getTypeArguments() (" + s.getTypeArgumentsLevel() + ")");

		s.markResetPosition();
		s.yylexNonNull(OPERATOR_LT, "'<' expected");

		List<TypeArgument> typeArgs = new ArrayList<>(1);

		Token t;
		do {
			typeArgs.add(getTypeArgument(cu, s));
			t = s.yylexNonNull("',' or '>' expected");
			if (t.getType()!=SEPARATOR_COMMA && t.getType()!=OPERATOR_GT) {
				// Assume we're in a code block, and are simply at the (much
				// more common) case of e.g. "if (i < 7) ...".
				s.resetToLastMarkedPosition();
				log("Exiting getTypeArguments() (" + s.getTypeArgumentsLevel() +
                        ") - NOT TYPE ARGUMENTS (" + t.getLexeme() + ")");
				s.decreaseTypeArgumentsLevel();
				return null;
			}
		} while (t.isType(SEPARATOR_COMMA));

		log("Exiting getTypeArguments() (" + s.getTypeArgumentsLevel() + ")");
		s.decreaseTypeArgumentsLevel();

		s.clearResetPosition();
		return typeArgs;

	}


	private TypeDeclaration getTypeDeclaration(CompilationUnit cu,
                                               Scanner s) throws IOException {

		/*
		 * TypeDeclaration:
		 *    ClassOrInterfaceDeclaration
		 *    ';'
		 */

		Token t = s.yylex();
		if (t == null) {
			return null; // End of source file.
		}

		// Skip any semicolons.
		while (t.isType(SEPARATOR_SEMICOLON)) {
			t = s.yylex();
			if (t == null) {
				return null; // End of source file
			}
		}

		s.yyPushback(t); // Probably some modifier, e.g. "public"

		String docComment = s.getLastDocComment();
		TypeDeclaration td = getClassOrInterfaceDeclaration(cu, s, cu, null);
		td.setDocComment(docComment); // May be null
		return td;

	}


	private TypeParameter getTypeParameter(CompilationUnit cu, Scanner s)
									throws IOException {

		log("Entering getTypeParameter()");

		Token identifier = s.yylexNonNull(IDENTIFIER, "Identifier expected");
		TypeParameter typeParam = new TypeParameter(identifier);

		if (s.yyPeekCheckType()==KEYWORD_EXTENDS) {
			do {
				s.yylex(); // Pop off "extends" or "&".
				typeParam.addBound(getType(cu, s));
			} while (s.yyPeekCheckType()==OPERATOR_BITWISE_AND);
		}

		log("Exiting getTypeParameter(): " + typeParam.getName());
		return typeParam;

	}


	private List<TypeParameter> getTypeParameters(CompilationUnit cu,
                                                  Scanner s) throws IOException {

		s.increaseTypeArgumentsLevel();
		log("Entering getTypeParameters() (" + s.getTypeArgumentsLevel() + ")");

		s.markResetPosition();
		Token t = s.yylexNonNull(OPERATOR_LT, "TypeParameters expected");

		List<TypeParameter> typeParams = new ArrayList<>(1);

		do {
			TypeParameter typeParam = getTypeParameter(cu, s);
			typeParams.add(typeParam);
			t = s.yylexNonNull(SEPARATOR_COMMA, OPERATOR_GT, "',' or '>' expected");
		} while (t.isType(SEPARATOR_COMMA));

		log("Exiting getTypeParameters() (" + s.getTypeArgumentsLevel() + ")");
		s.decreaseTypeArgumentsLevel();

		return typeParams;

	}


	private static boolean isDebug() {
		return DEBUG;
	}


	private int isModifier(Token t) {
		switch (t.getType()) {
			case KEYWORD_PUBLIC:
			case KEYWORD_PROTECTED:
			case KEYWORD_PRIVATE:
			case KEYWORD_STATIC:
			case KEYWORD_ABSTRACT:
			case KEYWORD_FINAL:
			case KEYWORD_NATIVE:
			case KEYWORD_SYNCHRONIZED:
			case KEYWORD_TRANSIENT:
			case KEYWORD_VOLATILE:
			case KEYWORD_STRICTFP:
				return t.getType();
			default:
				return -1;
		}
	}


	private static void log(String msg) {
		if (DEBUG) {
			LOG.log(System.Logger.Level.INFO, msg);
		}
	}


}
