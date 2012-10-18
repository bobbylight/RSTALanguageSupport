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
	 * Property length
	 * 
	 * @type Number
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber length;
	
	/**
    * Property prototype
    * 
    * @type Array
    * @memberOf Array
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSArray Array
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSArray protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Array
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;


}
