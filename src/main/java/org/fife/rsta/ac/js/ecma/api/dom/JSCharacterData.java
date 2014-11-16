package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.CDATASection;


public abstract class JSCharacterData implements CDATASection, JS5ObjectFunctions {

	/**
	 * Object CharacterData()
	 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113/ecma-script-binding.html
	 * 
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 * 
	 */
	public JSCharacterData() {}

	/**
	 * <b>property prototype</b>
	 * 
	 * @type CharacterData
	 * @memberOf CharacterData
	 * @see org.fife.rsta.ac.js.ecma.api.dom.JSNode Node
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSCharacterData protype;

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
