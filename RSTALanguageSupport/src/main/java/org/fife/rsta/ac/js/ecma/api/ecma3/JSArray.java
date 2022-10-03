package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSArrayFunctions;

/**
 * Object Array
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSArray implements JSArrayFunctions {

	/**
	 * Object Array()
	 *
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray() {

	}

	/**
	 * Object Array(size)
	 *
	 * @constructor
	 * @extends Object
	 * @param size The desired number of elements in the array. The returned value has its <b>length</b> field set to <b><i>size</b></i>
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray(JSNumber size) {

	}

	/**
	 * Object Array(element0, ... elementN)
	 *
	 * @constructor
	 * @extends Object
	 * @param element0  An argument list of two or more values. The <b>length</b> field set to the number of arguments.
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSArray(JSObject element0, JSObject elementN) {

	}


	/**
	 * <b>property length</b>
	 *
	 * @type Number
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber length;

	/**
    * <b>property prototype</b>
    *
    * @type Array
    * @memberOf Array
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSArray prototype;

   /**
    * <b>property constructor</b>
    *
    * @type Function
    * @memberOf Array
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;


}
