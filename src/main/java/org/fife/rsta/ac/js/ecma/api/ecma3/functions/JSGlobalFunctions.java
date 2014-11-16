package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;



public interface JSGlobalFunctions extends JSObjectFunctions {

	/**
	 * <b>function decodeURI(uri)</b> unescape characters in a URI.
	 * @param uri A string that contains an encoded URI or other text to be decoded. 
	 * @returns A copy of <b><i>uri</i></b>, with any hexidecimal escaped sequences replaced with characters they represent.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #decodeURIComponent(JSString) decodeURIComponent()
	 * @see #encodeURI(JSString) encodeURI()
	 * @see #encodeURIComponent(JSString) encodeURIComponent()
	 * @see #escape(JSString) escape()
	 * @see #unescape(JSString) unescape()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString decodeURI(JSString uri);
	
	/**
	 * <b>function decodeURIComponent(s)</b> unescape characters in a URI component.
	 * @param s A string that contains an encoded URI components or other text to be decoded. 
	 * @returns A copy of <b><i>s</i></b>, with any hexidecimal escaped sequences replaced with characters they represent.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #decodeURI(JSString) decodeURI()
	 * @see #encodeURI(JSString) encodeURI()
	 * @see #encodeURIComponent(JSString) encodeURIComponent()
	 * @see #escape(JSString) escape()
	 * @see #unescape(JSString) unescape()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString decodeURIComponent(JSString s);
	
	
	/**
	 * <b>function encodeURI(uri)</b> escape characters in a URI.
	 * @param uri A string that contains the URI or other text to be encoded. 
	 * @returns A copy of <b><i>uri</i></b>, with certain characters replaced by hexidecimal escape sequences.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #decodeURI(JSString) decodeURI()
	 * @see #decodeURIComponent(JSString) decodeURIComponent()
	 * @see #encodeURIComponent(JSString) encodeURIComponent()
	 * @see #escape(JSString) escape()
	 * @see #unescape(JSString) unescape()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString encodeURI(JSString uri);
	
	/**
	 * <b>function encodeURIComponent(s)</b> escape characters in a URI Component.
	 * @param s A string that contains a portion of a URI or other text to be encoded. 
	 * @returns A copy of <b><i>s</i></b>, with certain characters replaced by hexidecimal escape sequences.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #decodeURI(JSString) decodeURI()
	 * @see #decodeURIComponent(JSString) decodeURIComponent()
	 * @see #encodeURI(JSString) encodeURI()
	 * @see #escape(JSString) escape()
	 * @see #unescape(JSString) unescape()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString encodeURIComponent(JSString s);
	
	/**
	 * <b>function escape(s)</b> encode a string.
	 * @param s A string to be "escaped" or encoded. 
	 * @returns An encoded copy of <b><i>s</i></b>, with certain characters replaced by hexidecimal escape sequences.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #encodeURI(JSString) encodeURI()
	 * @see #encodeURIComponent(JSString) encodeURIComponent()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString escape(JSString s);
	
	/**
	 * <b>function eval(code)</b> execute JavaScript from a string.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * eval("2+5"); //Returns 7
	 * </pre> 
	 * @param code A string that contains the JavaScript expression to be evaluated or the statements to be executed. 
	 * @returns The value of the evaluated <b><i>code</i></b>, if any.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject eval(JSString code);
	
	/**
	 * <b>function isFinite(n)</b> determine whether a number is finite.
	 * @param n The number to be tested. 
	 * @returns <b><i>true</i></b> if <b><i>n</i></b> is or can be converted to a finite number or otherwise <b><i>false</i></b> if <b><i>n</i></b> is NaN or positive or negative Infinity.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal#NaN NaN
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber#POSITIVE_INFINITY Number.POSITIVE_INFINITY
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSBoolean isFinite(JSNumber n);
	
	/**
	 * <b>function isNaN(n)</b> check for not-a-number.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * isNaN(0); // => false
	 * isNaN(0/0); //=> true
	 * isNaN(parseInt("3")); //=> false
	 * isNaN(parseInt("hello")); //=> true
	 * isNaN("3"); //=> false
	 * isNaN("hello"); //=> true
	 * isNaN(true); //=> false
	 * isNaN(undefined); //=> true
	 * </pre> 
	 * @param n The number to be tested. 
	 * @returns <b><i>true</i></b> if <b><i>n</i></b> is not a number or if it is the special numeric value NaN, otherwise <b><i>false</i></b> if <b><i>n</i></b> is any other number.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal#NaN NaN
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber#NaN Number.NaN
	 * @see #parseFloat(JSString) parseFloat()
	 * @see #parseInt(JSString,JSNumber) parseInt()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSBoolean isNaN(JSNumber n);
	
	/**
	 * <b>function parseFloat(s)</b> convert a string to a number.
	 * @param s The string to be parsed and converted to a number
	 * @returns The parsed number or <b><i>NaN</i></b> if <b><i>s</i></b> does not begin with a valid number.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #isNaN(JSNumber) isNan()
	 * @see #parseInt(JSString,JSNumber) parseInt()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString parseFloat(JSString s);
	
	/**
	 * <b>function parseInt(s, radix)</b> convert a string to an integer.
	 * @param s The string to be parsed and converted to an integer
	 * @param radix An optional integer argument that represents the radix (i.e base) of the number to be parsed. If omitted or 0, the number is parsed in base 10.
	 * @returns The parsed number or <b><i>NaN</i></b> if <b><i>s</i></b> does not begin with a valid number.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #isNaN(JSNumber) isNan()
	 * @see #parseFloat(JSString) parseFloat()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString parseInt(JSString s, JSNumber radix);
	
	/**
	 * <b>function unescape(s)</b> decode an escaped string.
	 * @param s A string to be "unescaped" or decoded. 
	 * @returns An decoded copy of <b><i>s</i></b>.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @see #decodeURI(JSString) decodeURI()
	 * @see #decodeURIComponent(JSString) decodeURIComponent()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString unescape(JSString s);
}
