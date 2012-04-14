/*
 * 01/28/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.Element;

import org.fife.io.DocumentReader;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;


/**
 * Parses JavaScript code in an <code>RSyntaxTextArea</code>.
 * <p>
 * 
 * Like all RSTA <tt>Parser</tt>s, a <tt>JavaScriptParser</tt> instance is
 * notified when the RSTA's text content changes. After a small delay, it will
 * parse the content as JS code, building an AST and looking for any errors.
 * When parsing is complete, a property change event of type
 * {@link #PROPERTY_AST} is fired. Listeners can check the new value of the
 * property for the <code>AstRoot</code> built that represents the source code
 * in the text area.
 * <p>
 * 
 * This parser cannot be shared amongst multiple instances of
 * <code>RSyntaxTextArea</code>.
 * <p>
 * 
 * Please keep in mind that this class is a work-in-progress!
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaScriptParser extends AbstractParser {

	/**
	 * The property change event that's fired when the document is re-parsed.
	 * Applications can listen for this property change and update themselves
	 * accordingly.  The "new" value of this property will be an instance of
	 * <code>org.mozilla.javascript.ast.AstRoot</code>.
	 */
	public static final String PROPERTY_AST = "AST";

	private AstRoot astRoot;
	private JavaScriptLanguageSupport langSupport;
	private PropertyChangeSupport support;
	private DefaultParseResult result;

	/**
	 * Constructor.
	 */
	public JavaScriptParser(JavaScriptLanguageSupport langSupport,
			RSyntaxTextArea textArea) {
		this.langSupport = langSupport;
		support = new PropertyChangeSupport(this);
		result = new DefaultParseResult(this);
	}


	/**
	 * Registers a property change listener on this parser.  You'll probably
	 * want to listen for changes to {@link #PROPERTY_AST}.
	 *
	 * @param prop The property to listen for changes in.
	 * @param l The listener to add.
	 * @see #removePropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
		support.addPropertyChangeListener(prop, l);
	}


	/**
	 * Creates options for Rhino based off of the user's preferences.
	 *
	 * @param errorHandler The container for errors found while parsing.
	 * @return The properties for the JS compiler to use.
	 */
	private CompilerEnvirons createCompilerEnvironment(ErrorReporter errorHandler) {
		CompilerEnvirons env = new CompilerEnvirons();
		env.setErrorReporter(errorHandler);
		env.setIdeMode(true);
		env.setRecordingComments(true);
		env.setRecordingLocalJsDocComments(true);
		env.setRecoverFromErrors(true);
		env.setXmlAvailable(langSupport.isXmlAvailable());
		env.setStrictMode(langSupport.isStrictMode());
		int version = langSupport.getLanguageVersion();
		if (version > 0) {
			Logger.log("[JavaScriptParser]: JS language version set to: " + version);
			env.setLanguageVersion(version);
		}
		return env;
	}


	/**
	 * Returns the AST, or <code>null</code> if the editor's content has not
	 * yet been parsed.
	 * 
	 * @return The AST, or <code>null</code>.
	 */
	public AstRoot getAstRoot() {
		return astRoot;
	}


	
	/**
	 * {@inheritDoc}
	 */
	public ParseResult parse(RSyntaxDocument doc, String style) {

		astRoot = null;
		result.clearNotices();
		// Always spell check all lines, for now.
		Element root = doc.getDefaultRootElement();
		int lineCount = root.getElementCount();
		result.setParsedLines(0, lineCount - 1);

		DocumentReader r = new DocumentReader(doc);
		ErrorCollector errorHandler = new ErrorCollector();
		CompilerEnvirons env = createCompilerEnvironment(errorHandler);
		long start = System.currentTimeMillis();
		try {
			Parser parser = new Parser(env);
			astRoot = parser.parse(r, null, 0);
			long time = System.currentTimeMillis() - start;
			result.setParseTime(time);
		} catch (IOException ioe) { // Never happens
			result.setError(ioe);
			ioe.printStackTrace();
		} catch (RhinoException re) {
			// Shouldn't happen since we're passing an ErrorCollector in
			int line = re.lineNumber();
			// if (line>0) {
			Element elem = root.getElement(line);
			int offs = elem.getStartOffset();
			int len = elem.getEndOffset() - offs - 1;
			String msg = re.details();
			result.addNotice(new DefaultParserNotice(this, msg, line, offs, len));
			// }
		} catch (Exception e) {
			result.setError(e); // catch all
		}

		r.close();

		// Get any parser errors.
		List errors = errorHandler.getErrors();
		if (errors != null && errors.size() > 0) {
			for (Iterator i = errors.iterator(); i.hasNext();) {
				ParseProblem problem = (ParseProblem) i.next();
				int offs = problem.getFileOffset();
				int len = problem.getLength();
				int line = root.getElementIndex(offs);
				String desc = problem.getMessage();
				DefaultParserNotice notice = new DefaultParserNotice(this,
						desc, line, offs, len);
				if (problem.getType() == ParseProblem.Type.Warning) {
					notice.setLevel(ParserNotice.WARNING);
				}
				result.addNotice(notice);
			}
		}

		// addNotices(doc);
		support.firePropertyChange(PROPERTY_AST, null, astRoot);
		return result;

	}


	/**
	 * Removes a property change listener from this parser.
	 *
	 * @param prop The property that was being listened to.
	 * @param l The listener to remove.
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
		support.removePropertyChangeListener(prop, l);
	}

}