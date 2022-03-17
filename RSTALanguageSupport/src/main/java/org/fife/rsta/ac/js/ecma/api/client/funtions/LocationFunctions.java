package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


public interface LocationFunctions extends JS5ObjectFunctions {

	/**
	 * function assign(newURL) method loads a new document
	 * @param newURL
	 * @memberOf Location
	 */
	void assign(JSString newURL);

	/**
	 * function reload(optionalArg) - Reload the current document
	 * @param optionalArg - default <i><b>false</b></i> which reloads the page from the cache. Set this paramter to true if you want to force the browser to get the page from the server
	 * @memberOf Location
	 */
	void reload(JSBoolean optionalArg);

	/**
	 * function replace(newURL) - method replaces the current document with a new one
	 * @param newUrl
	 * @memberOf Location
	 */
	void replace(JSString newUrl);

}
