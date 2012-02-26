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
    public static Number MIN_VALUE;

    /**
     * property MAX_VALUE
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static Number MAX_VALUE;

    /**
     * property NaN
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static Number NaN;

    /**
     * property NEGATIVE_INFINITY
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static Number NEGATIVE_INFINITY;

    /**
     * property POSITIVE_INFINITY
     * 
     * @type Number
     * @memberOf Number
     * @static
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static Number POSITIVE_INFINITY;

    /**
     * function toFixed(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toFixed(Number fractionDigits);

    /**
     * function toExponential(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toExponential(Number fractionDigits);

    /**
     * function toPrecision(precision)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toPrecision(Number fractionDigits);
}
