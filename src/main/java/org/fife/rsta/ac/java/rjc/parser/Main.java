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
package org.fife.rsta.ac.java.rjc.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * Test application for the Java scanner.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Main {

	/**
	 * If this system property is set to "<code>true</code>",
	 * output will not be written to stdout for each file.
	 */
	public static final String PROPERTY_NO_OUTPUT = "no.output";

	private static final boolean LOG = !"true".equals(
							System.getProperty(PROPERTY_NO_OUTPUT));


	private static void log(Object text) {
		if (LOG) {
			System.out.println(text);
		}
	}


	/**
	 * Program entry point.
	 *
	 * @param args Command line arguments.
	 * @throws IOException If an IO error occurs.
	 */
	public static void main(String[] args) throws IOException {

		PrintStream oldOut = System.out;
		PrintStream oldErr = System.err;
		String outputFileName = File.separatorChar == '/' ? "/tmp/rofutr.out" : "C:/temp/rofutr.out";
		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFileName)));
		System.setOut(out);
		System.setErr(out);

		ASTFactory fact = new ASTFactory();
		//CompilationUnit cu = null;

		List<File> toDo = new ArrayList<>();

		if (args.length>0) {
			toDo.add(new File(args[0]));
		}
		else {

			// Smoke test by verifying that a large amount of valid source is parsed properly
			String[] roots = new String[2];
			if (File.separatorChar == '/') {
				roots[0] = "/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/src";
				roots[1] = "/Users/robert/dev/RText/src";
			}
			else {
				roots[0] = "C:/java/32/jdk1.6.0_16/src/";
				roots[1] = "D:/dev/rjava/RText/src";
			}

			for (String root : roots) {
				File[] files = new File(root).listFiles();
				if (files != null) {
					Collections.addAll(toDo, files);
				}
			}
			toDo.sort(Comparator.comparing(File::getAbsolutePath));
		}

		int count = 0;
		int typeParamCount = 0;
		int annotationTypeDecCount = 0;
		long entireStart = System.currentTimeMillis();

		for (int i=0; i<toDo.size(); i++) {

			File file = toDo.get(i);

			if (file.isDirectory()) {
				File[] contents = file.listFiles();
                Collections.addAll(toDo, contents);
				continue;
			}
			else if (!file.getName().endsWith(".java")) {
				continue;
			}

			BufferedReader r = new BufferedReader(new FileReader(file));
			Scanner scanner = new Scanner(r);
			long start = System.currentTimeMillis();
			try {
				/*cu = */fact.getCompilationUnit(file.getName(), scanner);
				long time = System.currentTimeMillis() - start;
				//log(cu);
				log(file.getAbsolutePath() + " (" + file.length() + "): " + time + " ms");
			} catch (InternalError ie) {
				System.err.println(file.getAbsolutePath());
				ie.printStackTrace();
				System.exit(1);
			}
			count++;
			r.close();

		}

		long entireTime = System.currentTimeMillis() - entireStart;
		log(count + " files parsed");
		log("TypeParameter errors: " + typeParamCount);
		log("AnnotationTypeDeclaration errors: " + annotationTypeDecCount);
		log(entireTime + " ms");
System.setOut(oldOut);
System.setErr(oldErr);
	}


}
