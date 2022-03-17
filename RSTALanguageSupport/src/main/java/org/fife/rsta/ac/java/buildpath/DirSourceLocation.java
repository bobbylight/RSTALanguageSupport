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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;


/**
 * Represents Java source in a directory, such as in a project's source folder.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DirSourceLocation implements SourceLocation {

	private File dir;


	/**
	 * Constructor.
	 *
	 * @param dir The directory containing the source files.
	 */
	public DirSourceLocation(String dir) {
		this(new File(dir));
	}


	/**
	 * Constructor.
	 *
	 * @param dir The directory containing the source files.
	 */
	public DirSourceLocation(File dir) {
		this.dir = dir;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationUnit getCompilationUnit(ClassFile cf) throws IOException {

		CompilationUnit cu = null;

		String entryName = cf.getClassName(true);
		entryName = entryName.replace('.', '/');
		entryName += ".java";
		//System.out.println("DEBUG: entry name: " + entryName);
		File file = new File(dir, entryName);
		if (!file.isFile()) {
			// Be nice and check for "src/" subdirectory
			file = new File(dir, "src/" + entryName);
		}

		if (file.isFile()) {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                Scanner s = new Scanner(r);
                cu = new ASTFactory().getCompilationUnit(entryName, s);
                //System.out.println("DEBUG: cu: " + cu);
            }
		}

		return cu;

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocationAsString() {
		return dir.getAbsolutePath();
	}


}
