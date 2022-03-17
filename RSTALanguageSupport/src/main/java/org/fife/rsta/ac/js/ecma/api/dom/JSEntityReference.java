package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.EntityReference;


public abstract class JSEntityReference implements EntityReference, JS5ObjectFunctions {

	/**
	 * Object EntityReference()
	 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113/ecma-script-binding.html
	 *
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 *
	 */
	public JSEntityReference() {

	}

	/**
	 * <b>property prototype</b>
	 *
	 * @type EntityReference
	 * @memberOf EntityReference
	 * @see org.fife.rsta.ac.js.ecma.api.dom.JSEntityReference Node
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSEntityReference protype;

	/**
	 * <b>property constructor</b>
	 *
	 * @type Function
	 * @memberOf Array
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSFunction constructor;
}
