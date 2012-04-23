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

import java.io.IOException;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;


/**
 * Represents the location of Java source, either in a zip file (src.zip),
 * a flat file (source in a project's source folder), or in some other location.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see DirSourceLocation
 * @see ZipSourceLocation
 * @see ClasspathSourceLocation
 */
public interface SourceLocation {


	/**
	 * Returns an AST for the specified class file.
	 *
	 * @param cf The class file to grab the AST for.
	 * @return The AST, or <code>null</code> if it cannot be found.
	 * @throws IOException If an IO error occurs.
	 */
	CompilationUnit getCompilationUnit(ClassFile cf) throws IOException;


}