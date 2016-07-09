/*
 * 04/25/2010
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A class that eats the stdout or stderr of a running <tt>Process</tt> to
 * prevent deadlock.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class OutputCollector implements Runnable {

	private InputStream in;
	private StringBuffer sb;


	/**
	 * Constructor.
	 *
	 * @param in The input stream.
	 */
	public OutputCollector(InputStream in) {
		this(in, true);
	}


	/**
	 * Constructor.
	 *
	 * @param in The input stream.
	 * @param sb The buffer in which to collect the output.
	 */
	public OutputCollector(InputStream in, StringBuffer sb) {
		this.in = in;
		this.sb = sb;
	}


	/**
	 * Constructor.
	 *
	 * @param in The input stream.
	 * @param collect Whether to actually collect the output in a buffer.
	 *        If this is <code>false</code>, then {@link #getOutput()} will
	 *        return <code>null</code>.  This parameter can be used if you want
	 *        to eat, but ignore, stdout or stderr for a process.
	 */
	public OutputCollector(InputStream in, boolean collect) {
		this.in = in;
		if (collect) {
			sb = new StringBuffer();
		}
	}


	/**
	 * Returns the output collected from the stream.
	 *
	 * @return The output.
	 */
	public StringBuffer getOutput() {
		return sb;
	}


	/**
	 * Called every time a line is read from the stream.  This allows
	 * subclasses to handle lines differently.  They can also call into the
	 * super implementation if they want to log the lines into the buffer.
	 *
	 * @param line The line read.
	 */
	protected void handleLineRead(String line) {
		if (sb!=null) {
			sb.append(line).append('\n');
		}
	}


	public void run() {

		String line = null;

		try {

			BufferedReader r = new BufferedReader(new InputStreamReader(in));

			try {
				while ((line=r.readLine())!=null) {
					handleLineRead(line);
				}
			} finally {
				r.close();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}


}