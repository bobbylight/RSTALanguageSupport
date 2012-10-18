package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5String;


public interface JS5DateFunctions extends JS5ObjectFunctions, JSDateFunctions {

	/**
	 * function toISOString()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5String toISOString(); 
	
	/**
	 * function toJSON()
	 * 
	 * @memberOf Date
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5String toJSON(); 
	
	
}
