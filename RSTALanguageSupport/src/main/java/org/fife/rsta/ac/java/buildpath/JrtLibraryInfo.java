/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.buildpath;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;


/**
 * Information about the classes in a Java 9+ runtime, to add to the "build
 * path".  The runtime image ({@code lib/modules}) is read via the
 * {@code jrt:/} file system.  Every Java 9+ runtime has such an image,
 * including JREs and {@code jlink}'d images, regardless of whether it also
 * ships {@code jmods}.
 * <p>
 * The runtime being read need not be the one running this application.  For
 * any other runtime, its {@code lib/jrt-fs.jar} is used to read its image,
 * and the resulting file system is kept open for the life of the JVM so it
 * can be shared.
 * <p>
 * For Java 8 and earlier, see {@link JarLibraryInfo}.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see DirLibraryInfo
 * @see ClasspathLibraryInfo
 * @see JarLibraryInfo
 * @see LibraryInfo#getJreJarInfo(File)
 */
public class JrtLibraryInfo extends LibraryInfo {

	/**
	 * File systems opened for runtimes other than the one running this
	 * application, keyed by the runtime's real path.
	 */
	private static final Map<Path, FileSystem> FOREIGN_FILE_SYSTEMS = new HashMap<>();

	private File jreHome;

	/**
	 * {@code jreHome}, as an absolute, normalized path.  Used for equality.
	 */
	private Path jreHomePath;

	/**
	 * Maps a class entry name (e.g. {@code java/lang/String.class}) to its
	 * location in the runtime image.  Built lazily, and rebuilt by
	 * {@link #createPackageMap()}.  Guarded by {@code this}.
	 */
	private Map<String, Path> entryToPath;


	/**
	 * Constructor.
	 *
	 * @param jreHome The home directory of a Java 9+ runtime, which must
	 *        contain {@code lib/modules}.
	 * @throws IllegalArgumentException If {@code jreHome} isn't a Java 9+
	 *         runtime image.
	 */
	public JrtLibraryInfo(File jreHome) {
		this(jreHome, null);
	}


	/**
	 * Constructor.
	 *
	 * @param jreHome The home directory of a Java 9+ runtime, which must
	 *        contain {@code lib/modules}.
	 * @param sourceLoc The location of the runtime's source.  This may be
	 *        {@code null}.
	 * @throws IllegalArgumentException If {@code jreHome} isn't a Java 9+
	 *         runtime image.
	 */
	public JrtLibraryInfo(File jreHome, SourceLocation sourceLoc) {
		setJreHome(jreHome);
		setSourceLocation(sourceLoc);
	}


	@Override
	public void bulkClassFileCreationEnd() {
		// Do nothing; the file system is shared and stays open
	}


	@Override
	public void bulkClassFileCreationStart() {
		// Do nothing; the file system is shared and stays open
	}


	/**
	 * Compares this {@code LibraryInfo} to another one.  Two instances of
	 * this class are equal if they represent the same runtime.  Source
	 * attachment is irrelevant.
	 *
	 * @return The sort order of these two library infos.
	 */
	@Override
	public int compareTo(LibraryInfo info) {
		if (info == this) {
			return 0;
		}
		if (info instanceof JrtLibraryInfo) {
			return jreHomePath.compareTo(((JrtLibraryInfo)info).jreHomePath);
		}
		return -1;
	}


	@Override
	public ClassFile createClassFile(String entryName) throws IOException {
		Path path = getEntryToPath().get(entryName);
		if (path == null) {
			// TODO: Throw an exception in 4.0.0 release
			return null;
		}
		byte[] bytes = Files.readAllBytes(path);
		try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
			return new ClassFile(in);
		}
	}


	@Override
	public ClassFile createClassFileBulk(String entryName) throws IOException {
		return createClassFile(entryName);
	}


	@Override
	public synchronized PackageMapNode createPackageMap() throws IOException {

		// Rescan the image, refreshing the index used to find classes later
		entryToPath = buildEntryToPath();

		PackageMapNode root = new PackageMapNode();
		for (String entryName : entryToPath.keySet()) {
			root.add(entryName);
		}
		return root;
	}


	/**
	 * Scans every module in the runtime image, recording where each class is.
	 * {@code module-info} descriptors are skipped since they aren't classes.
	 */
	private Map<String, Path> buildEntryToPath() throws IOException {

		Map<String, Path> index = new HashMap<>();
		Path modulesDir = getFileSystem().getPath("/modules");

		try (Stream<Path> modules = Files.list(modulesDir)) {
			for (Path module : (Iterable<Path>)modules::iterator) {
				try (Stream<Path> files = Files.walk(module)) {
					for (Path file : (Iterable<Path>)files::iterator) {
						String entryName = module.relativize(file).toString();
						if (entryName.endsWith(".class") &&
								!"module-info.class".equals(entryName)) {
							index.putIfAbsent(entryName, file);
						}
					}
				}
			}
		}

		return index;
	}


	private synchronized Map<String, Path> getEntryToPath() throws IOException {
		if (entryToPath == null) {
			entryToPath = buildEntryToPath();
		}
		return entryToPath;
	}


	/**
	 * Returns the {@code jrt:/} file system for the runtime.
	 */
	private FileSystem getFileSystem() throws IOException {

		Path home = toRealPath(jreHome);
		if (home.equals(toRealPath(new File(System.getProperty("java.home"))))) {
			return FileSystems.getFileSystem(URI.create("jrt:/"));
		}

		synchronized (FOREIGN_FILE_SYSTEMS) {
			FileSystem fs = FOREIGN_FILE_SYSTEMS.get(home);
			if (fs == null || !fs.isOpen()) {
				fs = FileSystems.newFileSystem(URI.create("jrt:/"),
					Map.of("java.home", home.toString()));
				FOREIGN_FILE_SYSTEMS.put(home, fs);
			}
			return fs;
		}
	}


	/**
	 * Returns the home directory of the runtime this object reads.
	 *
	 * @return The runtime's home directory.
	 */
	public File getJreHome() {
		return jreHome;
	}


	/**
	 * Since a runtime image doesn't change while in use, this method always
	 * returns {@code 0}.
	 *
	 * @return {@code 0} always.
	 */
	@Override
	public long getLastModified() {
		return 0;
	}


	@Override
	public String getLocationAsString() {
		return jreHome.getAbsolutePath();
	}


	@Override
	public int hashCodeImpl() {
		return jreHomePath.hashCode();
	}


	private void setJreHome(File jreHome) {
		if (jreHome == null || !new File(jreHome, "lib/modules").isFile()) {
			String name = jreHome == null ? "null" : jreHome.getAbsolutePath();
			throw new IllegalArgumentException(
				"Not a Java 9+ runtime image (no lib/modules): " + name);
		}
		this.jreHome = jreHome;
		this.jreHomePath = jreHome.toPath().toAbsolutePath().normalize();
	}


	private static Path toRealPath(File file) throws IOException {
		return file.toPath().toRealPath();
	}


	/**
	 * Returns a string representation of this library information.  Useful
	 * for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[JrtLibraryInfo: " +
			"jreHome=" + jreHome.getAbsolutePath() +
			"; source=" + getSourceLocation() +
			"]";
	}


}
