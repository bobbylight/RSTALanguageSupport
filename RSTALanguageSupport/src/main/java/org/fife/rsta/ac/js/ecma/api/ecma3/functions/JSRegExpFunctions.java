package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;


public interface JSRegExpFunctions extends JSObjectFunctions {

	/**
	 * <b>function exec(string)</b> general purpose pattern matching.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var r = new RegExp("/\bJava\w*\b/g");
	 * var text = "JavaScript is not the same as Java";
	 * while ((result = e.exec(text)) != null)
	 * {
	 *   alert("Matched: " + result[0]);
	 * }
	 * </pre>
	 *
	 * @param string The string to be searched
	 * @returns An array containing results on the match or <b><i>null</i></b> if no match is found.
	 * @type Array
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @see #test(String) test()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray exec(String string);


	/**
	 * <b>function test(string)</b> test whether a string matches a pattern.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var r = new RegExp("/java/i");
	 * r.test("JavaScript"); //returns true
	 * r.test("ECMAScript"); //returns false
	 * </pre>
	 *
	 * @param string The string to be tested
	 * @returns <b><i>true</i></b> if <b><i>string</i></b> contains text that matches <b><i>regexp</i></b>, otherwise <b><i>false</i></b>.
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
	 * @see #exec(String) exec()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSBoolean test(String string);

}
