package org.fife.rsta.ac.js.ecma.api;


public class JSNumber extends JSObject {

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
     * @see Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber protype;
    
    /**
     * Property constructor
     * 
     * @type Function
     * @memberOf Number
     * @see Function
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
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber POSITIVE_INFINITY;

    /**
     * function toFixed(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toFixed(JSNumber fractionDigits){return null;}

    /**
     * function toExponential(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toExponential(JSNumber fractionDigits){return null;}

    /**
     * function toPrecision(precision)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toPrecision(JSNumber fractionDigits){return null;}
}
