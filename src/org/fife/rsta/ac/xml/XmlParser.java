/*
 * 04/07/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.xml;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;


/**
 * Parses XML code in an <code>RSyntaxTextArea</code>.  Currently, this
 * class does not squiggle underline errors, but is simply a hook so that
 * <code>XmlOutlineTree</code>s know when to re-parse the contents of the
 * editor.  In the future, this language support will be more like others.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class XmlParser extends AbstractParser {

	private XmlLanguageSupport support;


	public XmlParser(XmlLanguageSupport support) {
		this.support = support;
	}


	/**
	 * {@inheritDoc}
	 */
	public ParseResult parse(RSyntaxDocument doc, String style) {
		support.refreshOutlineTrees();
		return null;
	}


}