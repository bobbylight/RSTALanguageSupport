/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.rjc.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for parsing field declarations that declare multiple variables,
 * e.g. {@code int a = 1, b[], c;}.
 */
class FieldDeclarationsTest {


	private static TypeDeclaration parseType(String source) throws IOException {
		Scanner s = new Scanner(new StringReader(source));
		CompilationUnit cu = new ASTFactory().getCompilationUnit("Test", s);
		assertEquals(1, cu.getTypeDeclarationCount());
		return cu.getTypeDeclarationIterator().next();
	}


	/**
	 * Returns the members of the single type in the given source, each as
	 * either {@code "<type> <name>"} for fields or {@code "<name>()"} for
	 * methods.
	 */
	private static List<String> parseMembers(String source) throws IOException {
		List<String> members = new ArrayList<>();
		Iterator<Member> i = parseType(source).getMemberIterator();
		while (i.hasNext()) {
			Member member = i.next();
			if (member instanceof Field) {
				members.add(member.getType() + " " + member.getName());
			}
			else if (member instanceof Method) {
				members.add(member.getName() + "()");
			}
		}
		return members;
	}


	private static List<String> parseClassMembers(String body) throws IOException {
		return parseMembers("public class Test {\n" + body + "\n}");
	}


	@Test
	void testNoInitializers() throws IOException {
		assertEquals(List.of("int a", "int b", "int c"),
			parseClassMembers("int a, b, c;"));
	}


	@Test
	void testInitializerOnLastVariable() throws IOException {
		assertEquals(List.of("int a", "int b"),
			parseClassMembers("int a, b = 2;"));
	}


	@Test
	void testInitializersOnAllVariables() throws IOException {
		assertEquals(List.of("int a", "int b", "int c"),
			parseClassMembers("int a = 1, b = 2, c = 3;"));
	}


	@Test
	void testInitializerOnFirstVariableOnly() throws IOException {
		assertEquals(List.of("int a", "int b"),
			parseClassMembers("int a = 1, b;"));
	}


	@Test
	void testInitializerWithMethodCallArguments() throws IOException {
		assertEquals(List.of("int a", "int b"),
			parseClassMembers("int a = Math.max(1, 2), b = 3;"));
	}


	@Test
	void testInitializerWithExplicitTypeArguments() throws IOException {
		assertEquals(List.of("Map<String, Integer> m", "Map<String, Integer> n"),
			parseClassMembers("Map<String, Integer> m = new HashMap<String, Integer>(), n;"));
	}


	@Test
	void testInitializerWithNestedTypeArguments() throws IOException {
		assertEquals(List.of("Map<String, List<Integer>> m", "Map<String, List<Integer>> n"),
			parseClassMembers(
				"Map<String, List<Integer>> m = new HashMap<String, List<Integer>>(), n;"));
	}


	@Test
	void testInitializerWithGenericMethodCall() throws IOException {
		assertEquals(List.of("Map<String, Integer> m", "Map<String, Integer> n"),
			parseClassMembers(
				"Map<String, Integer> m = Collections.<String, Integer>emptyMap(), n;"));
	}


	@Test
	void testInitializerWithLessThanComparison() throws IOException {
		assertEquals(List.of("boolean a", "boolean b"),
			parseClassMembers("boolean a = x < y, b;"));
	}


	@Test
	void testInitializerWithArrayInitializer() throws IOException {
		assertEquals(List.of("int[] a", "int[] b"),
			parseClassMembers("int[] a = { 1, 2, 3 }, b = new int[] { 4, 5 };"));
	}


	@Test
	void testInitializerWithAnonymousClass() throws IOException {
		assertEquals(List.of("Runnable r1", "Runnable r2", "foo()"),
			parseClassMembers(
				"Runnable r1 = new Runnable() {\n" +
				"    public void run() { int x = 1, y = 2; }\n" +
				"}, r2;\n" +
				"void foo() {}"));
	}


	@Test
	void testInitializerWithLambda() throws IOException {
		assertEquals(List.of("BinaryOperator<Integer> add", "BinaryOperator<Integer> sub"),
			parseClassMembers(
				"BinaryOperator<Integer> add = (x, y) -> x + y, sub = (x, y) -> { return x - y; };"));
	}


	@Test
	void testMembersAfterMultiVariableDeclaration() throws IOException {
		assertEquals(List.of("int a", "int b", "String s", "foo()"),
			parseClassMembers(
				"int a = 1, b = 2;\n" +
				"String s;\n" +
				"void foo() {}"));
	}


	@Test
	void testArrayBracketsOnFirstVariableOnly() throws IOException {
		assertEquals(List.of("int[] a", "int b"),
			parseClassMembers("int a[], b;"));
	}


	@Test
	void testArrayBracketsOnLaterVariable() throws IOException {
		assertEquals(List.of("int a", "int[][] b", "int c"),
			parseClassMembers("int a, b[][], c;"));
	}


	@Test
	void testArrayBracketsOnArrayType() throws IOException {
		assertEquals(List.of("int[] a", "int[][] b"),
			parseClassMembers("int[] a, b[];"));
	}


	@Test
	void testArrayBracketsWithInitializers() throws IOException {
		assertEquals(List.of("int[] a", "int b"),
			parseClassMembers("int a[] = { 1 }, b = 2;"));
	}


	@Test
	void testModifiersAppliedToAllVariables() throws IOException {
		Iterator<Member> i = parseType("public class Test {\n" +
			"private static final int A = 1, B = 2;\n" +
			"}").getMemberIterator();
		for (String name : List.of("A", "B")) {
			Field field = (Field)i.next();
			assertEquals(name, field.getName());
			assertTrue(field.getModifiers().isPrivate());
			assertTrue(field.getModifiers().isStatic());
			assertTrue(field.getModifiers().isFinal());
		}
		assertFalse(i.hasNext());
	}


	@Test
	void testDeprecationAppliedToAllVariables() throws IOException {
		Iterator<Member> i = parseType("public class Test {\n" +
			"@Deprecated int a = 1, b;\n" +
			"int c;\n" +
			"}").getMemberIterator();
		assertTrue(((Field)i.next()).isDeprecated());
		assertTrue(((Field)i.next()).isDeprecated());
		assertFalse(((Field)i.next()).isDeprecated());
	}


	@Test
	void testInterfaceConstants() throws IOException {
		assertEquals(List.of("int A", "int[] B", "int C", "foo()"),
			parseMembers("public interface Test {\n" +
				"int A = 1, B[] = { 2 }, C = Math.max(3, 4);\n" +
				"void foo();\n" +
				"}"));
	}
}
