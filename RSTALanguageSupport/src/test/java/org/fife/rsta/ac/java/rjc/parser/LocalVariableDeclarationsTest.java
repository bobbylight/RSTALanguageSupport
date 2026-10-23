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
 * Unit tests for parsing local variable declarations that declare multiple
 * variables, e.g. {@code int a = 1, b[], c;}.
 */
class LocalVariableDeclarationsTest {


	/**
	 * Returns the local variables declared directly in the body of a method
	 * with the given body.
	 */
	private static List<LocalVariable> parseLocalVariables(String body) throws IOException {
		String source = "public class Test {\nvoid foo() {\n" + body + "\n}\n}";
		Scanner s = new Scanner(new StringReader(source));
		CompilationUnit cu = new ASTFactory().getCompilationUnit("Test", s);
		TypeDeclaration typeDec = cu.getTypeDeclarationIterator().next();
		CodeBlock block = ((Method)typeDec.getMember(0)).getBody();
		List<LocalVariable> vars = new ArrayList<>();
		for (int i = 0; i < block.getLocalVarCount(); i++) {
			vars.add(block.getLocalVar(i));
		}
		return vars;
	}


	/**
	 * Returns the local variables declared directly in the body of a method
	 * with the given body, each as {@code "<type> <name>"}.
	 */
	private static List<String> parse(String body) throws IOException {
		List<String> vars = new ArrayList<>();
		for (LocalVariable var : parseLocalVariables(body)) {
			vars.add(var.getType() + " " + var.getName());
		}
		return vars;
	}


	@Test
	void testNoInitializers() throws IOException {
		assertEquals(List.of("int a", "int b", "int c"),
			parse("int a, b, c;"));
	}


	@Test
	void testInitializersOnAllVariables() throws IOException {
		assertEquals(List.of("int a", "int b", "int c"),
			parse("int a = 1, b = 2, c = 3;"));
	}


	@Test
	void testInitializerOnFirstVariableOnly() throws IOException {
		assertEquals(List.of("int a", "int b"),
			parse("int a = 1, b;"));
	}


	@Test
	void testInitializerWithMethodCallArguments() throws IOException {
		assertEquals(List.of("int a", "int b"),
			parse("int a = Math.max(1, 2), b = 3;"));
	}


	@Test
	void testInitializerWithExplicitTypeArguments() throws IOException {
		assertEquals(List.of("Map<String, Integer> m", "Map<String, Integer> n"),
			parse("Map<String, Integer> m = new HashMap<String, Integer>(), n;"));
	}


	@Test
	void testInitializerWithNestedTypeArguments() throws IOException {
		assertEquals(List.of("Map<String, List<Integer>> m", "Map<String, List<Integer>> n"),
			parse("Map<String, List<Integer>> m = new HashMap<String, List<Integer>>(), n;"));
	}


	@Test
	void testInitializerWithGenericMethodCall() throws IOException {
		assertEquals(List.of("Map<String, Integer> m", "Map<String, Integer> n"),
			parse("Map<String, Integer> m = Collections.<String, Integer>emptyMap(), n;"));
	}


	@Test
	void testInitializerWithLessThanComparison() throws IOException {
		assertEquals(List.of("boolean a", "boolean b"),
			parse("boolean a = x < y, b;"));
	}


	@Test
	void testInitializerWithArrayInitializer() throws IOException {
		assertEquals(List.of("int[] a", "int[] b"),
			parse("int[] a = { 1, 2, 3 }, b = new int[] { 4, 5 };"));
	}


	@Test
	void testInitializerWithAnonymousClass() throws IOException {
		assertEquals(List.of("Runnable r1", "Runnable r2", "int z"),
			parse(
				"Runnable r1 = new Runnable() {\n" +
				"    public void run() { int x = 1, y = 2; }\n" +
				"}, r2;\n" +
				"int z;"));
	}


	@Test
	void testInitializerWithLambda() throws IOException {
		assertEquals(List.of("BinaryOperator<Integer> add", "BinaryOperator<Integer> sub"),
			parse("BinaryOperator<Integer> add = (x, y) -> x + y, sub = (x, y) -> { return x - y; };"));
	}


	@Test
	void testVariablesAfterMultiVariableDeclaration() throws IOException {
		assertEquals(List.of("int a", "int b", "String s"),
			parse("int a = 1, b = 2;\nString s;"));
	}


	@Test
	void testArrayBracketsOnLaterVariable() throws IOException {
		assertEquals(List.of("String a", "String[] b"),
			parse("String a, b[];"));
	}


	@Test
	void testArrayBracketsOnFirstVariableOnly() throws IOException {
		assertEquals(List.of("int[] a", "int b"),
			parse("int a[], b;"));
	}


	@Test
	void testArrayBracketsOnMiddleVariable() throws IOException {
		assertEquals(List.of("int a", "int[][] b", "int c"),
			parse("int a, b[][], c;"));
	}


	@Test
	void testArrayBracketsOnArrayType() throws IOException {
		assertEquals(List.of("int[] a", "int[][] b"),
			parse("int[] a, b[];"));
	}


	@Test
	void testArrayBracketsWithInitializers() throws IOException {
		assertEquals(List.of("int[] a", "int b"),
			parse("int a[] = { 1 }, b = 2;"));
	}


	@Test
	void testFinalAppliedToAllVariables() throws IOException {
		List<LocalVariable> vars = parseLocalVariables("final int a = 1, b = 2;");
		assertEquals(2, vars.size());
		assertTrue(vars.get(0).isFinal());
		assertTrue(vars.get(1).isFinal());
	}
}
