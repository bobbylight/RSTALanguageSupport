package org.fife.rsta.ac.js.ecma.api.dom.html;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.html.HTMLButtonElement;


public abstract class JSHTMLButtonElement implements HTMLButtonElement, JS5ObjectFunctions {
	
	/**
	 * Object HTMLButtonElement()
	 * See also the <a href='http://www.w3.org/TR/2000/CR-DOM-Level-2-20000510'>Document Object Model (DOM) Level 2 Specification
	 * 
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSHTMLButtonElement() {

	}

	/**
	 * <b>property prototype</b>
	 * 
	 * @type HTMLButtonElement
	 * @memberOf HTMLButtonElement
	 * @see org.fife.rsta.ac.js.ecma.api.dom.html.JSHTMLElement HTMLElement
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSHTMLButtonElement protype;

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
