/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.buildpath;

import org.fife.rsta.ac.java.PackageMapNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code LibraryInfo} class, specifically how it picks an
 * implementation for a runtime's classes.
 */
class LibraryInfoTest {

	@TempDir
	Path tempDir;

	private File touch(String relativePath) throws IOException {
		Path file = tempDir.resolve(relativePath);
		Files.createDirectories(file.getParent());
		Files.write(file, new byte[0]);
		return file.toFile();
	}

	@Test
	void testGetJreJarInfo_runtimeImageWithJmods_usesJrtLibraryInfo() throws IOException {
		touch("lib/modules");
		touch("jmods/java.base.jmod");

		LibraryInfo info = LibraryInfo.getJreJarInfo(tempDir.toFile());

		assertInstanceOf(JrtLibraryInfo.class, info);
	}


	@Test
	void testGetJreJarInfo_runtimeImage_usesJrtLibraryInfo() throws IOException {
		touch("lib/modules");

		LibraryInfo info = LibraryInfo.getJreJarInfo(tempDir.toFile());

		assertInstanceOf(JrtLibraryInfo.class, info);
		assertEquals(tempDir.toFile(), ((JrtLibraryInfo)info).getJreHome());
		assertNull(info.getSourceLocation());
	}

	@Test
	void testGetJreJarInfo_runtimeImage_attachesSourceZip() throws IOException {
		touch("lib/modules");
		touch("lib/src.zip");

		LibraryInfo info = LibraryInfo.getJreJarInfo(tempDir.toFile());

		assertInstanceOf(JrtLibraryInfo.class, info);
		assertInstanceOf(ZipSourceLocation.class, info.getSourceLocation());
	}

	@Test
	void testGetJreJarInfo_legacyRtJar_usesJarLibraryInfo() throws IOException {
		touch("lib/rt.jar");

		LibraryInfo info = LibraryInfo.getJreJarInfo(tempDir.toFile());

		assertInstanceOf(JarLibraryInfo.class, info);
	}

	// jmods are not needed or used; the runtime image is what's read
	@Test
	void testGetJreJarInfo_jmodsWithoutRuntimeImage_returnsNull() throws IOException {
		touch("jmods/java.base.jmod");

		assertNull(LibraryInfo.getJreJarInfo(tempDir.toFile()));
	}

	@Test
	void testGetJreJarInfo_nothingRecognizable_returnsNull() {
		assertNull(LibraryInfo.getJreJarInfo(tempDir.toFile()));
	}

	@Test
	void testGetMainJreJarInfo_providesJdkClasses() throws IOException {
		LibraryInfo info = LibraryInfo.getMainJreJarInfo();

		assertNotNull(info);
		PackageMapNode map = info.createPackageMap();
		assertTrue(map.containsClass("java.lang.String"));
		assertNotNull(info.createClassFile("java/lang/String.class"));
	}
}
