package org.fife.rsta.ac.js.ecma.api;


public abstract class JSString extends JSObject {

    /**
     * Object String()
     * 
     * @constructor
     * @extends Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString() {
    }

    /**
     * static function fromCharCode(charCode1, ...)
     * 
     * @memberOf String
     * @param {Number} charCode
     * @returns {String}
     * @static
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String fromCharCode(Number charCode);

    /**
     * Property length
     * 
     * @type Number
     * @memberOf String
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static Number length;

    /**
     * function charAt(position)
     * 
     * @memberOf String
     * @param {Number} position
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String charAt(Number position);

    /**
     * function charCodeAt(position)
     * 
     * @memberOf String
     * @param {Number} position
     * @returns {Number}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract Number charCodeAt(Number position);

    /**
     * function concat(value1, ...)
     * 
     * @memberOf String
     * @param {String} value
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String concat(String value);

    /**
     * function indexOf(searchString, startPosition)
     * 
     * @memberOf String
     * @param {String} searchString
     * @param {Number} startPosition
     * @returns {Number}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract Number indexOf(Number searchString, Number startPosition);

    /**
     * function lastIndexOf(searchString, startPosition)
     * 
     * @memberOf String
     * @param {String} searchString
     * @param {Number} startPosition
     * @returns {Number}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract Number lastIndexOf(Number searchString, Number startPosition);

    /**
     * function localeCompare(otherString)
     * 
     * @memberOf String
     * @param {String} otherString
     * @returns {Number}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract Number localeCompare(String otherString);

    /**
     * function match(regexp)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @returns {Array}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String match(JSRegExp regexp);

    /**
     * function replace(regexp, replaceValue)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @param {String} replaceValue
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String replace(JSRegExp regexp, String replaceValue);

    /**
     * function search(regexp)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @returns {Number}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract Number search(JSRegExp regexp);

    /**
     * function slice(start, end)
     * 
     * @memberOf String
     * @param {Number} start
     * @param {Number} end
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String slice(Number start, Number end);

    /**
     * function split(separator, limit)
     * 
     * @memberOf String
     * @param {String} separator
     * @param {Number} limit
     * @returns {Array}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSArray split(String separator, Number limit);

    /**
     * function substring(start, end)
     * 
     * @memberOf String
     * @param {Number} start
     * @param {Number} end
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String substring(Number start, Number end);

    /**
     * function toLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toLowerCase();

    /**
     * function toLocaleLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toLocaleLowerCase();

    /**
     * function toUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toUpperCase();

    /**
     * function toLocaleUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract String toLocaleUpperCase();

}
