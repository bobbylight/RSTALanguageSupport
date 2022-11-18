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
package org.fife.rsta.ac.java.buildpath;

import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


/**
 * Information about the JDK 9+ runtime classes to add to the "build path".
 *
 * Note: this introspects the modules delivered as prt of the JDJ, in jmods.
 * A more complete implementation could look into the ct.sym file and rely on
 * a JDK version to find the proper signatures:
 *   https://www.morling.dev/blog/the-anatomy-of-ct-sym-how-javac-ensures-backwards-compatibility/
 *
 *
 * @author Robert Futrell
 * @author Philippe Riand
 * @version 1.0
 * @see DirLibraryInfo
 * @see ClasspathLibraryInfo
 * @see JarLibraryInfo
 */
public class Jdk9LibraryInfo extends LibraryInfo {

	private File[] jmodFiles;
	private JarFile[] bulkCreateJmods;

	public Jdk9LibraryInfo(File[] jmodFiles) {
		this(jmodFiles, null);
	}


	public Jdk9LibraryInfo(File[] jmodFiles, SourceLocation sourceLoc) {
		setJmodFiles(jmodFiles);
		setSourceLocation(sourceLoc);
	}


	@Override
	public void bulkClassFileCreationEnd() {
		for( JarFile bulkCreateJar: bulkCreateJmods) {
			try {
				bulkCreateJar.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		bulkCreateJmods = null;
	}


	@Override
	public void bulkClassFileCreationStart() {
		bulkCreateJmods = new JarFile[jmodFiles.length];
		for(int i = 0; i< jmodFiles.length; i++) {
			File jarFile = jmodFiles[i];
			try {
				bulkCreateJmods[i] = new JarFile(jarFile);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}


	/**
	 * Compares this <code>LibraryInfo</code> to another one.  Two instances of
	 * this class are only considered equal if they represent the same class
	 * file location.  Source attachment is irrelevant.
	 *
	 * @return The sort order of these two library infos.
	 */
	@Override
	public int compareTo(LibraryInfo info) {
		if (info==this) {
			return 0;
		}
		int result = -1;
		if (info instanceof Jdk9LibraryInfo) {
			// Only compare the object refs
			result = this==info ? 0 : -1;
		}
		return result;
	}


	@Override
	public ClassFile createClassFile(String entryName) throws IOException {
		for(File jarFile: jmodFiles) {
			try (JarFile jar = new JarFile(jarFile)) {
				ClassFile c = createClassFileImpl(jar, entryName);
				if(c!=null) {
					return c;
				}
			}
		}
		System.err.println("ERROR: Invalid entry: " + entryName);
		return null;
	}


	@Override
	public ClassFile createClassFileBulk(String entryName) throws IOException {
		for( JarFile bulkCreateJar: bulkCreateJmods) {
			ClassFile c = createClassFileImpl(bulkCreateJar, entryName);
			if(c!=null) {
				return c;
			}
		}
		System.err.println("ERROR: Invalid entry: " + entryName);
		return null;
	}


	private static ClassFile createClassFileImpl(JarFile jar,
			String entryName) throws IOException {
		JarEntry entry = (JarEntry)jar.getEntry("classes/"+entryName);
		if (entry==null) {
			return null;
		}
		DataInputStream in = new DataInputStream(
				new BufferedInputStream(jar.getInputStream(entry)));
		ClassFile cf;
		try {
			cf = new ClassFile(in);
		} finally {
			in.close();
		}
		return cf;
	}


	@Override
	public PackageMapNode createPackageMap() throws IOException {
		PackageMapNode root = new PackageMapNode();

		for( File jarFile: jmodFiles) {
			try (JarFile jar = new JarFile(jarFile)) {

				Enumeration<JarEntry> e = jar.entries();
				while (e.hasMoreElements()) {
					ZipEntry entry = e.nextElement();
					String entryName = entry.getName();
					if(entryName.startsWith("classes/")) {
						entryName = entryName.substring(8);
						if (entryName.endsWith(".class")) {
							root.add(entryName);
						}
					}
				}

			}
		}

		return root;

	}


	@Override
	public long getLastModified() {
		return 0; //jarFile.lastModified();
	}


	@Override
	public String getLocationAsString() {
		return ""; //jarFile.getAbsolutePath();
	}


	@Override
	public int hashCodeImpl() {
		int h = 0;
		for( File jarFile: jmodFiles) {
			h += jarFile.hashCode();
		}
		return h;
	}


	/**
	 * Sets the jar file location.
	 *
	 * @param jmodFiles The jar files location.  This cannot be <code>null</code>.
	 */
	private void setJmodFiles(File[] jmodFiles) {
		for( File jarFile: jmodFiles) {
			if (jarFile==null || !jarFile.exists()) {
				String name = jarFile==null ? "null" : jarFile.getAbsolutePath();
				throw new IllegalArgumentException("Jar does not exist: " + name);
			}
		}
		this.jmodFiles = jmodFiles;
	}


	/**
	 * Returns a string representation of this jar information.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[JarLibraryInfo: " +
			"jars=" + jmodFiles.toString() +
			"; source=" + getSourceLocation() +
			"]";
	}


}
