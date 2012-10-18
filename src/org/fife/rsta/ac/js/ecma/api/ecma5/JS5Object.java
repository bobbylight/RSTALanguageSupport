package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


/**
 * Base JavaScript Object
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Object extends JSObject implements JS5ObjectFunctions {
	
	/**
     * function create(proto, descriptors)
     * @param {Object} proto
     * @param {Object} descriptors
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object create(JS5Object proto, JS5Object descriptors){return null;}
	
	/**
     * function defineProperties(o, descriptors)
     * @param {Object} o
     * @param {Object} descriptors
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object defineProperties(JS5Object o, JS5Object descriptors){return null;}
	
	/**
     * function defineProperty(o, name, desc)
     * @param {Object} o
     * @param {String} name
     * @param {Object} desc
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object defineProperty(JS5Object o, JS5String name, JS5Object desc){return null;}
	
	/**
     * function freeze(o)
     * @param {Object} o
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object freeze(JS5Object o){return null;}
	
	/**
     * function getOwnPropertyDescriptor(o, name)
     * @param {Object} o
     * @param {String} name
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object getOwnPropertyDescriptor(JS5Object o, JS5String name){return null;}
	
	/**
     * function getOwnPropertyNames(o)
     * @param {Object} o
     * @returns {Array}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Array getOwnPropertyNames(JS5Object o){return null;}
	
	/**
     * function getPrototypeOf(o)
     * @param {Object} o
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object getPrototypeOf(JS5Object o){return null;}
	
	/**
     * function isExtensible(o)
     * @param {Object} o
     * @returns {Boolean}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JSBoolean isExtensible(JS5Object o){return null;}
	
	/**
     * function isFrozen(o)
     * @param {Object} o
     * @returns {Boolean}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JSBoolean isFrozen(JS5Object o){return null;}
	
	/**
     * function isSealed(o)
     * @param {Object} o
     * @returns {Boolean}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JSBoolean isSealed(JS5Object o){return null;}
	
	/**
     * function keys(o)
     * @param {Object} o
     * @returns {Array}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Array keys(JS5Object o){return null;}
	
	/**
     * function preventExtensions(o)
     * @param {Object} o
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object preventExtensions(JS5Object o){return null;}
	
	/**
     * function seal(o)
     * @param {Object} o
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
     * @since   Standard ECMA-262 5th. Edition 
     * 
     */
	public static JS5Object seal(JS5Object o){return null;}
	
}
