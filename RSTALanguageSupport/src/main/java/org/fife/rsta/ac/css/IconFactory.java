/*
 * 11/28/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
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
final class IconFactory {

	private static IconFactory instance;

	private Map<String, Icon> iconMap;


	/**
	 * Private constructor to prevent instantiation.
	 */
	private IconFactory() {
		iconMap = new HashMap<>();
	}


	/**
	 * Returns the singleton instance of this class.
	 *
	 * @return The singleton instance.
	 */
	public static IconFactory get() {
		if (instance ==null) {
			instance = new IconFactory();
		}
		return instance;
	}


	/**
	 * Returns the icon requested.
	 *
	 * @param key The key for the icon.
	 * @return The icon.
	 */
	public Icon getIcon(String key) {
		Icon icon = iconMap.get(key);
		if (icon==null) {
			icon = loadIcon(key + ".gif");
			iconMap.put(key, icon);
		}
		return icon;
	}


	/**
	 * Loads an icon by file name.
	 *
	 * @param name The icon file name.
	 * @return The icon.
	 */
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
