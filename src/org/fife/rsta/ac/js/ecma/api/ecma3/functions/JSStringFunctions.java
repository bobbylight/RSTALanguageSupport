package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSStringFunctions extends JSObjectFunctions {
	
	/**
     * function charAt(position)
     * 
     * @memberOf String
     * @param {Number} position
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString charAt(JSNumber position);

    /**
     * function charCodeAt(position)
     * 
     * @memberOf String
     * @param {Number} position
     * @returns {Number}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber charCodeAt(JSNumber position);

    /**
     * function concat(value1, ...)
     * 
     * @memberOf String
     * @param {String} value
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString concat(JSString value);

    /**
     * function indexOf(searchString, startPosition)
     * 
     * @memberOf String
     * @param {String} searchString
     * @param {Number} startPosition
     * @returns {Number}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber indexOf(JSNumber searchString, JSNumber startPosition);

    /**
     * function lastIndexOf(searchString, startPosition)
     * 
     * @memberOf String
     * @param {String} searchString
     * @param {Number} startPosition
     * @returns {Number}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber lastIndexOf(JSNumber searchString, JSNumber startPosition);

    /**
     * function localeCompare(otherString)
     * 
     * @memberOf String
     * @param {String} otherString
     * @returns {Number}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber localeCompare(JSString otherString);

    /**
     * function match(regexp)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @returns {Array}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString match(JSRegExp regexp);

    /**
     * function replace(regexp, replaceValue)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @param {String} replaceValue
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString replace(JSRegExp regexp, JSString replaceValue);

    /**
     * function search(regexp)
     * 
     * @memberOf String
     * @param {RegExp} regexp
     * @returns {Number}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSNumber search(JSRegExp regexp);

    /**
     * function slice(start, end)
     * 
     * @memberOf String
     * @param {Number} start
     * @param {Number} end
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString slice(JSNumber start, JSNumber end);

    /**
     * function split(separator, limit)
     * 
     * @memberOf String
     * @param {String} separator
     * @param {Number} limit
     * @returns {Array}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSArray split(JSString separator, JSNumber limit);

    /**
     * function substring(start, end)
     * 
     * @memberOf String
     * @param {Number} start
     * @param {Number} end
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString substring(JSNumber start, JSNumber end);

    /**
     * function toLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toLowerCase();

    /**
     * function toLocaleLowerCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toLocaleLowerCase();

    /**
     * function toUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toUpperCase();

    /**
     * function toLocaleUpperCase()
     * 
     * @memberOf String
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toLocaleUpperCase();
}
