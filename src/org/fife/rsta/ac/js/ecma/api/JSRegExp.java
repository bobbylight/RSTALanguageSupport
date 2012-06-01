package org.fife.rsta.ac.js.ecma.api;

public class JSRegExp extends JSObject {

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
	public JSArray exec(String string){return null;}


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
	public JSBoolean test(String string){return null;}


	/**
	 * property source
	 * 
	 * @type String
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSString source;

	/**
	 * property global
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSBoolean global;

	/**
	 * property ignoreCase
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSBoolean ignoreCase;

	/**
	 * property multiline
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSBoolean multiline;

	/**
	 * property lastIndex
	 * 
	 * @type Number
	 * @memberOf RegExp
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber lastIndex;

}
