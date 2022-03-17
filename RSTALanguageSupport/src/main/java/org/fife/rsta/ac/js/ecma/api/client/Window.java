package org.fife.rsta.ac.js.ecma.api.client;

import org.fife.rsta.ac.js.ecma.api.client.funtions.WindowFunctions;
import org.fife.rsta.ac.js.ecma.api.dom.html.JSHTMLDocument;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSGlobal;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public abstract class Window extends JSGlobal implements WindowFunctions {

	/**
     * Object Window()
     *
     * @constructor
     * @extends Global
     */
	public Window(){}

	/**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf Object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction Function
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property prototype</b>
     *
     * @type Window
     * @memberOf Window
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    public Window prototype;

	/**
	 * Property closed
	 * @type Boolean
	 * @memberOf Window
	 */
	public JSBoolean closed;

	/**
	 * Property window
	 * @type Window
	 * @memberOf Window
	 */
	public Window window;

	/**
	 * Property frames
	 * @type Array
	 * @memberOf Window
	 */
	public JSArray frames;

	/**
	 * Property defaultStatus
	 * @type String
	 * @memberOf Window
	 */
	public JSString defaultStatus;

	/**
	 * Property document
	 * @type Document
	 * @memberOf Window
	 */
	public JSHTMLDocument document;

	/**
	 * Property history
	 * @type History
	 * @memberOf Window
	 */
	public History history;

	/**
	 * Property location
	 * @type Location
	 * @memberOf Window
	 */
	public Location location;

	/**
	 * Property name
	 * @type String
	 * @memberOf Window
	 */
	public JSString name;

	/**
	 * Property navigator
	 * @type Navigator
	 * @memberOf Window
	 */
	public Navigator navigator;


	/**
	 * Property opener
	 * @type Window
	 * @memberOf Window
	 */
	public Window opener;

	/**
	 * Property outerWidth
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber outerWidth;

	/**
	 * Property outerHeight
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber outerHeight;

	/**
	 * Property pageXOffset
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber pageXOffset;

	/**
	 * Property pageYOffset
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber pageYOffset;

	/**
	 * Property parent
	 * @type Window
	 * @memberOf Window
	 */
	public Window parent;

	/**
	 * Property screen
	 * @type Screen
	 * @memberOf Window
	 */
	public Screen screen;

	/**
	 * Property status
	 * @type String
	 * @memberOf Window
	 */
	public JSString status;

	/**
	 * Property top
	 * @type Window
	 * @memberOf Window
	 */
	public Window top;

	 /**
	 * Property innerWidth
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber innerWidth;

	/**
	 * Property innerHeight
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber innerHeight;

	/**
	 * Property screenX
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber screenX;

	/**
	 * Property screenY
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber screenY;

	/**
	 * Property screenLeft
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber screenLeft;

	/**
	 * Property screenTop
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber screenTop;

	/**
	 * Property length
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber length;

	/**
	 * Property scrollbars
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp scrollbars;

	/**
	 * Property scrollX
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber scrollX;

	/**
	 * Property scrollY
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber scrollY;

	/**
	 * Property content
	 * @type Window
	 * @memberOf Window
	 */
	public Window content;

	/**
	 * Property menubar
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp menubar;

	/**
	 * Property toolbar
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp toolbar;

	/**
	 * Property locationbar
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp locationbar;

	/**
	 * Property personalbar
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp personalbar;

	/**
	 * Property statusbar
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp statusbar;

	/**
	 * Property directories
	 * @type BarProp
	 * @memberOf Window
	 */
	public BarProp directories;

	/**
	 * Property scrollMaxX
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber scrollMaxX;

	/**
	 * Property scrollMaxY
	 * @type Number
	 * @memberOf Window
	 */
	public JSNumber scrollMaxY;

	/**
	 * Property fullScreen
	 * @type String
	 * @memberOf Window
	 */
	public JSString fullScreen;

	/**
	 * Property frameElement
	 * @type String
	 * @memberOf Window
	 */
	public JSString frameElement;

	/**
	 * Property sessionStorage
	 * @type String
	 * @memberOf Window
	 */
	public JSString sessionStorage;


}
