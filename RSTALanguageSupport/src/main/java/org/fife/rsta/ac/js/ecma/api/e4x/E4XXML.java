package org.fife.rsta.ac.js.ecma.api.e4x;

import org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSFunction;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;


/**
 * Object XML
 *
 * @since Standard ECMA-357 2nd. Edition
 */
public abstract class E4XXML implements E4XXMLFunctions {


	/**
     * Object XML(xml)
     *
     * @constructor
     * @param xml The XML definition
     * @extends Object
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
	public E4XXML ( JSObject xml ){}


	/**
     * <b>property prototype</b>
     *
     * @type XML
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public E4XXML protype;

    /**
     * <b>property constructor</b>
     *
     * @type Function
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    protected JSFunction constructor;

    /**
     * <b>property ignoringComments</b> The initial value of the ignoreComments property is <b>true</b>. If ignoreComments is <b>true</b>, XML comments are ignored when constructing new XML objects.
     *
     * @type Boolean
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSBoolean ignoringComments;

    /**
     * <b>property ignoreProcessingInstructions</b> The initial value of the ignoreProcessingInstructions property is <b>true</b>. If ignoreProcessingInstructions is <b>true</b>, XML processing instructions are ignored when constructing new XML objects.
     *
     * @type Boolean
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSBoolean ignoreProcessingInstructions;


    /**
     * <b>property ignoreWhitespace</b> The initial value of the ignoreWhitespace property is <b>true</b>. If ignoreWhitespace is <b>true</b>, insignificant whitespace characters are ignored when processing constructing new XML objects.
     *
     * @type Boolean
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSBoolean ignoreWhitespace;

    /**
     * <b>property prettyPrinting</b> The initial value of the prettyPrinting property is <b>true</b>. If prettyPrinting is <b>true</b>, the ToString and ToXMLString operators will normalize whitespace characters between certain tags to achieve a uniform and aesthetic appearance.
     *
     * @type Boolean
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSBoolean prettyPrinting;

    /**
     * <b>property prettyIndent</b> The initial value of the prettyIndent property is <b>2</b>. If the prettyPrinting property of the XML constructor is <b>true</b>, the ToString and ToXMLString operators will normalize whitespace characters between certain tags to achieve a uniform and aesthetic appearance. Certain child nodes will be indented relative to their parent node by the number of spaces specified by prettyIndent.
     *
     * @type Boolean
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSNumber prettyIndent;


    /**
     * <b>function settings()</b> The settings method is a convenience method for managing the collection of global XML settings stored as properties of the XML constructor.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // Create a general purpose function that may need to save and restore XML settings
	 * function getXMLCommentsFromString(xmlString) {
	 *   // save previous XML settings and make sure comments are not ignored
	 *   var settings = XML.settings();
	 *   XML.ignoreComments = false;
	 *   var comments = XML(xmlString).comment();
	 *   // restore settings and return result
	 *   XML.setSettings(settings);
	 *   return comments;
	 * }
     * </pre>
     * @return an object containing the properties of the XML constructor used for storing XML settings.
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSObject settings(){return null;}

    /**
     * <b>function setSetting(settings)</b> The setSettings method is a convenience method for managing the collection of global XML settings stored as properties of the XML constructor.
     *
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static void setSettings(JSObject settings){}

    /**
     * <b>function defaultSettings()</b> The defaultSettings method is a convenience method for managing the collection of global XML settings stored as properties of the XML constructor.
     *
     * @memberOf XML
     * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
     * @since Standard ECMA-357 2nd. Edition
     * @since Level 3 Document Object Model Core Definition.
     */
    public static JSObject defaultSettings(){return null;}
}
