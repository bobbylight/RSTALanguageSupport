/*
 * 11/28/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.css;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * The icons for CSS properties and values.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class IconFactory {

	private static IconFactory INSTANCE;

	private Map<String, Icon> iconMap;


	private static final String[] IMAGE_FILES = {
		"aural_props",
		"boxmodel_props",
		"colback_props",
		"content_props",
		"css_propertyvalue_function",
		"css_propertyvalue_identifier",
		"css_propertyvalue_unit",
		"font_props",
		"pagedmedia_props",
		"table_props",
		"text_props",
		"ui_props",
		"visual_props",
	};


	private IconFactory() {

		iconMap = new HashMap<String, Icon>();
		for (String imageFile : IMAGE_FILES) {
			iconMap.put(imageFile, loadIcon(imageFile + ".gif"));
		}

	}


	public static IconFactory get() {
		if (INSTANCE==null) {
			INSTANCE = new IconFactory();
		}
		return INSTANCE;
	}


	public Icon getIcon(String key) {
		return iconMap.get(key);
	}


	private Icon loadIcon(String name) {
		URL res = getClass().getResource("img/" + name);
		if (res==null) {
			// IllegalArgumentException is what would be thrown if res
			// was null anyway, we're just giving the actual arg name to
			// make the message more descriptive
			throw new IllegalArgumentException("icon not found: img/" + name);
		}
		return new ImageIcon(res);
	}


}