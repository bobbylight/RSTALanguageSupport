/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.java;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
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


	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		 support.addPropertyChangeListener(prop, l);
	}


	public int getOffset(RSyntaxDocument doc, ParserNotice notice) {
		Element root = doc.getDefaultRootElement();
		Element elem = root.getElement(notice.getLine());
		int offs = elem.getStartOffset() + notice.getColumn();
		return offs>=elem.getEndOffset() ? -1 : offs;
	}


	/**
	 * {@inheritDoc}
	 */
	public ParseResult parse(RSyntaxDocument doc, String style) {

		cu = null;
		result.clearNotices();
		// Always spell check all lines, for now.
		int lineCount = doc.getDefaultRootElement().getElementCount();
		result.setParsedLines(0, lineCount-1);

		DocumentReader r = new DocumentReader(doc);
		Scanner scanner = new Scanner(r);
		ASTFactory fact = new ASTFactory();
		long start = System.currentTimeMillis();
		try {
			cu = fact.getCompilationUnit("SomeFile.java", scanner); // TODO: Real name?
			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
		} catch (IOException ioe) {
			result.setError(ioe);
			ioe.printStackTrace();
		}

		r.close();

		addNotices(doc);
		support.firePropertyChange(PROPERTY_COMPILATION_UNIT, null, cu);
		return result;

	}


	public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}


}