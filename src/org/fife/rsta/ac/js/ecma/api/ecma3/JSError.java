package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object Error
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSError implements JSObjectFunctions {

	/**
	 * Object Error(message)
	 * 
	 * @super Object
	 * @constructor
	 * @param {String} message
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSError() {
	}

	/**
    * Property <b>prototype</b>
    * 
    * @type Error
    * @memberOf Error
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSError Error
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSError protype;
   
   /**
    * <b>property constructor
    * 
    * @type Function
    * @memberOf Error
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;
	   
	/**
	 * <b>property name</b>
	 * 
	 * @type String
	 * @memberOf Error
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString name;

	/**
	 * <b>property message</b>
	 * 
	 * @type String
	 * @memberOf Error
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString message;
	
	

}
