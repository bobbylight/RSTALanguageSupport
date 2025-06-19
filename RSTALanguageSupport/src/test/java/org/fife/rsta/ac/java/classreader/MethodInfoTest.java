/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader;

import org.junit.jupiter.api.Test;
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
}
