package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.client.Window;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.Element;


public interface WindowFunctions extends JS5ObjectFunctions {

	/**
	 * function alert() Displays an alert box with a message and an OK button
	 * @param {String} arg
	 * @memberOf  Window
	 */
	public void alert(JSString arg);
	
	/**
	 * function blur() Removes focus from the current window
	 * @memberOf  Window
	 */
	public void blur();
	
	/**
	 * function clearInterval(arg) Clears a timer set with setInterval()
	 * @param arg
	 * @memberOf  Window
	 */
	public void clearInterval(JS5Object arg);
	
	/**
	 * function clearTimeout(arg) Clears a timer set with setTimeout()
	 * @param arg
	 * @memberOf  Window
	 */
	public void clearTimeout(JS5Object arg);
	
	/**
	 * function close() Closes the current window
	 * @memberOf  Window
	 */
	public void close();
	
	/**
	 * function confirm() Displays a dialog box with a message and an OK and a Cancel button
	 * @param {String} arg
	 * @memberOf  Window
	 * @returns Boolean
	 */
	public JSBoolean confirm(JSString arg);
	
	/**
	 * function focus() Sets focus to the current window
	 * @memberOf  Window
	 */
	public void focus();
	
	/**
	 * function getComputedStyle(arg1, arg2) 
	 * @param {Element} arg1
	 * @param {String} arg2
	 * @memberOf  Window
	 * @returns Object
	 */
	public JS5Object getComputedStyle(Element arg1, JSString arg2);
	
	/**
	 * function moveTo(arg1, arg2) Moves a window to the specified position
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void moveTo(JSNumber arg1,JSNumber arg2);
	
	/**
	 * function moveBy(arg1, arg2) Moves a window relative to its current position
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void moveBy(JSNumber arg1, JSNumber arg2);
	
	/**
	 * function open(URL, name, specs, replace) Opens a new browser window
	 * 
	 * @param {String} <i>URL</i>
	 * @param {String} <i>name</i>
	 * @param {String} <i>specs</i>
	 * @param {Boolean} <i>replace</i>
	 * @memberOf  Window
	 * @returns opened Window object 
	 */
	public Window open(JSString URL, JSString name, JSString specs, JSBoolean replace);
	
	/**
	 * function print() Prints the content of the current window
	 * @memberOf  Window
	 */
	public void print();
	
	/**
	 * function prompt(arg1, arg2)  Displays a dialog box that prompts the visitor for input
	 * @param {String} arg1
	 * @param {String} arg2
	 * @memberOf  Window
	 * @returns String
	 */
	public JSString prompt();
	
	/**
	 * function resizeTo(arg1, arg2) Resizes the window to the specified width and height
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void resizeTo(JSNumber arg1, JSNumber arg2);
	
	/**
	 * function resizeBy(arg1, arg2) Resizes the window by the specified pixels
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void resizeBy(JSNumber arg1, JSNumber arg2);
	
	/**
	 * function scrollTo(arg1, arg2) Scrolls the content to the specified coordinates
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void scrollTo(JSNumber arg1, JSNumber arg2);
	
	/**
	 * function scrollBy(arg1, arg2) Scrolls the content by the specified number of pixels
	 * @param {Number} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 */
	public void scrollBy(JSNumber arg1, JSNumber arg2);
	
	/**
	 * function setInterval(arg1, arg2) Calls a function or evaluates an expression at specified intervals (in milliseconds)
	 * @param {Object} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 * @returns Number
	 */
	public JSNumber setInterval(JSObject arg1, JSNumber arg2);
	
	/**
	 * function setTimeout(arg1, arg2) Calls a function or evaluates an expression after a specified number of milliseconds
	 * @param {Object} arg1
	 * @param {Number} arg2
	 * @memberOf  Window
	 * @returns Number
	 */
	public JSNumber setTimeout(JSObject arg1, JSNumber arg2);
	
	/**
	 * function atob(arg) The atob() method of window object decodes a string of data which has been encoded using base-64 encoding. For example, the window.btoa method takes a binary string as a parameter and returns a base-64 encoded string.
	 * @param {String} arg
	 * @memberOf  Window
	 * @returns String
	 */
	public JSString atob(JSString arg);
	
	/**
	 * function btoa(arg) The btoa() method of window object is used to convert a given string to a encoded data (using base-64 encoding) string.
	 * @param {String} arg
	 * @memberOf  Window
	 * @returns {String}
	 */
	public JSString btoa(JSString arg);
	
	/**
	 * function setResizable(arg) 
	 * @param {Boolean} arg
	 * @memberOf  Window
	 */
	public void setResizable(JSBoolean arg);

	
	public void captureEvents(JSObject arg1);
	public void releaseEvents(JSObject arg1);
	public void routeEvent(JSObject arg1);
	public void enableExternalCapture();
	public void disableExternalCapture();
	public void find();
	public void back();
	public void forward();
	public void home();
	public void stop();
	public void scroll(JSNumber arg1, JSNumber arg2);
	
}
