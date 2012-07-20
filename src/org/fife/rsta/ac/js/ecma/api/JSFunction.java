package org.fife.rsta.ac.js.ecma.api;

public class JSFunction extends JSObject {

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
     * property length
     * @type    Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */ 
   protected JSNumber length;
   
   /**
    * Property prototype
    * 
    * @type Function
    * @memberOf Function
    * @see Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSFunction protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Function
    * @see Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;
   
   
    
    /**
     * function apply (thisObject, argArray)
     * @param {Object} thisObject
     * @param {Array} argArray
     * @returns {Object}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
    public JSObject apply(JSObject thisObject, JSArray argArray){return null;}
    
    /**
      * function call (thisObject, args)
      * @param {Object} thisObject
      * @param {Object} args
      * @returns {Object}
      * @since   Standard ECMA-262 3rd. Edition 
      * @since   Level 2 Document Object Model Core Definition.    
     */ 
    public JSObject call(JSObject thisObject, JSObject args){return null;}
    
    

}
