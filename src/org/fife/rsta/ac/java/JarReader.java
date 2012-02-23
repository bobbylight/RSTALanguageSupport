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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

	/**
	 * Information about the jar or directory we're reading classes from.
	 */
	private JarInfo info;

	/**
	 * This is essentially a tree model of all classes in the jar or
	 * directory.  It's a recursive mapping of <code>String</code>s to either
	 * <code>Map</code>s or {@link ClassFile}s (which are lazily created and
	 * may be <code>null</code>).  At each level of the nested map, the string
	 * key is a package name iff its corresponding value is a <code>Map</code>.
	 * Examine that <code>Map</code>'s contents to explore the contents of
	 * that package.  If the corresponding value is a <code>ClassFile</code>,
	 * then the string key's value is the name of that class.  Finally, if
	 * the corresponding value is <code>null</code>, then the string key's
	 * value is the name of a class, but its contents have not yet been
	 * loaded for use by the code completion library (<code>ClassFile</code>s
	 * are lazily loaded to conserve memory).
	 */
	private TreeMap packageMap;

	private long lastModified;


	/**
	 * Constructor.
	 *
	 * @param info The jar file to read from.  This cannot be <code>null</code>.
	 * @throws IOException If an IO error occurs reading from the jar file.
	 */
	public JarReader(JarInfo info) throws IOException {
		this.info = info;
		packageMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		loadCompletions();
	}


	/**
	 * Gets the completions in this jar that match a given string.
	 *
	 * @param provider The parent completion provider.
	 * @param pkgNames The text to match, split into tokens around the
	 *        '<code>.</code>' character.  This should be (the start of) a
	 *        fully-qualified class, interface, or enum name.
	 * @param addTo The list to add completion choices to.
	 */
	public void addCompletions(CompletionProvider provider, String[] pkgNames,
								Set addTo) {

		checkLastModified();

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
					}
					sb.append(obj.toString());
					String text = sb.toString();//obj.toString();
					addTo.add(new PackageNameCompletion(provider, text,
														fromKey));
				}
			}

		}

	}


	/**
	 * Checks whether the jar or class file directory has been modified since
	 * the last use of this reader.  If it has, then any cached
	 * <code>ClassFile</code>s are cleared, in case any classes have been
	 * updated.
	 */
	private void checkLastModified() {

		long newLastModified = info.getJarFile().lastModified();
		if (newLastModified!=0 && newLastModified!=lastModified) {
			int count = 0;
			count = clearClassFiles(packageMap);
			System.out.println("DEBUG: Cleared " + count + " cached ClassFiles");
			lastModified = newLastModified;
		}

	}


	/**
	 * Removes all <code>ClassFile</code>s from a map.
	 *
	 * @param map The map.
	 * @return The number of class file entries removed.
	 */
	private int clearClassFiles(Map map) {

		int clearedCount = 0;

		for (Iterator i=map.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry entry = (Map.Entry)i.next();
			Object value = entry.getValue();
			if (value instanceof ClassFile) {
				entry.setValue(null);
				clearedCount++;
			}
			else if (value instanceof Map) {
				clearedCount += clearClassFiles((Map)value);
			}
		}

		return clearedCount;

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


	public boolean containsPackage(String pkgName) {

		String[] items = Util.splitOnChar(pkgName, '.');

		TreeMap m = packageMap;
		for (int i=0; i<items.length; i++) {
			// "value" can be a ClassFile "too early" here if className
			// is a nested class.
			// TODO: Handle nested classes better
			Object value = m.get(items[i]);
			if (!(value instanceof TreeMap)) {
				return false;
			}
			m = (TreeMap)value;
		}

		return true;

	}


	private ClassFile createClassFile(String entryName) throws IOException {
		File binLoc = info.getJarFile();
		if (binLoc.isFile()) {
			return createClassFileJar(entryName);
		}
		else if (binLoc.isDirectory()) {
			return createClassFileDirectory(entryName);
		}
		return null;
	}


	private ClassFile createClassFileDirectory(String entryName)
														throws IOException {

		File file = new File(info.getJarFile(), entryName);
		if (!file.isFile()) {
			System.err.println("ERROR: Invalid class file: " +
											file.getAbsolutePath());
			return null;
		}

		return new ClassFile(file);

	}


	private ClassFile createClassFileJar(String entryName) throws IOException {
		JarFile jar = new JarFile(info.getJarFile());
		try {
			JarEntry entry = (JarEntry)jar.getEntry(entryName);
			if (entry==null) {
				System.err.println("ERROR: Invalid entry: " + entryName);
				return null;
			}
			DataInputStream in = new DataInputStream(
					new BufferedInputStream(jar.getInputStream(entry)));
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
	 * Looks through all classes in this jar or directory, trying to find any
	 * whose unqualified names start with a given prefix.
	 *
	 * @param prefix The prefix of the class names.  Case is ignored on this
	 *        parameter.
	 * @return A list of {@link ClassFile}s representing classes in this
	 *         jar or directory whose unqualified names start with the prefix.
	 *         This will never be <code>null</code>, but may of course be
	 *         empty.
	 */
	public List getClassesWithNamesStartingWith(String prefix) {
		List res = new ArrayList();
		String currentPkg = ""; // Don't use null; we're appending to it
		getClassesWithNamesStartingWithImpl(prefix, packageMap, currentPkg,
												res);
		return res;
	}


	/**
	 * Method used to recursively scan our package map for classes whose names
	 * start with a given prefix, ignoring case.
	 *
	 * @param prefix The prefix that the unqualified class names must match
	 *        (ignoring case). 
	 * @param map A piece of our package map.
	 * @param currentPkg The package that <code>map</code> belongs to (i.e.
	 *        all levels of packages scanned before this one), separated by
	 *        '<code>/</code>'.
	 * @param addTo The list to add any matching <code>ClassFile</code>s to.
	 */
	private void getClassesWithNamesStartingWithImpl(String prefix, Map map,
											String currentPkg, List addTo) {

		final int prefixLen = prefix.length();

		// Loop through the map's entries, which are String keys mapping to
		// one of a Map (if the key is a package name), a ClassFile (if the
		// key is a class name), or null (if the key is a class name, but the
		// corresponding ClassFile has not been loaded yet).
		for (Iterator i=map.entrySet().iterator(); i.hasNext(); ) {

			Map.Entry entry = (Map.Entry)i.next();
			String key = (String)entry.getKey();
			Object value = entry.getValue();

			if (value instanceof Map) {
				getClassesWithNamesStartingWithImpl(prefix, (Map)value,
										currentPkg + key + "/", addTo);
			}
			else { // value is either a ClassFile or null
				// If value is null, we only lazily create the ClassFile if
				// necessary (i.e. if the class name does match what they've
				// typed).
				String className = key;
				if (className.regionMatches(true, 0, prefix, 0, prefixLen)) {
					if (value==null) {
						String fqClassName = currentPkg + className + ".class";
						try {
							value = createClassFile(fqClassName);
							entry.setValue(value); // Update the map
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
					if (value!=null) { // possibly null if IOException above
						addTo.add(/*(ClassFile)*/value);
					}
				}
			}

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


	private void loadCompletions() throws IOException {
		File binLoc = info.getJarFile();
		// Check explicitly for directory and file.  If the specified
		// location does not exist, we don't want to give an error, just
		// do nothing.
		if (binLoc.isDirectory()) {
			loadCompletionsDirectory();
		}
		else if (binLoc.isFile()) {
			loadCompletionsJarFile();
		}
	}


	/**
	 * Loads all classes, enums, and interfaces from a directory of class
	 * files.
	 *
	 * @throws IOException If an IO error occurs.
	 */
	private void loadCompletionsDirectory() throws IOException {
		File root = info.getJarFile();
		loadCompletionsDirectoryImpl(root, null);
		lastModified = root.lastModified();
	}


	/**
	 * Does the dirty-work of finding all class files in a directory tree.
	 *
	 * @param dir The directory to scan.
	 * @param pkg The package name scanned so far, in the form
	 *        "<code>com/company/pkgname</code>"...
	 * @throws IOException If an IO error occurs.
	 */
	private void loadCompletionsDirectoryImpl(File dir, String pkg)
											throws IOException {

		File[] children = dir.listFiles();
		TreeMap m = packageMap;
		boolean firstTimeThrough = true;

		for (int i=0; i<children.length; i++) {
			File child = children[i];
			if (child.isFile() && child.getName().endsWith(".class")) {
				if (pkg!=null) { // will be null the first time through
					if (firstTimeThrough) { // Lazily drill down to pkg map node
						firstTimeThrough = false;
						String[] items = Util.splitOnChar(pkg, '/');
						for (int j=0; j<items.length; j++) {
							Object temp = m.get(items[j]);
							if (temp instanceof TreeMap) {
								m = (TreeMap)temp;
							}
							else if (temp==null) {
								TreeMap submap = new TreeMap();
								m.put(items[j], submap);
								m = submap;
							}
							else { // e.g. a ClassFile
								// A class with the same name as a package
								// name - very unlikely, but could happen.  In
								// this case, all peer classes/directories will
								// share this package/class name conflict, so
								// might as well bail now.
								return;
							}
						}
					}
				}
				String className = child.getName().
								substring(0, child.getName().length()-6);
				m.put(className, null);
			}
			else if (child.isDirectory()) {
				String subpkg = pkg==null ? child.getName() :
										(pkg + "/" + child.getName());
				loadCompletionsDirectoryImpl(child, subpkg);
			}
		}

	}


	/**
	 * Loads all classes, interfaces, and enums from the jar.
	 *
	 * @throws IOException If an IO error occurs.
	 */
	private void loadCompletionsJarFile() throws IOException {

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
					m.put(className, null); // Lazily set value to ClassFile later
				}
			}

		} finally {
			jar.close();
		}

		lastModified = info.getJarFile().lastModified();

	}


	private void possiblyAddTo(Collection addTo, ClassFile cf, boolean inPkg) {
		if (inPkg || org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
			addTo.add(cf);
		}
	}


}