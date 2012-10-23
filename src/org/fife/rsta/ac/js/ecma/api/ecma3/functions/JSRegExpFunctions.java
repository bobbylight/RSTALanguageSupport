package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;


public interface JSRegExpFunctions extends JSObjectFunctions{

	/**
	 * <b>function exec(string)</b> general purpose pattern matching.
	 * <h4>Example</h4>
	 * <code>
	 * 	var r = new RegExp("/\bJava\w*\b/g");<br>
	 * 	var text = "JavaScript is not the same as Java";<br>
	 *  while((result = e.exec(text)) != null)<br>
	 *  {<br>
	 *  &nbsp;&nbsp;alert("Matched: " + result[0]);<br>
	 *  }<br>
	 * </code> 
	 * @param string The string to be searched
	 * @returns An array containing results on the match or <b><i>null</i></b> if no match is found.
	 * @type Array
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @see #test(String) test()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray exec(String string);


	/**
	 * <b>function test(string)</b> test whether a string matches a pattern
	 * <h4>Example</h4>
	 * <code>
	 * 	var r = new RegExp("/java/i");<br>
	 * 	r.test("JavaScript"); //returns true<br>
	 *  r.test("ECMAScript"); //returns false<br>
	 * </code> 
	 * @param string The string to be tested
	 * @returns <b><i>true</i></b> if <b><i>string</i></b> contains text that matches <b><i>regexp</i></b>, otherwise <b><i>false</i></b>.
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @see #exec(String) exec()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSBoolean test(String string);

}
