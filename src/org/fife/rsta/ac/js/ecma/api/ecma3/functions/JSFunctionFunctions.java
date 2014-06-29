package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;


public interface JSFunctionFunctions extends JSObjectFunctions {
	 
	/**
     * <b>function apply (thisObject, argArray)</b> invoke a function as a method of an object.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * //Apply the default  Object.toString() method to an object that
	 * //overrides it with its own version of the method
	 * Object.prototype.toString().apply(o);
	 * </pre> 
     * @param thisObject The object to which the <b><i>function</i></b> is applied.
     * @param argArray An array of arguments to be passed to <b><i>function</i></b>
     * @returns Whatever value is returned by <b><i>function</i></b>
     * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
     */ 
    public JSObject apply(JSObject thisObject, JSArray argArray);
    
    /**
     * <b>function call (thisObject, args)</b> invoke a function as a method of an object
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * //Call the default  Object.toString() method to an object that
	 * //overrides it with its own version of the method
	 * Object.prototype.toString().call(o);
	 * </pre> 
     *  @param thisObject The object to which the <b><i>function</i></b> is applied.
     *  @param args An array of arguments to be passed to <b><i>function</i></b>
     *  @returns Whatever value is returned by <b><i>function</i></b>
      * @see  org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
      * @since   Standard ECMA-262 3rd. Edition 
      * @since   Level 2 Document Object Model Core Definition.    
     */ 
    public JSObject call(JSObject thisObject, JSObject args);
    
}
