package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSDateFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma5.JS5String;


public interface JS5DateFunctions extends JS5ObjectFunctions, JSDateFunctions {

	/**
	 * <b>function toISOString()</b> converts a Date to ISO8601-formatted string.
	 * 
	 * @memberOf Date
	 * @returns A string representation of <b><i>date</i></b>, formatted according to ISO-8601 - yyyy-mm-ddThh:mm:ss.sssZ
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5String toISOString(); 
	
	/**
	 * <b>function toJSON(key)</b> JSON-serialize a Date object.
	 * 
	 * @memberOf Date
	 * @param key JSON.stringify() passes this argument.
	 * @returns A string representation of the date, obtained by calling the toISOString() method. 
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Date Date
	 * @see #toISOString() toISOString()
	 * @since Standard ECMA-262 5th. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5String toJSON(JS5String key); 
	
	
}
