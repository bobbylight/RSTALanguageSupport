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
package org.fife.rsta.ac.perl;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Element;

import org.fife.rsta.ac.OutputCollector;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;


/**
 * Listens to stderr from Perl to determine syntax errors in code.
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
	@Override
	protected void handleLineRead(String line) {

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