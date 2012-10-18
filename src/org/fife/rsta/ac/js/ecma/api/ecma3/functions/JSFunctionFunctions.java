package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;


public interface JSFunctionFunctions extends JSObjectFunctions {
	 
	/**
     * function apply (thisObject, argArray)
     * @param {Object} thisObject
     * @param {Array} argArray
     * @returns {Object}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
    public JSObject apply(JSObject thisObject, JSArray argArray);
    
    /**
      * function call (thisObject, args)
      * @param {Object} thisObject
      * @param {Object} args
      * @returns {Object}
      * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
      * @since   Standard ECMA-262 3rd. Edition 
      * @since   Level 2 Document Object Model Core Definition.    
     */ 
    public JSObject call(JSObject thisObject, JSObject args);
    
}
