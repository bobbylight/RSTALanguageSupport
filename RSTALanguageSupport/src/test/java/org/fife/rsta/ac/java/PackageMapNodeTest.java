/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@code PackageMapNode} class.
 */
class PackageMapNodeTest {

	@Test
	void testAdd_classInPackage() {
		PackageMapNode root = new PackageMapNode();
		root.add("org/fife/Foo.class");

		assertTrue(root.containsPackage("org.fife"));
		assertTrue(root.containsClass("org.fife.Foo"));
	}

	@Test
	void testAdd_classInDefaultPackage() {
		PackageMapNode root = new PackageMapNode();
		root.add("Foo.class");

		assertTrue(root.containsClass("Foo"));
	}

	@Test
	void testAdd_moduleInfo_ignored() {
		PackageMapNode root = new PackageMapNode();
		root.add("module-info.class");

		assertFalse(root.containsClass("module-info"));
	}

	@Test
	void testAdd_versionedModuleInfo_ignored() {
		PackageMapNode root = new PackageMapNode();
		root.add("META-INF/versions/9/module-info.class");

		assertFalse(root.containsClass("META-INF.versions.9.module-info"));
		// Nothing, including intermediate packages, should have been added
		assertFalse(root.containsPackage("META-INF"));
	}

	@Test
	void testAdd_classNamedLikeModuleInfoPrefix_notIgnored() {
		PackageMapNode root = new PackageMapNode();
		root.add("org/fife/module-info-helper.class");

		assertTrue(root.containsClass("org.fife.module-info-helper"));
	}
}
