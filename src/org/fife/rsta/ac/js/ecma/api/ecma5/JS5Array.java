package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ArrayFunctions;


/**
 * Object Array
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Array extends JSArray implements JS5ArrayFunctions {
	
	/**
	 * Object JS5Array()
	 * 
	 * @constructor
	 * @extends JS5Object
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Array() {

	}
	
	/**
	 * <b>function isArray(o)</b> test whether argument is an array.
	 * 
	 * @param o object to test.
	 * @returns <b><i>true</b></i> if object is of type array, otherwise <b><i>false</b></i>.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JSBoolean isArray(JS5Object o) {return null;}
	
	
}
