package org.fife.rsta.ac.js.ecma.api.e4x.functions;

import org.fife.rsta.ac.js.ecma.api.e4x.E4XNamespace;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XQName;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXML;
import org.fife.rsta.ac.js.ecma.api.e4x.E4XXMLList;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;
import org.fife.rsta.ac.js.ecma.api.ecma3.functions.JSObjectFunctions;


public interface E4XXMLFunctions extends JSObjectFunctions {

	/**
	 * <b>function addNamespace(namespace)</b> adds a namespace declaration to the in scope namespaces for this XML object and returns this XML object.
	 * @param namespace The namespace to be added.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	void addNamespace(E4XNamespace namespace);

	/**
	 * <b>function appendChild(xml)</b> appends the given child to the end of this XML object's properties and returns this XML object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * {@code var e = <employees>
	 * 	  <employee id="0" ><name>Jim</name><age>25</age></employee>
	 * 	  <employee id="1" ><name>Joe</name><age>20</age></employee>
	 * 	</employees>;
	 * // Add a new child element to the end of Jim's employee element
	 * e.employee.(name == "Jim").appendChild(<hobby>snorkeling</hobby>);}
	 * </pre>
	 * @param xml to be appended.
	 * @returns <b>this</b> XML Object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML appendChild(E4XXML xml);

	/**
	 * <b>function attribute(attributeName)</b> finds list of XML attributes associated with the attribute name.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // get the id of the employee named Jim
	 * e.employee.(name == "Jim").attribute("id");
	 * </pre>
	 * @param attributeName name of attribute to find.
	 * @returns an XMLList containing zero or one XML attributes associated with this XML object that have the given <b><i>attributeName</i></b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList attribute(JSString attributeName);

	/**
	 * <b>function attributes()</b> list of XML attributes associated with an XML object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // print the attributes of an XML object
	 *  function printAttributes(x) {
	 *   for each (var a in x.attributes()) {
     *     print("The attribute named " + a.name() + " has the value " + a);
     *   }
     *  }
	 * </pre>
	 * @returns an XMLList containing the XML attributes of this object.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList attributes();


	/**
	 * <b>function child(propertyName)</b> finds list of XML object matching a given <b><i>propertyName</i></b>.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var name = customer.child("name"); // equivalent to: var name = customer.name;
	 * var secondChild = customer.child(1); // equivalent to: var secondChild = customer.*[1]
	 * </pre>
	 * @param propertyName name of XML element to find.
	 * @returns the list of children in this XML object matching the given propertyName. If propertyName is a numeric index, the child method returns a list containing the child at the ordinal position identified by <b><i>propertyName</i></b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList child(JSString propertyName);

	/**
	 * <b>function child(propertyName)</b> finds list of XML object matching a given <b><i>propertyName</i></b>.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var name = customer.child("name"); // equivalent to: var name = customer.name;
	 * var secondChild = customer.child(1); // equivalent to: var secondChild = customer.*[1]
	 * </pre>
	 * @param propertyName name of XML element to find.
	 * @returns the list of children in this XML object matching the given propertyName. If propertyName is a numeric index, the child method returns a list containing the child at the ordinal position identified by <b><i>propertyName</i></b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList child(JSNumber propertyName);

	/**
	 * <b>function childIndex()</b> returns the index position of the XML element.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // Get the ordinal index of the employee named Joe.
	 * var joeindex = e.employee.(name == "Joe").childIndex();
	 * </pre>
	 * @returns a Number representing the ordinal position of this XML object within the context of its parent.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSNumber childIndex();

	/**
	 * <b>function children()</b> returns list of children for the XML element.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // Get child elements of first employee: returns an XMLList containing:
	 * // <name>Jim</name>, <age>25</age> and <hobby>Snorkeling</hobby>
	 * var employees = e.employee[0].children();
	 * </pre>
	 * @returns an XMLList containing all the properties of this XML object in order.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList children();

	/**
	 * <b>function comments()</b> returns list of comments for the XML element.
	 * @returns an XMLList containing the properties of this XML object that represent XML comments.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList comments();

	/**
	 * <b>function contains(value)</b> returns the result of comparing this XML object with the given value.
	 * @returns the result of comparing this XML object with the given value. This treatment intentionally blurs the distinction between a single XML object and an XMLList containing only one value.
	 * @param value XML object to test.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean contains(E4XXML value);

	/**
	 * <b>function contains(value)</b> returns the result of comparing this XML object with the given value.
	 * @returns the result of comparing this XML object with the given value. This treatment intentionally blurs the distinction between a single XML object and an XMLList containing only one value.
	 * @param value XMLList object to test.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean contains(E4XXMLList value);

	/**
	 * <b>function copy()</b> returns a deep copy of the XML object.
	 * @returns the copy method returns a deep copy of this XML object with the internal [[Parent]] property set to <b>null</b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean copy();

	/**
	 * <b>function descendants(name)</b> returns all the XML valued descendants (children, grandchildren, great-grandchildren, etc.)
	 * @returns all the XML valued descendants (children, grandchildren, great-grandchildren, etc.) of this XML object with the given name. If the name parameter is omitted, it returns all descendants of this XML object.
	 * @param name optional parameter to identity the decendants. If omitted all decendants are returned.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList descendants(JSString name);

	/**
	 * <b>function elements(name)</b> returns the child elements.
	 * @returns an XMLList containing all the children of this XML object that are XML elements with the given name. When the elements method is called with no parameters, it returns an XMLList containing all the children of this XML object that are XML elements regardless of their name.
	 * @param name optional parameter to identity the element. If omitted all children are returned.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList elements(JSString name);

	/**
	 * <b>function hasComplexContent()</b> a Boolean value indicating whether this XML object contains complex content.
	 * @returns a Boolean value indicating whether this XML object contains complex content. An XML object is considered to contain complex content if it represents an XML element that has child elements. XML objects representing attributes, comments, processing instructions and text nodes do not have complex content.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean hasComplexContent();

	/**
	 * <b>function hasSimpleContent()</b> a Boolean value indicating whether this XML object contains simple content.
	 * @returns a Boolean value indicating whether this XML object contains simple content. An XML object is considered to contain simple content if it represents a text node, represents an attribute node or if it represents an XML element that has no child elements. XML objects representing comments and processing instructions do not have simple content.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSBoolean hasSimpleContent();

	/**
	 * <b>function inScopeNamespaces()</b> returns an array of Namespace objects representing the namespaces in scope for this object.
	 * @returns an array of Namespace objects representing the namespaces in scope for this object.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSArray inScopeNamespaces();

	/**
	 * <b>function insertChildAfter( child1 , child2)</b> inserts the given {@code child2} after the given {@code child1} in this XML object and returns this XML object. If {@code child1} is <b><i>null</i></b>, the insertChildAfter method inserts {@code child2} before all children of this XML object (i.e., after none of them). If {@code child1} does not exist in this XML object, it returns without modifying this XML object.
	 * @returns XML object representing <b><i>x</i></b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML insertChildAfter(E4XXML child1 , E4XXML child2);

	/**
	 * Inserts the given {@code child2} before the given {@code child1} in this XML object and returns this XML object. If {@code child1} is null, the insertChildBefore method inserts {@code child2} after all children in this XML object (i.e., before none of them). If {@code child1} does not exist in this XML object, it returns without modifying this XML object.
	 *
	 * @returns XML object representing <b><i>x</i></b>.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML insertChildBefore(E4XXML child1 , E4XXML child2);

	/**
	 * <b>function length()</b> the length method always returns the integer 1 for XML objects.
	 * @returns 1 for XML objects (allowing an XML object to be treated like an XML List with a single item.)
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSNumber length();

	/**
	 * <b>function localName()</b> returns the localName part of the XML object.
	 * @returns the local name of this object.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSNumber localName();

	/**
	 * <b>function name()</b> returns qualified name for the XML object.
	 * @returns the qualified name associated with this XML object.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XQName name();

	/**
	 * <b>function namespace(prefix)</b> returns the namespace associated with this object.
	 * @param prefix optional prefix identifier
	 * @returns the namespace associated with this object, or if a prefix is specified, an in-scope namespace with that prefix.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XNamespace namespace(JSString prefix);

	/**
	 * <b>function namespaceDeclarations()</b> returns a string representing the kind of object this is (e.g. "element").
	 * @returns an Array of Namespace objects representing the namespace declarations associated with this XML object in the context of its parent.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSArray namespaceDeclarations();

	/**
	 * <b>function nodeKind()</b> return an array of Namespace objects representing the namespace declarations associated with this object.
	 * @returns a string representing the <b><i>Class</i></b> of this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSString nodeKind();

	/**
	 * <b>function normalize()</b> puts all text nodes in this and all descendant XML objects into a normal form by merging adjacent text nodes and eliminating empty text nodes.
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML normalize();

	/**
	 * <b>function parent()</b> returns the parent of this XML object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // Get the parent element of the second name in "e". Returns {@code <employee id="1" ...}
	 * var firstNameParent = e..name[1].parent();
	 * </pre>
	 * @returns the parent of this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML parent();

	/**
	 * <b>function processingInstructions(name)</b> A list of all processing instructions that are children of this element.
	 * @param name optional node name filter.
	 * @returns an XMLList containing all the children of this XML object that are processing-instructions with the given <b><i>name</i></b>. When the processingInstructions method is called with no parameters, it returns an XMLList containing all the children of this XML object that are processing-instructions regardless of their name.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList processingInstructions(JSString name);

	/**
	 * <b>function prependChild(value)</b> adds a new child to an element, prior to all other children.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * // Add a new child element to the front of John's employee element
	 * e.employee.(name == "John").prependChild({@code <prefix>Mr.</prefix>});
	 * </pre>
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML prependChild(E4XXML value);

	/**
	 * <b>function removeNamespace(namespace)</b> removes a namespace from the in-scope namespaces of the element.
	 * @param namespace to remove
	 * @returns a copy of this XML object.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML removeNamespace(E4XNamespace namespace);

	/**
	 * <b>function replace(propertyName, value)</b> replaces the XML properties of this XML object specified by propertyName with value and returns this XML object. If this XML object contains no properties that match propertyName, the replace method returns without modifying this XML object. The propertyName parameter may be a numeric property name, an unqualified name for a set of XML elements, a qualified name for a set of XML elements or the properties wildcard "*". When the propertyName parameter is an unqualified name, it identifies XML elements in the default namespace. The value parameter may be an XML object, XMLList object or any value that may be converted to a String with ToString().
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * {@code // Replace the first employee record with an open staff requisition
	 * employees.replace(0, <requisition status="open"/>);
	 * // Replace all item elements in the order with a single empty item
	 * order.replace("item", <item/>);}
	 * </pre>
	 * @param propertyName name of property to replace.
	 * @param value XML, XMLList or any other object (that can be converted using ToString()) to insert.
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML replace(JSString propertyName, JSObject value);

	/**
	 * <b>function replace(propertyName, value)</b> replaces the XML properties of this XML object specified by propertyName with value and returns this XML object. If this XML object contains no properties that match propertyName, the replace method returns without modifying this XML object. The propertyName parameter may be a numeric property name, an unqualified name for a set of XML elements, a qualified name for a set of XML elements or the properties wildcard "*". When the propertyName parameter is an unqualified name, it identifies XML elements in the default namespace. The value parameter may be an XML object, XMLList object or any value that may be converted to a String with ToString().
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * {@code // Replace the first employee record with an open staff requisition
	 * employees.replace(0, <requisition status="open"/>);
	 * // Replace all item elements in the order with a single empty item
	 * order.replace("item", <item/>);}
	 * </pre>
	 * @param propertyName number index to replace.
	 * @param value XML, XMLList or any other object (that can be converted using ToString()) to insert.
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML replace(JSNumber propertyName, JSObject value);

	/**
	 * <b>function setChildren(value)</b> replaces the XML properties of this XML object with a new set of XML properties from value. value may be a single XML object or an XMLList. setChildren returns this XML object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * {@code // Replace the entire contents of Jim's employee element
	 * employees.replace(0, <requisition status="open"/>);
	 * e.employee.(name == "Jim").setChildren(<name>John</name> + <age>35</age>);}
	 * </pre>
	 * @param value new XML to set.
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML setChildren(E4XXML value);

	/**
	 * <b>function setChildren(value)</b> replaces the XML properties of this XML object with a new set of XML properties from value. value may be a single XML object or an XMLList. setChildren returns this XML object.
	 * <p>
     * <strong>Example</strong>
	 * <pre>
	 * {@code // Replace the entire contents of Jim's employee element
	 * employees.replace(0, <requisition status="open"/>);
	 * e.employee.(name == "Jim").setChildren(<name>John</name> + <age>35</age>);}
	 * </pre>
	 * @param value new XMLList to set.
	 * @returns this XML object
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXML setChildren(E4XXMLList value);

	/**
	 * <b>function setLocalName(name)</b> replaces the local name of this XML object with a string constructed from the given <b><i>name</i></b>.
	 * @param name to set for the XML element.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	void setLocalName(JSString name);

	/**
	 * <b>function setName(name)</b> sets the name of the XML object to the requested value (possibly qualified).
	 * @param name qualified name to set.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	void setName(E4XQName name);

	/**
	 * <b>function setNamespace(namespace)</b> sets the namespace of the XML object to the requested value.
	 * @param namespace to set.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	void setNamespace(E4XNamespace namespace);

	/**
	 * <b>function text()</b> returns an XMLList containing all XML properties of this XML object that represent XML text nodes.
	 * @return concatenation of all text node children as a list.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	E4XXMLList text();

	/**
	 * <b>function toXMLString()</b> returns the toXMLString() method returns an XML encoded string representation of this XML object. Unlike the toString method, toXMLString provides no special treatment for XML objects that contain only XML text nodes (i.e., primitive values). The toXMLString method always includes the start tag, attributes and end tag of the XML object regardless of its content. It is provided for cases when the default XML to string conversion rules are not desired.
	 * @return Serializes this XML object as parseable XML.
	 * @memberOf XML
	 * @see org.fife.rsta.ac.js.ecma.api.e4x.E4XXML XML
	 * @since Standard ECMA-357 2nd. Edition
	 */
	JSString toXMLString();
}
