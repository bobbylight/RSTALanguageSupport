package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSObjectFunctions {
	

    /**
     * function toString()
     * 
     * @memberOf Object
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public String toString();

    /**
     * function toLocaleString()
     * 
     * @memberOf Object
     * @returns {String}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString toLocaleString();

    /**
     * function valueOf()
     * 
     * @memberOf Object
     * @returns {Object}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSObject valueOf();

    /**
     * function hasOwnProperty(name)
     * 
     * @memberOf Object
     * @param {String} name
     * @returns {Boolean}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean hasOwnProperty();

    /**
     * function isPrototypeOf(o)
     * 
     * @memberOf Object
     * @param {Object} o
     * @returns {Boolean}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean isPrototypeOf(JSObject o);

    /**
     * function propertyIsEnumerable(name)
     * 
     * @memberOf Object
     * @param {Object} name
     * @returns {Boolean}
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean propertyIsEnumerable(JSObject name);
}
