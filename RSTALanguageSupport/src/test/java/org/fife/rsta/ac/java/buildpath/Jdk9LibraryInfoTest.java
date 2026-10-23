/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.buildpath;

import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code Jdk9LibraryInfo} class.  Since many JDKs and JREs
 * no longer ship a {@code jmods} directory, these tests build small synthetic
 * {@code .jmod} files (which are zip files whose classes live under
 * {@code classes/}) from class files in the running JVM.
 */
class Jdk9LibraryInfoTest {

	@TempDir
	Path tempDir;

	private static byte[] readClassBytes(Class<?> clazz) throws IOException {
		String res = clazz.getName().replace('.', '/') + ".class";
		try (InputStream in = ClassLoader.getSystemResourceAsStream(res)) {
			assertNotNull(in, "Couldn't find class bytes for " + res);
			return in.readAllBytes();
		}
	}

	/**
	 * Creates a fake jmod file.
	 *
	 * @param name The file name.
	 * @param entries Entry name to contents.
	 */
	private File createJmod(String name, Map<String, byte[]> entries) throws IOException {
		File file = tempDir.resolve(name).toFile();
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
			for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
				out.putNextEntry(new ZipEntry(entry.getKey()));
				out.write(entry.getValue());
				out.closeEntry();
			}
		}
		return file;
	}

	private File createBaseJmod() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put("classes/java/lang/Object.class", readClassBytes(Object.class));
		entries.put("classes/java/lang/String.class", readClassBytes(String.class));
		entries.put("classes/java/lang/readme.txt", new byte[] { 'h', 'i' });
		entries.put("lib/ignored/Fake.class", readClassBytes(Object.class));
		return createJmod("java.base.jmod", entries);
	}

	private File createUtilJmod() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put("classes/java/util/ArrayList.class", readClassBytes(java.util.ArrayList.class));
		entries.put("classes/java/util/List.class", readClassBytes(java.util.List.class));
		return createJmod("java.util.jmod", entries);
	}

	@Test
	void testConstructor_nonExistentFile_throws() {
		File missing = tempDir.resolve("nope.jmod").toFile();
		assertThrows(IllegalArgumentException.class,
			() -> new Jdk9LibraryInfo(new File[] { missing }));
	}

	@Test
	void testConstructor_nullFile_throws() {
		assertThrows(IllegalArgumentException.class,
			() -> new Jdk9LibraryInfo(new File[] { null }));
	}

	@Test
	void testConstructor_sourceLocation() throws IOException {
		File jmod = createBaseJmod();

		assertNull(new Jdk9LibraryInfo(new File[] { jmod }).getSourceLocation());

		SourceLocation loc = new DirSourceLocation(tempDir.toFile());
		assertSame(loc, new Jdk9LibraryInfo(new File[] { jmod }, loc).getSourceLocation());
	}

	@Test
	void testCreateClassFile_foundInFirstJmod() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		ClassFile cf = info.createClassFile("java/lang/String.class");
		assertNotNull(cf);
		assertEquals("java.lang.String", cf.getClassName(true));
	}

	@Test
	void testCreateClassFile_foundInLaterJmod() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		ClassFile cf = info.createClassFile("java/util/ArrayList.class");
		assertNotNull(cf);
		assertEquals("java.util.ArrayList", cf.getClassName(true));
	}

	@Test
	void testCreateClassFile_notFound_returnsNull() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		assertNull(info.createClassFile("java/lang/DoesNotExist.class"));
	}

	@Test
	void testCreateClassFile_entryOutsideClassesDir_returnsNull() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });

		// Only exists as "lib/ignored/Fake.class"
		assertNull(info.createClassFile("ignored/Fake.class"));
	}

	@Test
	void testCreateClassFile_canBeCalledRepeatedly() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		for (int i = 0; i < 3; i++) {
			assertNotNull(info.createClassFile("java/util/List.class"));
			assertNotNull(info.createClassFile("java/lang/Object.class"));
		}
	}

	@Test
	void testCreateClassFileBulk_found() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		info.bulkClassFileCreationStart();
		try {
			ClassFile object = info.createClassFileBulk("java/lang/Object.class");
			assertNotNull(object);
			assertEquals("java.lang.Object", object.getClassName(true));

			ClassFile list = info.createClassFileBulk("java/util/List.class");
			assertNotNull(list);
			assertEquals("java.util.List", list.getClassName(true));
		} finally {
			info.bulkClassFileCreationEnd();
		}
	}

	@Test
	void testCreateClassFileBulk_notFound_returnsNull() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		info.bulkClassFileCreationStart();
		try {
			assertNull(info.createClassFileBulk("java/lang/DoesNotExist.class"));
		} finally {
			info.bulkClassFileCreationEnd();
		}
	}

	@Test
	void testBulkClassFileCreation_canBeRestarted() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });

		for (int i = 0; i < 2; i++) {
			info.bulkClassFileCreationStart();
			try {
				assertNotNull(info.createClassFileBulk("java/lang/String.class"));
			} finally {
				info.bulkClassFileCreationEnd();
			}
		}
	}

	@Test
	void testCreatePackageMap_containsClassesFromAllJmods() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });

		PackageMapNode map = info.createPackageMap();

		assertTrue(map.containsPackage("java"));
		assertTrue(map.containsPackage("java.lang"));
		assertTrue(map.containsPackage("java.util"));
		assertTrue(map.containsClass("java.lang.Object"));
		assertTrue(map.containsClass("java.lang.String"));
		assertTrue(map.containsClass("java.util.ArrayList"));
		assertTrue(map.containsClass("java.util.List"));
	}

	@Test
	void testCreatePackageMap_ignoresNonClassEntries() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });

		PackageMapNode map = info.createPackageMap();

		// Not under "classes/"
		assertFalse(map.containsPackage("ignored"));
		assertFalse(map.containsPackage("lib"));
		// Under "classes/", but not a .class file
		assertFalse(map.containsClass("java.lang.readme"));
		assertFalse(map.containsClass("java.lang.readme.txt"));
		// A class that simply isn't there
		assertFalse(map.containsClass("java.util.ArrayList"));
	}

	@Test
	void testCreatePackageMap_ignoresModuleInfo() throws IOException {
		Map<String, byte[]> entries = new LinkedHashMap<>();
		entries.put("classes/module-info.class", new byte[] { 0 });
		entries.put("classes/java/lang/String.class", readClassBytes(String.class));
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createJmod("m.jmod", entries) });

		PackageMapNode map = info.createPackageMap();

		assertTrue(map.containsClass("java.lang.String"));
		assertFalse(map.containsClass("module-info"));
	}

	@Test
	void testGetJreHome() throws IOException {
		// <home>/jmods/java.base.jmod
		File home = tempDir.resolve("home").toFile();
		File jmods = new File(home, "jmods");
		assertTrue(jmods.mkdirs());
		File jmod = new File(jmods, "java.base.jmod");
		writeEmptyJmod(jmod);

		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { jmod });
		assertEquals(home, info.getJreHome());
	}

	private static void writeEmptyJmod(File f) throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f))) {
			out.putNextEntry(new ZipEntry("classes/"));
			out.closeEntry();
		}
	}

	@Test
	void testGetJreHome_noJmods_returnsNull() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[0]);
		assertNull(info.getJreHome());
	}

	@Test
	void testGetLastModified_alwaysZero() throws IOException {
		assertEquals(0, new Jdk9LibraryInfo(new File[] { createBaseJmod() }).getLastModified());
	}

	@Test
	void testGetLocationAsString_isEmpty() throws IOException {
		assertEquals("", new Jdk9LibraryInfo(new File[] { createBaseJmod() }).getLocationAsString());
	}

	@Test
	void testCompareTo_sameInstance() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		assertEquals(0, info.compareTo(info));
		assertEquals(info, info);
	}

	@Test
	void testCompareTo_differentInstance_sameFiles_notEqual() throws IOException {
		File jmod = createBaseJmod();
		Jdk9LibraryInfo info1 = new Jdk9LibraryInfo(new File[] { jmod });
		Jdk9LibraryInfo info2 = new Jdk9LibraryInfo(new File[] { jmod });

		// Only object identity is compared
		assertEquals(-1, info1.compareTo(info2));
		assertNotEquals(info1, info2);
	}

	@Test
	void testCompareTo_otherLibraryInfoType() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		LibraryInfo other = new DirLibraryInfo(tempDir.toFile());
		assertEquals(-1, info.compareTo(other));
	}

	@Test
	void testHashCodeImpl_sumOfFileHashCodes() throws IOException {
		File jmod1 = createBaseJmod();
		File jmod2 = createUtilJmod();

		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { jmod1, jmod2 });
		assertEquals(jmod1.hashCode() + jmod2.hashCode(), info.hashCodeImpl());
		assertEquals(info.hashCodeImpl(), info.hashCode());
	}

	// Verifies the first jmod wins, matching the original linear search order
	@Test
	void testCreateClassFile_classInMultipleJmods_firstWins() throws IOException {
		Map<String, byte[]> first = new LinkedHashMap<>();
		first.put("classes/dup/Thing.class", readClassBytes(String.class));
		Map<String, byte[]> second = new LinkedHashMap<>();
		second.put("classes/dup/Thing.class", readClassBytes(Object.class));
		File jmod1 = createJmod("a.jmod", first);
		File jmod2 = createJmod("b.jmod", second);

		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { jmod1, jmod2 });
		assertEquals("java.lang.String", info.createClassFile("dup/Thing.class").getClassName(true));

		info.bulkClassFileCreationStart();
		try {
			assertEquals("java.lang.String", info.createClassFileBulk("dup/Thing.class").getClassName(true));
		} finally {
			info.bulkClassFileCreationEnd();
		}
	}

	@Test
	void testBulkClassFileCreationEnd_withoutStart_doesNotThrow() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		assertDoesNotThrow(info::bulkClassFileCreationEnd);
	}

	@Test
	void testCreateClassFileBulk_withoutStart_returnsNull() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		assertNull(info.createClassFileBulk("java/lang/String.class"));
	}

	// Class lookups must work whether or not createPackageMap() was called first
	@Test
	void testCreateClassFile_afterCreatePackageMap() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod(), createUtilJmod() });
		info.createPackageMap();
		assertNotNull(info.createClassFile("java/util/List.class"));
		assertNull(info.createClassFile("java/lang/DoesNotExist.class"));
	}

	@Test
	void testCreatePackageMap_canBeCalledRepeatedly() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		assertTrue(info.createPackageMap().containsClass("java.lang.String"));
		assertTrue(info.createPackageMap().containsClass("java.lang.String"));
	}

	@Test
	void testToString() throws IOException {
		Jdk9LibraryInfo info = new Jdk9LibraryInfo(new File[] { createBaseJmod() });
		assertTrue(info.toString().startsWith("[JarLibraryInfo: jars="));
		assertTrue(info.toString().contains("source=null"));
	}
}
