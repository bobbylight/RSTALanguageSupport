package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSNumberFunctions;


/**
 * Object Number
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSNumber implements JSNumberFunctions {

    /**
     * Object Number(value)
     * 
     * @constructor
     * @extends Object
     * @param value The numeric value of the Number object being created or a value to be converted to a number.
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber(JSObject value) {
    }
    
    /**
     * <b>property prototype</b>
     * 
     * @type Number
     * @memberOf Number
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber protype;
    
    /**
     * <b>property constructor</b>
     * 
     * @type Function
     * @memberOf Number
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property MIN_VALUE</b> The smallest representable number.
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
     * <b>property MAX_VALUE</b> The largest representable number.
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
     * <b>property NaN</b> Not-a-number value.
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
     * <b>property NEGATIVE_INFINITY</b> Negative infinite value.
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
     * <b>property POSITIVE_INFINITY</b> Infinite value.
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
