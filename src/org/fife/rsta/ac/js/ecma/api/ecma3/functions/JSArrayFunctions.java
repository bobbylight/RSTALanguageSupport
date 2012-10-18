package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;


public interface JSArrayFunctions extends JSObjectFunctions {
	/**
	 * function concat(args)
	 * 
	 * @param {Array} args
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray concat(JSArray args);


	/**
	 * function join(seperator)
	 * 
	 * @param {String} seperator
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray join(String seperator);


	/**
	 * function pop()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject pop();


	/**
	 * function push(args)
	 * 
	 * @param {Array} args
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public void push(JSArray array);


	/**
	 * function reverse()
	 * 
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray reverse();


	/**
	 * function shift()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject shift();


	/**
	 * function slice(start, end)
	 * 
	 * @param {Number} start
	 * @param {Number} end
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray slice(Number start, Number end);


	/**
	 * function sort(function)
	 * 
	 * @param {Function} function
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray sort(JSFunction function);


	/**
	 * function splice(start, deletecount, items)
	 * 
	 * @param {Number} start
	 * @param {Number} deletecount
	 * @param {Array} items
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray splice(JSNumber start, JSNumber deletecount, JSArray items);


	/**
	 * function unshift(items)
	 * 
	 * @param {Array} start
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray unshift(JSArray start);
}
