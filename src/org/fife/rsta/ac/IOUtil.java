/*
 * 05/11/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * I/O related utility methods.  We should think of a better location for
 * these methods.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class IOUtil {


	/**
	 * Private constructor to prevent instantiation.
	 */
	private IOUtil() {
	}


	/**
	 * Returns the value of an environment variable.  This method is here so
	 * we don't get an exception when calling <tt>System.getenv()</tt> in Java
	 * 1.4 (which we support).
	 *
	 * @param var The environment variable.
	 * @return The value of the variable, or <code>null</code> if it is not
	 *         defined.
	 */
	public static String getEnvSafely(String var) {

		String value = null;

		try {
			value = System.getenv(var);
		} catch (java.lang.Error e) { // Throws exception in 1.4

			// Windows
			if (File.separatorChar=='\\') {
				String command = "cmd.exe /c set " + var;
				StringBuffer stdout = new StringBuffer();
				try {
					Process p = Runtime.getRuntime().exec(command);
					IOUtil.waitForProcess(p, stdout, null);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				// set will return multiple matches if "var" is a prefix of
				// other vars, but we'll just check the first line for "var=".
				String str = stdout.toString();
				int equals = str.indexOf('=');
				int newline = str.indexOf('\n');
				if (equals==var.length() && newline>equals) {
					String test = str.substring(0, var.length());
					// Be careful with casing - Windows doesn't always return
					// the same casing as what you enter.
					if (test.equalsIgnoreCase(var)) {
						value = str.substring(var.length()+1, str.length()-1);
					}
				}
			}

			// Unix
			else {
				String command = "/bin/sh -c env | grep \"var\"=";
				StringBuffer stdout = new StringBuffer();
				try {
					Process p = Runtime.getRuntime().exec(command);
					IOUtil.waitForProcess(p, stdout, null);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				// env will return multiple matches if "var" is a prefix of
				// other vars, but we'll just check the first line for "var=".
				String str = stdout.toString();
				int equals = str.indexOf('=');
				int newline = str.indexOf('\n');
				if (equals==var.length() && newline>equals) {
					String test = str.substring(0, var.length());
					if (test.equals(var)) {
						value = str.substring(var.length()+1, str.length()-1);
					}
				}
			}

		}

		return value;

	}


	/**
	 * Runs a process, possibly capturing its stdout and/or stderr.
	 *
	 * @param p The process.
	 * @param stdout A buffer in which to put stdout, or <code>null</code> if
	 *        you don't want to keep it.
	 * @param stderr A buffer in which to keep stderr, or <code>null</code>
	 *        if you don't want to keep it.
	 * @return The return code of the process.
	 * @throws IOException If an IO error occurs.
	 */
	// TODO: Allow a timeout to be passed in
	public static int waitForProcess(Process p, StringBuffer stdout,
									StringBuffer stderr) throws IOException {

		InputStream in = p.getInputStream();
		InputStream err = p.getErrorStream();
		Thread t1 = new Thread(new OutputCollector(in, stdout));
		Thread t2 = new Thread(new OutputCollector(err, stderr));
		t1.start();
		t2.start();
		int rc = -1;

		try {
			rc = p.waitFor();
			t1.join();
			t2.join();
		} catch (InterruptedException ie) {
			p.destroy();
		} finally {
			in.close();
			err.close();
		}

		return rc;

	}


	/**
	 * Utility testing method.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		for (int i=0; i<args.length; i++) {
			String value = IOUtil.getEnvSafely(args[i]);
			System.out.println(args[i] + "=" + value);
		}
	}


}