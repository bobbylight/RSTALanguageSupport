/*
 * 12/10/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java.rjc.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.LocalVariable;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;

import junit.framework.TestCase;


/**
 * Simple test case verifying parsing common cases of:
 * 
 * <ul>
 *    <li>Class members (methods and fields)
 *    <li>Local variables
 *    <li>Documentation comments for methods and fields
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ClassAndLocalVariablesTest extends TestCase {

	private CompilationUnit cu;


	public ClassAndLocalVariablesTest() {
		try {
			cu = createCompilationUnit();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	private CompilationUnit createCompilationUnit() throws IOException {
		//InputStream in = getClass().getResourceAsStream("tests/SimpleClass.java");
		BufferedReader r = new BufferedReader(//new InputStreamReader(in));
				new FileReader("src/test/resources/SimpleClass.java"));
		Scanner s = new Scanner(r);
		ASTFactory fact = new ASTFactory();
		CompilationUnit cu = fact.getCompilationUnit("SimpleClass", s);
		r.close();
		return cu;
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testImports() throws IOException {

		assertEquals(4, cu.getImportCount());

		// Imports should be returned in the order in which they are
		// found.
		Iterator<ImportDeclaration> i = cu.getImportIterator();

		ImportDeclaration id = i.next();
		assertEquals("java.io.*", id.getName());
		assertEquals(true, id.isWildcard());
		assertEquals(false, id.isStatic());

		id = i.next();
		assertEquals("java.util.List", id.getName());
		assertEquals(false, id.isWildcard());
		assertEquals(false, id.isStatic());

		id = i.next();
		assertEquals("java.util.ArrayList", id.getName());
		assertEquals(false, id.isWildcard());
		assertEquals(false, id.isStatic());

		id = i.next();
		assertEquals("java.lang.Math.*", id.getName());
		assertEquals(true, id.isWildcard());
		assertEquals(true, id.isStatic());

	}


	public void testMembers() throws IOException {

		// A single class is defined
		assertEquals(1, cu.getTypeDeclarationCount());

		// The class is named "SimpleClass"
		TypeDeclaration typeDec = cu.getTypeDeclarationIterator().next();
		assertEquals("SimpleClass", typeDec.getName());

		// 4 fields, 1 constructor and 4 methods
		int memberCount = typeDec.getMemberCount();
		assertEquals(9, memberCount);

		// Iterate through members.  They should be returned in the
		// order they are found in.
		Iterator<Member> i = typeDec.getMemberIterator();

		Member member = i.next();
		assertTrue(member instanceof Field);
		Field field = (Field)member;
		assertEquals("int", field.getType().toString());
		assertEquals("classInt1", field.getName());
		assertTrue(field.getModifiers().isPublic());
		assertTrue(field.getDocComment()!=null && 
				field.getDocComment().indexOf("A member int variable.")>=0);

		member = i.next();
		assertTrue(member instanceof Field);
		field = (Field)member;
		assertEquals("int", field.getType().toString());
		assertEquals("classInt2", field.getName());
		assertTrue(field.getModifiers().isProtected());
		assertEquals(null, field.getDocComment());

		member = i.next();
		assertTrue(member instanceof Field);
		field = (Field)member;
		assertEquals("String", field.getType().toString());
		assertEquals("classStr1", field.getName());
		assertTrue(field.getModifiers().isPrivate());
		assertTrue(field.getDocComment()!=null &&
				field.getDocComment().indexOf("A string member variable.")>=0);

		member = i.next();
		assertTrue(member instanceof Field);
		field = (Field)member;
		assertEquals("list", field.getName());
		assertEquals("List<Double>", field.getType().toString());
		assertTrue(field.getModifiers().isPrivate());
		assertEquals(field.getDocComment(), null);

		member = i.next();
		assertTrue(member instanceof Method);
		Method method = (Method)member;
		assertEquals("SimpleClass", method.getName());
		assertTrue(method.getModifiers().isPublic());
		assertTrue(method.isConstructor());

		member = i.next();
		assertTrue(member instanceof Method);
		method = (Method)member;
		assertEquals("getValue", method.getName());
		assertTrue(method.getModifiers().isPublic());
		assertTrue(method.getDocComment()!=null &&
				method.getDocComment().indexOf("Returns a value.")>=0);

		member = i.next();
		assertTrue(member instanceof Method);
		method = (Method)member;
		assertEquals("computeValue", method.getName());
		assertTrue(method.getModifiers().isPrivate());
		assertNull(method.getDocComment());

		member = i.next();
		assertTrue(member instanceof Method);
		method = (Method)member;
		assertEquals("localVarsComplex", method.getName());
		assertTrue(method.getModifiers().isPublic());
		// This method takes two parameters.
		assertEquals(2, method.getParameterCount());
		FormalParameter param = method.getParameter(0);
		assertEquals("newValue", param.getName());
		param = method.getParameter(1);
		assertEquals("unused", param.getName());

		member = i.next();
		assertTrue(member instanceof Method);
		method = (Method)member;
		assertEquals("localVarsSimple", method.getName());
		assertTrue(method.getModifiers().isPublic());

	}


	public void testLocalVariablesComplex() {

		TypeDeclaration td = cu.getTypeDeclaration(0);
		List<Method> methods = td.getMethodsByName("localVarsComplex");
		assertEquals(1, methods.size());
		Method method = methods.get(0);
		CodeBlock body = method.getBody();
		assertEquals(5, body.getLocalVarCount());

		LocalVariable var = body.getLocalVar(0);
		assertEquals("int", var.getType().getName(false));
		assertEquals("foo", var.getName());

		for (int i=1; i<5; i++) {
			var = body.getLocalVar(i);
			assertEquals("double", var.getType().getName(false));
			assertEquals("val" + i, var.getName());
		}

	}


	public void testLocalVariablesSimple() {

		TypeDeclaration td = cu.getTypeDeclaration(0);
		List<Method> methods = td.getMethodsByName("localVarsSimple");
		assertEquals(1, methods.size());
		Method method = methods.get(0);
		CodeBlock body = method.getBody();
		assertEquals(2, body.getLocalVarCount());

		LocalVariable var = body.getLocalVar(0);
		assertEquals("int", var.getType().getName(false));
		assertEquals("temp", var.getName());

		var = body.getLocalVar(1);
		assertEquals("boolean", var.getType().getName(false));
		assertEquals("unnecessary", var.getName());

		assertEquals(1, body.getChildBlockCount());
		CodeBlock ifStatementBlock = body.getChildBlock(0);
		assertEquals(1, ifStatementBlock.getLocalVarCount());
		var = ifStatementBlock.getLocalVar(0);
		assertEquals("float", var.getType().getName(false));
		assertEquals("f", var.getName());

	}


}