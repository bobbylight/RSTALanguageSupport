package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object Boolean.
 *
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSBoolean implements JSObjectFunctions {

    /**
     * Object Boolean(value)
     *
     * @constructor
     * @extends Object
     * @param value The value to be held by the Boolean object or be converted to a boolean value
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean(JSObject value) {
    }

    /**
     * <b>property prototype</b>
     *
     * @type Boolean
     * @memberOf Boolean
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public JSBoolean prototype;

    /**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf Boolean
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

}
