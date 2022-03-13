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


/**
 * Configuration information to get an {@link XmlParser} validating against
 * either a DTD or a schema.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public interface ValidationConfig {


	/**
	 * Configures the parser itself.  Called when this config is first set
	 * on an <code>XmlParsre</code>.
	 */
	void configureParser(XmlParser parser);


	/**
	 * Configures the actual handler instance.  Called before each parsing
	 * of the document.
	 */
	void configureHandler(XmlParser.Handler handler);


}
