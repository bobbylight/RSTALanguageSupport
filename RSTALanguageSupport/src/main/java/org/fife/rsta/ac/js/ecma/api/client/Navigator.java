package org.fife.rsta.ac.js.ecma.api.client;

import org.fife.rsta.ac.js.ecma.api.client.funtions.NavigatorFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;


public abstract class Navigator implements NavigatorFunctions {

	/**
	 * Object Navigator().
	 *
	 * @super Object
	 * @constructor
	 * @since Common Usage, no standard
	*/
	public Navigator(){}

    /**
    * <b>property constructor</b>
    *
    * @type Function
    * @memberOf Object
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;

   /**
    * <b>property prototype</b>
    *
    * @type Navigator
    * @memberOf Navigator
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
    */
   public Navigator prototype;

	/**
	 * Property appCodeName.
	 *
	 * @type String
	 * @memberOf Navigator
	 */
	public JSString appCodeName;

	/**
	 * Property appName.
	 *
	 * @type String
	 * @memberOf Navigator
	 */
	public JSString appName;

	/**
	 * Property appVersion.
	 *
	 * @type String
	 * @memberOf Navigator
	 */
	public JSString appVersion;

	/**
	 * Property cookieEnabled.
	 *
	 * @type Boolean
	 * @memberOf Navigator
	 */
	public JSBoolean cookieEnabled;

	/**
	 * Property mimeTypes.
	 *
	 * @type Array
	 * @memberOf Navigator
	 */
	public JS5Array mimeTypes;

	/**
	 * Property platform.
	 *
	 * @type String
	 * @memberOf Navigator
	 */
	public JSString platform;

	/**
	 * Property plugins.
	 *
	 * @type Array
	 * @memberOf Navigator
	 */
	public JS5Array plugins;

	/**
	 * Property userAgent.
	 *
	 * @type String
	 * @memberOf Navigator
	 */
	public JSString userAgent;


}
