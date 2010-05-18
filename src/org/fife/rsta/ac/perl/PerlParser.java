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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;


/**
 * Parses Perl code in an <tt>RSyntaxTextArea</tt>.<p>
 * 
 * This parser cannot be shared amongst multiple instances of
 * <tt>RSyntaxTextArea</tt>.<p>
 *
 * Please keep in mind that this class is a work-in-progress!
 * 
 * @author Robert Futrell
 * @version 0.5
 */
public class PerlParser extends AbstractParser {

	private DefaultParseResult result;


	/**
	 * Constructor.
	 */
	public PerlParser() {
		result = new DefaultParseResult(this);
	}


//	public int getOffset(RSyntaxDocument doc, ParserNotice notice) {
//		Element root = doc.getDefaultRootElement();
//		Element elem = root.getElement(notice.getLine());
//		int offs = elem.getStartOffset() + notice.getColumn();
//		return offs>=elem.getEndOffset() ? -1 : offs;
//	}


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
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
			try {
				new DefaultEditorKit().write(out, doc, 0, doc.getLength());
			} catch (BadLocationException ble) {
				ble.printStackTrace();
				throw new IOException(ble.getMessage());
			}
			out.close();

			String[] cmd = { perl.getAbsolutePath(), "-cw", tempFile.getAbsolutePath() };
			Process p = Runtime.getRuntime().exec(cmd);
			Element root = doc.getDefaultRootElement();
			OutputCollector stdout = new OutputCollector(p.getInputStream(),
														false);
			Thread t = new Thread(stdout);
			t.start();
			PerlOutputCollector stderr = new PerlOutputCollector(
										p.getErrorStream(), this, result, root);
			Thread t2 = new Thread(stderr);
			t2.start();
			int rc = 0;
			try {
				t2.join(10000);
				t.join(10000);
				if (t.isAlive()) {
					t.interrupt();
					rc = -1;
				}
				else {
					rc = p.waitFor();
				}
				//System.out.println(rc);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}

			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
System.out.println(time + "ms");

		} catch (IOException ioe) {
			result.setError(ioe);
			ioe.printStackTrace();
		}
System.out.println("--- " + result.getNotices().size());
		return result;

	}

}