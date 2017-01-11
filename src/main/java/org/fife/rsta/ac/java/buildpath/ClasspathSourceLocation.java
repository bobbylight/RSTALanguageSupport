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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;


/**
 * Represents Java source files somewhere on the classpath.  This might be
 * somewhat of a unique situation, since often source isn't on the classpath,
 * only class files are.  However, there may be times when you want to ship
 * both the classes and source for a library and put them on your classpath
 * for simplicity of integrating with this code completion library.  In such a
 * case, you would use a <code>ClasspathLibraryInfo</code> and use this class
 * for the source location.<p>
 *
 * This class has no state; any classes it's asked about, it assumes it can
 * find the corresponding .java file somewhere on the classpath using the
 * class's ClassLoader.
 *
 * @author Robert Futrell
 * @version 1.0
 * @see ClasspathLibraryInfo
 */
public class ClasspathSourceLocation implements SourceLocation {


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationUnit getCompilationUnit(ClassFile cf) throws IOException {

		CompilationUnit cu = null;

		String res = cf.getClassName(true).replace('.', '/') + ".java";
		InputStream in = getClass().getClassLoader().getResourceAsStream(res);
		if (in!=null) {
			Scanner s = new Scanner(new InputStreamReader(in));
			cu = new ASTFactory().getCompilationUnit(res, s);
		}

		return cu;

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocationAsString() {
		return null;
	}


}