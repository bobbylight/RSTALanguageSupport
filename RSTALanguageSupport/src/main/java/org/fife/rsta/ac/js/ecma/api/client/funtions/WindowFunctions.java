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
	 * @param arg
	 * @memberOf  Window
	 */
	void alert(JSString arg);

	/**
	 * function blur() Removes focus from the current window
	 * @memberOf  Window
	 */
	void blur();

	/**
	 * function clearInterval(arg) Clears a timer set with setInterval()
	 * @param arg
	 * @memberOf  Window
	 */
	void clearInterval(JS5Object arg);

	/**
	 * function clearTimeout(arg) Clears a timer set with setTimeout()
	 * @param arg
	 * @memberOf  Window
	 */
	void clearTimeout(JS5Object arg);

	/**
	 * function close() Closes the current window
	 * @memberOf  Window
	 */
	void close();

	/**
	 * function confirm() Displays a dialog box with a message and an OK and a Cancel button
	 * @param arg
	 * @memberOf  Window
	 * @returns Boolean
	 */
	JSBoolean confirm(JSString arg);

	/**
	 * function focus() Sets focus to the current window
	 * @memberOf  Window
	 */
	void focus();

	/**
	 * function getComputedStyle(arg1, arg2)
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 * @returns Object
	 */
	JS5Object getComputedStyle(Element arg1, JSString arg2);

	/**
	 * function moveTo(arg1, arg2) Moves a window to the specified position
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void moveTo(JSNumber arg1,JSNumber arg2);

	/**
	 * function moveBy(arg1, arg2) Moves a window relative to its current position
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void moveBy(JSNumber arg1, JSNumber arg2);

	/**
	 * function open(URL, name, specs, replace) Opens a new browser window
	 *
	 * @param URL
	 * @param name
	 * @param specs
	 * @param replace
	 * @memberOf  Window
	 * @returns opened Window object
	 */
	Window open(JSString URL, JSString name, JSString specs, JSBoolean replace);

	/**
	 * function print() Prints the content of the current window
	 * @memberOf  Window
	 */
	void print();

	/**
	 * function prompt(arg1, arg2)  Displays a dialog box that prompts the visitor for input
	 * @memberOf  Window
	 * @returns String
	 */
	JSString prompt();

	/**
	 * function resizeTo(arg1, arg2) Resizes the window to the specified width and height
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void resizeTo(JSNumber arg1, JSNumber arg2);

	/**
	 * function resizeBy(arg1, arg2) Resizes the window by the specified pixels
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void resizeBy(JSNumber arg1, JSNumber arg2);

	/**
	 * function scrollTo(arg1, arg2) Scrolls the content to the specified coordinates
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void scrollTo(JSNumber arg1, JSNumber arg2);

	/**
	 * function scrollBy(arg1, arg2) Scrolls the content by the specified number of pixels
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 */
	void scrollBy(JSNumber arg1, JSNumber arg2);

	/**
	 * function setInterval(arg1, arg2) Calls a function or evaluates an expression at specified intervals (in milliseconds)
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 * @returns Number
	 */
	JSNumber setInterval(JSObject arg1, JSNumber arg2);

	/**
	 * function setTimeout(arg1, arg2) Calls a function or evaluates an expression after a specified number of milliseconds
	 * @param arg1
	 * @param arg2
	 * @memberOf  Window
	 * @returns Number
	 */
	JSNumber setTimeout(JSObject arg1, JSNumber arg2);

	/**
	 * function atob(arg) The atob() method of window object decodes a string of data which has been encoded using base-64 encoding. For example, the window.btoa method takes a binary string as a parameter and returns a base-64 encoded string.
	 * @param arg
	 * @memberOf  Window
	 * @returns String
	 */
	JSString atob(JSString arg);

	/**
	 * function btoa(arg) The btoa() method of window object is used to convert a given string to an encoded data (using base-64 encoding) string.
	 * @param arg
	 * @memberOf  Window
	 * @returns {String}
	 */
	JSString btoa(JSString arg);

	/**
	 * function setResizable(arg)
	 * @param arg
	 * @memberOf  Window
	 */
	void setResizable(JSBoolean arg);


	void captureEvents(JSObject arg1);
	void releaseEvents(JSObject arg1);
	void routeEvent(JSObject arg1);
	void enableExternalCapture();
	void disableExternalCapture();
	void find();
	void back();
	void forward();
	void home();
	void stop();
	void scroll(JSNumber arg1, JSNumber arg2);

}
