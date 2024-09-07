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

import java.io.File;
import java.io.IOException;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;


/**
 * Information about a jar, compiled class folder, or other source of classes
 * to add to the "build path" for Java completion.  Instances of this class are
 * added to a {@link JarManager} for each library that should be on the build
 * path.<p>
 *
 * This class also keeps track of an optional source location, such as a zip
 * file or source folder.  If defined, this location is used to find the .java
 * source corresponding to the library's classes, which is used to display
 * Javadoc comments during code completion.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see DirLibraryInfo
 * @see JarLibraryInfo
 * @see ClasspathLibraryInfo
 */
public abstract class LibraryInfo implements Comparable<LibraryInfo>,
		Cloneable {

	/**
	 * The location of the source files corresponding to this library.  This
	 * may be <code>null</code>.
	 */
	private SourceLocation sourceLoc;


	/**
	 * Does any cleanup necessary after a call to
	 * {@link #bulkClassFileCreationStart()}.
	 *
	 * @throws IOException If an IO error occurs.
	 * @see #bulkClassFileCreationStart()
	 * @see #createClassFileBulk(String)
	 */
	public abstract void bulkClassFileCreationEnd() throws IOException;


	/**
	 * Readies this library for many class files being fetched via
	 * {@link #createClassFileBulk(String)}.  After calling this method,
	 * the actual class file fetching should be done in a try/finally block
	 * that ensures a call to {@link #bulkClassFileCreationEnd()}; e.g.
	 *
	 * <pre>
	 * libInfo.bulkClassFileCreationStart();
	 * try {
	 *    String entryName = ...;
	 *    ClassFile cf = createClassFileBulk(entryName);
	 *    ...
	 * } finally {
	 *    libInfo.bulkClassFileCreationEnd();
	 * }
	 * </pre>
	 *
	 * @throws IOException If an IO error occurs.
	 * @see #bulkClassFileCreationEnd()
	 * @see #createClassFileBulk(String)
	 */
	public abstract void bulkClassFileCreationStart() throws IOException;


	/**
	 * Returns a deep copy of this library.
	 *
	 * @return A deep copy.
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) { // Never happens
			throw new IllegalStateException(
			"Doesn't support cloning, but should! - " + getClass().getName());
		}
	}


	/**
	 * Returns the class file information for the specified class.  Instances
	 * of <code>JarReader</code> can call this method to lazily load
	 * information on individual classes and shove it into their package maps.
	 * <p>
	 * If many class files will be fetched at a time, you should prefer using
	 * {@link #bulkClassFileCreationStart()} and
	 * {@link #createClassFileBulk(String)} over this method, for performance
	 * reasons.
	 *
	 * @param entryName The fully qualified name of the class file.
	 * @return The class file, or <code>null</code> if it isn't found in this
	 *         library.
	 * @throws IOException If an IO error occurs.
	 * @see #createClassFileBulk(String)
	 */
	public abstract ClassFile createClassFile(String entryName) throws IOException;


	/**
	 * Returns the class file information for the specified class.  Instances
	 * of <code>JarReader</code> can call this method to lazily load
	 * information on individual classes and shove it into their package maps.
	 * <p>
	 * This method should be used when multiple classes will be fetched from
	 * this library at the same time.  It should only be called after a call to
	 * {@link #bulkClassFileCreationStart()}.  If only a single class file is
	 * being fetched, it is simpler to call {@link #createClassFile(String)}.
	 *
	 * @param entryName The fully qualified name of the class file.
	 * @return The class file, or <code>null</code> if it isn't found in this
	 *         library.
	 * @throws IOException If an IO error occurs.
	 * @see #createClassFile(String)
	 */
	public abstract ClassFile createClassFileBulk(String entryName)
			throws IOException;


	/**
	 * Creates and returns a map of maps representing the hierarchical package
	 * structure in this library.
	 *
	 * @return The package structure in this library.
	 * @throws IOException If an IO error occurs.
	 */
	public abstract PackageMapNode createPackageMap() throws IOException;


	/**
	 * Two <code>LibraryInfo</code>s are considered equal if they represent
	 * the same class file location.  Source attachment is irrelevant.
	 *
	 * @return Whether the specified instance represents the same class
	 *         source as this one.
	 */
	@Override
	public boolean equals(Object o) {
		return o instanceof LibraryInfo &&
				compareTo((LibraryInfo)o)==0;
	}



	/**
	 * Returns information on the "main" jar for a JRE. This will be
	 * <tt>rt.jar</tt> everywhere except OS X, where it will be
	 * <tt>classes.jar</tt>. The associated source zip/jar file is also
	 * checked for.
	 *
	 * <p>This method has been extended to handle JDK 9+ environments, which
	 * use <tt>jrt-fs.jar</tt> instead of <tt>rt.jar</tt>.</p>
	 *
	 * @param jreHome The location of the JRE.
	 * @return The information, or <code>null</code> if there is not a JRE in
	 *         the specified directory.
	 * @see #getMainJreJarInfo()
	 * possible due to assistance of kovadam69
	 * now method is handling java 9+ also.
	 */
	public static LibraryInfo getJreJarInfo(File jreHome) {

		LibraryInfo info = null;

		File mainJar = new File(jreHome, "lib/rt.jar"); // Sun JRE's
		File sourceZip;

		if (mainJar.isFile()) { // Sun JRE's
			sourceZip = new File(jreHome, "src.zip");
			if (!sourceZip.isFile()) {
				// Might be a JRE inside a JDK
				sourceZip = new File(jreHome, "../src.zip");
			}
		} else { // Might be OS X
			mainJar = new File(jreHome, "../Classes/classes.jar");
			// ${java.home}/src.jar is the common location on OS X.
			sourceZip = new File(jreHome, "src.jar");
		}

		//////////////////////////////////////////////////
		if (!mainJar.isFile()) { // Java 11 and later
			mainJar = new File(jreHome, "lib/jrt-fs.jar");
			sourceZip = new File(jreHome, "src.zip");
		}
		//////////////////////////////////////////////////

		if (mainJar.isFile()) {
			if (mainJar.getName().equals("jrt-fs.jar")) {
				// Handle JDK 9+ using JRT file system
				info = new JDK9ClasspathLibraryInfo();
			} else {
				// Handle pre-JDK 9 JARs
				info = new JarLibraryInfo(mainJar);
			}
			if (sourceZip.isFile()) { // Make sure our last guess actually exists
				info.setSourceLocation(new ZipSourceLocation(sourceZip));
			}
		} else {
			System.err.println("[ERROR]: Cannot locate JRE jar in " + jreHome.getAbsolutePath());
			mainJar = null;
		}

		return info;
	}



	/**
	 * Returns the time this library was last modified.  For jar files, this
	 * would be the modified date of the file.  For directories, this would be
	 * the time a file in the directory was most recently modified.  This
	 * information is used to determine whether callers should clear their
	 * cached package map information and load it anew.<p>
	 *
	 * This API may change in the future.
	 *
	 * @return The last time this library was modified.
	 */
	public abstract long getLastModified();


	/**
	 * Returns the location of this library, as a string.  If this library
	 * is contained in a single jar file, this will be the full path to that
	 * jar.  If it is a directory containing classes, it will be the full path
	 * of the directory.  Otherwise, this value will be <code>null</code>.
	 *
	 * @return The location of this library.
	 */
	public abstract String getLocationAsString();


	/**
	 * Returns information on the JRE running this application.  This will be
	 * <tt>rt.jar</tt> everywhere except OS X, where it will be
	 * <tt>classes.jar</tt>.  The associated source zip/jar file is also
	 * checked for.
	 *
	 * @return The information, or <code>null</code> if an error occurs.
	 * @see #getJreJarInfo(File)
	 */
	public static LibraryInfo getMainJreJarInfo() {
		String javaHome = System.getProperty("java.home");
		return getJreJarInfo(new File(javaHome));
	}


	/**
	 * Returns the location of the source corresponding to this library.
	 *
	 * @return The source for this library, or <code>null</code> if none.
	 * @see #setSourceLocation(SourceLocation)
	 */
	public SourceLocation getSourceLocation() {
		return sourceLoc;
	}


	@Override
	public int hashCode() {
		return hashCodeImpl();
	}


	/**
	 * Subclasses should override this method since {@link #equals(Object)} is
	 * overridden.  Instances of <code>LibraryInfo</code> aren't typically
	 * stored in maps, so the hash value isn't necessarily important to
	 * <code>RSTALanguageSupport</code>.
	 *
	 * @return The hash code for this library.
	 */
	public abstract int hashCodeImpl();


	/**
	 * Sets the location of the source corresponding to this library.
	 *
	 * @param sourceLoc The source location.  This may be <code>null</code>.
	 * @see #getSourceLocation()
	 */
	public void setSourceLocation(SourceLocation sourceLoc) {
		this.sourceLoc = sourceLoc;
	}


}
