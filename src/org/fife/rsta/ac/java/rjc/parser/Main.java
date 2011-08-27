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
package org.fife.rsta.ac.java.rjc.parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lexer.*;


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
//PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("C:/temp/rofutr.out")));
//System.setOut(out);
//System.setErr(out);

		ASTFactory fact = new ASTFactory();
		//CompilationUnit cu = null;

		List toDo = new ArrayList();

		if (args.length>0) {
			toDo.add(new File(args[0]));
		}
		else {
//toDo.add(new File("C:\\java\\32\\jdk1.6.0_16\\src\\java\\util\\concurrent\\TimeUnit.java"));
			File rootDir = new File("C:/java/32/jdk1.6.0_16/src/");
//rootDir = new File("C:/dev/rsta/JavaAst/src");
//rootDir = new File("C:/dev/rjava/Common/src");
			File[] files = rootDir.listFiles();
			for (int i=0; i<files.length; i++) {
				toDo.add(files[i]);
			}
		}

		int count = 0;
		int typeParamCount = 0;
		int annotationTypeDecCount = 0;
		long entireStart = System.currentTimeMillis();

		for (int i=0; i<toDo.size(); i++) {

			File file = (File)toDo.get(i);

			if (file.isDirectory()) {
				File[] contents = file.listFiles();
				for (int j=0; j<contents.length; j++) {
					toDo.add(contents[j]);
				}
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
			} catch (IOException ioe) {
				String msg = ioe.getMessage();
				if (msg.startsWith("TypeParameters")) {
					log(file.getName() + ": ****** TYPEPARAMETERS ******");
					typeParamCount++;
				}
				else if (msg.startsWith("AnnotationTypeDeclaration")) {
					log(file.getName() + ": ****** AnnotationTypeDeclaration ******");
					annotationTypeDecCount++;
				}
				else {
					System.err.println(file.getAbsolutePath());
					ioe.printStackTrace();
					System.exit(1);
				}
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