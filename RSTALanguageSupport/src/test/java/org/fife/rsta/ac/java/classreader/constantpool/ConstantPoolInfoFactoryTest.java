/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code ConstantPoolInfoFactory} class.
 */
class ConstantPoolInfoFactoryTest {

	private static DataInputStream streamOf(int tag, int... u2s) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bout);
		out.writeByte(tag);
		for (int u2 : u2s) {
			out.writeShort(u2);
		}
		out.writeByte(0x7F); // Sentinel to verify exactly the right number of bytes are consumed
		return new DataInputStream(new ByteArrayInputStream(bout.toByteArray()));
	}

	private static void assertOnlySentinelLeft(DataInputStream in) throws IOException {
		assertEquals(0x7F, in.read());
		assertEquals(-1, in.read());
	}

	@Test
	void testReadConstantPoolInfo_class() throws IOException {
		DataInputStream in = streamOf(7, 12);
		ConstantPoolInfo info = ConstantPoolInfoFactory.readConstantPoolInfo(null, in);
		assertInstanceOf(ConstantClassInfo.class, info);
		assertEquals(12, ((ConstantClassInfo)info).getNameIndex());
		assertOnlySentinelLeft(in);
	}

	@Test
	void testReadConstantPoolInfo_dynamic() throws IOException {
		DataInputStream in = streamOf(17, 3, 9);
		ConstantPoolInfo info = ConstantPoolInfoFactory.readConstantPoolInfo(null, in);
		assertInstanceOf(ConstantDynamicInfo.class, info);
		assertEquals(17, info.getTag());
		assertEquals(3, ((ConstantDynamicInfo)info).getBootstrapMethodAttrIndex());
		assertEquals(9, ((ConstantDynamicInfo)info).getNameAndTypeIndex());
		assertOnlySentinelLeft(in);
	}

	@Test
	void testReadConstantPoolInfo_invokeDynamic_stillSupported() throws IOException {
		DataInputStream in = streamOf(18, 4, 8);
		ConstantPoolInfo info = ConstantPoolInfoFactory.readConstantPoolInfo(null, in);
		assertInstanceOf(ConstantInvokeDynamicInfo.class, info);
		assertEquals(18, info.getTag());
		assertEquals(4, ((ConstantInvokeDynamicInfo)info).getBootstrapMethodAttrIndex());
		assertEquals(8, ((ConstantInvokeDynamicInfo)info).getNameAndTypeIndex());
		assertOnlySentinelLeft(in);
	}

	@Test
	void testReadConstantPoolInfo_module() throws IOException {
		DataInputStream in = streamOf(19, 21);
		ConstantPoolInfo info = ConstantPoolInfoFactory.readConstantPoolInfo(null, in);
		assertInstanceOf(ConstantModuleInfo.class, info);
		assertEquals(19, info.getTag());
		assertEquals(21, ((ConstantModuleInfo)info).getNameIndex());
		assertOnlySentinelLeft(in);
	}

	@Test
	void testReadConstantPoolInfo_package() throws IOException {
		DataInputStream in = streamOf(20, 33);
		ConstantPoolInfo info = ConstantPoolInfoFactory.readConstantPoolInfo(null, in);
		assertInstanceOf(ConstantPackageInfo.class, info);
		assertEquals(20, info.getTag());
		assertEquals(33, ((ConstantPackageInfo)info).getNameIndex());
		assertOnlySentinelLeft(in);
	}

	@Test
	void testReadConstantPoolInfo_unknownTag_throws() throws IOException {
		DataInputStream in = streamOf(99);
		IOException ioe = assertThrows(IOException.class,
			() -> ConstantPoolInfoFactory.readConstantPoolInfo(null, in));
		assertTrue(ioe.getMessage().contains("99"));
	}

	@Test
	void testToString() {
		assertTrue(new ConstantDynamicInfo(1, 2).toString().contains("bootstrapMethodAttrIndex=1"));
		assertTrue(new ConstantDynamicInfo(1, 2).toString().contains("nameAndTypeIndex=2"));
		assertTrue(new ConstantModuleInfo(5).toString().contains("nameIndex=5"));
		assertTrue(new ConstantPackageInfo(6).toString().contains("nameIndex=6"));
	}
}
