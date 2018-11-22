package org.fife.rsta.ac.js.ecma.api.e4x.functions;

import org.fife.rsta.ac.js.ecma.api.e4x.E4XXML;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


public interface E4XXMLListFunctions extends JSObjectFunctions {

	/**
	 * <b>function attribute(attributeName)</b> calls the attribute method of each XML object in this XMLList object passing attributeName as a parameter and returns an XMLList containing the results in order.
	 * @param attributeName name of attribute to find. 
	 * @returns an XMLList containing the results in order
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#attribute(JSString) XML.attribute();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList attribute(JSString attributeName);
	
	/**
	 * <b>function attributes()</b> calls the attributes() method of each XML object in this XMLList object and returns an XMLList containing the results in order.
	 * @returns an XMLList containing the results in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#attributes() XML.attributes();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList attributes();
	
	/**
	 * <b>function child(propertyName)</b> calls the child() method of each XML object in this XMLList object and returns an XMLList containing the results in order.
	 * @param propertyName name of XML element to find. 
	 * @returns an XMLList containing the results in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#child(JSString) XML.child();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList child(JSString propertyName);
	
	/**
	 * <b>function children()</b> calls the children() method of each XML object in this XMLList object and returns an XMLList containing the results concatenated in order.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // get all the children of all the items in the order
	 * var allitemchildren = order.item.children();
	 * 
	 * // get all grandchildren of the order that have the name price
	 * var grandChildren = order.children().price;
	 * </pre> 
	 * @returns an XMLList containing the results concatenated in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#children() XML.children();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList children();
	
	/**
	 * <b>function comments()</b> calls the comments method of each XML object in this XMLList object and returns an XMLList containing the results concatenated in order.
	 * @returns an XMLList containing the results concatenated in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#comments() XML.comments();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList comments();
	
	/**
	 * <b>function contains(value)</b> returns a boolean value indicating whether this XMLList object contains an XML object that compares equal to the given <b><i>value</i></b>.
	 * @returns <b><i>true</i></b> if XMLList contains XML <b><i>value</i></b>, otherwise <b><i>false</i></b>.
	 * @param value XML object to test.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean contains(E4XXML value);
	
	/**
	 * <b>function copy()</b> returns a deep copy of the XMLList object.
	 * @returns the copy method returns a deep copy of this XMLList object..
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean copy();
	
	/**
	 * <b>function descendants(name)</b> calls the descendants method of each XML object in this XMLList object with the optional parameter name (or the string "*" if name is omitted) and returns an XMLList containing the results concatenated in order.
	 * @returns all the XML valued descendants (children, grandchildren, great-grandchildren, etc.) of this XMLList object with the given name. If the name parameter is omitted, it returns all descendants of this XMLList object.
	 * @param name optional parameter to identity the decendants. If omitted all decendants are returned.  
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#descendants(JSString) XML.descendants();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList descendants(JSString name);
	
	/**
	 * <b>function elements(name)</b> calls the elements method of each XML object in this XMLList object passing the optional parameter name (or "*" if it is omitted) and returns an XMList containing the results in order.
	 * @returns an XMLList containing all the children of this XMLList object that are XML elements with the given name. When the elements method is called with no parameters, it returns an XMLList containing all the children of this XML object that are XML elements regardless of their name.
	 * @param name optional parameter to identity the element. If omitted all children are returned.  
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#elements(JSString) XML.elements();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList elements(JSString name);
	
	/**
	 * <b>function hasComplexContent()</b> returns a Boolean value indicating whether this XMLList object contains complex content. An XMLList object is considered to contain complex content if it is not empty, contains a single XML item with complex content or contains elements.
	 * @returns <b><i>true</i></b> if XMLList contains complex content, otherwise <b><i>false</i></b>.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean hasComplexContent();
	
	/**
	 * <b>function hasSimpleContent()</b> returns a Boolean value indicating whether this XMLList contains simple content. An XMLList object is considered to contain simple content if it is empty, contains a single XML item with simple content or contains no elements.
	 * @returns <b><i>true</i></b> if XMLList contains simple content, otherwise <b><i>false</i></b>.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean hasSimpleContent();
	
	/**
	 * <b>function length()</b> returns the number of properties in this XMLList object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * for (var i = 0; i &lt; e..name.length(); i++) {
	 *   print("Employee name:" + e..name[i]);
	 * }
	 * </pre> 
	 * @returns the number of properties in this XMLList object.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSNumber length();
	
	/**
	 * <b>function normalize()</b> puts all text nodes in this XMLList, all the XML objects it contains and the descendents of all the XML objects it contains into a normal form by merging adjacent text nodes and eliminating empty text nodes.
	 * @returns this XMLList object
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList normalize();
	
	/**
	 * <b>function parent()</b> If all items in this XMLList object have the same parent, it is returned. Otherwise, the parent method returns <b><i>undefined</i></b>.
	 * @returns the parent of this XMLList object
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML parent();
	
	/**
	 * <b>function processingInstructions(name)</b> calls the processingInstructions method of each XML object in this XMLList object passing the optional parameter name (or "*" if it is omitted) and returns an XMList containing the results in order.
	 * @param name optional node name filter. 
	 * @returns an XMList containing the results in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#processingInstructions(JSString) XML.processingInstructions();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList processingInstructions(JSString name);
	
	/**
	 * <b>function text()</b> calls the text method of each XML object contained in this XMLList object and returns an XMLList containing the results concatenated in order.
	 * @return an XMLList containing the results concatenated in order.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.functions.E4XXMLFunctions#text() XML.text();
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList text();
	
	/**
	 * <b>function toXMLString()</b> returns the toXMLString() method returns an XML encoded string representation of this XML object. Unlike the toString method, toXMLString provides no special treatment for XML objects that contain only XML text nodes (i.e., primitive values). The toXMLString method always includes the start tag, attributes and end tag of the XML object regardless of its content. It is provided for cases when the default XML to string conversion rules are not desired.
	 * @return Serializes this XML object as parseable XML.
	 * @memberOf XMLList
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList XMLList
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSString toXMLString();
}
