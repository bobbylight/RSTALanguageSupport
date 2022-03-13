/*
 * 08/14/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.xml;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;


/**
 * Configuration information for validating an XML file against an XML schema.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SchemaValidationConfig implements ValidationConfig {

	private Schema schema;


	public SchemaValidationConfig(String language, InputStream in)
			throws IOException {

		SchemaFactory sf = SchemaFactory.newInstance(language);

        try (BufferedInputStream bis = new BufferedInputStream(in)) {
            schema = sf.newSchema(new StreamSource(bis));
        } catch (SAXException se) {
            se.printStackTrace();
            throw new IOException(se.toString());
        }

	}


	@Override
	public void configureParser(XmlParser parser) {
		SAXParserFactory spf = parser.getSaxParserFactory();
		spf.setValidating(false); // Not using a DTD
		if (schema!=null) {
			spf.setSchema(schema);
		}
	}


	@Override
	public void configureHandler(XmlParser.Handler handler) {
		handler.setEntityResolver(null); // Not used in schema validation
	}


}
