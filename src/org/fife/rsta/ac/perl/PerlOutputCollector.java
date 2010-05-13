/*
 * 04/25/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.perl;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Element;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;


/**
 * A class that eats the stdout or stderr of a running <tt>Process</tt> to
 * prevent deadlock.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class PerlOutputCollector extends OutputCollector {

	private PerlParser parser;
	private DefaultParseResult result;
	private Element root;

	private static final Pattern ERROR_PATTERN = Pattern.compile(" at .+ line (\\d+)\\.$");


	/**
	 * Constructor.
	 *
	 * @param in The input stream.
	 */
	public PerlOutputCollector(InputStream in, PerlParser parser,
								DefaultParseResult result, Element root) {
		super(in);
		this.parser = parser;
		this.result = result;
		this.root = root;
	}


	/**
	 * {@inheritDoc}
	 */
	protected void handleLineRead(String line) throws IOException {

		Matcher m = ERROR_PATTERN.matcher(line);

		if (m.find()) {

			line = line.substring(0, line.length()-m.group().length());

			int lineNumber = Integer.parseInt(m.group(1)) - 1;
			Element elem = root.getElement(lineNumber);
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();

			DefaultParserNotice pn = new DefaultParserNotice(
					parser, line, lineNumber, start, end-start);

			result.addNotice(pn);

		}

	}


}