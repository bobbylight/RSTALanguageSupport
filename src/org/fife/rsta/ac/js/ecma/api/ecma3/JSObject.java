package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Base JavaScript Object
 * @since Standard ECMA-262 3rd. Edition
 * 
 */
public abstract class JSObject implements JSObjectFunctions {

    /**
     * Object Object()
     * 
     * @constructor
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSObject() {
    }
    
    /**
     * Property prototype
     * 
     * @type Object
     * @memberOf Object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSObject protype;
    
    /**
     * Property constructor
     * 
     * @type Function
     * @memberOf Object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;
}
