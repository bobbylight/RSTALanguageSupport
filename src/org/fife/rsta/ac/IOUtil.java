/*
 * 05/11/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * I/O related utility methods.  We should think of a better location for
 * these methods.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class IOUtil {

	private static Map DEFAULT_ENV;


	/**
	 * Private constructor to prevent instantiation.
	 */
	private IOUtil() {
	}


	/**
	 * Gets the environment of the current process.  Works with Java 1.4 as
	 * well as 1.5+.
	 *
	 * @return A mapping of environment variable names to values.
	 */
	private static Map getDefaultEnvMap() {

		// If we've already created it...
		if (DEFAULT_ENV!=null) {
			return DEFAULT_ENV;
		}

		// In Java 5+, we can just get the environment directly
		try {

			Method m = System.class.getDeclaredMethod("getenv", null);
			DEFAULT_ENV = (Map)m.invoke(null, null);

		} catch (Exception e) { // Java 1.4

			DEFAULT_ENV = new HashMap();
			StringBuffer stdout = new StringBuffer();
			String command = null;

			// Windows
			if (File.separatorChar=='\\') {
				command = "cmd.exe /c set";
			}

			// Unix
			else {
				command = "/bin/sh -c env";
			}

			try {
				Process p = Runtime.getRuntime().exec(command);
				IOUtil.waitForProcess(p, stdout, null);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			String str = stdout.toString();
			BufferedReader r = new BufferedReader(new StringReader(str));
			String line = null;
			try {
				while ((line=r.readLine())!=null) {
					int equals = line.indexOf('=');
					if (equals>-1) { // Always true
						String key = line.substring(0, equals);
						String value = line.substring(equals+1);
						DEFAULT_ENV.put(key, value);
					}
				}
				r.close();
			} catch (IOException ioe) { // Never happens
				ioe.printStackTrace();
			}

		}

		return DEFAULT_ENV;

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
	 * Returns the environment of the current process, with some variables
	 * possibly added/overwritten.  This method works even with Java 1.4.
	 *
	 * @param toAdd The environment variables to add/overwrite in the returned
	 *        array.  This array should have an even length, with even indices
	 *        containing variable names and odd indices containing the variable
	 *        values.
	 * @return The environment variables.  This array's entries will be of the
	 *         form "<code>name=value</code>", so it can be passed directly
	 *         into <code>Runtime.exec()</code>.
	 */
	public static String[] getEnvironmentSafely(String[] toAdd) {

		Map env = getDefaultEnvMap();

		// Put any vars they want to explicitly specify
		if (toAdd!=null) {
			Map temp = new HashMap(env);
			for (int i=0; i<toAdd.length; i+=2) {
				temp.put(toAdd[i], toAdd[i+1]);
			}
			env = temp;
		}

		// Create an array of "name=value" items, like Runtime.exec() wants
		int count = env.size();
		String[] vars = new String[count];
		int i = 0;
		for (Iterator j=env.entrySet().iterator(); j.hasNext(); ) {
			Map.Entry entry = (Map.Entry)j.next();
			vars[i++] = entry.getKey() + "=" + entry.getValue();
		}

		return vars;

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