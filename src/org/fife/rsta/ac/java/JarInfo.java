/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.io.File;


/**
 * Information about a jar to add to the "build path" for Java completion.
 * This includes both the jar itself, as well as its source location (e.g. a
 * zip file, jar file or directory).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JarInfo implements Comparable, Cloneable {

	private File jarFile;
	private File sourceLocation;


	/**
	 * Constructor.
	 *
	 * @param jarFile The jar file.  This cannot be <code>null</code>.
	 */
	public JarInfo(File jarFile) {
		setJarFile(jarFile);
	}


	/**
	 * {@inheritDoc}
	 */
	public Object clone() {
		JarInfo clone = null;
		try {
			clone = (JarInfo)super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(); // Never happens
		}
		return clone;
	}


	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o) {
		if (o==this) {
			return 0;
		}
		int result = -1;
		if (o instanceof JarInfo) {
			result = ((JarInfo)o).jarFile.compareTo(this.jarFile);
		}
		return result;
	}


	/**
	 * Returns whether this instance points to the same jar as another instance
	 * of <tt>JarInfo</tt>.  The source location is not taken into account
	 * for this comparison.
	 *
	 * @param obj Another <tt>JarInfo</tt>.
	 * @return Whether these two instances point to the same jar file.
	 */
	public boolean equals(Object obj) {
		if (obj==this) {
			return true;
		}
		return (obj instanceof JarInfo) &&
				((JarInfo)obj).jarFile.equals(jarFile);
	}


	/**
	 * Gets the jar file.
	 *
	 * @return The jar file.
	 * @see #setJarFile(File)
	 */
	public File getJarFile() {
		return jarFile;
	}


	/**
	 * Returns information on the "main" jar for a JRE.  This will be
	 * <tt>rt.jar</tt> everywhere except OS X, where it will be
	 * <tt>classes.jar</tt>.  The associated source zip/jar file is also
	 * checked for.
	 *
	 * @return The information, or <code>null</code> if there is not a JRE in
	 *         the specified directory.
	 * @see #getMainJREJarInfo()
	 */
	public static JarInfo getJREJarInfo(File jreHome) {

		JarInfo info = null;

		File mainJar = new File(jreHome, "lib/rt.jar"); // Sun JRE's
		File sourceZip = null;

		if (mainJar.isFile()) { // Sun JRE's
			sourceZip = new File(jreHome, "src.zip");
			if (!sourceZip.isFile()) {
				// Might be a JRE inside a JDK
				sourceZip = new File(jreHome, "../src.zip");
			}
		}

		else { // Might be OS X
			mainJar = new File(jreHome, "../Classes/classes.jar");
			// ${java.home}/src.jar is the common location on OS X.
			sourceZip = new File(jreHome, "src.jar");
		}

		if (mainJar.isFile()) {
			info = new JarInfo(mainJar);
			if (sourceZip.isFile()) { // Make sure our last guess actually exists
				info.setSourceLocation(sourceZip);
			}
		}
		else {
			System.err.println("[ERROR]: Cannot locate JRE jar in " +
								jreHome.getAbsolutePath());
			mainJar = null;
		}

		return info;

	}


	/**
	 * Returns information on the JRE running this application.  This will be
	 * <tt>rt.jar</tt> everywhere except OS X, where it will be
	 * <tt>classes.jar</tt>.  The associated source zip/jar file is also
	 * checked for.
	 *
	 * @return The information, or <code>null</code> if an error occurs.
	 * @see #getJREJarInfo(File)
	 */
	public static JarInfo getMainJREJarInfo() {
		String javaHome = System.getProperty("java.home");
		return getJREJarInfo(new File(javaHome));
	}


	/**
	 * Returns the source location for the jar file.
	 *
	 * @return The source location (an archive or directory), or
	 *         <code>null</code> if there is no attached source location.
	 * @see #setSourceLocation(File)
	 */
	public File getSourceLocation() {
		return sourceLocation;
	}


	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return jarFile.hashCode();
	}


	/**
	 * Sets the jar file location.
	 *
	 * @param jarFile The jar file location.  This cannot be <code>null</code>.
	 * @see #getJarFile()
	 */
	public void setJarFile(File jarFile) {
		if (jarFile==null || !jarFile.exists()) {
			String name = jarFile==null ? "null" : jarFile.getAbsolutePath();
			throw new IllegalArgumentException("Jar does not exist: " + name);
		}
		this.jarFile = jarFile;
	}


	/**
	 * Sets the location of the source for this jar file's classes.
	 *
	 * @param loc The source location.  This should be a zip file, a jar file,
	 *        or a directory.  This value may be <code>null</code>.
	 * @see #getSourceLocation()
	 */
	public void setSourceLocation(File loc) {
		sourceLocation = loc;
	}


	/**
	 * Returns a string representation of this jar information.  Useful for
	 * debugging.
	 *
	 * @return A string representation of this object.
	 */
	public String toString() {
		return "[JarInfo: " +
			"jar=" + getJarFile().getAbsolutePath() +
			"; source=" + getSourceLocation().getAbsolutePath() +
			"]";
	}


}