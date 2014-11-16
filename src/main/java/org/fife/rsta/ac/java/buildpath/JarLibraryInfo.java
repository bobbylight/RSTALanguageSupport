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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.ClassFile;


/**
 * Information about a jar of classes to add to the "build path."
 *
 * @author Robert Futrell
 * @version 1.0
 * @see DirLibraryInfo
 * @see ClasspathLibraryInfo
 */
public class JarLibraryInfo extends LibraryInfo {

	private File jarFile;
	private JarFile bulkCreateJar;


	public JarLibraryInfo(String jarFile) {
		this(new File(jarFile));
	}


	public JarLibraryInfo(File jarFile) {
		this(jarFile, null);
	}


	public JarLibraryInfo(File jarFile, SourceLocation sourceLoc) {
		setJarFile(jarFile);
		setSourceLocation(sourceLoc);
	}


	@Override
	public void bulkClassFileCreationEnd() {
		try {
			bulkCreateJar.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	@Override
	public void bulkClassFileCreationStart() {
		try {
			bulkCreateJar = new JarFile(jarFile);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	/**
	 * Compares this <code>LibraryInfo</code> to another one.  Two instances of
	 * this class are only considered equal if they represent the same class
	 * file location.  Source attachment is irrelevant.
	 *
	 * @return The sort order of these two library infos.
	 */
	public int compareTo(Object o) {
		if (o==this) {
			return 0;
		}
		int result = -1;
		if (o instanceof JarLibraryInfo) {
			result = jarFile.compareTo(((JarLibraryInfo)o).jarFile);
		}
		return result;
	}


	@Override
	public ClassFile createClassFile(String entryName) throws IOException {
		JarFile jar = new JarFile(jarFile);
		try {
			return createClassFileImpl(jar, entryName);
		} finally {
			jar.close();
		}
	}


	@Override
	public ClassFile createClassFileBulk(String entryName) throws IOException {
		return createClassFileImpl(bulkCreateJar, entryName);
	}


	private static final ClassFile createClassFileImpl(JarFile jar,
			String entryName) throws IOException {
		JarEntry entry = (JarEntry)jar.getEntry(entryName);
		if (entry==null) {
			System.err.println("ERROR: Invalid entry: " + entryName);
			return null;
		}
		DataInputStream in = new DataInputStream(
				new BufferedInputStream(jar.getInputStream(entry)));
		ClassFile cf = null;
		try {
			cf = new ClassFile(in);
		} finally {
			in.close();
		}
		return cf;
	}


	@Override
	public TreeMap createPackageMap() throws IOException {

		TreeMap packageMap = new TreeMap();
		JarFile jar = new JarFile(jarFile);

		try {

			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".class")) {
					entryName = entryName.substring(0, entryName.length()-6);
					String[] items = Util.splitOnChar(entryName, '/');
					Map m = packageMap;
					for (int i=0; i<items.length-1; i++) {
						TreeMap submap = (TreeMap)m.get(items[i]);
						if (submap==null) {
							submap = new TreeMap();
							m.put(items[i], submap);
						}
						m = submap;
					}
					String className = items[items.length-1];
					m.put(className, null); // Lazily set value to ClassFile later
				}
			}

		} finally {
			jar.close();
		}

		return packageMap;

	}


	@Override
	public long getLastModified() {
		return jarFile.lastModified();
	}


	@Override
	public String getLocationAsString() {
		return jarFile.getAbsolutePath();
	}


	/**
	 * Returns the jar file this instance is wrapping.
	 *
	 * @return The jar file.
	 */
	public File getJarFile() {
		return jarFile;
	}


	@Override
	public int hashCode() {
		return jarFile.hashCode();
	}


	/**
	 * Sets the jar file location.
	 *
	 * @param jarFile The jar file location.  This cannot be <code>null</code>.
	 */
	private void setJarFile(File jarFile) {
		if (jarFile==null || !jarFile.exists()) {
			String name = jarFile==null ? "null" : jarFile.getAbsolutePath();
			throw new IllegalArgumentException("Jar does not exist: " + name);
		}
		this.jarFile = jarFile;
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
			"jar=" + jarFile.getAbsolutePath() +
			"; source=" + getSourceLocation() +
			"]";
	}


}