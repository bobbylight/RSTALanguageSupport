package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSDate;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5DateFunctions;

/**
 * Object Boolean
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Date extends JSDate implements JS5DateFunctions {

	/**
	 * function now()
	 * 
	 * @memberOf Date
	 * @param {String} string
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @static
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber now(){return null;}
}
