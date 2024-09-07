package org.fife.rsta.ac.java.buildpath;

import org.fife.rsta.ac.java.PackageMapNode;
import org.fife.rsta.ac.java.classreader.ClassFile;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to help with java9+
 * helper kovadam69
 */
public class JDK9ClasspathLibraryInfo extends LibraryInfo {

	private static final Logger LOGGER = Logger.getLogger(JDK9ClasspathLibraryInfo.class.getName());

	/**
	 * Mapping of class names to <code>ClassFile</code>s.
	 */
	private final Map<String, ClassFile> classNameToClassFile = new HashMap<>();
	private final Map<String, String> classNameToFullyQualified = new HashMap<>();
	private final Map<String, String> classNameToModule = new HashMap<>();

	private final Path pathToJrt;

	/**
	 * Constructor.
	 */
	public JDK9ClasspathLibraryInfo() {
		setSourceLocation(null);

		Path pathToJRE = Paths.get(System.getProperty("java.home"));
		pathToJrt = pathToJRE.resolve("lib").resolve("jrt-fs.jar");

		// Read path entries and cache them
		if (Files.exists(pathToJrt)) {
			try (URLClassLoader loader = new URLClassLoader(new URL[]{pathToJrt.toUri().toURL()});
				 FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap(), loader)) {

				List<Path> paths = Files.list(fs.getPath("/modules")).collect(Collectors.toList());
				cacheFiles(paths);
			} catch (Exception ex) {
				LOGGER.log(Level.SEVERE, "Error loading JDK9+ module information", ex);
			}
		} else {
			LOGGER.warning("JRT file does not exist: " + pathToJrt);
		}
	}

	private void cacheFiles(List<Path> paths) throws IOException {
		for (Path p : paths) {
			if (Files.isDirectory(p)) {
				try (Stream<Path> stream = Files.list(p)) {
					cacheFiles(stream.collect(Collectors.toList()));
				}
			} else if (p.toString().endsWith(".class")) {
				StringBuilder className = new StringBuilder();
				StringBuilder fqName = new StringBuilder();

				for (int i = 2; i < p.getNameCount(); i++) {
					if (i > 2) {
						className.append("/");
						fqName.append(".");
					}
					String namePart = p.getName(i).toString();
					className.append(namePart);
					if (namePart.endsWith(".class")) {
						fqName.append(namePart, 0, namePart.lastIndexOf(".class"));
					} else {
						fqName.append(namePart);
					}
				}

				String module = p.getName(1).toString();
				classNameToModule.put(className.toString(), module);
				classNameToClassFile.put(className.toString(), null);
				classNameToFullyQualified.put(className.toString(), fqName.toString());
			}
		}
	}

	@Override
	public int compareTo(LibraryInfo o) {
		if (o == this) {
			return 0;
		}
		if (o instanceof JDK9ClasspathLibraryInfo other) {
			int sizeComparison = Integer.compare(classNameToClassFile.size(), other.classNameToClassFile.size());
			if (sizeComparison == 0) {
				return classNameToClassFile.keySet().stream()
					.filter(key -> !other.classNameToClassFile.containsKey(key))
					.findFirst()
					.map(key -> -1)
					.orElse(0);
			}
			return sizeComparison;
		}
		return -1;
	}

	@Override
	public ClassFile createClassFile(String entryName) throws IOException {
		if (classNameToClassFile.containsKey(entryName)) {
			return classNameToClassFile.computeIfAbsent(entryName, this::createClassFileImpl);
		}
		return null;
	}

	private ClassFile createClassFileImpl(String res) {
		String module = classNameToModule.get(res);

		try (URLClassLoader loader = new URLClassLoader(new URL[]{pathToJrt.toUri().toURL()});
			 FileSystem fs = FileSystems.newFileSystem(URI.create("jrt:/"), Collections.emptyMap(), loader)) {

			byte[] result = Files.readAllBytes(fs.getPath("/modules/" + module + "/" + res));
			if (result.length > 0) {
				try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(result))) {
					return new ClassFile(din);
				} catch (Exception ex) {
					LOGGER.log(Level.WARNING, "Error reading class file", ex);
				}
			}
		} catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Error creating ClassFile", ex);
		}
		return null;
	}

	@Override
	public PackageMapNode createPackageMap() {
		PackageMapNode packageMap = new PackageMapNode();
		classNameToClassFile.keySet().forEach(packageMap::add);
		return packageMap;
	}

	@Override
	public long getLastModified() {
		return 0;
	}

	@Override
	public String getLocationAsString() {
		return null;
	}

	@Override
	public int hashCode() {
		return classNameToClassFile.hashCode();
	}

	@Override
	public int hashCodeImpl() {
		return 0;
	}

	@Override
	public void bulkClassFileCreationEnd() {
		// No bulk operations to finalize in this implementation
	}

	@Override
	public void bulkClassFileCreationStart() {
		// No bulk operations to initialize in this implementation
	}

	@Override
	public ClassFile createClassFileBulk(String string) throws IOException {
		return createClassFileImpl(string);
	}
}
