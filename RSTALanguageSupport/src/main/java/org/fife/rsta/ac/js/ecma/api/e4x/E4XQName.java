package org.fife.rsta.ac.js.ecma.api.e4x;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


/**
 * Object QName
 *
 * @since Standard ECMA-357 2nd. Edition
 */
public abstract class E4XQName implements JSObjectFunctions {
	/**
     * Object QName()
     *
     * @constructor
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XQName ( ){}

	/**
     * Object QName(name)
     *
     * @constructor
     * @param name localname of the QName
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XQName ( JSString name ){}

	/**
     * Object QName(namespace, name)
     * @constructor
     * @param namespace optional namespace part of QName
     * @param name localname of the QName
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XQName ( E4XNamespace namespace, JSString name ){}

	/**
     * <b>property prototype</b>
     *
     * @type QName
     * @memberOf QName
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XQName QName
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public E4XQName protype;

    /**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf QName
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XQName QName
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property localName</b> local name part of QName
     *
     * @type String
     * @memberOf QName
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XQName QName
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSString localName;


    /**
     * <b>property uri</b> namespace uri part of QName
     *
     * @type String
     * @memberOf QName
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XQName QName
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace Namespace
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSString uri;
}
