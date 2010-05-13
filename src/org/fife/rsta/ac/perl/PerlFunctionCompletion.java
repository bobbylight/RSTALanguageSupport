/*
 * 05/10/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.perl;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.UIManager;

import org.fife.ui.autocomplete.CompletionProvider;
/*
 * 05/10/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
import org.fife.ui.autocomplete.FunctionCompletion;


/**
 * Completion for Perl functions.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class PerlFunctionCompletion extends FunctionCompletion {


	/**
	 * Constructor.
	 *
	 * @param provider
	 * @param name
	 * @param returnType
	 */
	public PerlFunctionCompletion(CompletionProvider provider, String name,
			String returnType) {
		super(provider, name, returnType);
	}


	/**
	 * {@inheritDoc}
	 */
	public String getSummary() {

		String summary = null;
		File installLoc = PerlLanguageSupport.getPerlInstallLocation();
		if (installLoc!=null) {
			summary = getSummaryFromPerldoc(installLoc);
		}
		//else { // Don't use else - fallback for if perldoc isn't found
		if (summary==null) {
			summary = super.getSummary();
		}

		return summary;

	}


	/**
	 * Gets a summary of this function from perldoc.
	 *
	 * @param installLoc The Perl install location.
	 * @return The summary.
	 */
	private String getSummaryFromPerldoc(File installLoc) {

		Process p = null;

		String fileName = "bin/perldoc";
		if (File.separatorChar=='\\') {
			fileName += ".bat";
		}
		File perldoc = new File(installLoc, fileName);
		if (!perldoc.isFile()) {
			return null;
		}

		String[] cmd = { perldoc.getAbsolutePath(), "-f", getName() };
		try {
			p = Runtime.getRuntime().exec(cmd);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}

		// TODO: Launch waitFor() in a thread and interrupt after set time
		BufferedReader r = new BufferedReader(new InputStreamReader(
													p.getInputStream()));
		OutputCollector outputCollector = new OutputCollector(r);
		Thread t = new Thread(outputCollector);
		t.start();
		int rc = 0;
		try {
			rc = p.waitFor();
			t.join();
			//System.out.println(rc);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		StringBuffer output = null;
		if (rc==0) {
			output = outputCollector.getOutput();
			if (output!=null && output.length()>0) {
				output = perldocToHtml(output);
			}
		}

		return output==null ? null : output.toString();

	}


	private static final StringBuffer perldocToHtml(StringBuffer text) {

		StringBuffer sb = null;

		Font font = UIManager.getFont("Label.font");
		// Even Nimbus sets Label.font, but just to be safe...
		if (font!=null) {
			sb = new StringBuffer("<html><style>pre { font-family: ").
						append(font.getFamily()).append("; }</style><pre>");
		}
		else { // Just use monospaced font
			sb = new StringBuffer("<html><pre>");
		}

		sb.append(text);
		return sb;

	}


	private static class OutputCollector implements Runnable {

		private BufferedReader r;
		private StringBuffer sb;

		public OutputCollector(BufferedReader r) {
			this.r = r;
			sb = new StringBuffer();
		}

		public StringBuffer getOutput() {
			return sb;
		}

		public void run() {
			String line = null;
			try {
				while ((line=r.readLine())!=null) {
					sb.append(line).append('\n');
					//System.out.println(line);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}


}