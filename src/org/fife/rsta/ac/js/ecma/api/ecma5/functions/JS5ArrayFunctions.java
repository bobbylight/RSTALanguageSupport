package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSArrayFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;



public interface JS5ArrayFunctions extends JS5ObjectFunctions, JSArrayFunctions {
	
	/**
	 * <b>function every(predicate, o)</b> test whether predicate is true for every element
	 * <h3>Example</h3>
	 * <code>
	 *  [1,2,3].every(function(x){return x < 5;} //=>true<br>
	 *  [1,2,3].every(function(x){return x < 2;} //=>false<br>
	 * 	[].every(function(x){return false;} //=>true, always true for []<br>
	 * </code> 
	 * @param {Function} predicate A predicate function to test array elements
	 * @param {Object} <i>o</i> The optional <b><i>this</i></b> value for invocations of <b><i>predicate</i></b>.
	 * @returns {Boolean} <b><i>true</i></b> if <b><i>predicate</i></b> is true for every element of the <b>array</b> or <b><i>false</i></b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #filter(JS5Function, JS5Object) filter()
	 * @see #forEach(JS5Function, JS5Object) forEach()
	 * @see #some(JS5Function, JS5Object) some()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSBoolean every(JS5Function predicate, JS5Object o);
	
	
	/**
	 * <b>function filter(predicate, o)</b> return array elements that pass a predicate
	 * <h3>Example</h3>
	 * <code>
	 *  [1,2,3].filter(function(x){return x > 1}); // returns [2,3]<br>
	 * </code> 
	 * @param {Function} predicate The function to invoke to determine whether an element of <b>array</b> will be included in the returned array.
	 * @param {Object} <i>o</i> An optional value on which <b><i>predicate</i></b> is invoked
	 * @returns {Array} a new <b>array</b> containing only those elements of <b>array</b> for which <b><i>predicate</i></b> returned <b><i>true</i></b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #every(JS5Function, JS5Object) every()
	 * @see #forEach(JS5Function, JS5Object) forEach()
	 * @see #indexOf(JS5Object, JSNumber) indexOf()
	 * @see #map(JS5Function, JS5Object) map()
	 * @see #reduce(JS5Function, JS5Object) reduce()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Array filter(JS5Function predicate, JS5Object o);
	
	
	/**
	 * <b>function forEach(f, o)</b> invoke a function for each array element
	 * <h3>Example</h3>
	 * <code>
	 *  var a = [1,2,3];<br>
	 *  a.forEach(function(x,i,a){a[i]++;}); //a is now [2,3,4]<br>
	 * </code> 
	 * @param {Function} f The function to invoke for each element of <b>array</b>
	 * @param {Object} <i>o</i> An optional value on which <b><i>f</b></i> is invoked 
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public void forEach(JS5Function f, JS5Object o);
	
	
	/**
	 * <b>function indexOf(value, start)</b> search an array
	 * <h3>Example</h3>
	 * <code>
	 *  ['a','b','c'].indexOf('b'); //returns 1<br>
	 *  ['a','b','c'].indexOf('d'); //returns -1<br>
	 *  ['a','b','c'].indexOf('a',1); //returns -1<br>
	 * </code> 
	 * @param {Object} value The value to search <b>array</b> for.
	 * @param {Number} <i>start</i> An optional array index at which to begin the search. If omitted, 0 is used.
	 * @returns {Number} The <i>lowest</i> index => start of <b>array</b> at which the element matches <b><i>value</i></b>. Or -1 if no match is found
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #lastIndexOf(JS5Object, JSNumber) lastIndexOf()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSNumber indexOf(JS5Object value, JSNumber start);
	
	
	/**
	 * <b>function lastIndexOf(value, start)</b> search backwards through an array
	 * 
	 * @param {Object} value The value to search <b>array</b> for.
	 * @param {Number} <i>start</i> An optional array index at which to begin the search. If omitted, the search begins at the last element 
	 * @returns {Number} The <i>highest</i> index => start of <b>array</b> at which the element matches <b><i>value</i></b>. Or -1 if no match is found
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #indexOf(JS5Object, JSNumber) indexOf()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSNumber lastIndexOf(JS5Object value, JSNumber start);
	
	
	/**
	 * <b>function map(f, o)</b> compute new array elements from old
	 * <h3>Example</h3>
	 * <code>
	 *  [1,2,3].map(function(x){return x*x;}); //returns [1,4,9]<br>
	 * </code> 
	 * @param {Function} f The function to invoke for each element of <b>array</b>. Its return becomes the elements of the returned array
	 * @param {Object} <i>o</i> An optional value of which <b><i>f</i></b> is invoked 
	 * @returns {Array} A new array with elements computed by function <b><i>f</i></b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #every(JS5Function, JS5Object) every()
	 * @see #filter(JS5Function, JS5Object) filter()
	 * @see #forEach(JS5Function, JS5Object) forEach()
	 * @see #reduce(JS5Function, JS5Object) reduce()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Array map(JS5Function f, JS5Object o);
	
	/**
	 * <b>function reduce(f, initial)</b> compute a value from the elements of an array
	 * <h3>Example</h3>
	 * <code>
	 *  [1,2,3].reduce(function(x,y){return x*y;}); //returns 6 ((1*2(*3))<br>
	 * </code> 
	 * @param {Function} f A function that combines two values and returns a "reduced" value
	 * @param {Object} <i>initial</i> An optional initial value to see the array reduction with. 
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #forEach(JS5Function, JS5Object) forEach()
	 * @see #map(JS5Function, JS5Object) map()
	 * @see #reduceRight(JS5Function, JS5Object) reduceRight()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Object reduce(JS5Function f, JS5Object initial);
	
	
	/**
	 * <b>function reduceRight(f, initial)</b> reduce an array from right-to-left
	 * <h3>Example</h3>
	 * <code>
	 *  [2,10,60].reduceRight(function(x,y){return x/y;}); //returns 3 (60/10)/2<br>
	 * </code> 
	 * @param {Function} f A function that combines two values and returns a "reduced" value
	 * @param {Object} <i>initial</i> An optional initial value to see the array reduction with. 
	 * @returns {Object}
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @see #reduce(JS5Function, JS5Object) reduce()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JS5Object reduceRight(JS5Function f, JS5Object initial);
	
	
	/**
	 * <b>function some(predicate, o)</b> test whether a predicate is true for any element.
	 * <h3>Example</h3>
	 * <code>
	 *  [1,2,3].some(function(x){return x > 5;} //=>false<br>
	 *  [1,2,3].some(function(x){return x > 2;} //=>true<br>
	 * 	[].some(function(x){return true;} //=>false, always false for []<br>
	 * </code> 
	 * @param {Function} predicate A predicate function to test array elements.
	 * @param {Object} <i>o</i> The optional <b><i>this</i></b> value for the invocations of  <b><i>predicate</i></b>.
	 * @returns {Boolean} <b><i>true</i></b> if <b><i>predicate</i></b> returns <b><i>true</i></b> for at least one element of <b>array</b>, otherwise <b><i>false</i></b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array Array
	 * @since Standard ECMA-262 5th. Edition
	 */
	public JSBoolean some(JS5Function predicate, JS5Object o);
}
