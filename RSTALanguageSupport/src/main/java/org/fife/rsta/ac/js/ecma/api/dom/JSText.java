package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.Text;


public abstract class JSText implements Text, JS5ObjectFunctions {

	/**
	 * Object Text()
	 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113/ecma-script-binding.html
	 * 
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSText() {

	}

	/**
	 * <b>property prototype</b>
	 * 
	 * @type Text
	 * @memberOf Text
	 * @see org.fife.rsta.ac.js.ecma.api.dom.JSCharacterData CharacterData
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSNode protype;

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
