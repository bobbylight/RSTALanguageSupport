/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

/**
 * Unit tests for the {@code MethodInfo} class.
 */
class MethodInfoTest {

	@Test
	void testGetReturnTypeString_fullyQualified_true() {
		// A method taking a String and an int as inputs that returns an Object
		String methodDescriptor = "(Ljava/lang/String;I)Ljava/lang/Object;";

		ClassFile cf = Mockito.mock(ClassFile.class);
		doReturn(methodDescriptor).when(cf).getUtf8ValueFromConstantPool(anyInt());

		MethodInfo mi = new MethodInfo(cf, 0, 0, 0);
		assertEquals("java.lang.Object", mi.getReturnTypeString(true));
	}

	@Test
	void testGetReturnTypeString_fullyQualified_false() {
		// A method taking a String and an int as inputs that returns an Object
		String methodDescriptor = "(Ljava/lang/String;I)Ljava/lang/Object;";

		ClassFile cf = Mockito.mock(ClassFile.class);
		doReturn(methodDescriptor).when(cf).getUtf8ValueFromConstantPool(anyInt());

		MethodInfo mi = new MethodInfo(cf, 0, 0, 0);
		assertEquals("Object", mi.getReturnTypeString(false));
	}

	// Verifies fix for https://github.com/bobbylight/RSTALanguageSupport/issues/68
	@Test
	void testGetReturnTypeString_fullyQualified_falseThenTrue() {
		// A method taking a String and an int as inputs that returns an Object
		String methodDescriptor = "(Ljava/lang/String;I)Ljava/lang/Object;";

		ClassFile cf = Mockito.mock(ClassFile.class);
		doReturn(methodDescriptor).when(cf).getUtf8ValueFromConstantPool(anyInt());

		MethodInfo mi = new MethodInfo(cf, 0, 0, 0);

		// First call returns unqualified, second returns qualified
		assertEquals("Object", mi.getReturnTypeString(false));
		assertEquals("java.lang.Object", mi.getReturnTypeString(true));
	}

	// Compiler-added constructor parameters (the outer instance of an inner
	// class) are included in the descriptor, but aren't in the source.
	@Test
	void testGetSignature_innerClassConstructor_hidesOuterInstanceParam(
			@TempDir Path dir) throws IOException {

		compile(dir, "Test", """
			import java.util.List;
			public class Test {
				class Inner {
					Inner(List<String> names, int count) {}
				}
			}
			""");

		MethodInfo ctor = constructor(new ClassFile(
			new File(dir.toFile(), "Test$Inner.class")));
		assertEquals(1, ctor.getSyntheticParameterCount());
		assertEquals(3, ctor.getParameterCount());
		assertEquals("Test.Inner(java.util.List names, int count)",
			ctor.getSignature());
	}

	// Compiler-added enum constructor parameters (name and ordinal) are
	// included in the descriptor, but aren't in the source.
	@Test
	void testGetSignature_enumConstructor_hidesNameAndOrdinalParams(
			@TempDir Path dir) throws IOException {

		compile(dir, "Test", """
			import java.util.List;
			public class Test {
				enum Color {
					RED(null, 1);
					Color(List<String> names, int count) {}
				}
			}
			""");

		MethodInfo ctor = constructor(new ClassFile(
			new File(dir.toFile(), "Test$Color.class")));
		assertEquals(2, ctor.getSyntheticParameterCount());
		assertEquals(4, ctor.getParameterCount());
		assertEquals("Test.Color(java.util.List names, int count)",
			ctor.getSignature());
	}

	// A static nested class has no hidden outer-instance parameter, so a
	// real first parameter of the outer type must not be hidden.
	@Test
	void testGetSignature_staticNestedClassConstructor_keepsOuterTypeParam(
			@TempDir Path dir) throws IOException {

		compile(dir, "Test", """
			public class Test {
				static class Nested {
					Nested(Test outer, int count) {}
				}
			}
			""");

		MethodInfo ctor = constructor(new ClassFile(
			new File(dir.toFile(), "Test$Nested.class")));
		assertEquals(0, ctor.getSyntheticParameterCount());
		assertEquals("Test.Nested(Test outer, int count)", ctor.getSignature());
	}


	// Compilers leave synthetic parameters out of the generic Signature
	// attribute, which is used once type arguments have been set.
	@Test
	void testGetSignature_innerClassConstructor_genericSignature_hidesOuterInstanceParam(
			@TempDir Path dir) throws IOException {

		compile(dir, "Test", """
			import java.util.List;
			public class Test {
				class Inner<T> {
					Inner(List<String> names, T value, int count) {}
				}
			}
			""");

		ClassFile cf = new ClassFile(new File(dir.toFile(), "Test$Inner.class"));
		cf.setTypeParamsToTypeArgs(Map.of("T", "Integer"));
		MethodInfo ctor = constructor(cf);
		assertEquals(1, ctor.getSyntheticParameterCount());
		assertEquals(4, ctor.getParameterCount());
		assertEquals("Test.Inner(List<String> names, Integer value, int count)",
			ctor.getSignature());
	}

	@Test
	void testGetSignature_enumConstructor_genericSignature_hidesNameAndOrdinalParams(
			@TempDir Path dir) throws IOException {

		compile(dir, "Test", """
			import java.util.List;
			public class Test {
				enum Color {
					RED(null, 1);
					Color(List<String> names, int count) {}
				}
			}
			""");

		ClassFile cf = new ClassFile(new File(dir.toFile(), "Test$Color.class"));
		cf.setTypeParamsToTypeArgs(null);
		MethodInfo ctor = constructor(cf);
		assertEquals(2, ctor.getSyntheticParameterCount());
		assertEquals(4, ctor.getParameterCount());
		assertEquals("Test.Color(List<String> names, int count)",
			ctor.getSignature());
	}


	private static void compile(Path dir, String className, String source)
			throws IOException {

		Path src = dir.resolve(className + ".java");
		Files.writeString(src, source);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "A JDK is required to run this test");
		int result = compiler.run(null, null, null, "-g", "-d",
			dir.toString(), src.toString());
		assertEquals(0, result, "Compilation failed");
	}

	private static MethodInfo constructor(ClassFile cf) {
		for (int i=0; i<cf.getMethodCount(); i++) {
			MethodInfo mi = cf.getMethodInfo(i);
			if (mi.isConstructor()) {
				return mi;
			}
		}
		throw new IllegalStateException("No constructor found");
	}
}
