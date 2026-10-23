/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for class reader utilities.
 */
class UtilTest {

	@ParameterizedTest
	@ValueSource(ints = { 0, AccessFlags.ACC_PUBLIC, AccessFlags.ACC_PRIVATE,
		AccessFlags.ACC_PROTECTED | AccessFlags.ACC_STATIC | AccessFlags.ACC_FINAL })
	void testIsSynthetic(int flags) {
		assertFalse(Util.isSynthetic(flags));
		assertTrue(Util.isSynthetic(flags | AccessFlags.ACC_SYNTHETIC));
	}
}
