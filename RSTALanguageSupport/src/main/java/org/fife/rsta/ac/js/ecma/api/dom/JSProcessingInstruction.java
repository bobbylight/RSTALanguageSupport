package org.fife.rsta.ac.js.ecma.api.dom;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;
import org.w3c.dom.ProcessingInstruction;


public abstract class JSProcessingInstruction implements ProcessingInstruction, JS5ObjectFunctions {

	/**
	 * Object ProcessingInstruction()
	 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Core-20001113/ecma-script-binding.html
	 *
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 *
	 */
	public JSProcessingInstruction() {

	}

	/**
	 * <b>property prototype</b>
	 *
	 * @type ProcessingInstruction
	 * @memberOf ProcessingInstruction
	 * @see org.fife.rsta.ac.js.ecma.api.dom.JSNode Node
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JSProcessingInstruction protype;

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
