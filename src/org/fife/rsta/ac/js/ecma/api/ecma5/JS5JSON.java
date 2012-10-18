package org.fife.rsta.ac.js.ecma.api.ecma5;

/**
 * Object JSON
 * @since Standard ECMA-262 5th. Edition
 */
public abstract class JS5JSON  {
	
	
	/**
	 * function parse(s, reviver)
	 * 
	 * @memberOf Date
	 * @param {String} s
	 * @param {JS5Function} reviver
	 * @returns {Object}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5JSON JSON
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object parse(JS5String s, JS5Function reviver){return null;} 
	
	/**
	 * function stringify(o, filter, indent)
	 * 
	 * @memberOf Date
	 * @param {Object} o
	 * @param {JS5Function} filter
	 * @param {JS5Object} indent
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5JSON JSON
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object stringify(JS5Object o, JS5Function filter, JS5Object indent){return null;} 
}
