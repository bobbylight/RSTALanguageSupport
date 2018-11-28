package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSFunctionFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Array;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object;


public interface JS5FunctionFunctions extends JS5ObjectFunctions, JSFunctionFunctions {

	/**
     * <b>function bind (thisObject, argArray)</b> return a function that invokes this as a method.
     * 
     * @param thisObject The object to which the function should be bound.
     * @param argArray Zero or more argument values that will also be bound.
     * @returns A new function which invokes this function as a method of <b><i>thisObject</i></b> and passes arguments <b><i>argArray</i></b>.
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5Function Function
     * @since   Standard ECMA-262 5th. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
	JS5Function bind(JS5Object thisObject, JS5Array argArray);
    
}
