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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Reads entries from a Jar.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JarReader {

	private JarInfo info;
	private TreeMap packageMap;


	/**
	 * Constructor.
	 *
	 * @param info The jar file to read from.  If this is <code>null</code>,
	 *        then this instance will try to read from the current JVM's main
	 *        JRE jar (rt.jar, or classes.jar on OS X).  If the main JRE jar is
	 *        found, an attempt will also be made to locate its associated
	 *        source jar or zip file. 
	 * @throws IOException If an IO error occurs reading from the jar file.
	 */
	public JarReader(JarInfo info) throws IOException {
		if (info==null) {
			info = getMainJREJarInfo();
		}
		this.info = info;
		packageMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		loadCompletions();
	}


	/**
	 * Gets the completions in this jar that match a given string.
	 *
	 * @param provider The parent completion provider.
	 * @param text The text to match.  This should be (the start of) a
	 *        fully-qualified class, interface, or enum name.
	 * @param addTo The list to add completion choices to.
	 */
	public void addCompletions(CompletionProvider provider, String[] pkgNames,
								Set addTo) {

		TreeMap map = packageMap;
		for (int i=0; i<pkgNames.length-1; i++) {
			Object obj = map.get(pkgNames[i]);
			if (obj instanceof TreeMap) {
				map = (TreeMap)obj;
			}
			else {
				return;
			}
		}

		String fromKey = pkgNames[pkgNames.length-1];
		String toKey = fromKey + '{'; // Ascii char > largest valid class char
		SortedMap sm = map.subMap(fromKey, toKey);

		for (Iterator i=sm.keySet().iterator(); i.hasNext(); ) {

			Object obj = i.next();
			//System.out.println(obj + " - " + sm.get(obj));
			Object value = sm.get(obj);

			// See if this is a class, and we already have the ClassFile
			if (value instanceof ClassFile) {
				ClassFile cf = (ClassFile)value;
				boolean inPkg = false; // TODO: Pass me in
				if (inPkg || org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
					addTo.add(new ClassCompletion(provider, cf));
				}
			}

			// If a ClassFile isn't cached, it's either a class that hasn't
			// had its ClassFile cached yet, or a package.
			else {
				String[] items = new String[pkgNames.length];
				System.arraycopy(pkgNames, 0, items, 0, pkgNames.length-1);
				items[items.length-1] = obj.toString();
				ClassFile cf = getClassEntry(items);
				if (cf!=null) {
					boolean inPkg = false; // TODO: Pass me in
					if (inPkg || org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
						addTo.add(new ClassCompletion(provider, cf));
					}
				}
				else {
					StringBuffer sb = new StringBuffer();
					for (int j=0; j<pkgNames.length-1; j++) {
						sb.append(pkgNames[j]).append('.');
System.out.println("Appending: " + pkgNames[j]);
					}
					sb.append(obj.toString());
					String text = sb.toString();//obj.toString();
					addTo.add(new PackageNameCompletion(provider, text,
														fromKey));
				}
			}

		}

	}


	public boolean containsClass(String className) {

		String[] items = className.split("\\.");

		TreeMap m = packageMap;
		for (int i=0; i<items.length-1; i++) {
			// "value" can be a ClassFile "too early" here if className
			// is a nested class.
			// TODO: Handle nested classes better
			Object value = m.get(items[i]);
			if (!(value instanceof TreeMap)) {
				return false;
			}
			m = (TreeMap)value;
		}

		return m.containsKey(items[items.length-1]);

	}


	private ClassFile createClassFile(String entryName) throws IOException {
		JarFile jar = new JarFile(info.getJarFile());
		try {
			JarEntry entry = (JarEntry)jar.getEntry(entryName);
			if (entry==null) {
				System.err.println("ERROR: Invalid entry: " + entryName);
				return null;
			}
			DataInputStream in = new DataInputStream(jar.getInputStream(entry));
			ClassFile cf = new ClassFile(in);
			in.close();
			return cf;
		} finally {
			jar.close();
		}
	}


	public ClassFile getClassEntry(String[] items) {

		SortedMap map = packageMap;
		for (int i=0; i<items.length-1; i++) {
			if (map.containsKey(items[i])) {
				Object value = map.get(items[i]);
				if (!(value instanceof SortedMap)) {
					return null;
				}
				map = (SortedMap)value;
			}
			else {
				return null;
			}
		}

		String className = items[items.length-1];
		if (map.containsKey(className)) {
			Object value = map.get(className);
			if (value instanceof Map) { // i.e., it's a package
				return null;
			}
			else if (value instanceof ClassFile) { // Already created
				ClassFile cf = (ClassFile)value;
				return cf;
			}
			else { // A class, just no ClassFile cached yet
				try {
					StringBuffer name = new StringBuffer(items[0]);
					for (int i=1; i<items.length; i++) {
						name.append('/').append(items[i]);
					}
					name.append(".class");
					ClassFile cf = createClassFile(name.toString());
					map.put(className, cf);
					return cf;
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}

		return null;

	}


	public void getClassesInPackage(List addTo, String[] pkgs, boolean inPkg) {

		SortedMap map = packageMap;

		for (int i=0; i<pkgs.length; i++) {
			if (map.containsKey(pkgs[i])) {
				Object value = map.get(pkgs[i]);
				if (!(value instanceof SortedMap)) {
					// We have a class with the same name as a package...
					return;
				}
				map = (SortedMap)value;
			}
			else {
				return;
			}
		}

		// We can't modify map during our iteration, so we save any
		// newly-created ClassFiles.
		Map newClassFiles = null;

		for (Iterator i=map.entrySet().iterator(); i.hasNext(); ) {

			Map.Entry entry = (Map.Entry)i.next();
			Object value = entry.getValue();
			if (value==null) {
				StringBuffer name = new StringBuffer(pkgs[0]);
				for (int j=1; j<pkgs.length; j++) {
					name.append('/').append(pkgs[j]);
				}
				name.append('/');
				name.append((String)entry.getKey()).append(".class");
				try {
					ClassFile cf = createClassFile(name.toString());
					if (newClassFiles==null) {
						newClassFiles = new TreeMap();
					}
					newClassFiles.put(entry.getKey(), cf);
					possiblyAddTo(addTo, cf, inPkg);
				} catch (IOException ioe) {
					ioe.printStackTrace();
					break;
				}
			}
			else if (value instanceof ClassFile) {
				possiblyAddTo(addTo, (ClassFile)value, inPkg);
			}

		}

		if (newClassFiles!=null) {
			map.putAll(newClassFiles);
		}

	}


	/**
	 * Returns the physical file on disk.<p>
	 *
	 * Modifying the returned object will <em>not</em> have any effect on
	 * code completion; e.g. changing the source location will not have any
	 * effect.
	 *
	 * @return The info.
	 */
	public JarInfo getJarInfo() {
		return (JarInfo)info.clone();
	}


	private JarInfo getMainJREJarInfo() {

		JarInfo info = null;
		String javaHome = System.getProperty("java.home");

		File mainJar = new File(javaHome, "lib/rt.jar"); // Sun JRE's
		File sourceZip = null;

		if (mainJar.isFile()) { // Sun JRE's
			sourceZip = new File(javaHome, "src.zip");
			if (!sourceZip.isFile()) {
				// Might be a JRE inside a JDK
				sourceZip = new File(javaHome, "../src.zip");
			}
		}

		else { // Might be OS X
			mainJar = new File(javaHome, "../Classes/classes.jar");
			// ${java.home}/src.jar is the common location on OS X.
			sourceZip = new File(javaHome, "src.jar");
		}

		if (mainJar.isFile()) {
			info = new JarInfo(mainJar);
			if (sourceZip.isFile()) { // Make sure our last guess actually exists
				info.setSourceLocation(sourceZip);
			}
		}
		else {
			System.err.println("[ERROR]: Cannot locate JRE jar");
			mainJar = null;
		}

		return info;

	}


	public SortedMap getPackageEntry(String[] pkgs) {

		SortedMap map = packageMap;

		for (int i=0; i<pkgs.length; i++) {
			if (map.containsKey(pkgs[i])) {
				Object value = map.get(pkgs[i]);
				if (!(value instanceof SortedMap)) { // ClassFile or null
					return null;
				}
				map = (SortedMap)value;
			}
			else {
				return null;
			}
		}

		return map;

	}


	/**
	 * Loads all classes, interfaces, and enums from the jar.
	 *
	 * @throws IOException If an IO error occurs.
	 */
	private void loadCompletions() throws IOException {

		JarFile jar = new JarFile(info.getJarFile());

		try {

			Enumeration e = jar.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)e.nextElement();
				String entryName = entry.getName();
				if (entryName.endsWith(".class")) {
					entryName = entryName.substring(0, entryName.length()-6);
					String[] items = Util.splitOnChar(entryName, '/');
					TreeMap m = packageMap;
					for (int i=0; i<items.length-1; i++) {
						TreeMap submap = (TreeMap)m.get(items[i]);
						if (submap==null) {
							submap = new TreeMap();
							m.put(items[i], submap);
						}
						m = submap;
					}
					String className = items[items.length-1];
					m.put(className, null);
				}
			}

		} finally {
			jar.close();
		}

	}


	private void possiblyAddTo(Collection addTo, ClassFile cf, boolean inPkg) {
		if (inPkg || org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
			addTo.add(cf);
		}
	}


}