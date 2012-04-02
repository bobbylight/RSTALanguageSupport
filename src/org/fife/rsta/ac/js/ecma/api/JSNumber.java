package org.fife.rsta.ac.js.ecma.api;


public abstract class JSNumber extends JSObject {

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
    public abstract JSString toFixed(JSNumber fractionDigits);

    /**
     * function toExponential(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toExponential(JSNumber fractionDigits);

    /**
     * function toPrecision(precision)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toPrecision(JSNumber fractionDigits);
}
