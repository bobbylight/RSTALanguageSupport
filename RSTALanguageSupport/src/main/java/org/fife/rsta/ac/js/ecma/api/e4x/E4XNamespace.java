package org.fife.rsta.ac.js.ecma.api.e4x;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object Namespace
 *
 * @since Standard ECMA-357 2nd. Edition
 */
public abstract class E4XNamespace implements JSObjectFunctions {

	/**
     * Object Namespace()
     *
     * @constructor
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XNamespace ( ){}

	/**
     * Object Namespace(uriValue)
     *
     * @constructor
     * @param uriValue uri part of Namespace
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XNamespace ( JSString uriValue ){}

	/**
     * Object Namespace(prefixValue, uriValue)
     *
     * @constructor
     * @param prefixValue Optional prefix part of Namespace
     * @param uriValue uri part of Namespace
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XNamespace ( JSString prefixValue, JSString uriValue ){}

	/**
     * <b>property prototype</b>
     *
     * @type Namespace
     * @memberOf Namespace
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace Namespace
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public E4XNamespace protype;

    /**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf Namespace
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace Namespace
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property prefix</b>
     *
     * @type String
     * @memberOf Namespace
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace Namespace
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSString prefix;


    /**
     * <b>property uri</b>
     *
     * @type String
     * @memberOf Namespace
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace Namespace
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSString uri;

}
