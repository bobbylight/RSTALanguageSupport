/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Reads entries from a source of class files, such as a jar or a "bin/"
 * directory.  This class acts as an intermediary between a raw
 * <code>LibraryInfo</code> and the higher level Java completion classes.
 * It caches information about classes and refreshes that cache when
 * appropriate.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JarReader {

	/**
	 * Information about the jar or directory we're reading classes from.
	 */
	private LibraryInfo info;

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
	public JarReader(LibraryInfo info) throws IOException {
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
					StringBuilder sb = new StringBuilder();
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
		long newLastModified = info.getLastModified();
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
					StringBuilder name = new StringBuilder(items[0]);
					for (int i=1; i<items.length; i++) {
						name.append('/').append(items[i]);
					}
					name.append(".class");
					ClassFile cf = info.createClassFile(name.toString());
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
				StringBuilder name = new StringBuilder(pkgs[0]);
				for (int j=1; j<pkgs.length; j++) {
					name.append('/').append(pkgs[j]);
				}
				name.append('/');
				name.append((String)entry.getKey()).append(".class");
				try {
					ClassFile cf = info.createClassFile(name.toString());
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
							value = info.createClassFile(fqClassName);
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
	public LibraryInfo getLibraryInfo() {
		return (LibraryInfo)info.clone();
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
		packageMap = info.createPackageMap();
		lastModified = info.getLastModified();
	}


	private void possiblyAddTo(Collection addTo, ClassFile cf, boolean inPkg) {
		if (inPkg || org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
			addTo.add(cf);
		}
	}


}