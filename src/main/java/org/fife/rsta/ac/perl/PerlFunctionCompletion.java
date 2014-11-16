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

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.autocomplete.CompletionProvider;
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
	@Override
	public String getSummary() {

		String summary = null;
		File installLoc = PerlLanguageSupport.getPerlInstallLocation();
		if (installLoc!=null && PerlLanguageSupport.getUseSystemPerldoc()) {
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
		OutputCollector oc = new OutputCollector(p.getInputStream());
		Thread t = new Thread(oc);
		t.start();
		int rc = 0;
		try {
			rc = p.waitFor();
			t.join();
			//System.out.println(rc);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		CharSequence output = null;
		if (rc==0) {
			output = oc.getOutput();
			if (output!=null && output.length()>0) {
				output = perldocToHtml(output);
			}
		}

		return output==null ? null : output.toString();

	}


	private static final StringBuilder perldocToHtml(CharSequence text) {

		StringBuilder sb = null;

		Font font = UIManager.getFont("Label.font");
		// Even Nimbus sets Label.font, but just to be safe...
		if (font!=null) {
			sb = new StringBuilder("<html><style>pre { font-family: ").
						append(font.getFamily()).append("; }</style><pre>");
		}
		else { // Just use monospaced font
			sb = new StringBuilder("<html><pre>");
		}

		sb.append(text);
		return sb;

	}


}