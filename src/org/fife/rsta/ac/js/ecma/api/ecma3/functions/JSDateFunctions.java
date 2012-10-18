package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSDateFunctions extends JSObjectFunctions {
	
	/**
	 * function toDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toDateString();


	/**
	 * function toTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toTimeString();


	/**
	 * function toLocaleString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleString();


	/**
	 * function toLocaleDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleDateString();


	/**
	 * function toLocaleTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toLocaleTimeString();


	/**
	 * function valueOf()
	 * 
	 * @memberOf Date
	 * @returns {Object}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSObject valueOf();


	/**
	 * function getFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getFullYear();


	/**
	 * function getTime()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getTime();


	/**
	 * function getUTCFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCFullYear();


	/**
	 * function getMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMonth();


	/**
	 * function getUTCMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMonth();


	/**
	 * function getDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getDate();


	/**
	 * function getUTCDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCDate();


	/**
	 * function getDay()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getDay();


	/**
	 * function getUTCDay()
	 * 
	 * @memberOf Date
	 * @type Number
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSNumber getUTCDay();


	/**
	 * function getHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getHours();


	/**
	 * function getUTCHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCHours();


	/**
	 * function getMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMinutes();


	/**
	 * function getUTCMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMinutes();


	/**
	 * function getSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getSeconds();


	/**
	 * function getUTCSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCSeconds();


	/**
	 * function getMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getMilliseconds();


	/**
	 * function getUTCMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getUTCMilliseconds();


	/**
	 * function getTimezoneOffset()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber getTimezoneOffset();


	/**
	 * function setTime(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setTime(JSNumber value);


	/**
	 * function setMilliseconds(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMilliseconds(JSNumber value);


	/**
	 * function setUTCMilliseconds(ms)
	 * 
	 * @memberOf JSDate
	 * @returns {Number}
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMilliseconds(JSNumber ms);


	/**
	 * function setSeconds(sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setSeconds(JSNumber sec, JSNumber ms);


	/**
	 * function setUTCSeconds(sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCSeconds(JSNumber sec, JSNumber ms);


	/**
	 * function setMinutes(min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMinutes(JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * function setUTCMinute(min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMinute(JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * function setHours(hour, min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setHours(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * function setUTCHours(hour, min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCHours(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * function setDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setDate(JSNumber date);


	/**
	 * function setUTCDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCDate(JSNumber date);


	/**
	 * function setMonth(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setMonth(JSNumber month, JSNumber date);


	/**
	 * function setUTCMonth(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCMonth(JSNumber month, JSNumber date);


	/**
	 * function setFullYear(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @param {Number} year
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * function setUTCFullYear(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Date}
	 * @param {Number} date
	 * @param {Number} month
	 * @param {Number} year
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNumber setUTCFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * function toUTCString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSDate Date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSString toUTCString();
}
