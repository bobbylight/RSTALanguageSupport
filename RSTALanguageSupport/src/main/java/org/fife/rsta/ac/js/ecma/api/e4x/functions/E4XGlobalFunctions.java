package org.fife.rsta.ac.js.ecma.api.e4x.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


public interface E4XGlobalFunctions extends JS5ObjectFunctions {

	/**
	 * <b>function isXMLName(name)</b> determines whether name is a valid XML name.
	 *
	 * @param name The name to be tested.
	 * @returns examines the given value and determines whether it is a valid XML {@code name} that can be used as an XML element or attribute name. If so, it returns <b>true</b>, otherwise it returns <b>false</b>.
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XGlobal Global
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean isXMLName(JSString name);

}
