package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSRegExpFunctions;


/**
 * Object RegExp
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSRegExp implements JSRegExpFunctions {

	/**
	* Object RegExp(pattern, attributes)
	* 
	* @super Object
	* @constructor
	* @memberOf RegExp
	* @param pattern, a string that specifies the pattern of the regular expression.
	* @param <i>attributes</i>, an optional string containing and of the "g", "i" an "m" attributes that specify global, case-insensitive, and multiline matches respectively. 
	* @since Standard ECMA-262 3rd. Edition
	* @since Level 2 Document Object Model Core Definition.
	*/
	public JSRegExp(JSString pattern, JSString attributes) {};
	
	/**
    * <b>property prototype</b>
    * 
    * @type RegExp
    * @memberOf RegExp
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp RegExp
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
    public JSRegExp prototype;
   
	/**
    * <b>property constructor</b>
    * 
    * @type Function
    * @memberOf RegExp
    * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
    protected JSFunction constructor;
   
	/**
	 * <b>property source</b>
	 * 
	 * @type String
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSString source;

	/**
	 * <b>property global</b>
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean global;

	/**
	 * <b>property ignoreCase</b>
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean ignoreCase;

	/**
	 * <b>property multiline</b>
	 * 
	 * @type Boolean
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean Boolean
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSBoolean multiline;

	/**
	 * <b>property lastIndex</b>
	 * 
	 * @type Number
	 * @memberOf RegExp
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	protected JSNumber lastIndex;

}
