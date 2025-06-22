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
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var c = [1,2,3];
	 * c.concat(4,5); //Returns [1,2,3,4,5]
	 * c.concat([4,5]); //Returns [1,2,3,4,5]
	 * c.concat(4,[5,[6, 7]]); //Returns [1,2,3,4,5, [6,7]]
	 * </pre>
	 *
	 * @param args Any number of values to be concatenated to an <b>array</b>.
	 * @returns A new <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray concat(JSArray args);


	/**
	 * <b>function join(separator)</b> converts each element of the <b>array</b> into a string. Uses the separator between the elements.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var a = [1,2,3];
	 * var s = a.join("|"); //s is the string "1|2|3"
	 * </pre>
	 *
	 * @param separator An optional character or string used to separate each element with the string.
	 * @returns A string representing the result of all the elements in the <b>array</b> concatenated together, separated by the separator.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString join(String separator);


	/**
	 * <b>function pop()</b> removes and returns the last element of an <b>array</b>.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var stack = [1,2,3];
	 * stack.pop(); // returns 3, stack [1, 2]
	 * stack.pop(); // returns 2,  stack [1]
	 * stack.pop(); // returns 1,  stack []
	 * </pre>
	 *
	 * @returns The last element of the <b>array</b>.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #push(JSArray) push()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSObject pop();


	/**
	 * <b>function push(args)</b> append elements to an <b>array</b>.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var vals = [];
	 * vals.push(1,2,3); // returns new array [1,2,3]
	 * </pre>
	 *
	 * @param array One or more values to be appended to the end of the <b>array</b>.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #pop()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	void push(JSArray array);


	/**
	 * <b>function reverse()</b> reverse the elements of an <b>array</b>.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var r = [1,2,3];
	 * r.reverse(); // r is now [3,2,1]
	 * </pre>
	 *
	 * @returns The <b>array</b> after it has been reversed.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray reverse();


	/**
	 * <b>function shift()</b> shift <b>array</b> elements down.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var s = [1,2,3];
	 * s.shift(); // Returns 1; s = [2,3]
	 * s.shift(); // Returns 2; s = [3]
	 * </pre>
	 *
	 * @returns The former first element of the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #pop() pop()
	 * @see #unshift(JSArray) unshift()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSObject shift();


	/**
	 * <b>function slice(start, end)</b> return a portion of an <b>array</b>
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var s = [1,2,3,4,5];
	 * s.slice(0,3); // Returns [1,2,3]
	 * s.slice(3); // Returns [4,5]
	 * s.slice(1,-1); // Returns [2,3,4]
	 * </pre>
	 *
	 * @param start The array index from where to begin.  If negative, this argument specifies a position measured from the end of the array.
	 * @param end The array index immediately after the end of the slice. If not specified then the slice includes all the array elements from the start to the end of the array.
	 * @returns A new <b>array</b> containing elements from the {@code start} up to, but not including the <b><i>end</i></b> of the slice.
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #splice(JSNumber, JSNumber, JSArray) splice();
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray slice(Number start, Number end);


	/**
	 * <b>function sort(function)</b> sort the elements of an <b>array</b>
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * function numbersort(a,b) {return a-b;}
	 * var s = new Array(22,1111,33,4,55]);
	 * s.sort(); //Alphabetical order : 1111,22,33,4,55
	 * s.sort(numbersort); //Numerical order : 4, 22, 33, 55, 1111
	 * </pre>
	 *
	 * @param function an optional function used to specify the sorting order
	 * @returns A reference to the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray sort(JSFunction function);


	/**
	 * <b>function splice(start, deletecount, items)</b> insert, remove, or replace <b>array</b> elements
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var s = [1,2,3,4,5,6,7,8];
	 * s.splice(1,2); //Returns [2,3]; s is [1,4]
	 * s.splice(1,1); //Returns [4]; s is [1]
	 * s.splice(1,0,2,3); //Returns []; s is [1 2 3]
	 * </pre>
	 *
	 * @param start the <b>array</b> element at which the insertion and/or deletion is to begin
	 * @param deletecount The number of elements starting with and including <i><b>start</b></i>.
	 * @param items zero or more items to be inserted into the <b>array</b>
	 * @returns An <b>array</b> containing the elements, if any, deleted from the <b>array</b>
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #shift() shift();
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray splice(JSNumber start, JSNumber deletecount, JSArray items);


	/**
	 * <b>function unshift(items)</b> insert elements at the beginning of an <b>array</b>
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var s = [];
	 * s.unshift(1,2); //Returns 2; s is [1,2]
	 * s.unshift(22); //Returns 3; s is [22,1,2]
	 * s.shift(); //Returns 22; s is [1,2]
	 * </pre>
	 *
	 * @param value One or more values to insert at the beginning of the <b>array</b>
	 * @returns The new length of the array
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
	 * @see #shift() shift()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber unshift(JSArray value);
}
