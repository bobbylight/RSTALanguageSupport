package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSNumberFunctions extends JSObjectFunctions {
	/**
     * function toFixed(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toFixed(JSNumber fractionDigits);

    /**
     * function toExponential(fractionDigits)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toExponential(JSNumber fractionDigits);

    /**
     * function toPrecision(precision)
     * 
     * @memberOf Number
     * @param {Number} fractionDigits
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toPrecision(JSNumber fractionDigits);
}
