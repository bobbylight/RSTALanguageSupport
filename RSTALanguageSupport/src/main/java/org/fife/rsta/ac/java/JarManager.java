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
import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fife.rsta.ac.java.buildpath.JarLibraryInfo;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Manages a list of jars and gets completions from them.  This can be shared
 * amongst multiple {@link JavaCompletionProvider} instances.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JarManager {

	private static final Logger LOG =
		System.getLogger(JarManager.class.getName());

	/**
	 * Locations of class files to get completions from.
	 */
	private List<JarReader> classFileSources;

	/**
	 * Whether to check datestamps on jars/directories when completion
	 * information is requested.
	 */
	private static boolean checkModified;


	/**
	 * Constructor.
	 */
	public JarManager() {
		classFileSources = new ArrayList<>();
		setCheckModifiedDatestamps(true);
	}


	/**
	 * Adds completions matching the specified text to a list.
	 *
	 * @param p The parent completion provider.
	 * @param text The text to match.
	 * @param addTo The list to add completion choices to.
	 */
	public void addCompletions(CompletionProvider p, String text,
			Set<Completion> addTo) {
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
            for (JarReader jar : classFileSources) {
                jar.addCompletions(p, pkgNames, addTo);
            }
		}

		// If they are (possibly) typing an unqualified class name, see if
		// what they're typing matches any classes in any of our jars, and if
		// so, add completions for them too.  This allows the user to get
		// completions for classes not in their import statements.
		// Thanks to Guilherme Joao Frantz and Jonatas Schuler for the patch!
		else {//if (text.indexOf('.')==-1) {
			String lowerCaseText = text.toLowerCase();
            for (JarReader jar : classFileSources) {
                List<ClassFile> classFiles = jar.
                        getClassesWithNamesStartingWith(lowerCaseText);
                if (classFiles != null) {
                    for (ClassFile cf : classFiles) {
                        if (org.fife.rsta.ac.java.classreader.Util.isPublic(cf.getAccessFlags())) {
                            addTo.add(new ClassCompletion(p, cf));
                        }
                    }
                }
            }
		}

	}


	/**
	 * Adds a jar to read from.  This is a convenience method for folks only
	 * reading classes from jar files.
	 *
	 * @param jarFile The jar to add.  This cannot be <code>null</code>.
	 * @return Whether this jar was added (e.g. it wasn't already loaded, or
	 *         it has a new source path).
	 * @throws IOException If an IO error occurs.
	 * @see #addClassFileSource(LibraryInfo)
	 * @see #addCurrentJreClassFileSource()
	 * @see #getClassFileSources()
	 * @see #removeClassFileSource(File)
	 */
	public boolean addClassFileSource(File jarFile) throws IOException {
		if (jarFile==null) {
			throw new IllegalArgumentException("jarFile cannot be null");
		}
		return addClassFileSource(new JarLibraryInfo(jarFile));
	}


	/**
	 * Adds a class file source to read from.
	 *
	 * @param info The source to add.  If this is <code>null</code>, then
	 *        the current JVM's main JRE jar (rt.jar, or classes.jar on OS X)
	 *        will be added.  If this source has already been added, adding it
	 *        again will do nothing (except possibly update its attached source
	 *        location).
	 * @return Whether this source was added (e.g. it wasn't already loaded, or
	 *         it has a new source path).
	 * @throws IOException If an IO error occurs.
	 * @see #addClassFileSource(File)
	 * @see #addCurrentJreClassFileSource()
	 * @see #getClassFileSources()
	 * @see #removeClassFileSource(LibraryInfo)
	 */
	public boolean addClassFileSource(LibraryInfo info) throws IOException {

		if (info==null) {
			throw new IllegalArgumentException("info cannot be null");
		}

		// First see if this jar is already on the "build path."
		for (int i=0; i<classFileSources.size(); i++) {
			JarReader jar = classFileSources.get(i);
			LibraryInfo info2 = jar.getLibraryInfo();
			if (info2.equals(info)) {
				// Only update if the source location is different.
				SourceLocation  source = info.getSourceLocation();
				SourceLocation  source2 = info2.getSourceLocation();
				if ((source==null && source2!=null) ||
						(source!=null && !source.equals(source2))) {
					classFileSources.set(i, new JarReader((LibraryInfo)info.clone()));
					return true;
				}
				return false;
			}
		}

		// If it isn't on the build path, add it now.
		classFileSources.add(new JarReader(info));
		return true;

	}


	/**
	 * Adds the current JVM's rt.jar (or class.jar if on OS X) to the list of
	 * class file sources.  If the application is running in a JDK, the
	 * associated source zip is also located and used.
	 *
	 * @throws IOException If an IO error occurs.
	 * @see #addClassFileSource(LibraryInfo)
	 */
	public void addCurrentJreClassFileSource() throws IOException {
		addClassFileSource(LibraryInfo.getMainJreJarInfo());
	}


	/**
	 * Removes all class file sources from the "build path".
	 *
	 * @see #removeClassFileSource(LibraryInfo)
	 * @see #removeClassFileSource(File)
	 * @see #addClassFileSource(LibraryInfo)
	 * @see #getClassFileSources()
	 */
	public void clearClassFileSources() {
		classFileSources.clear();
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


	/**
	 * Returns a class file's entry.
	 *
	 * @param className The class name.
	 * @return Its entry, or {@code null} if it cannot be found.
	 */
	public ClassFile getClassEntry(String className) {

		String[] items = Util.splitOnChar(className, '.');

        for (JarReader jar : classFileSources) {
            ClassFile cf = jar.getClassEntry(items);
            if (cf != null) {
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
	public List<ClassFile> getClassesWithUnqualifiedName(String name,
								List<ImportDeclaration> importDeclarations) {

		// Might be more than one class/interface/enum with the same name.
		List<ClassFile> result = null;

		// Loop through all of our imports.
		for (ImportDeclaration idec : importDeclarations) {

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
							result = new ArrayList<>(1); // Usually small
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
								result = new ArrayList<>(1); // Usually small
							}
							result.add(entry);
						}
						else {
							LOG.log(System.Logger.Level.ERROR,
								"Class not found! - " + name2);
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
				result = new ArrayList<>(1); // Usually small
			}
			result.add(entry);
		}

		return result;

	}


	/**
	 * Returns all classes in a package.
	 *
	 * @param pkgName A package name.
	 * @param inPkg Not sure here
	 * @return A list of all classes in that package.
	 */
	public List<ClassFile> getClassesInPackage(String pkgName, boolean inPkg) {

		List<ClassFile> list = new ArrayList<>();
		String[] pkgs = Util.splitOnChar(pkgName, '.');

        for (JarReader jar : classFileSources) {
            jar.getClassesInPackage(list, pkgs, inPkg);
        }

		return list;

	}


	/**
	 * Returns the jars on the "build path".
	 *
	 * @return A list of {@link LibraryInfo}s. Modifying a
	 *         <code>LibraryInfo</code> in this list will have no effect on
	 *         this completion provider; in order to do that, you must re-add
	 *         the jar via {@link #addClassFileSource(LibraryInfo)}. If there
	 *         are no jars on the "build path," this will be an empty list.
	 * @see #addClassFileSource(LibraryInfo)
	 */
	public List<LibraryInfo> getClassFileSources() {
		List<LibraryInfo> jarList =
                new ArrayList<>(classFileSources.size());
		for (JarReader reader : classFileSources) {
			jarList.add(reader.getLibraryInfo()); // Already cloned
		}
		return jarList;
	}


	/**
	 * Returns the source location for a specific class.
	 *
	 * @param className The class.
	 * @return Its source location.
	 */
	public SourceLocation getSourceLocForClass(String className) {
		SourceLocation  sourceLoc = null;
		for (JarReader jar : classFileSources) {
			if (jar.containsClass(className)) {
				sourceLoc = jar.getLibraryInfo().getSourceLocation();
				break;
			}
		}
		return sourceLoc;
	}


	/**
	 * Removes a jar from the "build path".  This is a convenience method for
	 * folks only adding and removing jar sources.
	 *
	 * @param jar The jar to remove.
	 * @return Whether the jar was removed.  This will be <code>false</code>
	 *         if the jar was not on the build path.
	 * @see #removeClassFileSource(LibraryInfo)
	 * @see #addClassFileSource(LibraryInfo)
	 * @see #getClassFileSources()
	 */
	public boolean removeClassFileSource(File jar) {
		return removeClassFileSource(new JarLibraryInfo(jar));
	}


	/**
	 * Removes a class file source from the "build path".
	 *
	 * @param toRemove The source to remove.
	 * @return Whether source jar was removed.  This will be <code>false</code>
	 *         if the source was not on the build path.
	 * @see #removeClassFileSource(File)
	 * @see #addClassFileSource(LibraryInfo)
	 * @see #getClassFileSources()
	 */
	public boolean removeClassFileSource(LibraryInfo toRemove) {
		for (Iterator<JarReader> i=classFileSources.iterator(); i.hasNext();) {
			JarReader reader = i.next();
			LibraryInfo info = reader.getLibraryInfo();
			if (info.equals(toRemove)) {
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
