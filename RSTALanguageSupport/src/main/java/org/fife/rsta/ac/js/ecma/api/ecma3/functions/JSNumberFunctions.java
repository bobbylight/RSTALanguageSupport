package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSNumberFunctions extends JSObjectFunctions {

	/**
     * <b>function toFixed(fractionDigits)</b> format a number using fixed-point notation.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var n = 12345.6789;
	 * n.toFixed(); //returns 12346: note rounding up
	 * n.toFixed(1); //returns 12345.7: note rounding up
	 * n.toFixed(6); //returns 12345.678900: note zeros
	 * (1.23e+20).toFixed(2); //returns 123000000000000000000.00
	 * </pre>
     * @memberOf Number
     * @param fractionDigits The number of digits to appear after the decimal point. If omitted it is treated as 0.
     * @returns A string representation of <b><i>number</i></b> that does not use exponential notation and has exactly the digits applied.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSString toFixed(JSNumber fractionDigits);

    /**
     * <b>function toExponential(fractionDigits)</b> format a number using exponential notation.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var n = 12345.6789;
	 * n.toExponential(1); //returns 1.2e+4
	 * n.toExponential(5); //returns 1.23457e+4
	 * n.toExponential(10); //returns 1.2345678900e+4
	 * n.toExponential(); //returns 1.23456789e+4
	 * </pre>
     * @memberOf Number
     * @param fractionDigits The number of digits that appear after the decimal point.
     * @returns a string representation of <b><i>number</i></b> in exponential notation.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSString toExponential(JSNumber fractionDigits);

    /**
     * <b>function toPrecision(precision)</b> format the significant digits of a number.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var n = 12345.6789;
	 * n.toPrecision(1); //returns 1e+4
	 * n.toPrecision(3); //returns 1.23e+4
	 * n.toPrecision(5); //returns 12346
	 * n.toPrecision(10); //returns 12345.67890
	 * </pre>
     * @memberOf Number
     * @param fractionDigits The number of significant digits to appear in the returned string.
     * @returns a string representation of <b><i>number</i></b> that contains the number significant digits.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSString toPrecision(JSNumber fractionDigits);
}
