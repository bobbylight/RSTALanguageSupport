package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSBoolean;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSObject;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSObjectFunctions {
	

    /**
     * <b>function toString()</b> define an objects string representation.
     * @memberOf Object
     * @returns a string representing the object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @see #toLocaleString() toLocalString()
     * @see #valueOf() valueOf()
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    @Override
	String toString();

    /**
     * <b>function toLocaleString()</b> return an object localized string representation.
     * @memberOf Object
     * @returns A string representing the object
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @see #toString() toString()
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSString toLocaleString();

    /**
     * <b>function valueOf()</b> the primitive value of a specified object.
     * @memberOf Object
     * @returns The primitive value associated with the <b><i>object</i></b>, if any. 
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object.
     * @see #toString() toString()
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSObject valueOf();

    /**
     * <b>function hasOwnProperty(name)</b> check whether a property is inherited.
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var o = new Object();
	 * o.x = 3.14;
	 * o.hasOwnProperty("x"); //return true; o has property x.
	 * o.hasOwnProperty("y"); //return false; o does not have property y.
	 * o.hasOwnProperty("toString"); //return false; o inherits toString.
	 * </pre> 
     * @memberOf Object
     * @param name A string that contains the name of a property of <b><i>object</i></b>.
     * @returns <b><i>true</i></b> if <b><i>object</i></b> has a noninherited property with the name specified by <b><i>name</i></b>. 
     * <b><i>false</i></b> if <b><i>object</i></b> does not contain the property with the specified name or if it inherits the property from
     * its prototype object. 
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @see #propertyIsEnumerable(JSObject) propertyIsEnumerable()
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSBoolean hasOwnProperty(String name);

    /**
     * <b>function isPrototypeOf(o)</b> is an object the prototype of another?
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var o = new Object();
	 * Object.prototype.isPrototypeOf(o); //true: o is an object.
	 * Function.prototype.isPrototypeOf(o.toString(); //return true: toString is a function.
	 * Array.prototype.isPrototypeOf([1,2,3]; //return true: [1,2,3] is an Array.
	 * </pre> 
     * @memberOf Object
     * @param o Any object
     * @returns <b><i>true</i></b> if <b><i>object</i></b> is prototype of o. <b><i>false</i></b> is not an object or if <b><i>object</i></b>
     * is not prototype of o
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSBoolean isPrototypeOf(JSObject o);

    /**
     * <b>function propertyIsEnumerable(name)</b>  will property be seen by for/in loop?
     * <p>
     * <strong>Example</strong>
	 * <pre>
	 * var o = new Object();
	 * o.x = 3.14;
	 * o.propertyIsEnumerable("x"); //return true; property x is local and enumerable.
	 * o.propertyIsEnumerable("y"); //return false; o does not have property y.
	 * o.propertyIsEnumerable("toString"); //return false; o inherits toString.
	 * </pre> 
     * @memberOf Object
     * @param name A string that contains the name of a property of <b><i>object</i></b>.
     * @returns <b><i>true</i></b> if <b><i>object</i></b> has a noninherited property with the name specified by <b><i>name</i></b> and
     * if that name is enumerable.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSObject Object
     * @see #hasOwnProperty(String)
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
     */
    JSBoolean propertyIsEnumerable(JSObject name);
}
