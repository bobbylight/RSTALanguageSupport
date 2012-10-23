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
   public JSArray protype;
   
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
