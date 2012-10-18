package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSArrayFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;



public interface JS5ArrayFunctions extends JS5ObjectFunctions, JSArrayFunctions {
	
	/**
	 * function every(predicate, o)
	 * 
	 * @param {Function} predicate
	 * @param {Object} o
	 * @returns {Boolean}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSBoolean every(JS5Function predicate, JS5Object o);
	
	
	/**
	 * function filter(predicate, o)
	 * 
	 * @param {Function} predicate
	 * @param {Object} o
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Array filter(JS5Function predicate, JS5Object o);
	
	
	/**
	 * function forEach(f, o)
	 * 
	 * @param {Function} f
	 * @param {Object} o
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public void forEach(JS5Function f, JS5Object o);
	
	
	/**
	 * function indexOf(value, start)
	 * 
	 * @param {Object} value
	 * @param {Number} start
	 * @returns {Number}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSNumber indexOf(JS5Object value, JSNumber start);
	
	
	/**
	 * function lastIndexOf(value, start)
	 * 
	 * @param {Object} value
	 * @param {Number} start
	 * @returns {Number}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSNumber lastIndexOf(JS5Object value, JSNumber start);
	
	
	/**
	 * function map(f, o)
	 * 
	 * @param {Function} f
	 * @param {Object} o
	 * @returns {Array}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Array map(JS5Function f, JS5Object o);
	
	/**
	 * function reduce(f, initial)
	 * 
	 * @param {Function} f
	 * @param {Object} initial
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Object reduce(JS5Function f, JS5Object initial);
	
	
	/**
	 * function reduceRight(f, initial)
	 * 
	 * @param {Function} f
	 * @param {Object} initial
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Object reduceRight(JS5Function f, JS5Object initial);
	
	
	/**
	 * function some(predicate, o)
	 * 
	 * @param {Function} predicate
	 * @param {Object} o
	 * @returns {Boolean}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSBoolean some(JS5Function predicate, JS5Object o);
}
