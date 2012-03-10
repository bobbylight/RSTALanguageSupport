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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Manages a list of jars and gets completions from them.  This can be shared
 * amongst multiple {@link JavaCompletionProvider} instances.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JarManager {

	/**
	 * Jars to get completions from.
	 */
	private List jars;

	/**
	 * Whether to check datestamps on jars/directories when completion
	 * information is requested.
	 */
	private static boolean checkModified;


	/**
	 * Constructor.
	 */
	public JarManager() {
		jars = new ArrayList();
		setCheckModifiedDatestamps(true);
	}


	/**
	 * Adds completions matching the specified text to a list.
	 *
	 * @param p The parent completion provider.
	 * @param text The text to match.
	 * @param addTo The list to add completion choices to.
	 */
	public void addCompletions(CompletionProvider p, String text, Set addTo) {
/*
 * The commented-out code below is probably replaced by the rest of the code
 * in this method...
TODO: Verify me!!!
 * 
		// Add any completions matching the text for each jar we know about
		String[] pkgNames = Util.splitOnChar(text, '.');
		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			jar.addCompletions(p, pkgNames, addTo);
		}
*/
		if (text.length()==0) {
			return;
		}

		// If what they've typed is qualified, add qualified completions.
		if (text.indexOf('.')>-1) {
			String[] pkgNames = Util.splitOnChar(text, '.');
			for (int i=0; i<jars.size(); i++) {
				JarReader jar = (JarReader)jars.get(i);
				jar.addCompletions(p, pkgNames, addTo);
			}
		}

		// If they are (possibly) typing an unqualified class name, see if
		// what they're typing matches any classes in any of jar jars, and if
		// so, add completions for them too.  This allows the user to get
		// completions for classes not in their import statements.
		// Thanks to Guilherme Joao Frantz and Jonatas Schuler for the patch!
		else {//if (text.indexOf('.')==-1) {
			String lowerCaseText = text.toLowerCase();
			for (int i=0; i<jars.size(); i++) {
				JarReader jar = (JarReader) jars.get(i);
				List classFiles = jar.getClassesWithNamesStartingWith(lowerCaseText);
				if (classFiles!=null) {
					for (Iterator j=classFiles.iterator(); j.hasNext(); ) {
						ClassFile cf = (ClassFile)j.next();
						if (org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
							addTo.add(new ClassCompletion(p, cf));
						}
					}
				}
			}
		}

	}


	/**
	 * Adds a jar to read from.
	 *
	 * @param info The jar to add.  If this is <code>null</code>, then
	 *        the current JVM's main JRE jar (rt.jar, or classes.jar on OS X)
	 *        will be added.  If this jar has already been added, adding it
	 *        again will do nothing (except possibly update its attached source
	 *        location).
	 * @return Whether this jar was added (e.g. it wasn't already loaded, or
	 *         it has a new source path).
	 * @throws IOException If an IO error occurs.
	 * @see #getJars()
	 * @see #removeJar(File)
	 */
	public boolean addJar(JarInfo info) throws IOException {

		// First see if this jar is already on the "build path."
		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			JarInfo info2 = jar.getJarInfo();
			if (info2.equals(info)) {
				// Only update if the source location is different.
				File source = info.getSourceLocation();
				File source2 = info2.getSourceLocation();
				if ((source==null && source2!=null) ||
						(source!=null && !source.equals(source2))) {
					jars.set(i, new JarReader((JarInfo)info.clone()));
					return true;
				}
				return false;
			}
		}

		if (info==null) {
			info = JarInfo.getMainJREJarInfo();
		}

		// If it isn't on the build path, add it now.
		jars.add(new JarReader(info));
		return true;

	}


	/**
	 * Removes all jars from the "build path."
	 *
	 * @see #removeJar(File)
	 * @see #addJar(JarInfo)
	 * @see #getJars()
	 */
	public void clearJars() {
		jars.clear();
	}


	/**
	 * Returns whether the "last modified" time stamp on jars and class
	 * directories should be checked whenever completions are requested, and
	 * if the jar/directory has been modified since the last time, reload any
	 * cached class file data.  This allows for code completion to update
	 * whenever dependencies are rebuilt, but has the side effect of increased
	 * file I/O.  By default this option is enabled; if you somehow find the
	 * file I/O to be a bottleneck (perhaps accessing jars over a slow NFS
	 * mount), you can disable this option.
	 *
	 * @return Whether jars/directories are checked for modification since
	 *         the last access, and clear any cached completion information if
	 *         so.
	 * @see #setCheckModifiedDatestamps(boolean)
	 */
	public static boolean getCheckModifiedDatestamps() {
		return checkModified;
	}


	public ClassFile getClassEntry(String className) {

		String[] items = Util.splitOnChar(className, '.');

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			ClassFile cf = jar.getClassEntry(items);
			if (cf!=null) {
				return cf;
			}
		}

		return null;

	}


	/**
	 * Returns a list of all classes/interfaces/enums with a given (unqualified)
	 * name.  There may be several, since the name is unqualified.
	 *
	 * @param name The unqualified name of a type declaration.
	 * @param importDeclarations The imports of the compilation unit, if any.
	 * @return A list of type declarations with the given name, or
	 *         <code>null</code> if there are none.
	 */
	public List getClassesWithUnqualifiedName(String name,
												List importDeclarations) {

		// Might be more than one class/interface/enum with the same name.
		List result = null;

		// Loop through all of our imports.
		for (int i=0; i<importDeclarations.size(); i++) {

			ImportDeclaration idec = (ImportDeclaration)importDeclarations.get(i);

			// Static imports are for fields/methods, not classes
			if (!idec.isStatic()) {

				// Wildcard => See if package contains a class with this name
				if (idec.isWildcard()) {

					String qualified = idec.getName();
					qualified = qualified.substring(0, qualified.indexOf('*'));
					qualified += name;
					ClassFile entry = getClassEntry(qualified);
					if (entry!=null) {
						if (result==null) {
							result = new ArrayList(1); // Usually small
						}
						result.add(entry);
					}

				}

				// Not wildcard => fully-qualified class/interface name
				else {
					String name2 = idec.getName();
					String unqualifiedName2 = name2.substring(name2.lastIndexOf('.')+1);
					if (unqualifiedName2.equals(name)) {
						ClassFile entry = getClassEntry(name2);
						if (entry!=null) { // Should always be true
							if (result==null) {
								result = new ArrayList(1); // Usually small
							}
							result.add(entry);
						}
						else {
							System.err.println("ERROR: Class not found! - " + name2);
						}
					}
				}

			}

		}

		// Also check java.lang
		String qualified = "java.lang." + name;
		ClassFile entry = getClassEntry(qualified);
		if (entry!=null) {
			if (result==null) {
				result = new ArrayList(1); // Usually small
			}
			result.add(entry);
		}

		return result;

	}


	/**
	 * 
	 * @param pkgName A package name.
	 * @return A list of all classes in that package.
	 */
	public List getClassesInPackage(String pkgName, boolean inPkg) {

		List list = new ArrayList();
		String[] pkgs = Util.splitOnChar(pkgName, '.');

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			jar.getClassesInPackage(list, pkgs, inPkg);
		}

		return list;

	}


	/**
	 * Returns the jars on the "build path."
	 *
	 * @return A list of {@link JarInfo}s.  Modifying a <tt>JarInfo</tt> in
	 *         this list will have no effect on this completion provider; in
	 *         order to do that, you must re-add the jar via
	 *         {@link #addJar(JarInfo)}. If there are no jars on the
	 *         "build path," this will be an empty list.
	 * @see #addJar(JarInfo)
	 */
	public List getJars() {
		List jarList = new ArrayList(jars.size());
		for (Iterator i=jars.iterator(); i.hasNext(); ) {
			JarReader reader = (JarReader)i.next();
			jarList.add(reader.getJarInfo().clone());
		}
		return jarList;
	}


	public SortedMap getPackageEntry(String pkgName) {

		String[] pkgs = Util.splitOnChar(pkgName, '.');

		SortedMap map = new TreeMap();

		for (int i=0; i<jars.size(); i++) {
			JarReader jar = (JarReader)jars.get(i);
			SortedMap map2 = jar.getPackageEntry(pkgs);
			if (map2!=null) {
				mergeMaps(map, map2);
			}
		}

		return map;

	}


