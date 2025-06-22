package org.fife.rsta.ac.js.ecma.api.ecma5;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma5.functions.JS5ObjectFunctions;


/**
 * Base JavaScript Object.
 *
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JS5Object extends JSObject implements JS5ObjectFunctions {

	/**
	 * Object Object().
	 *
	 * <p>Creates a new object instance</p>
	 *
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Object() {
	}

	/**
	 * Object Object(value).
	 *
	 * @param value optional argument specifies a primitive JavaScript value - a number, boolean etc..
	 * @constructor
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	public JS5Object(JSObject value) {
	}

	/**
	 * <b>function create(proto, descriptors)</b> create an object with specified prototype and properties.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var p = Object.create({z:0}), {
	 *   x: { value: 1, writable, false, enumerable:true. configurable:true},
	 *   y: { value: 2, writable, false, enumerable:true. configurable:true},
	 * });
	 * </pre>
	 *
	 * @param proto       The prototype of the newly created object, or null.
	 * @param descriptors An optional object that maps property names to property descriptors.
	 * @returns A newly created object that inherits from {@code proto} and has properties described by <b><i>descriptors</i></b>
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #defineProperties(JS5Object, JS5Object) defineProperties()
	 * @see #getOwnPropertyDescriptor(JS5Object, JS5String) getOwnPropertyDescriptor()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object create(JS5Object proto, JS5Object descriptors) {
		return null;
	}


	/**
	 * <b>function defineProperties(o, descriptors)</b> create or configure multiple object properties.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var p = Object.defineProperties({}), {
	 *   x: { value: 1, writable, false, enumerable:true. configurable:true},
	 *   y: { value: 2, writable, false, enumerable:true. configurable:true},
	 * });
	 * </pre>
	 *
	 * @param o           The object on which properties are to be created or configured.
	 * @param descriptors An object that maps property names to property descriptors.
	 * @returns The object {@code o}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #create(JS5Object, JS5Object) create()
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #getOwnPropertyDescriptor(JS5Object, JS5String) getOwnPropertyDescriptor()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object defineProperties(JS5Object o, JS5Object descriptors) {
		return null;
	}

	/**
	 * <b>function defineProperty(o, name, desc)</b> create or configure an object property.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * function constant(o, n, v) { //define a constant with value v
	 *   Object.defineProperty (o, n, { value: v, writable, false, enumerable:true. configurable:true});
	 * }
	 * </pre>
	 *
	 * @param o    The object on which a property is to be created or configured.
	 * @param name The name of the property created or configured.
	 * @param desc A property descriptor object that describes the new property.
	 * @returns The object {@code o}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object defineProperty(JS5Object o, JS5String name, JS5Object desc) {
		return null;
	}

	/**
	 * <b>function freeze(o)</b> make an object immutable.
	 *
	 * @param o The object to be frozen.
	 * @returns The now-frozen argument object {@code o}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #isFrozen(JS5Object) isFrozen()
	 * @see #preventExtensions(JS5Object) preventExtensions()
	 * @see #seal(JS5Object) seal()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object freeze(JS5Object o) {
		return null;
	}

	/**
	 * <b>function getOwnPropertyDescriptor(o, name)</b> query property attributes.
	 *
	 * @param o    The object that is to have its property attributes queried.
	 * @param name The name of the property to query.
	 * @returns A property descriptor object for the specified property or {@code undefined} if no such property exists.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object getOwnPropertyDescriptor(JS5Object o, JS5String name) {
		return null;
	}

	/**
	 * <b>function getOwnPropertyNames(o)</b> return the names of non-inherited properties.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * Object.getOwnPropertyNames([]); //returns [length]: "length" is non enumerable
	 * </pre>
	 *
	 * @param o An object
	 * @returns An array that contains the names of all non-inherited properties of {@code o}, including non-enumerable properties.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #keys(JS5Object) keys()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Array getOwnPropertyNames(JS5Object o) {
		return null;
	}

	/**
	 * <b>function getPrototypeOf(o)</b> return the prototype of an object
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var p = {}; //create object
	 * Object.getPrototypeOf(p); //=&gt; Object.prototype
	 * var o = Object.create(p); //an object inherited from p
	 * Object.getPrototypeOf(o); //=&gt; p
	 * </pre>
	 *
	 * @param o An object.
	 * @returns The prototype of object {@code o}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #create(JS5Object, JS5Object) create()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object getPrototypeOf(JS5Object o) {
		return null;
	}

	/**
	 * <b>function isExtensible(o)</b> can new properties be added to an object?
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var o = {}; //create object
	 * Object.isExtensible(o); //=&gt; true
	 * Object.preventExtensions(o); //Make it non-extensible
	 * Object.isExtensible(o); //=&gt; false
	 * </pre>
	 *
	 * @param o The object to be checked for extensibility
	 * @returns {code true} if the object can be extended with new properties, otherwise {code false}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JSBoolean isExtensible(JS5Object o) {
		return null;
	}

	/**
	 * <b>function isFrozen(o)</b> is an object immutable?
	 *
	 * @param o The object to be checked.
	 * @returns {code true} if the object is frozen or immutable, otherwise {code false}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #freeze(JS5Object) freeze()
	 * @see #isExtensible(JS5Object) isExtensible()
	 * @see #isSealed(JS5Object) isSealed()
	 * @see #preventExtensions(JS5Object) preventExtensions()
	 * @see #seal(JS5Object) seal()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JSBoolean isFrozen(JS5Object o) {
		return null;
	}

	/**
	 * <b>function isSealed(o)</b> can properties be added or deleted from an object?
	 *
	 * @param o The object to be checked.
	 * @returns {code true} if the object is sealed, otherwise {code false}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #freeze(JS5Object) freeze()
	 * @see #isExtensible(JS5Object) isExtensible()
	 * @see #isFrozen(JS5Object) isFrozen()
	 * @see #preventExtensions(JS5Object) preventExtensions()
	 * @see #seal(JS5Object) seal()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JSBoolean isSealed(JS5Object o) {
		return null;
	}

	/**
	 * <b>function keys(o)</b> return enumerable property names.
	 *
	 * <strong>Example</strong>
	 * <pre>
	 * Object.keys({x:1, y:2}); // =&gt; ["x", "y"]
	 * </pre>
	 *
	 * @param o an object
	 * @returns An array that contains the names of all enumerable own (non-inherited) properties of {@code o}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #getOwnPropertyNames(JS5Object) getOwnPropertyNames()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Array keys(JS5Object o) {
		return null;
	}

	/**
	 * <b>function preventExtensions(o)</b> don't allow new properties on an object.
	 *
	 * @param o The object is to have its extensibility attribute set.
	 * @returns The argument {@code o}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #freeze(JS5Object) freeze()
	 * @see #isExtensible(JS5Object) isExtensible()
	 * @see #seal(JS5Object) seal()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object preventExtensions(JS5Object o) {
		return null;
	}

	/**
	 * <b>function seal(o)</b> prevent the addition or deletion of properties.
	 *
	 * @param o The object to be sealed.
	 * @returns The now-sealed argument of {@code o}.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma5.JS5Object Object
	 * @see #defineProperty(JS5Object, JS5String, JS5Object) defineProperty()
	 * @see #freeze(JS5Object) freeze()
	 * @see #isSealed(JS5Object) isSealed()
	 * @see #preventExtensions(JS5Object) preventExtensions()
	 * @since Standard ECMA-262 5th. Edition
	 */
	public static JS5Object seal(JS5Object o) {
		return null;
	}

}
