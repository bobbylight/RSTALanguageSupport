/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader;

import org.fife.rsta.ac.java.classreader.constantpool.ConstantDynamicInfo;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantModuleInfo;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantPackageInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@code ClassFile} can read class files containing newer
 * constant pool entry types, such as those found in {@code module-info}
 * class files.
 */
class ClassFileConstantPoolTest {

	private static final int ACC_MODULE = 0x8000;

	/**
	 * Builds a minimal, valid {@code module-info.class} that also contains a
	 * {@code CONSTANT_Dynamic} entry.
	 */
	private static byte[] createModuleInfoClassBytes() throws IOException {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);

		out.writeInt(0xCAFEBABE);
		out.writeShort(0); // minor
		out.writeShort(53); // major (Java 9)

		out.writeShort(11); // constant_pool_count (entries 1-10)
		out.writeByte(1); out.writeUTF("module-info"); // 1
		out.writeByte(7); out.writeShort(1);            // 2: Class -> 1
		out.writeByte(1); out.writeUTF("java.base");    // 3
		out.writeByte(19); out.writeShort(3);           // 4: Module -> 3
		out.writeByte(1); out.writeUTF("java/lang");    // 5
		out.writeByte(20); out.writeShort(5);           // 6: Package -> 5
		out.writeByte(1); out.writeUTF("foo");          // 7
		out.writeByte(1); out.writeUTF("I");            // 8
		out.writeByte(12); out.writeShort(7); out.writeShort(8); // 9: NameAndType
		out.writeByte(17); out.writeShort(0); out.writeShort(9); // 10: Dynamic

		out.writeShort(ACC_MODULE); // access_flags
		out.writeShort(2); // this_class
		out.writeShort(0); // super_class
		out.writeShort(0); // interfaces_count
		out.writeShort(0); // fields_count
		out.writeShort(0); // methods_count
		out.writeShort(0); // attributes_count

		return bout.toByteArray();
	}

	@Test
	void testConstructor_moduleInfoWithNewConstantTypes() throws IOException {

		ClassFile cf = new ClassFile(new DataInputStream(
			new ByteArrayInputStream(createModuleInfoClassBytes())));

		assertEquals(ACC_MODULE, cf.getAccessFlags());
		assertEquals("module-info", cf.getClassName(true));
		assertEquals(11, cf.getConstantPoolCount());

		assertInstanceOf(ConstantModuleInfo.class, cf.getConstantPoolInfo(4));
		assertEquals(3, ((ConstantModuleInfo)cf.getConstantPoolInfo(4)).getNameIndex());
		assertInstanceOf(ConstantPackageInfo.class, cf.getConstantPoolInfo(6));
		assertEquals(5, ((ConstantPackageInfo)cf.getConstantPoolInfo(6)).getNameIndex());
		assertInstanceOf(ConstantDynamicInfo.class, cf.getConstantPoolInfo(10));
		assertEquals(9, ((ConstantDynamicInfo)cf.getConstantPoolInfo(10)).getNameAndTypeIndex());
	}
}
