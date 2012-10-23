package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSArrayFunctions extends JSObjectFunctions {
	/**
	 * <b>function concat(args)</b> creates and returns a new <b>array</b> that is the result of concatenating each
	 * of its arguments to an array.
	 * <h4>Example</h4>
	 * <code>
	 * 	var c = [1,2,3];<br>
	 * 	c.concat(4,5); //Returns [1,2,3,4,5]<br>
	 *  c.concat([4,5]); //Returns [1,2,3,4,5]<br>
	 *  c.concat(4,[5,[6, 7]]); //Returns [1,2,3,4,5, [6,7]]<br>
	 * </code> 
	 * 
	 * @param args... Any number of values to be concatenated to an <b>array</b>
	 * @returns A new <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray concat(JSArray args);


	/**
	 * <b>function join(separator)</b> converts each element of the <b>array</b> into a string. Uses the separator between the elements. 
	 * <h4>Example</h4>
	 * <code>
	 * 	var a = [1,2,3];<br>
	 * 	var s = a.join("|"); //s is the string "1|2|3"<br>
	 *  
	 * </code> 
	 * 
	 * @param <i>separator</i> An optional character or string used to separate each element with the string.  
	 * @returns A string representing the result of all the elements in the <b>array</b> concatenated together, separated by the separator. 
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString join(String seperator);


	/**
	 * <b>function pop()</b> removes and returns the last element of an <b>array</b>.
	 * <h4>Example</h4>
	 * <code>
	 * 	var stack = [1,2,3];<br>
	 * 	stack.pop(); // returns 3, stack [1, 2]<br>
	 * 	stack.pop(); // returns 2,  stack [1]<br>
	 *  stack.pop(); // returns 1,  stack []<br>
	 * </code> 
	 * @returns The last element of the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #push(JSArray) push()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject pop();


	/**
	 * <b>function push(args)</b> append elements to an <b>array</b>.
	 * <h4>Example</h4>
	 * <code>
	 * 	var vals = [];<br>
	 * 	vals.push(1,2,3); // returns new array [1,2,3]<br>
	 * </code> 
	 * @param args One or more values to be appended to the end of the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #pop()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public void push(JSArray array);


	/**
	 * <b>function reverse()</b> reverse the elements of an <b>array</b>
	 * <h4>Example</h4>
	 * <code>
	 * 	var r = [1,2,3];<br>
	 * 	r.reverse(); // r is now [3,2,1]<br>
	 * </code> 
	 * @returns The <b>array</b> after it has been reversed
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray reverse();


	/**
	 * <b>function shift()</b> shift <b>array</b> elements down
	 * <h4>Example</h4>
	 * <code>
	 * 	var s = [1,2,3];<br>
	 * 	s.shift(); // Returns 1; s = [2,3]<br>
	 *  s.shift(); // Returns 2; s = [3]<br>
	 * </code> 
	 * @returns The former first element of the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #pop() pop()
	 * @see #unshift(JSArray) unshift()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject shift();


	/**
	 * <b>function slice(start, end)</b> return a portion of an <b>array</b>
	 * <h4>Example</h4>
	 * <code>
	 * 	var s = [1,2,3,4,5];<br>
	 * 	s.slice(0,3); // Returns [1,2,3]<br>
	 *  s.slice(3); // Returns [4,5]<br>
	 *  s.slice(1,-1); // Returns [2,3,4]<br>
	 * </code> 
	 * @param start The array index from where to begin.  If negative, this argument specifies a position measured from the end of the array. 
	 * @param end The array index immediately after the end of the slice. If not specified then the slice includes all the array elements from the start to the end of the array.
	 * @returns A new <b>array</b> containing elements from the <b><i>start</b></i> up to, but not including the <b><i>end</i></b> of the slice.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #splice(JSNumber, JSNumber, JSArray) splice();
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray slice(Number start, Number end);


	/**
	 * <b>function sort(function)</b> sort the elements of an <b>array</b>
	 * <h4>Example</h4>
	 * <code>
	 *  function numbersort (a,b){return a-b;}<br>
	 * 	var s = new Array(22,1111,33,4,55]);<br>
	 * 	s.sort(); //Alphabetical order : 1111,22,33,4,55<br>
	 *  s.sort(numbersort); //Numerical order : 4, 22, 33, 55, 1111<br>
	 * </code> 
	 * @param <i>function</i> an optional function used to specify the sorting order
	 * @returns A reference to the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray sort(JSFunction function);


	/**
	 * <b>function splice(start, deletecount, items)</b> insert, remove, or replace <b>array</b> elements
	 * <h4>Example</h4>
	 * <code>
	 *  var s = [1,2,3,4,5,6,7,8];<br>
	 * 	s.splice(1,2); //Returns [2,3]; s is [1,4]<br>
	 *  s.splice(1,1); //Returns [4]; s is [1]<br>
	 *  s.splice(1,0,2,3); //Returns []; s is [1 2 3]<br>
	 * </code> 
	 * @param start the <b>array</b> element at which the insertion and/or deletion is to begin
	 * @param <i>deletecount</i> The number of elements starting with and including <i><b>start</b></i>. 
	 * @param items zero or more items to be inserted into the <b>array</b>
	 * @returns An <b>array</b> containing the elements, if any, deleted from the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #shift() shift();
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray splice(JSNumber start, JSNumber deletecount, JSArray items);


	/**
	 * <b>function unshift(items)</b> insert elements at the beginning of an <b>array</b>
	 * <h4>Example</h4>
	 * <code>
	 *  var s = [];<br>
	 * 	s.unshift(1,2); //Returns 2; s is [1,2]<br>
	 *  s.unshift(22); //Returns 3; s is [22,1,2]<br>
	 *  s.shift(); //Returns 22; s is [1,2]<br>
	 * </code> 
	 * @param value One or more values to insert at the beginning of the <b>array</b>
	 * @returns The new length of the array
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #shift() shift()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber unshift(JSArray value);
}
