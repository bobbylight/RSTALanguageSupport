package org.fife.rsta.ac.js.ecma.api.client;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


public abstract class Screen implements JS5ObjectFunctions {

	/**
	 * Object Screen()
	 *
	 * @super Object
	 * @constructor
	 * @since Common Usage, no standard
	 */
	public Screen(){}

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
	 * @type Location
	 * @memberOf Location
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
	 */
   public Screen prototype;


	/**
	 * Property availHeight
	 *
	 * @type Number
	 * @memberOf Screen
	 */
	public JSNumber availHeight;

	/**
	 * Property availWidth
	 *
	 * @type Number
	 * @memberOf Screen
	 */
	public JSNumber availWidth;

	/**
	 * Property colorDepth
	 *
	 * @type Number
	 * @memberOf Screen
	 */
	public JSNumber colorDepth;

	/**
	 * Property height
	 *
	 * @type Number
	 * @memberOf Screen
	 */
	public JSNumber height;

	/**
	 * Property width
	 *
	 * @type Number
	 * @memberOf Screen
	 */
	public JSNumber width;
}
