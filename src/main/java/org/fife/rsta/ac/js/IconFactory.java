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
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.fife.ui.autocomplete.EmptyIcon;


/**
 * Holds icons used by JavaScript auto-completion.
 * 
 * @author Robert Futrell
 * @version 1.0
 */
public class IconFactory {

	public static final String FUNCTION_ICON = "function";
	public static final String LOCAL_VARIABLE_ICON = "local_variable";
	public static final String TEMPLATE_ICON = "template";
	public static final String EMPTY_ICON = "empty";
	public static final String GLOBAL_VARIABLE_ICON = "global_variable";
	public static final String DEFAULT_FUNCTION_ICON = "default_function";
	public static final String PUBLIC_STATIC_FUNCTION_ICON = "public_static_function";
	public static final String STATIC_VAR_ICON = "static_var";
	public static final String DEFAULT_VARIABLE_ICON = "default_variable";
	public static final String DEFAULT_CLASS_ICON = "default_class";
	public static final String PUBLIC_METHOD_ICON = "methpub_obj";
	public static final String PUBLIC_FIELD_ICON = "field_public_obj";
	public static final String JSDOC_ITEM_ICON		= "jsdoc_item";

	private Map<String, Icon> iconMap;

	private static final IconFactory INSTANCE = new IconFactory();


	private IconFactory() {

		iconMap = new HashMap<String, Icon>();

		iconMap.put(FUNCTION_ICON, loadIcon("methpub_obj.gif"));
		iconMap.put(PUBLIC_STATIC_FUNCTION_ICON, loadIcon("methpub_static.gif"));
		iconMap.put(LOCAL_VARIABLE_ICON, loadIcon("localvariable_obj.gif"));
		iconMap.put(GLOBAL_VARIABLE_ICON, loadIcon("field_public_obj.gif"));
		iconMap.put(TEMPLATE_ICON, loadIcon("template_obj.gif"));
		iconMap.put(DEFAULT_FUNCTION_ICON, loadIcon("methdef_obj.gif"));
		iconMap.put(STATIC_VAR_ICON, loadIcon("static_co.gif"));
		iconMap.put(DEFAULT_VARIABLE_ICON, loadIcon("field_default_obj.gif"));
		iconMap.put(DEFAULT_CLASS_ICON, loadIcon("class_obj.gif"));
		iconMap.put(PUBLIC_METHOD_ICON, loadIcon("methpub_obj.gif"));
		iconMap.put(PUBLIC_FIELD_ICON, loadIcon("field_public_obj.gif"));
		iconMap.put(JSDOC_ITEM_ICON, loadIcon("jdoc_tag_obj.gif"));
		iconMap.put(EMPTY_ICON, new EmptyIcon(16));

	}


	private Icon getIconImage(String name) {
		return iconMap.get(name);
	}


	public static Icon getIcon(String name) {
		return INSTANCE.getIconImage(name);
	}


	public static String getEmptyIcon() {
		return EMPTY_ICON;
	}


	/**
	 * Loads an icon.
	 * 
	 * @param name The file name of the icon to load.
	 * @return The icon.
	 */
	private Icon loadIcon(String name) {
		name = "org/fife/rsta/ac/js/img/" + name;
		URL res = getClass().getClassLoader().getResource(name);
		if (res == null) { // Never happens
			// IllegalArgumentException is what would be thrown if res
			// was null anyway, we're just giving the actual arg name to
			// make the message more descriptive
			throw new IllegalArgumentException("icon not found: " + name);
		}
		return new ImageIcon(res);
	}

}