package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions;


/**
 * Object Date
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSDate implements JSDateFunctions {

	/**
	 * Object Date()
	 * 
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSDate() {

	}
	
	/**
    * Property prototype
    * 
    * @type Date
    * @memberOf Date
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSDate protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Date
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;


   /**
	 * function UTC(hour, min, sec, ms)
	 * 
	 * @memberOf Date
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber UTC(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms){return null;}


	/**
	 * function parse(string)
	 * 
	 * @memberOf Date
	 * @param {String} string
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber parse(JSString string){return null;}
	
}
