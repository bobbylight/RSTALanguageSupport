package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5StringFunctions;


/**
 * Object String
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5String extends JSString implements JS5StringFunctions {


	/**
     * Object String(s)
     *
     * @constructor
     * @extends Object
     * @param s The value to be stored in a String or converted to a primitive type
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
	public JS5String(JSString s) {
		super(s);
	}
}
