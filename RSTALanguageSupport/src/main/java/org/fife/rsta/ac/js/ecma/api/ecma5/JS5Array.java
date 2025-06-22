package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ArrayFunctions;


/**
 * Object Array
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Array extends JSArray implements JS5ArrayFunctions {

	/**
	 * Object Array()
	 *
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Array() {

	}

	/**
	 * Object Array(size)
	 *
	 * @constructor
	 * @extends Object
	 * @param size The desired number of elements in the array. The returned value has its <b>length</b> field set to {@code size}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Array(JSNumber size) {

	}

	/**
	 * Object Array(element0, ... elementn)
	 *
	 * @constructor
	 * @extends Object
	 * @param element0  An argument list of two or more values. The <b>length</b> field set to the number of arguments.
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Array(JSObject element0, JSObject elementn) {

	}

	/**
	 * <b>function isArray(o)</b> test whether argument is an array.
	 *
	 * @param o object to test.
	 * @returns {code true} if object is of type array, otherwise {code false}.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JSBoolean isArray(JS5Object o) {
		return null;
	}


}
