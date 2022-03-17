/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.text.Element;

import org.fife.io.DocumentReader;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;


/**
 * Parses Java code in an <tt>RSyntaxTextArea</tt>.<p>
 *
 * Like all RSTA <tt>Parser</tt>s, a <tt>JavaParser</tt> instance is notified
 * when the RSTA's text content changes.  After a small delay, it will parse
 * the content as Java code, building an AST and looking for any errors.  When
 * parsing is complete, a property change event of type
 * {@link #PROPERTY_COMPILATION_UNIT} is fired.  Listeners can check the new
 * value of the property for the {@link CompilationUnit} built that represents
 * the source code in the text area.  Note that the <tt>CompilationUnit</tt>
 * may be incomplete if there were parsing/syntax errors (it will usually be
 * complete "up to" the error in the content).<p>
 *
 * This parser cannot be shared amongst multiple instances of
 * <tt>RSyntaxTextArea</tt>.<p>
 *
 * Please keep in mind that this class is a work-in-progress!
 *
 * @author Robert Futrell
 * @version 0.5
 */
public class JavaParser extends AbstractParser {

	/**
	 * The property change event that's fired when the document is re-parsed.
	 * Applications can listen for this property change and update themselves
	 * accordingly.
	 */
	public static final String PROPERTY_COMPILATION_UNIT = "CompilationUnit";

	private CompilationUnit cu;
	private PropertyChangeSupport support;
	private DefaultParseResult result;


	/**
	 * Constructor.
	 *
	 * @param textArea The text area to parse.
	 */
	public JavaParser(RSyntaxTextArea textArea) {
		support = new PropertyChangeSupport(this);
		result = new DefaultParseResult(this);
	}


	/**
	 * Adds all notices from the Java parser to the results object.
	 */
	private void addNotices(RSyntaxDocument doc) {

		result.clearNotices();
		int count = cu==null ? 0 : cu.getParserNoticeCount();

		if (count==0) {
			return;
		}

		for (int i=0; i<count; i++) {
			ParserNotice notice = cu.getParserNotice(i);
			int offs = getOffset(doc, notice);
			if (offs>-1) {
				int len = notice.getLength();
				result.addNotice(new DefaultParserNotice(this,
						notice.getMessage(), notice.getLine(), offs, len));
			}
		}

	}


	/**
	 * Adds a listener to this parser.
	 *
	 * @param prop The property to listen for changes to.
	 * @param l The listener to add.
	 * @see #removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		 support.addPropertyChangeListener(prop, l);
	}


	/**
	 * Returns the compilation unit from the last time the text area was
	 * parsed.
	 *
	 * @return The compilation unit, or <code>null</code> if it hasn't yet
	 *         been parsed or an unexpected error occurred while parsing.
	 */
	public CompilationUnit getCompilationUnit() {
		return cu;
	}


	/**
	 * Returns the offset into the document of a parser notice.
	 *
	 * @param doc The document.
	 * @param notice The parser notice.
	 * @return The offset, or {@code -1} if it is invalid.
	 */
	public int getOffset(RSyntaxDocument doc, ParserNotice notice) {
		Element root = doc.getDefaultRootElement();
		Element elem = root.getElement(notice.getLine());
		int offs = elem.getStartOffset() + notice.getColumn();
		return offs>=elem.getEndOffset() ? -1 : offs;
	}


	@Override
	public ParseResult parse(RSyntaxDocument doc, String style) {

		cu = null;
		result.clearNotices();
		// Always spell check all lines, for now.
		int lineCount = doc.getDefaultRootElement().getElementCount();
		result.setParsedLines(0, lineCount-1);

		DocumentReader r = new DocumentReader(doc);
		Scanner scanner = new Scanner(r);
		scanner.setDocument(doc);
		ASTFactory fact = new ASTFactory();
		long start = System.currentTimeMillis();
		try {
			cu = fact.getCompilationUnit("SomeFile.java", scanner); // TODO: Real name?
			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
		} finally {
			r.close();
		}

		addNotices(doc);
		support.firePropertyChange(PROPERTY_COMPILATION_UNIT, null, cu);
		return result;

	}


	/**
	 * Removes a property change listener from this parser.
	 *
	 * @param prop The property being listened to.
	 * @param l The listener to remove.
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}


}
