package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions;


/**
 * Object Date
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSDate implements JSDateFunctions {

	/**
	 * Object Date()
	 * 
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSDate() {

	}
	
	/**
    * <b>property prototype</b>
    * 
    * @type Date
    * @memberOf Date
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSDate protype;
   
   /**
    * <b>property constructor</b>
    * 
    * @type Function
    * @memberOf Date
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;


   /**
	 * <b>function UTC(year,month,day,hour,min,sec,ms)</b> Converts a Date specification to milliseconds
	 * 
	 * @memberOf Date
	 * @param year The year in four digit format. If the year is added between 0 and 99 --> 1900 is added to it. 
	 * @param month The month specified from 0 (January) to 11 (December). 
	 * @param day The day in the month between 1 and 31.
	 * @param hour The hour specified from 0 (midnight) and 23 (11 p.m).
	 * @param min The minutes in the hour, specified from 0 to 59.
	 * @param sec The seconds in the minute, specified from 0 to 59.
	 * @param ms The milliseconds within the second, specified from 0 to 999.
	 * @returns The millisecond representation of the specified universal time (between 1st January 1970 and the specified time).
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @see #parse(JSString) parse()
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber UTC(JSNumber year, JSNumber month, JSNumber day, JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms){return null;}


	/**
	 * <b>function parse(string)</b> parse a date/time string
	 * 
	 * @memberOf Date
	 * @param string A string containing the date and time to be parsed.
	 * @returns The millisecond between 1st January 1970 and the specified date and time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber parse(JSString string){return null;}
	
}
