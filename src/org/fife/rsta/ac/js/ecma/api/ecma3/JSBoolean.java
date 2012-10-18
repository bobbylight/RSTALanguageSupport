package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object Boolean
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSBoolean implements JSObjectFunctions {

    /**
     * Object Boolean()
     * 
     * @constructor
     * @extends Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean() {
    }
    
    /**
     * Property prototype
     * 
     * @type Boolean
     * @memberOf Boolean
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean protype;
    
    /**
     * Property constructor
     * 
     * @type Function
     * @memberOf Boolean
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

}
