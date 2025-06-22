package org.fife.rsta.ac.js.ecma.api.client;

import org.fife.rsta.ac.js.ecma.api.client.funtions.LocationFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public abstract class Location implements LocationFunctions {

	/**
	 * Object Location().
	 *
	 * @super Object
	 * @constructor
	 * @since Common Usage, no standard
	 */
	public Location() {
	}

	/**
	 * <b>property constructor</b>.
	 *
	 * @type Function
	 * @memberOf Object
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSFunction constructor;

	/**
	 * <b>property prototype</b>.
	 *
	 * @type Location
	 * @memberOf Location
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
	 */
	public Location prototype;

	/**
	 * <b>property location</b>.
	 *
	 * @type Location
	 * @memberOf Location
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
	 */
	public Location location;

	/**
	 * Property hash.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString hash;

	/**
	 * Property host.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString host;

	/**
	 * Property hostname.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString hostname;

	/**
	 * Property href.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString href;

	/**
	 * Property pathname.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString pathname;

	/**
	 * Property port.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString port;

	/**
	 * Property protocol
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString protocol;

	/**
	 * Property search.
	 *
	 * @type String
	 * @memberOf Location
	 */
	public JSString search;


}
