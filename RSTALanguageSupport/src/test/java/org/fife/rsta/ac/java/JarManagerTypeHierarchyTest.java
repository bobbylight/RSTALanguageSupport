/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.util.List;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JarManager#getTypeHierarchy(ClassFile)}.
 */
class JarManagerTypeHierarchyTest {

	private static ClassFile mockClass(String name, String superName,
			String... interfaces) {
		ClassFile cf = mock(ClassFile.class);
		when(cf.getClassName(true)).thenReturn(name);
		when(cf.getSuperClassName(true)).thenReturn(superName);
		when(cf.getImplementedInterfaceCount()).thenReturn(interfaces.length);
		for (int i = 0; i < interfaces.length; i++) {
			when(cf.getImplementedInterfaceName(i, true)).thenReturn(interfaces[i]);
		}
		return cf;
	}

	@Test
	void superclassesComeBeforeInterfaces() {
		ClassFile sub = mockClass("p.Sub", "p.Base", "p.A");
		ClassFile base = mockClass("p.Base", null, "p.B", "p.A");
		ClassFile a = mockClass("p.A", null, "p.C");
		ClassFile b = mockClass("p.B", null);
		ClassFile c = mockClass("p.C", null);
		JarManager jars = spy(new JarManager());
		doReturn(base).when(jars).getClassEntry("p.Base");
		doReturn(a).when(jars).getClassEntry("p.A");
		doReturn(b).when(jars).getClassEntry("p.B");
		doReturn(c).when(jars).getClassEntry("p.C");

		// Each type is listed once, even though p.A is implemented twice.
		assertEquals(List.of(sub, base, a, b, c), jars.getTypeHierarchy(sub));
	}

	@Test
	void superclassInDefaultPackage() {
		ClassFile sub = mockClass("p.Sub", "Base");
		ClassFile base = mockClass("Base", null);
		JarManager jars = spy(new JarManager());
		doReturn(base).when(jars).getClassEntry("Base");

		assertEquals(List.of(sub, base), jars.getTypeHierarchy(sub));
	}

	@Test
	void missingSupertypesAreSkipped() {
		ClassFile sub = mockClass("p.Sub", "p.Missing", "p.AlsoMissing");
		JarManager jars = new JarManager();

		assertEquals(List.of(sub), jars.getTypeHierarchy(sub));
	}
}
