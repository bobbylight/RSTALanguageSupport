package org.fife.rsta.ac.js.ecma.api.e4x;

import org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLListFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;


/**
 * Object XMLList
 * 
 * @since Standard ECMA-357 2nd. Edition
 */
public abstract class E4XXMLList implements E4XXMLListFunctions {

	/**
     * Object E4XXMLList(xml)
     * 
     * @constructor
     * @param xml The XML definition 
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XXMLList ( JSObject xml ){}
	
	/**
     * <b>property prototype</b>
     * 
     * @type XMLList
     * @memberOf XMLList
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public E4XXMLList protype;
    
    /**
     * <b>property constructor</b>
     * 
     * @type Function
     * @memberOf XMLList
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSFunction constructor;
	
}
