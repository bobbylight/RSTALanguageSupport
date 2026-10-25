/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.ecma.api.client.funtions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


public interface NavigatorFunctions extends JS5ObjectFunctions {

	/**
	 * function javaEnabled() - Specifies whether the browser has Java enabled.
	 *
	 * @returns true if java is enabled
	 * @memberOf Navigator
	 */
	JSBoolean javaEnabled();
}
