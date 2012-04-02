package org.fife.rsta.ac.js.ecma.api;

public abstract class JSDate extends JSObject {

	/**
	 * JSObject JSDate(s)
	 * 
	 * @constructor
	 * @extends Object
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSDate() {

	}


	/**
	 * function UTC(hour, min, sec, ms)
	 * 
	 * @memberOf Date
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @returns {Number}
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber UTC(JSNumber hour, JSNumber min, JSNumber sec, JSNumber ms);


	/**
	 * function parse(string)
	 * 
	 * @memberOf Date
	 * @param {String} string
	 * @returns {Number}
	 * @static
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber parse(JSString string);


	/**
	 * function toDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toDateString();


	/**
	 * function toTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toTimeString();


	/**
	 * function toLocaleString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toLocaleString();


	/**
	 * function toLocaleDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toLocaleDateString();


	/**
	 * function toLocaleTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toLocaleTimeString();


	/**
	 * function valueOf()
	 * 
	 * @memberOf Date
	 * @returns {Object}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSObject valueOf();


	/**
	 * function getFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getFullYear();


	/**
	 * function getTime()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getTime();


	/**
	 * function getUTCFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCFullYear();


	/**
	 * function getMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getMonth();


	/**
	 * function getUTCMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCMonth();


	/**
	 * function getDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getDate();


	/**
	 * function getUTCDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCDate();


	/**
	 * function getDay()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getDay();


	/**
	 * function getUTCDay()
	 * 
	 * @memberOf Date
	 * @type Number
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public abstract JSNumber getUTCDay();


	/**
	 * function getHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getHours();


	/**
	 * function getUTCHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCHours();


	/**
	 * function getMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getMinutes();


	/**
	 * function getUTCMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCMinutes();


	/**
	 * function getSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getSeconds();


	/**
	 * function getUTCSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCSeconds();


	/**
	 * function getMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getMilliseconds();


	/**
	 * function getUTCMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getUTCMilliseconds();


	/**
	 * function getTimezoneOffset()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber getTimezoneOffset();


	/**
	 * function setTime(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setTime(Number value);


	/**
	 * function setMilliseconds(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setMilliseconds(Number value);


	/**
	 * function setUTCMilliseconds(ms)
	 * 
	 * @memberOf JSDate
	 * @returns {Number}
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCMilliseconds(Number ms);


	/**
	 * function setSeconds(sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setSeconds(Number sec, Number ms);


	/**
	 * function setUTCSeconds(sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCSeconds(Number sec, Number ms);


	/**
	 * function setMinutes(min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setMinutes(Number min, Number sec, Number ms);


	/**
	 * function setUTCMinute(min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCMinute(Number min, Number sec, Number ms);


	/**
	 * function setHours(hour, min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setHours(JSNumber hour, JSNumber min, JSNumber sec,
			JSNumber ms);


	/**
	 * function setUTCHours(hour, min,sec,ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} hour
	 * @param {Number} min
	 * @param {Number} sec
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCHours(JSNumber hour, JSNumber min, JSNumber sec,
			JSNumber ms);


	/**
	 * function setDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setDate(JSNumber date);


	/**
	 * function setUTCDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCDate(JSNumber date);


	/**
	 * function setMonth(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setMonth(JSNumber month, JSNumber date);


	/**
	 * function setUTCMonth(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCMonth(JSNumber month, JSNumber date);


	/**
	 * function setFullYear(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @param {Number} month
	 * @param {Number} year
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * function setUTCFullYear(month,date)
	 * 
	 * @memberOf Date
	 * @returns {Date}
	 * @param {Number} date
	 * @param {Number} month
	 * @param {Number} year
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSNumber setUTCFullYear(JSNumber year, JSNumber month, JSNumber date);


	/**
	 * function toUTCString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract JSString toUTCString();
}
