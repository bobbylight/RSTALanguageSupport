package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


public interface HistoryFunctions extends JS5ObjectFunctions {


	/**
	 * <b>function back - Loads the previous URL in the history list</b>.
	 *
	 * @memberOf History
	 * @see org.fife.rsta.ac.js.ecma.api.client.History History
	 */
	void back();

	/**
	 * <b>function forward - Loads the next URL in the history list</b>.
	 *
	 * @memberOf History
	 * @see org.fife.rsta.ac.js.ecma.api.client.History History
	 */
	void forward();

	/**
	 * <b>function go - Loads a specific URL from the history list</b>.
	 *
	 * @memberOf History
	 * @param arg goes to the URL within the specific position (-1 goes back one page, 1 goes forward one page)
	 * @see org.fife.rsta.ac.js.ecma.api.client.History History
	 */
	void go(JSNumber arg);

	/**
	 * <b>function go - Loads a specific URL from the history list</b>.
	 *
	 * @memberOf History
	 * @param arg the string must be a partial or full URL, and the function will go to the first URL that matches the string
	 * @see org.fife.rsta.ac.js.ecma.api.client.History History
	 */
	void go(JSString arg);
}
