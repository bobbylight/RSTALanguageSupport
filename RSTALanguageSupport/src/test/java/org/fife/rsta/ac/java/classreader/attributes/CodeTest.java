/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.attributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code Code} attribute class.
 */
class CodeTest {

	@Test
	void testGetParameterSlots_static() {
		assertArrayEquals(new int[0], Code.getParameterSlots("()V", true));
		assertArrayEquals(new int[] { 0, 2, 3 },
			Code.getParameterSlots("(JII)V", true));
		assertArrayEquals(new int[] { 0, 2, 4 },
			Code.getParameterSlots("(DDD)D", true));
	}

	@Test
	void testGetParameterSlots_instance() {
		assertArrayEquals(new int[] { 1, 2, 3 },
			Code.getParameterSlots("(ILjava/lang/String;D)V", false));
	}

	@Test
	void testGetParameterSlots_arraysTakeOneSlot() {
		assertArrayEquals(new int[] { 0, 1, 2, 3 },
			Code.getParameterSlots("([J[[DLjava/lang/Object;I)V", true));
	}

	// Parameter names in the LocalVariableTable are keyed by JVM slot, not by
	// parameter index; long and double parameters use two slots.
	@Test
	void testParameterNames_longAndDoubleParams(@TempDir Path dir)
			throws IOException {

		ClassFile cf = compile(dir, "Test", """
			public class Test {
				public static void staticMixed(long a, int b, int c) {}
				public void instanceMixed(double x, String y, long z, int w) {}
				public static void simple(int p, String q) {}
				public static void arrays(long[] arr, int n) {}
			}
			""");

		assertArrayEquals(new String[] { "a", "b", "c" },
			paramNames(cf, "staticMixed"));
		assertArrayEquals(new String[] { "x", "y", "z", "w" },
			paramNames(cf, "instanceMixed"));
		assertArrayEquals(new String[] { "p", "q" }, paramNames(cf, "simple"));
		assertArrayEquals(new String[] { "arr", "n" },
			paramNames(cf, "arrays"));
	}

	private static ClassFile compile(Path dir, String className, String source)
			throws IOException {

		Path src = dir.resolve(className + ".java");
		Files.writeString(src, source);

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "A JDK is required to run this test");
		int result = compiler.run(null, null, null, "-g", "-d",
			dir.toString(), src.toString());
		assertEquals(0, result, "Compilation failed");

		return new ClassFile(new File(dir.toFile(), className + ".class"));
	}

	private static String[] paramNames(ClassFile cf, String methodName) {
		for (int i=0; i<cf.getMethodCount(); i++) {
			MethodInfo mi = cf.getMethodInfo(i);
			if (methodName.equals(mi.getName())) {
				String[] names = new String[mi.getParameterCount()];
				for (int p=0; p<names.length; p++) {
					names[p] = mi.getParameterName(p);
				}
				return names;
			}
		}
		throw new IllegalArgumentException("No such method: " + methodName);
	}
}