public File getSourceLocForClass(String className) {
	File sourceLoc = null;
	for (int i=0; i<jars.size(); i++) {
		JarReader jar = (JarReader)jars.get(i);
		if (jar.containsClass(className)) {
			sourceLoc = jar.getJarInfo().getSourceLocation();
			break;
		}
	}
	return sourceLoc;
}


	private void mergeMaps(SortedMap map, SortedMap toAdd) {

		for (Iterator i=toAdd.entrySet().iterator(); i.hasNext(); ) {

			Map.Entry entry = (Map.Entry)i.next();
			Object key = entry.getKey();
			Object value = entry.getValue();

			if (map.containsKey(key)) {
				if ((map.get(key) instanceof SortedMap) &&
						(value instanceof SortedMap)) {
					mergeMaps((SortedMap)map.get(key), (SortedMap)value);
				}
			}

			else {
				map.put(key, value);
			}

		}

	}


	/**
	 * Removes a jar from the "build path."
	 *
	 * @param jar The jar to remove.
	 * @return Whether the jar was removed.  This will be <code>false</code>
	 *         if the jar was not on the build path.
	 * @see #addJar(JarInfo)
	 * @see #getJars()
	 */
	public boolean removeJar(File jar) {
		for (Iterator i=jars.iterator(); i.hasNext(); ) {
			JarReader reader = (JarReader)i.next();
			JarInfo info = reader.getJarInfo();
			File jar2 = info.getJarFile();
			if (jar.equals(jar2)) {
				i.remove();
				return true;
			}
		}
		return false;
	}


	/**
	 * Sets whether the "last modified" time stamp on jars and class
	 * directories should be checked whenever completions are requested, and
	 * if the jar/directory has been modified since the last time, reload any
	 * cached class file data.  This allows for code completion to update
	 * whenever dependencies are rebuilt, but has the side effect of increased
	 * file I/O.  By default this option is enabled; if you somehow find the
	 * file I/O to be a bottleneck (perhaps accessing jars over a slow NFS
	 * mount), you can disable this option.
	 *
	 * @param check Whether to check if any jars/directories have been
	 *        modified since the last access, and clear any cached completion
	 *        information if so.
	 * @see #getCheckModifiedDatestamps()
	 */
	public static void setCheckModifiedDatestamps(boolean check) {
		checkModified = check;
	}


}