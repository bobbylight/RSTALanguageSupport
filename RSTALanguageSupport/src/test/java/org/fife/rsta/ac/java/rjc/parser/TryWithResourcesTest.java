/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for parsing the variables declared by try-with-resources
 * statements.
 */
class TryWithResourcesTest {


	/**
	 * Parses a class with a single method with the given body.
	 */
	private static CompilationUnit parseCompilationUnit(String body) {
		String source = "public class Test {\nvoid foo() {\n" + body + "\n}\n}";
		Scanner s = new Scanner(new StringReader(source));
		return new ASTFactory().getCompilationUnit("Test", s);
	}


	/**
	 * Returns the body of the single method in a compilation unit.
	 */
	private static CodeBlock getMethodBody(CompilationUnit cu) {
		TypeDeclaration typeDec = cu.getTypeDeclarationIterator().next();
		return ((Method)typeDec.getMember(0)).getBody();
	}


	private static List<String> getLocalVars(CodeBlock block) {
		List<String> vars = new ArrayList<>();
		for (int i = 0; i < block.getLocalVarCount(); i++) {
			LocalVariable var = block.getLocalVar(i);
			vars.add(var.getType() + " " + var.getName());
		}
		return vars;
	}


	/**
	 * Returns the local variables of the first try block in a method with
	 * the given body, each as {@code "<type> <name>"}.
	 */
	private static List<String> parseTryBlockVars(String body) {
		CompilationUnit cu = parseCompilationUnit(body);
		assertEquals(0, cu.getParserNoticeCount(), "Unexpected parser notices");
		return getLocalVars(getMethodBody(cu).getChildBlock(0));
	}


	@Test
	void testSingleResource() {
		assertEquals(List.of("BufferedReader r"),
			parseTryBlockVars("try (BufferedReader r = Files.newBufferedReader(p)) { }"));
	}


	@Test
	void testMultipleResources() {
		assertEquals(List.of("InputStream in", "Reader r"),
			parseTryBlockVars("try (InputStream in = open(); Reader r = wrap(in)) { }"));
	}


	@Test
	void testTrailingSemicolon() {
		assertEquals(List.of("InputStream in", "Reader r"),
			parseTryBlockVars("try (InputStream in = open(); Reader r = wrap(in);) { }"));
	}


	@Test
	void testQualifiedAndGenericTypes() {
		assertEquals(List.of("java.io.Reader r", "Stream<String> lines"),
			parseTryBlockVars("try (java.io.Reader r = open();\n" +
				"     Stream<String> lines = Files.lines(p)) { }"));
	}


	@Test
	void testFinalAndAnnotatedResources() {
		CompilationUnit cu = parseCompilationUnit(
			"try (final Reader a = open(); @Foo Reader b = open(); @Bar(\"x\") final Reader c = open()) { }");
		assertEquals(0, cu.getParserNoticeCount());
		CodeBlock tryBlock = getMethodBody(cu).getChildBlock(0);
		assertEquals(List.of("Reader a", "Reader b", "Reader c"), getLocalVars(tryBlock));
		assertTrue(tryBlock.getLocalVar(0).isFinal());
		assertFalse(tryBlock.getLocalVar(1).isFinal());
		assertTrue(tryBlock.getLocalVar(2).isFinal());
	}


	@Test
	void testVar() {
		assertEquals(List.of("var r"),
			parseTryBlockVars("try (var r = open()) { }"));
	}


	@Test
	void testComplexInitializers() {
		assertEquals(List.of("Reader a", "Reader b", "Closeable c"),
			parseTryBlockVars(
				"try (Reader a = open(x, (y)); " +
				"Reader b = new StringReader(cond ? \"a;b\" : \"c\"); " +
				"Closeable c = () -> { log(\"closed\"); }) { }"));
	}


	@Test
	void testExistingVariableReferencesAreSkipped() {
		assertEquals(List.of("Reader r"),
			parseTryBlockVars("try (in; this.out; Reader r = open(); a.b) { }"));
	}


	@Test
	void testResourcesBeforeLocalsInTryBlock() {
		assertEquals(List.of("Reader r", "int x"),
			parseTryBlockVars("try (Reader r = open()) { int x = r.read(); }"));
	}


	@Test
	void testResourcesNotInCatchOrFinallyBlocks() {
		CompilationUnit cu = parseCompilationUnit(
			"try (Reader r = open()) { } catch (IOException e) { } finally { }");
		assertEquals(0, cu.getParserNoticeCount());
		CodeBlock body = getMethodBody(cu);
		assertEquals(0, body.getLocalVarCount());
		assertEquals(List.of("Reader r"), getLocalVars(body.getChildBlock(0)));
		assertEquals(List.of("IOException e"), getLocalVars(body.getChildBlock(1)));
		assertEquals(List.of(), getLocalVars(body.getChildBlock(2)));
	}


	@Test
	void testCodeAfterTryStatementStillParsed() {
		CompilationUnit cu = parseCompilationUnit(
			"try (Reader r = open()) { }\nString s = \"\";");
		assertEquals(0, cu.getParserNoticeCount());
		assertEquals(List.of("String s"), getLocalVars(getMethodBody(cu)));
	}


	@Test
	void testDuplicateResourceNames() {
		CompilationUnit cu = parseCompilationUnit(
			"try (Reader r = open(); Reader r = open()) { }");
		assertEquals(1, cu.getParserNoticeCount());
		assertEquals("Duplicate local variable: r", cu.getParserNotice(0).getMessage());
	}


	@Test
	void testResourceShadowingOuterLocal() {
		CompilationUnit cu = parseCompilationUnit(
			"Reader r = null;\ntry (Reader r = open()) { }");
		assertEquals(1, cu.getParserNoticeCount());
		assertEquals("Duplicate local variable: r", cu.getParserNotice(0).getMessage());
	}


	@Test
	void testLocalInTryBlockShadowingResource() {
		CompilationUnit cu = parseCompilationUnit(
			"try (Reader r = open()) { Reader r = null; }");
		assertEquals(1, cu.getParserNoticeCount());
		assertEquals("Duplicate local variable: r", cu.getParserNotice(0).getMessage());
	}


	@Test
	void testInvalidResourceReportsNoticeAndContinues() {
		CompilationUnit cu = parseCompilationUnit(
			"try (Reader = open(); Reader r = open()) { }\nString s;");
		assertEquals(1, cu.getParserNoticeCount());
		CodeBlock body = getMethodBody(cu);
		assertEquals(List.of("Reader r"), getLocalVars(body.getChildBlock(0)));
		assertEquals(List.of("String s"), getLocalVars(body));
	}
}
