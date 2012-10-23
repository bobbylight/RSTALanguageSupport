package org.fife.rsta.ac.js.ecma.api.ecma5.functions;

import org.fife.rsta.ac.js.ecma.api.ecma5.JS5String;


public interface JS5StringFunctions extends JS5ObjectFunctions {

	/**
     * <b>function trim ()</b> string leading and trailing whitespace.
     * @returns A copy of <b><i>string</i></b>, with all leading and trailing whitespace removed.
     * @see  org.fife.rsta.ac.js.ecma.api.ecma5.JS5String String
     * @since   Standard ECMA-262 5th. Edition 
     */ 
    public JS5String trim();
}
