package org.fife.rsta.ac.js.ecma.api;

public class JSArray extends JSObject {

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
	public JSNumber length;
	
	/**
    * Property prototype
    * 
    * @type Array
    * @memberOf Array
    * @see Array
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSArray protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Array
    * @see Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;


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
	public JSArray concat(JSArray args){return null;}


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
	public JSArray join(String seperator){return null;}


	/**
	 * function pop()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject pop(){return null;}


	/**
	 * function push(args)
	 * 
	 * @param {Array} args
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public void push(JSArray array){}


	/**
	 * function reverse()
	 * 
	 * @returns {Array}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray reverse(){return null;}


	/**
	 * function shift()
	 * 
	 * @returns {Object}
	 * @memberOf Array
	 * @see Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject shift(){return null;}


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
	public JSArray slice(Number start, Number end){return null;}


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
	public JSArray sort(JSFunction function){return null;}


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
	public JSArray splice(JSNumber start, JSNumber deletecount,
			JSArray items){return null;}


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
	public JSArray unshift(JSArray start){return null;}

}
