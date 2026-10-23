/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests synthetic metadata in both old and modern member structures.
 */
class MemberInfoTest {

	@ParameterizedTest
	@CsvSource({
		"true, false, false", "true, true, false", "true, false, true", "true, true, true",
		"false, false, false", "false, true, false", "false, false, true", "false, true, true"
	})
	void testRead_synthetic(boolean field, boolean flag, boolean attribute) throws IOException {
		ClassFile cf = mock(ClassFile.class);
		when(cf.getUtf8ValueFromConstantPool(1)).thenReturn("generated");
		when(cf.getUtf8ValueFromConstantPool(2)).thenReturn(field ? "I" : "()V");
		when(cf.getUtf8ValueFromConstantPool(3)).thenReturn("Synthetic");

		int flags = AccessFlags.ACC_PUBLIC | AccessFlags.ACC_STATIC;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (DataOutputStream out = new DataOutputStream(bytes)) {
			out.writeShort(flags | (flag ? AccessFlags.ACC_SYNTHETIC : 0));
			out.writeShort(1); // name_index
			out.writeShort(2); // descriptor_index
			out.writeShort(attribute ? 1 : 0);
			if (attribute) {
				out.writeShort(3); // Synthetic attribute_name_index
				out.writeInt(0); // attribute_length
			}
		}

		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
			MemberInfo info = field ? FieldInfo.read(cf, in) : MethodInfo.read(cf, in);
			assertEquals(flag || attribute, Util.isSynthetic(info.getAccessFlags()));
			assertEquals(flags | (flag || attribute ? AccessFlags.ACC_SYNTHETIC : 0), info.getAccessFlags());
			assertEquals("generated", info.getName());
			assertEquals(0, in.available());
		}
	}
}
