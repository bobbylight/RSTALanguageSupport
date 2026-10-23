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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Unit tests for the {@code JrtLibraryInfo} class.  These read the runtime
 * running the tests, which has a {@code lib/modules} image like any Java 9+
 * runtime.
 */
class JrtLibraryInfoTest {

	@TempDir
	Path tempDir;

	private static File runningJreHome() {
		return new File(System.getProperty("java.home"));
	}

	/**
	 * Creates a runtime "home" that is a different directory than the running
	 * one, but whose {@code lib} contents are links to the running runtime.
	 */
	private File createLinkedJreHome() throws IOException {
		Path home = tempDir.resolve("otherhome");
		Path lib = Files.createDirectories(home.resolve("lib"));
		Path realLib = runningJreHome().toPath().resolve("lib");
		try {
			Files.createSymbolicLink(lib.resolve("modules"), realLib.resolve("modules"));
			Files.createSymbolicLink(lib.resolve("jrt-fs.jar"), realLib.resolve("jrt-fs.jar"));
		} catch (UnsupportedOperationException | IOException | SecurityException e) {
			assumeTrue(false, "Symbolic links not supported: " + e);
		}
		return home.toFile();
	}

	@Test
	void testConstructor_nullHome_throws() {
		assertThrows(IllegalArgumentException.class, () -> new JrtLibraryInfo(null));
	}

	@Test
	void testConstructor_noModulesImage_throws() {
		assertThrows(IllegalArgumentException.class, () -> new JrtLibraryInfo(tempDir.toFile()));
	}

	@Test
	void testConstructor_sourceLocation() {
		assertNull(new JrtLibraryInfo(runningJreHome()).getSourceLocation());

		SourceLocation loc = new DirSourceLocation(tempDir.toFile());
		assertSame(loc, new JrtLibraryInfo(runningJreHome(), loc).getSourceLocation());
	}

	@Test
	void testCreatePackageMap_containsJdkClasses() throws IOException {
		PackageMapNode map = new JrtLibraryInfo(runningJreHome()).createPackageMap();

		assertTrue(map.containsPackage("java.lang"));
		assertTrue(map.containsClass("java.lang.Object"));
		assertTrue(map.containsClass("java.lang.String"));
		assertTrue(map.containsClass("java.util.ArrayList"));
	}

	@Test
	void testCreatePackageMap_ignoresModuleInfo() throws IOException {
		PackageMapNode map = new JrtLibraryInfo(runningJreHome()).createPackageMap();

		assertFalse(map.containsClass("module-info"));
	}

	@Test
	void testCreatePackageMap_canBeCalledRepeatedly() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		assertTrue(info.createPackageMap().containsClass("java.lang.String"));
		assertTrue(info.createPackageMap().containsClass("java.lang.String"));
	}

	@Test
	void testCreateClassFile_found() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		ClassFile cf = info.createClassFile("java/lang/String.class");
		assertNotNull(cf);
		assertEquals("java.lang.String", cf.getClassName(true));
		assertNotEquals(0, cf.getMethodCount());
	}

	@Test
	void testCreateClassFile_afterCreatePackageMap() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());
		info.createPackageMap();

		assertEquals("java.util.List", info.createClassFile("java/util/List.class").getClassName(true));
	}

	@Test
	void testCreateClassFile_notFound_returnsNull() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		assertNull(info.createClassFile("java/lang/DoesNotExist.class"));
	}

	@Test
	void testCreateClassFile_moduleInfo_returnsNull() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		assertNull(info.createClassFile("module-info.class"));
	}

	@Test
	void testCreateClassFileBulk() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		info.bulkClassFileCreationStart();
		try {
			assertEquals("java.lang.Object", info.createClassFileBulk("java/lang/Object.class").getClassName(true));
			assertNull(info.createClassFileBulk("java/lang/DoesNotExist.class"));
		} finally {
			info.bulkClassFileCreationEnd();
		}
	}

	@Test
	void testBulkClassFileCreation_withoutStart_isHarmless() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());

		assertDoesNotThrow(info::bulkClassFileCreationEnd);
		assertNotNull(info.createClassFileBulk("java/lang/Object.class"));
	}

	@Test
	void testCreateClassFile_classesUsingNewerConstructs() throws IOException {
		// Guards against the class file reader choking on constructs used by
		// classes in newer runtimes
		JrtLibraryInfo info = new JrtLibraryInfo(runningJreHome());
		for (String entry : new String[] { "java/lang/invoke/MethodHandles.class",
				"java/util/concurrent/CompletableFuture.class", "java/lang/Record.class" }) {
			assertNotNull(info.createClassFile(entry), entry);
		}
	}

	@Test
	void testForeignJreHome_readsThatRuntime() throws IOException {
		JrtLibraryInfo info = new JrtLibraryInfo(createLinkedJreHome());

		PackageMapNode map = info.createPackageMap();
		assertTrue(map.containsClass("java.lang.String"));
		assertEquals("java.lang.String", info.createClassFile("java/lang/String.class").getClassName(true));
		assertNull(info.createClassFile("java/lang/DoesNotExist.class"));
	}

	@Test
	void testForeignJreHome_missingJrtFsJar_throwsIOException() throws IOException {
		File home = createLinkedJreHome();
		Files.delete(new File(home, "lib/jrt-fs.jar").toPath());

		JrtLibraryInfo info = new JrtLibraryInfo(home);
		assertThrows(IOException.class, info::createPackageMap);
	}

	@Test
	void testGetJreHome() {
		assertEquals(runningJreHome(), new JrtLibraryInfo(runningJreHome()).getJreHome());
	}

	@Test
	void testGetLastModified_alwaysZero() {
		assertEquals(0, new JrtLibraryInfo(runningJreHome()).getLastModified());
	}

	@Test
	void testGetLocationAsString() {
		assertEquals(runningJreHome().getAbsolutePath(),
			new JrtLibraryInfo(runningJreHome()).getLocationAsString());
	}

	@Test
	void testEquals_sameHome_equalEvenAcrossInstancesAndClones() {
		JrtLibraryInfo info1 = new JrtLibraryInfo(runningJreHome());
		JrtLibraryInfo info2 = new JrtLibraryInfo(runningJreHome(), new DirSourceLocation(tempDir.toFile()));

		assertEquals(0, info1.compareTo(info1));
		assertEquals(info1, info2);
		assertEquals(info1.hashCode(), info2.hashCode());
		assertEquals(info1, info1.clone());
	}

	@Test
	void testEquals_differentHomes_notEqual() throws IOException {
		JrtLibraryInfo info1 = new JrtLibraryInfo(runningJreHome());
		JrtLibraryInfo info2 = new JrtLibraryInfo(createLinkedJreHome());

		assertNotEquals(info1, info2);
		assertNotEquals(0, info1.compareTo(info2));
	}

	@Test
	void testCompareTo_otherLibraryInfoType() {
		LibraryInfo other = new DirLibraryInfo(tempDir.toFile());
		assertEquals(-1, new JrtLibraryInfo(runningJreHome()).compareTo(other));
	}

	@Test
	void testToString() {
		String str = new JrtLibraryInfo(runningJreHome()).toString();
		assertTrue(str.startsWith("[JrtLibraryInfo: jreHome="));
		assertTrue(str.contains("source=null"));
	}
}
