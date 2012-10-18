package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSNumberFunctions;


/**
 * Object Number
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSNumber implements JSNumberFunctions {

    /**
     * Object Number()
     * 
     * @constructor
     * @extends Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber() {
    }
    
    /**
     * Property prototype
     * 
     * @type Number
     * @memberOf Number
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber protype;
    
    /**
     * Property constructor
     * 
     * @type Function
     * @memberOf Number
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * property MIN_VALUE
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber MIN_VALUE;

    /**
     * property MAX_VALUE
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber MAX_VALUE;

    /**
     * property NaN
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber NaN;

    /**
     * property NEGATIVE_INFINITY
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber NEGATIVE_INFINITY;

    /**
     * property POSITIVE_INFINITY
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber POSITIVE_INFINITY;

}
