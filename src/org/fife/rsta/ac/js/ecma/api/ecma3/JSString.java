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
     * <b>property length</b>
     * 
     * @type Number
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSNumber length;
    
    /**
     * <b>property prototype</b>
     * 
     * @type String
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSString protype;
    
    /**
     * <b>property constructor</b>
     * 
     * @type Function
     * @memberOf String
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;
    
    
    /**
     * <b>function fromCharCode(charCode1, ...)</b> create a string from character encodings.
     * <h4>Example</h4>
	 * <code>
     *  var s = String.fromCharCode(104,101,108,108,111); //returns the string hello<br>
	 * </code>
     * @memberOf String
     * @param charCode Zero or more integers that specify Unicode encodings of the characters in the string to be created.
     * @returns A new string containing characters with the specified encoding.
     * @static
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public static JSString fromCharCode(JSNumber charCode){return null;}
}
