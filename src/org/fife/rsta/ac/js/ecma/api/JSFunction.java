package org.fife.rsta.ac.js.ecma.api;

public abstract class JSFunction extends JSObject {

    /**
     * Object Function()
     * @constructor
     * @extends Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
    */
    public JSFunction(){
	
    }
    
    /**
     * function apply (thisObject, argArray)
     * @param {Object} thisObject
     * @param {Array} argArray
     * @returns {Object}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
    public abstract JSObject apply(JSObject thisObject, JSArray argArray);
    
    /**
      * function call (thisObject, args)
      * @param {Object} thisObject
      * @param {Object} args
      * @returns {Object}
      * @since   Standard ECMA-262 3rd. Edition 
      * @since   Level 2 Document Object Model Core Definition.    
     */ 
    public abstract JSObject call(JSObject thisObject, JSObject args);
    
    /**
      * property length
      * @type    Number
      * @since   Standard ECMA-262 3rd. Edition 
      * @since   Level 2 Document Object Model Core Definition.    
     */ 
    public static JSNumber length;

}
