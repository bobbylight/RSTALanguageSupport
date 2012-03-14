package org.fife.rsta.ac.js.ecma.api;

public abstract class JSRegExp extends JSObject {

	/**
	 * Object RegExp()
	 * 
	 * @super Object
	 * @constructor
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSRegExp() {
	};


	/**
	 * function exec(string)
	 * 
	 * @param {String} string
	 * @returns {Array}
	 * @type Array
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray exec(String string);


	/**
	 * function test(string)
	 * 
	 * @param {String} string
	 * @returns {Boolean}
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Boolean test(String string);


	/**
	 * property source
	 * 
	 * @type String
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static String source;

	/**
	 * property global
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static Boolean global;

	/**
	 * property ignoreCase
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static Boolean ignoreCase;

	/**
	 * property multiline
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static Boolean multiline;

	/**
	 * property lastIndex
	 * 
	 * @type Number
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static Number lastIndex;

}
