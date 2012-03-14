package org.fife.rsta.ac.js.ecma.api;

public abstract class JSDate extends JSObject {

	/**
	 * Object Date(s)
	 * 
	 * @constructor
	 * @param {String} s
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
	public abstract Number UTC(Number hour, Number min, Number sec, Number ms);


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
	public abstract Number parse(String string);


	/**
	 * function toDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toDateString();


	/**
	 * function toTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toTimeString();


	/**
	 * function toLocaleString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toLocaleString();


	/**
	 * function toLocaleDateString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toLocaleDateString();


	/**
	 * function toLocaleTimeString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toLocaleTimeString();


	/**
	 * function valueOf()
	 * 
	 * @memberOf Date
	 * @returns {Object}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Object valueOf();


	/**
	 * function getFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getFullYear();


	/**
	 * function getTime()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getTime();


	/**
	 * function getUTCFullYear()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCFullYear();


	/**
	 * function getMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getMonth();


	/**
	 * function getUTCMonth()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCMonth();


	/**
	 * function getDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getDate();


	/**
	 * function getUTCDate()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCDate();


	/**
	 * function getDay()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getDay();


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
	public abstract Number getUTCDay();


	/**
	 * function getHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getHours();


	/**
	 * function getUTCHours()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCHours();


	/**
	 * function getMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getMinutes();


	/**
	 * function getUTCMinutes()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCMinutes();


	/**
	 * function getSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getSeconds();


	/**
	 * function getUTCSeconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCSeconds();


	/**
	 * function getMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getMilliseconds();


	/**
	 * function getUTCMilliseconds()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getUTCMilliseconds();


	/**
	 * function getTimezoneOffset()
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number getTimezoneOffset();


	/**
	 * function setTime(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number setTime(Number value);


	/**
	 * function setMilliseconds(value)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} value
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number setMilliseconds(Number value);


	/**
	 * function setUTCMilliseconds(ms)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} ms
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number setUTCMilliseconds(Number ms);


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
	public abstract Number setSeconds(Number sec, Number ms);


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
	public abstract Number setUTCSeconds(Number sec, Number ms);


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
	public abstract Number setMinutes(Number min, Number sec, Number ms);


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
	public abstract Number setUTCMinute(Number min, Number sec, Number ms);


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
	public abstract Number setHours(Number hour, Number min, Number sec,
			Number ms);


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
	public abstract Number setUTCHours(Number hour, Number min, Number sec,
			Number ms);


	/**
	 * function setDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number setDate(Number date);


	/**
	 * function setUTCDate(date)
	 * 
	 * @memberOf Date
	 * @returns {Number}
	 * @param {Number} date
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract Number setUTCDate(Number date);


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
	public abstract Number setMonth(Number month, Number date);


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
	public abstract Number setUTCMonth(Number month, Number date);


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
	public abstract Number setFullYear(Number year, Number month, Number date);


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
	public abstract Number setUTCFullYear(Number year, Number month, Number date);


	/**
	 * function toUTCString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public abstract String toUTCString();
}
