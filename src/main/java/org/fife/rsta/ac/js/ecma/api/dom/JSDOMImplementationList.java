package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.DOMImplementationList;


public abstract class JSDOMImplementationList implements DOMImplementationList, JS5ObjectFunctions {

	/**
	 * Object DOMImplementationList()
	 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113/ecma-script-binding.html
	 * 
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSDOMImplementationList() {

	}

	/**
	 * <b>property prototype</b>
	 * 
	 * @type DOMImplementationList
	 * @memberOf DOMImplementationList
	 * @see org.fife.rsta.ac.js.ecma.api.dom.JSDOMImplementationList DOMImplementationList
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public DOMImplementationList protype;

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
