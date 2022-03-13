package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSFunctionFunctions;


/**
 * Object Function
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSFunction implements JSFunctionFunctions {

    /**
     * Object Function(argument_names..., body)
     * @constructor
     * @extends Object
     * @param argument_names Any number of string arguments, each naming one or more arguments of the Function object to be created.
     * @param body A string that represents the body of the function. It may contain a number of JavaScript statements, separated by semicolons.
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
    */
    public JSFunction(JSString argument_names, JSString body) {
	
    }
    
    /**
     * <b>property length</b>
     * 
     * @type    Number
     * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */ 
   protected JSNumber length;
   
   /**
    * <b>property prototype</b>
    * 
    * @type Function
    * @memberOf Function
    * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSFunction prototype;
   
   /**
    * <b>property constructor</b>
    * 
    * @type Function
    * @memberOf Function
    * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;
   
}
