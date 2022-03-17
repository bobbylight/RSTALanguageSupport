package org.fife.rsta.ac.js.ecma.api.ecma3;

import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSGlobalFunctions;


/**
 * Object Global
 *
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSGlobal implements JSGlobalFunctions {

	/**
	 * <b>property Infinity</b> A numerical property that represents infinity.
	 *
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber Infinity;

	/**
	 * <b>property NaN</b> Not a number value.
	 *
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSNumber NaN;

	/**
	 * <b>property undefined</b> The undefined value.
	 *
	 * @memberOf Global
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal Global
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public static JSUndefined undefined;
}
