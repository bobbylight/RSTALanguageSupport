package org.fife.rsta.ac.js.ecma.api;

public class JSError extends JSObject {

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
    * Property prototype
    * 
    * @type Error
    * @memberOf Error
    * @see Error
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSError protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Error
    * @see Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;
	   
	/**
	 * property name
	 * 
	 * @type String
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString name;

	/**
	 * property message
	 * 
	 * @type String
	 * @memberOf Error
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString message;
	
	

}
