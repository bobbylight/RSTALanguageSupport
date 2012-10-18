package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;


public interface JSRegExpFunctions extends JSObjectFunctions{

	/**
	 * function exec(string)
	 * 
	 * @param {String} string
	 * @returns {Array}
	 * @type Array
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray exec(String string);


	/**
	 * function test(string)
	 * 
	 * @param {String} string
	 * @returns {Boolean}
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSBoolean test(String string);

}
