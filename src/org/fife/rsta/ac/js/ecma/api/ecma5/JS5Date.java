package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSDate;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5DateFunctions;

/**
 * Object Boolean
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Date extends JSDate implements JS5DateFunctions {

	/**
	 * Object Date()
	 * 
	 * <p>Creates a Date object set to the current date and time.</p>
	 * 
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Date() {

	}
	
	/**
	 * Object Date(milliseconds)
	 * 
	 * @constructor
	 * @extends Object
	 * @param milliseconds The number of milliseconds between the desired date and midnight January 1, 1970 (UTC).
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Date(JSNumber milliseconds) {

	}
	
	/**
	 * Object Date(datestring)
	 * 
	 * @constructor
	 * @extends Object
	 * @param datestring A single argument that specifies date and optionally, the time as a string. The string should be in a format accepted by <b>Date.parse()</b>
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Date(JSString datestring) {

	}
	
	/**
	 * Object Date(year, month, day, hours, minutes, seconds, ms)
	 * 
	 * @constructor
	 * @extends Object
	 * @param year The year in a four digit format. e.g 2011 for the year 2011
	 * @param month The month specified as a single integer from 0 (January) to 11 (December)
	 * @param day The day of the month as an integer between 1 to 31.
	 * @param <i>hours<i> Optional hour value, specified as an integer from 0 (midnight) to 23 (11pm).
	 * @param <i>minutes<i> Optional minute value, specified as an integer from 0 to 59.
	 * @param <i>seconds<i> Optional second value, specified as an integer from 0 to 59.
	 * @param <i>ms<i> Optional milliseconds value, specified as an integer from 0 to 999.
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Date(JSNumber year, JSNumber month, JSNumber day, JSNumber hours, JSNumber minutes, JSNumber seconds, JSNumber ms) {

	}
	
	/**
	 * <b>function now()</b> return the current time in milliseconds.
	 * 
	 * @memberOf Date
	 * @returns The current time in milliseconds since midnight GMT on January 1, 1970
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @static
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber now(){return null;}
}
