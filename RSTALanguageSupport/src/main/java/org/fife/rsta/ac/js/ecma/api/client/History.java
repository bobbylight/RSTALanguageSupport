package org.fife.rsta.ac.js.ecma.api.client;

import org.fife.rsta.ac.js.ecma.api.client.funtions.HistoryFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;



public abstract class History implements HistoryFunctions {

	/**
	  * Object History()
	  * @super Object
	  * @constructor
	  * @since Common Usage, no standard
	 */
	public History(){}

	/**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf Object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property prototype</b>
     *
     * @type History
     * @memberOf History
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public History prototype;

	/**
	 * Property length
	 * @type Number
	 * @memberOf History
	 */
	public JSNumber length;


}
