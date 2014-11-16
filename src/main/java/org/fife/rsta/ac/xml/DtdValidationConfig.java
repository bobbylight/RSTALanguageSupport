/*
 * 08/14/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.xml;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;


/**
 * Configuration information for validating XML against a DTD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class DtdValidationConfig implements ValidationConfig {

	private EntityResolver entityResolver;


	public DtdValidationConfig(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}


	public void configureParser(XmlParser parser) {
		SAXParserFactory spf = parser.getSaxParserFactory();
		spf.setValidating(true);
		spf.setSchema(null);
	}


	public void configureHandler(XmlParser.Handler handler) {
		handler.setEntityResolver(entityResolver);
	}


}