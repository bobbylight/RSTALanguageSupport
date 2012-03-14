/*
 * 01/29/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;


/**
 * Holds icons used by JavaScript auto-completion.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class IconFactory {

	public static final int FUNCTION_ICON = 0;
	public static final int LOCAL_VARIABLE_ICON = 1;
	public static final int PUBLIC_STATIC_VARIABLE_ICON = 2;
	public static final int PUBLIC_VARIABLE_ICON = 3;

	private Icon[] icons;

	private static final IconFactory INSTANCE = new IconFactory();


	private IconFactory() {
		icons = new Icon[4];
		icons[FUNCTION_ICON] = loadIcon("methdef_obj.gif");
		icons[LOCAL_VARIABLE_ICON] = loadIcon("field_default_obj.gif");
		icons[PUBLIC_STATIC_VARIABLE_ICON] = loadIcon("static_co.gif");
		icons[PUBLIC_VARIABLE_ICON] = loadIcon("field_public_obj.gif");
	}


	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return The singleton instance.
	 */
	public static IconFactory get() {
		return INSTANCE;
	}


	/**
	 * Returns the specified icon.
	 * 
	 * @param key The icon to retrieve.
	 * @return The icon.
	 */
	public Icon getIcon(int key) {
		return icons[key];
	}


	/**
	 * Loads an icon.
	 * 
	 * @param name The file name of the icon to load.
	 * @return The icon.
	 */
	private Icon loadIcon(String name) {
		URL res = getClass().getResource("img/" + name);
		if (res == null) { // Never happens
			// IllegalArgumentException is what would be thrown if res
			// was null anyway, we're just giving the actual arg name to
			// make the message more descriptive
			throw new IllegalArgumentException("icon not found: img/" + name);
		}
		return new ImageIcon(res);
	}

}