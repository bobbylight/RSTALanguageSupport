package org.fife.rsta.ac.js.ecma.api;


public abstract class JSString extends JSObject {

    /**
     * Object JSString()
     * 
     * @constructor
     * @extends JSObject
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
    public abstract JSString fromCharCode(JSNumber charCode);

    /**
     * Property length
     * 
     * @type Number
     * @memberOf String
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSNumber length;

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
    public abstract JSString charAt(JSNumber position);

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
    public abstract JSNumber charCodeAt(JSNumber position);

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
    public abstract JSString concat(JSString value);

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
    public abstract JSNumber indexOf(JSNumber searchString, JSNumber startPosition);

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
    public abstract JSNumber lastIndexOf(JSNumber searchString, JSNumber startPosition);

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
    public abstract JSNumber localeCompare(JSString otherString);

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
    public abstract JSString match(JSRegExp regexp);

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
    public abstract JSString replace(JSRegExp regexp, JSString replaceValue);

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
    public abstract JSNumber search(JSRegExp regexp);

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
    public abstract JSString slice(JSNumber start, JSNumber end);

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
    public abstract JSArray split(JSString separator, JSNumber limit);

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
    public abstract JSString substring(JSNumber start, JSNumber end);

    /**
     * function toLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toLowerCase();

    /**
     * function toLocaleLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toLocaleLowerCase();

    /**
     * function toUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toUpperCase();

    /**
     * function toLocaleUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public abstract JSString toLocaleUpperCase();

}
