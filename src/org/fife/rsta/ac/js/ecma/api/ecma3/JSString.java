package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSStringFunctions;


/**
 * Object String
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSString implements JSStringFunctions {

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
     * Property length
     * 
     * @type Number
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSNumber length;
    
    /**
     * Property prototype
     * 
     * @type String
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString protype;
    
    /**
     * Property constructor
     * 
     * @type Function
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;
    
    
    /**
     * static function fromCharCode(charCode1, ...)
     * 
     * @memberOf String
     * @param {Number} charCode
     * @returns {String}
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSString fromCharCode(JSNumber charCode){return null;}
}
