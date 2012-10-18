package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSRegExpFunctions;


/**
 * Object RegExp
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSRegExp implements JSRegExpFunctions {

	/**
	* Object RegExp()
	* 
	* @super Object
	* @constructor
	* @memberOf RegExp
	* @since Standard ECMA-262 3rd. Edition
	* @since Level 2 Document Object Model Core Definition.
	*/
	public JSRegExp() {};
	
	/**
    * Property prototype
    * 
    * @type RegExp
    * @memberOf RegExp
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
    public JSRegExp protype;
   
	/**
    * Property constructor
    * 
    * @type Function
    * @memberOf RegExp
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
    protected JSFunction constructor;
   
	/**
	 * property source
	 * 
	 * @type String
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString source;

	/**
	 * property global
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean global;

	/**
	 * property ignoreCase
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean ignoreCase;

	/**
	 * property multiline
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean multiline;

	/**
	 * property lastIndex
	 * 
	 * @type Number
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSNumber lastIndex;

}
