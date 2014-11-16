package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSDateFunctions extends JSObjectFunctions {
	
	/**
	 * <b>function toDateString()</b> return the date portion of a Date as a string.
	 * 
	 * @memberOf Date
	 * @returns a human-readable representation of the date portion of <b><i>date</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toDateString();


	/**
	 * <b>function toTimeString()</b> return the time portion of Date as a string.
	 * 
	 * @memberOf Date
	 * @returns a human-readable representation of the time portion of <b><i>date</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toTimeString();


	/**
	 * <b>function toLocaleString()</b> convert a Date to a locally formatted string.
	 * 
	 * @memberOf Date
	 * @returns a string representation of the date and time specified by <b><i>date</i></b> in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleString();


	/**
	 * <b>function toLocaleDateString()</b> return the date portion of a Date as a locally formatted string.
	 * 
	 * @memberOf Date
	 * @returns a string representation of the date portion specified by <b><i>date</i></b> in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleDateString();


	/**
	 * <b>function toLocaleTimeString()</b> return the time portion of a date as a locally formatted string.
	 * 
	 * @memberOf Date
	 * @returns a string representation of the time portion specified by <b><i>date</i></b> in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleTimeString();


	/**
	 * <b>function valueOf()</b> convert a Date to millisecond representation.
	 * 
	 * @memberOf Date
	 * @returns The millisecond representation of <b><i>date</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject valueOf();


	/**
	 * <b>function getFullYear()</b> return the year field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The year that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getFullYear();


	/**
	 * <b>function getTime()</b> return a Date in milliseconds.
	 * 
	 * @memberOf Date
	 * @returns The millisecond representation of a specified Date object <b><i>date</i></b>, between midnight 1/1/1970 and date and time specified by <b><i>date</i></b>. 
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getTime();


	/**
	 * <b>function getUTCFullYear()</b> return the year field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The year that results when <b><i>date</i></b> is expressed in local time in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCFullYear();


	/**
	 * <b>function getMonth()</b> return the month field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The month that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMonth();


	/**
	 * <b>function getUTCMonth()</b> return the month field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The month that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMonth();


	/**
	 * <b>function getDate()</b> return the day-of-month field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The day of the month that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getDate();


	/**
	 * <b>function getUTCDate()</b> return the day-of-month field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The day of the month that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCDate();


	/**
	 * <b>function getDay()</b> return the day-of-week field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The day of the week that results when <b><i>date</i></b> is expressed in local time. 0 == Sunday --> 6 Saturday.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getDay();


	/**
	 * <b>function getUTCDay()</b> return the day-of-week field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @type Number
	 * @returns The day of the week that results when <b><i>date</i></b> is expressed in universal time. 0 == Sunday --> 6 Saturday.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSNumber getUTCDay();


	/**
	 * <b>function getHours()</b> return the hour field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The hour field that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getHours();


	/**
	 * <b>function getUTCHours()</b> return the hour field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The hour field that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCHours();


	/**
	 * <b>function getMinutes()</b> return the minute field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The minute field that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMinutes();


	/**
	 * <b>function getUTCMinutes()</b> return the minute field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The minute field that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMinutes();


	/**
	 * <b>function getSeconds()</b> return the second field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The second field that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getSeconds();


	/**
	 * <b>function getUTCSeconds()</b> return the second field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The second field that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCSeconds();


	/**
	 * <b>function getMilliseconds()</b> return the millisecond field of a Date.
	 * 
	 * @memberOf Date
	 * @returns The millisecond field that results when <b><i>date</i></b> is expressed in local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMilliseconds();


	/**
	 * <b>function getUTCMilliseconds()</b> return the millisecond field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @returns The millisecond field that results when <b><i>date</i></b> is expressed in universal time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMilliseconds();


	/**
	 * <b>function getTimezoneOffset()</b> determine the offset from GMT.
	 * 
	 * @memberOf Date
	 * @returns The difference in minutes, between GMT and local time.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getTimezoneOffset();


	/**
	 * <b>function setTime(value)</b> set a Date in milliseconds.
	 * 
	 * @memberOf Date
	 * @param value The number of milliseconds between desired date and time and midnight GMT on 1/1/1970.
	 * @returns the milliseconds argument.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setTime(JSNumber value);


	/**
	 * <b>function setMilliseconds(value)</b> set the milliseconds field of a Date.
	 * 
	 * @memberOf Date
	 * @param value The milliseconds field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMilliseconds(JSNumber value);


	/**
	 * <b>function setUTCMilliseconds(ms)</b> set the milliseconds field of a Date (universal time).
	 * 
	 * @memberOf JSDate
	 * @param ms The millisecond field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMilliseconds(JSNumber ms);


	/**
	 * <b>function setSeconds(sec,ms)</b> set the seconds and milliseconds field of a Date.
	 * 
	 * @memberOf Date
	 * @param sec The second field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setSeconds(JSNumber sec, JSNumber ms);


	/**
	 * <b>function setUTCSeconds(sec,ms)</b> set the seconds and milliseconds field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @param sec The second field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCSeconds(JSNumber sec, JSNumber ms);


	/**
	 * <b>function setMinutes(min,sec,ms)</b> set the minutes, seconds and milliseconds field of a Date.
	 * 
	 * @memberOf Date
	 * @param min The minute field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param sec Optional second field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns {Number} The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMinutes(JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * <b>function setUTCMinute(min,sec,ms)</b> set the minutes, seconds and milliseconds field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @param min The minute field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param sec Optional second field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns {Number} The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMinute(JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * <b>function setHours(hour,min,sec,ms)</b> set the hours, minutes, seconds and milliseconds field of a Date.
	 * 
	 * @memberOf Date
	 * @param hour The hour field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param min Optional minute field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param sec Optional second field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns {The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setHours(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * <b>function setUTCHours(hour,min,sec,ms)</b> set the hours, minutes, seconds and milliseconds field of a Date (universal time). 
	 * 
	 * @memberOf Date
	 * @param hour The hour field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param min Optional minute field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param sec Optional second field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param ms Optional millisecond field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCHours(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * <b>function setDate(date)</b> set the day of month field of a Date.
	 * 
	 * @memberOf Date
	 * @param date The day of the month field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setDate(JSNumber date);


	/**
	 * <b>function setUTCDate(date)</b> set the day of month field of a Date (universal time).
	 * 
	 * @memberOf Date
	 * @param date The day of the month field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCDate(JSNumber date);


	/**
	 * <b>function setMonth(month,date)</b> set the month, day field of a Date.
	 * 
	 * @memberOf Date
	 * @param month An integer between 0 (January) and 11 (December) representing the month field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param date Optional day of the month field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMonth(JSNumber month, JSNumber date);


	/**
	 * <b>function setUTCMonth(month,date)</b> set the month, day field of a Date (Universal time).
	 * 
	 * @memberOf Date
	 * @param month An integer between 0 (January) and 11 (December) representing the month field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param date Optional day of the month field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMonth(JSNumber month, JSNumber date);


	/**
	 * <b>function setFullYear(year,month,date)</b> set the year, month, day field of a Date.
	 * 
	 * @memberOf Date
	 * @param year The year field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param month Optional integer between 0 (January) and 11 (December) representing the month field expressed in local time, to be set in <b></i>date</i></b>.
	 * @param date Optional day of the month field expressed in local time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * <b>function setUTCFullYear(year,month,date)</b> set the year, month, day field of a Date (Universal time).
	 * 
	 * @memberOf Date
	 * @param year The year field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param month Optional integer between 0 (January) and 11 (December) representing the month field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @param date Optional day of the month field expressed in universal time, to be set in <b></i>date</i></b>.
	 * @returns The millisecond representation of the adjusted date.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * <b>function toUTCString()</b> convert Date to a string (universal time).
	 * 
	 * @memberOf Date
	 * @returns a human-readable string representation, expressed in universal time, of <b><i>date</i></b> 
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toUTCString();
}
