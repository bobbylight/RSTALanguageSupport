package org.fife.rsta.ac.js.ecma.api;

public abstract class JSError extends JSObject {

	/**
	 * Object Error(message)
	 * 
	 * @super Object
	 * @constructor
	 * @param {String} message
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSError() {
	}


	/**
	 * property name
	 * 
	 * @type String
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static String name;

	/**
	 * property message
	 * 
	 * @type String
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static String message;

}
