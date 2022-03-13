package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object Error
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSError implements JSObjectFunctions {

	/**
	* Object Error()
	* 
	* @super Object
	* @constructor
	* 
	* @memberOf Error
	* @since Standard ECMA-262 3rd. Edition
	* @since Level 2 Document Object Model Core Definition.
	*/
	public JSError() {
		
	}
	
	/**
	* Object Error(message)
	* 
	* @super Object
	* @constructor
	* @param message An optional error message that provides details about the exception
	* @memberOf Error
	* @since Standard ECMA-262 3rd. Edition
	* @since Level 2 Document Object Model Core Definition.
	*/
	public JSError(JSString message) {
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
   public JSError prototype;
   
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
