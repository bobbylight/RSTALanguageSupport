/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link GenericTypes} class.
 */
class GenericTypesTest {

	@Test
	void testGetRawType() {
		assertEquals("java.util.Set", GenericTypes.getRawType("java.util.Set<String>"));
		assertEquals("java.lang.String", GenericTypes.getRawType("java.lang.String"));
	}

	@Test
	void testGetRawType_wildcard() {
		assertEquals("Number", GenericTypes.getRawType("? extends Number"));
		assertEquals("java.util.List", GenericTypes.getRawType("? extends java.util.List<Foo>"));
		assertEquals("java.lang.Object", GenericTypes.getRawType("?"));
		assertEquals("java.lang.Object", GenericTypes.getRawType("? super Number"));
	}

	@Test
	void testGetTypeArguments() {
		assertEquals(Arrays.asList("String", "Integer"),
				GenericTypes.getTypeArguments("java.util.Map<String, Integer>"));
	}

	@Test
	void testGetTypeArguments_nested() {
		assertEquals(Arrays.asList("java.util.Map$Entry<String, Integer>"),
				GenericTypes.getTypeArguments("java.util.Set<java.util.Map$Entry<String, Integer>>"));
		assertEquals(Arrays.asList("String", "java.util.List<Foo>"),
				GenericTypes.getTypeArguments("java.util.Map<String, java.util.List<Foo>>"));
	}

	@Test
	void testGetTypeArguments_none() {
		assertEquals(Collections.emptyList(), GenericTypes.getTypeArguments("java.lang.String"));
	}
}
