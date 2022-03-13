package org.fife.rsta.ac.js.ecma.api.ecma5;

/**
 * Object JSON
 * @since Standard ECMA-262 5th. Edition
 */
public abstract class JS5JSON  {
	
	
	/**
	 * <b>function parse(s, reviver)</b> parse a JSON-formatted string.
	 * 
	 * @memberOf Date
	 * @param s The string to be parsed.
	 * @param reviver An optional argument function that can transform parsed values. 
	 * @returns {Object}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5JSON JSON
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object parse(JS5String s, JS5Function reviver){return null;} 
	
	/**
	 * <b>function stringify(o, filter, indent)</b> serialize an object, array or primitive value. 
	 * 
	 * @memberOf Date
	 * @param o The object, array or primitive value to convert to JSON string.
	 * @param filter An optional function that can replace values before stringification.
	 * @param indent An optional argument that specifies am indentation string or number of spaces to use for indentation. 
	 * @returns A JSON formatted string representing the value <b><i>o</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5JSON JSON
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object stringify(JS5Object o, JS5Function filter, JS5Object indent){return null;} 
}
