/*
 * 04/21/2012
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.bsh.buildpath;

import java.io.File;
import java.io.IOException;

import org.fife.rsta.ac.bsh.PackageMapNode;
import org.fife.rsta.ac.bsh.classreader.ClassFile;


/**
 * Information about a folder containing a set of classes to add to the "build
 * path."  This type of library info could be used, for example, to add sibling
 * projects in a workspace, not yet built into jars, to another project's build
 * path.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see JarLibraryInfo
 * @see ClasspathLibraryInfo
 */
public class DirLibraryInfo extends LibraryInfo {

	private File dir;


	public DirLibraryInfo(File dir) {
		this(dir, null);
	}


	public DirLibraryInfo(String dir) {
		this(new File(dir));
	}


	public DirLibraryInfo(File dir, SourceLocation sourceLoc) {
		setDirectory(dir);
		setSourceLocation(sourceLoc);
	}


	public DirLibraryInfo(String dir, SourceLocation sourceLoc) {
		this(new File(dir), sourceLoc);
	}


	@Override
	public void bulkClassFileCreationEnd() {
		// Do nothing
	}


	@Override
	public void bulkClassFileCreationStart() {
		// Do nothing
	}


	/**
	 * Compares this <code>LibraryInfo</code> to another one.  Two instances of
	 * this class are only considered equal if they represent the same class
	 * file location.  Source attachment is irrelevant.
	 *
	 * @return The sort order of these two library infos.
	 */
	public int compareTo(LibraryInfo info) {
		if (info==this) {
			return 0;
		}
		int result = -1;
		if (info instanceof DirLibraryInfo) {
			return dir.compareTo(((DirLibraryInfo)info).dir);
		}
		return result;
	}


	@Override
	public ClassFile createClassFile(String entryName) throws IOException {
		return createClassFileBulk(entryName);
	}


	@Override
	public ClassFile createClassFileBulk(String entryName) throws IOException {
		File file = new File(dir, entryName);
		if (!file.isFile()) {
			System.err.println("ERROR: Invalid class file: " + file.getAbsolutePath());
			return null;
		}
		return new ClassFile(file);
	}


	@Override
	public PackageMapNode createPackageMap() throws IOException {
		PackageMapNode root = new PackageMapNode();
		getPackageMapImpl(dir, null, root);
		return root;
	}


	@Override
	public long getLastModified() {
		return dir.lastModified();
	}


	@Override
	public String getLocationAsString() {
		return dir.getAbsolutePath();
	}


	/**
	 * Does the dirty-work of finding all class files in a directory tree.
	 *
	 * @param dir The directory to scan.
	 * @param pkg The package name scanned so far, in the form
	 *        "<code>com/company/pkgname</code>"...
	 * @throws IOException If an IO error occurs.
	 */
	private void getPackageMapImpl(File dir, String pkg, PackageMapNode root)
			throws IOException {

		File[] children = dir.listFiles();

		for (int i=0; i<children.length; i++) {
			File child = children[i];
			if (child.isFile() && child.getName().endsWith(".class")) {
				if (pkg!=null) { // will be null the first time through
					// TODO: Split pkg here to prevent repeated splits
					// for performance
					root.add(pkg + "/" + child.getName());
				}
				else {
					root.add(child.getName());
				}
			}
			else if (child.isDirectory()) {
				String subpkg = pkg==null ? child.getName() :
										(pkg + "/" + child.getName());
				getPackageMapImpl(child, subpkg, root);
			}
		}

	}


	@Override
	public int hashCode() {
		return dir.hashCode();
	}


	/**
	 * Sets the directory containing the classes.
	 *
	 * @param dir The directory.  This cannot be <code>null</code>.
	 */
	private void setDirectory(File dir) {
		if (dir==null || !dir.isDirectory()) {
			String name = dir==null ? "null" : dir.getAbsolutePath();
			throw new IllegalArgumentException("Directory does not exist: " + name);
		}
		this.dir = dir;
	}


	/**
	 * Returns a string representation of this jar information.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[DirLibraryInfo: " +
			"jar=" + dir.getAbsolutePath() +
			"; source=" + getSourceLocation() +
			"]";
	}


}