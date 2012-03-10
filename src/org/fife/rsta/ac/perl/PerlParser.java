/*
 * 05/10/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.perl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;

import org.fife.rsta.ac.IOUtil;
import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;


/**
 * Parses Perl code in an <tt>RSyntaxTextArea</tt>.<p>
 * 
 * Please keep in mind that this class is a work-in-progress!
 * 
 * @author Robert Futrell
 * @version 0.6
 */
public class PerlParser extends AbstractParser {

	private DefaultParseResult result;

	private boolean taintModeEnabled;
	private boolean warningsEnabled;

	/**
	 * The user's requested value for <code>PERL5LIB</code> when parsing Perl
	 * code, or <code>null</code> to use the default.
	 */
	private String perl5LibOverride;

	/**
	 * The environment to use when launching Perl to parse code, in the format
	 * expected by <code>Runtime.exec()</code>.  This may be <code>null</code>.
	 */
	private String[] perlEnvironment;

	/**
	 * The maximum amount of time to wait for Perl to finish compiling a
	 * source file.
	 */
	private static final int MAX_COMPILE_MILLIS = 10000;


	/**
	 * Constructor.
	 */
	public PerlParser() {
		result = new DefaultParseResult(this);
	}


	/**
	 * Creates the array of environment variables to use when running Perl to
	 * syntax check code, based on the user's requested <code>PERL5LIB</code>.
	 */
	private void createPerlEnvironment() {

		// Default to using the same environment as parent process
		perlEnvironment = null;

		// See if they specified a special value for PERL5LIB
		String perl5Lib = getPerl5LibOverride();
		if (perl5Lib!=null) {
			String[] toAdd = { "PERL5LIB", perl5Lib };
			perlEnvironment = IOUtil.getEnvironmentSafely(toAdd);
		}

	}


	/**
	 * Returns the value to use for <code>PERL5LIB</code> when parsing Perl
	 * code.
	 *
	 * @return The value, or <code>null</code> to use the system default.
	 * @see #setPerl5LibOverride(String)
	 */
	public String getPerl5LibOverride() {
		return perl5LibOverride;
	}


	/**
	 * Returns whether warnings are enabled when checking syntax.
	 *
	 * @return Whether warnings are enabled.
	 * @see #setWarningsEnabled(boolean)
	 */
	public boolean getWarningsEnabled() {
		return warningsEnabled;
	}


	/**
	 * Returns whether taint mode is enabled when checking syntax.
	 *
	 * @return Whether taint mode is enabled.
	 * @see #setTaintModeEnabled(boolean)
	 */
	public boolean isTaintModeEnabled() {
		return taintModeEnabled;
	}


	/**
	 * {@inheritDoc}
	 */
	public ParseResult parse(RSyntaxDocument doc, String style) {

		result.clearNotices();

		int lineCount = doc.getDefaultRootElement().getElementCount();
		result.setParsedLines(0, lineCount-1);

		long start = System.currentTimeMillis();
		try {

			// Bail early if install location is misconfigured.
			File dir = PerlLanguageSupport.getPerlInstallLocation();
			if (dir==null) {
				return result;
			}
			String exe = File.separatorChar=='\\' ? "bin/perl.exe" : "bin/perl";
			File perl = new File(dir, exe);
			if (!perl.isFile()) {
				return result;
			}

			File tempFile = File.createTempFile("perlParser", ".tmp");
			BufferedOutputStream out = new BufferedOutputStream(
											new FileOutputStream(tempFile));
			try {
				new DefaultEditorKit().write(out, doc, 0, doc.getLength());
			} catch (BadLocationException ble) {
				ble.printStackTrace();
				throw new IOException(ble.getMessage());
			}
			out.close();

			String opts = "-c";
			if (getWarningsEnabled()) {
				opts += "w";
			}
			if (isTaintModeEnabled()) {
				opts += "t";
			}

			String[] envp = perlEnvironment;
			String[] cmd = { perl.getAbsolutePath(), opts, tempFile.getAbsolutePath() };
			Process p = Runtime.getRuntime().exec(cmd, envp);
			Element root = doc.getDefaultRootElement();
			OutputCollector stdout = new OutputCollector(p.getInputStream(),
														false);
			Thread t = new Thread(stdout);
			t.start();
			PerlOutputCollector stderr = new PerlOutputCollector(
										p.getErrorStream(), this, result, root);
			Thread t2 = new Thread(stderr);
			t2.start();
			//int rc = 0;
			try {
				t2.join(MAX_COMPILE_MILLIS);
				t.join(MAX_COMPILE_MILLIS);
				if (t.isAlive()) {
					t.interrupt();
					//rc = -1;
				}
				else {
					/*rc = */p.waitFor();
				}
				//System.out.println(rc);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
			//System.out.println(time + "ms");

		} catch (IOException ioe) {
			result.setError(ioe);
			ioe.printStackTrace();
		}

		return result;

	}


	/**
	 * Sets the value to use for <code>PERL5LIB</code> when parsing Perl
	 * code.
	 *
	 * @param override The value, or <code>null</code> to use the system
	 *        default.
	 * @see #getPerl5LibOverride()
	 */
	public void setPerl5LibOverride(String override) {
		perl5LibOverride = override;
		createPerlEnvironment(); // Refresh our cached environment map.
	}


	/**
	 * Toggles whether taint mode is enabled when checking syntax.
	 *
	 * @param enabled Whether taint mode should be enabled.
	 * @see #isTaintModeEnabled()
	 */
	public void setTaintModeEnabled(boolean enabled) {
		taintModeEnabled = enabled;
	}


	/**
	 * Toggles whether warnings are returned when checking syntax.
	 *
	 * @param enabled Whether warnings are enabled.
	 * @see #getWarningsEnabled()
	 */
	public void setWarningsEnabled(boolean enabled) {
		warningsEnabled = enabled;
	}


}