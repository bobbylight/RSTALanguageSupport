package org.fife.rsta.ac.js.ecma.api;

public abstract class JSArray extends JSObject {

	/**
	 * Object JSArray()
	 * 
	 * @constructor
	 * @extends JSObject
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray() {

	}


	/**
	 * Property length
	 * 
	 * @type JSNumber
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber length;


	/**
	 * function concat(args)
	 * 
	 * @param {Array} args
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray concat(JSArray args);


	/**
	 * function join(seperator)
	 * 
	 * @param {String} seperator
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray join(String seperator);


	/**
	 * function pop()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSObject pop();


	/**
	 * function push(args)
	 * 
	 * @param {Array} args
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract void push(JSArray array);


	/**
	 * function reverse()
	 * 
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray reverse();


	/**
	 * function shift()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSObject shift();


	/**
	 * function slice(start, end)
	 * 
	 * @param {Number} start
	 * @param {Number} end
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray slice(Number start, Number end);


	/**
	 * function sort(funct)
	 * 
	 * @param {Function} funct
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray sort(JSFunction function);


	/**
	 * function splice(start, deletecount, items)
	 * 
	 * @param {Number} start
	 * @param {Number} deletecount
	 * @param {Array} items
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray splice(JSNumber start, JSNumber deletecount,
			JSArray items);


	/**
	 * function unshift(items)
	 * 
	 * @param {Array} start
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSArray unshift(JSArray start);

}
