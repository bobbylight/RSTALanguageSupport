package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSFunctionFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;


public interface JS5FunctionFunctions extends JS5ObjectFunctions, JSFunctionFunctions {

	/**
     * function bind (thisObject, argArray)
     * @param {Object} thisObject
     * @param {Array} argArray
     * @returns {Function}
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function Function
     * @since   Standard ECMA-262 5th. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
    public JS5Function bind(JS5Object thisObject, JS5Array argArray);
    
    
    
}
