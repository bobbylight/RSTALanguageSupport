package org.fife.rsta.ac.js.ecma.api.ecma3;


/**
 * Object Math
 * @since Standard ECMA-262 3rd. Edition
 */
public abstract class JSMath {

    /**
     * <b>property E</b> the constant e, the base of the natural logarithm.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber E;
   
   /**
     * <b>property LN10</b> the natural logarithm of 10.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber LN10;
   
   /**
     * <b>property LN2</b> the natural logarithm of 2.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber LN2;
   
   /**
     * <b>property LOG2E</b> the base-2 logarithm of e.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber LOG2E;
   
   /**
     * <b>property LOG10E</b> the base-10 logarithm of e.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber LOG10E;
   
   /**
     * <b>property PI</b> The constant PI.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber PI;
   
   /**
     * <b>property SQRT1_2</b> the number 1 divided by the square root of 2.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber SQRT1_2;
   
   /**
     * <b>property SQRT2</b> the square root of 2.
     * @memberOf Math
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber SQRT2;
   
   /**
     * <b>function abs(x)</b> computes an absolute value.
     * @memberOf Math
     * @param x any number
     * @type Number
     * @returns The absolute value of x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public static JSNumber abs(JSNumber x){return null;}
   
   /**
     * <b>function acos(x)</b> compute an arccosine
     * @memberOf Math
     * @param x a number between -1.0 and 1.0
     * @type Number
     * @returns The arccosine, or inverse cosine, of the specified value x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber acos(JSNumber x){return null;}
   
   /**
     * <b>function asin(x)</b> compute an arcsine
     * @memberOf Math
     * @param x a number between -1.0 and 1.0
     * @type Number
     * @returns The arcsine of the specified value x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber asin(JSNumber x){return null;}
   
   /**
     * <b>function atan(x)</b> compute an arctangent
     * @memberOf Math
     * @param x Any number
     * @type Number
     * @returns The arc tangent of the specified value x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber atan(JSNumber x){return null;}
   
   /**
     * <b>function atan2(x,y)</b> compute the angle from the X axis to a point.
     * @memberOf Math
     * @param y The Y coordinate of the point 
     * @param x The X coordinate of the point
     * @type Number
     * @returns the arctangent of the quotient of its arguments.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber atan2(JSNumber y, JSNumber x){return null;}
   
   /**
     * <b>function ceil(x)</b> round a number up
     * <h4>Example</h4>
	 * <code>
     *  a = Math.ceil(1.99); //returns 2.0<br>
	 *  b = Math.ceil(1.01); //returns 2.0<br>
	 *  c = Math.ceil(1.0) //returns 1.0<br>
	 *  d = Math.ceil(-1.99); //returns -1.0<br>
	 * </code>
     * @memberOf Math 
     * @param x any number or numeric value.
     * @type Number
     * @returns The closest integer greater to or equal to x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber ceil(JSNumber x){return null;}
   
   /**
     * <b>function cos(x)</b> compute a cosine
     * @memberOf Math
     * @param x an angle, measured in radians.
     * @type Number
     * @returns The cosine of the specified value.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber cos(JSNumber x){return null;}
   
   /**
     * <b>function exp(x)</b> compute E<sup>x</sup>.
     * @memberOf Math
     * @param x a numeric value or expression.  
     * @type Number
     * @returns E<sup>x</sup>, e raised to the power of the specified exponent x. 
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber exp(JSNumber x){return null;}
   
   /**
     * <b>function floor(x)</b> round a number down.
     * <h4>Example</h4>
	 * <code>
     *  a = Math.floor(1.99); //returns 1.0<br>
	 *  b = Math.floor(1.01); //returns 1.0<br>
	 *  c = Math.floor(1.0) //returns 1.0<br>
	 *  d = Math.floor(-1.99); //returns -2.0<br>
	 * </code>
     * @memberOf Math
     * @param x any number or numeric value.
     * @type Number
     * @returns The closest integer less than or equal to x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber floor(JSNumber x){return null;}
   
   /**
     * <b>function log(x)</b> compute a natural logarithm.
     * @memberOf Math
     * @param x any number or numeric value greater than 0.
     * @type Number
     * @returns The natural logarithm of <b><i>x</i></b>.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber log(JSNumber x){return null;}
   
   /**
     * <b>function max(args)</b> Return the largest argument
     * @memberOf Math
     * @param args Zero or more values
     * @type Number
     * @returns The largest of the arguments. Returns -Infinity if there are no arguments.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber max(JSNumber args){return null;}
   
   /**
     * <b>function min(args)</b> return the smallest argument.
     * @memberOf Math
     * @param args Any number of arguments
     * @type Number
     * @returns The smallest of the arguments. Returns -Infinity if there are no arguments.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber min(JSNumber args){return null;}
   
   /**
     * <b>function pow(x,y)</b> compute X<sub>y</sub>
     * @memberOf Math
     * @param x The number to be raised to a power.
     * @param y The power that x to be raised to.
     * @type Number
     * @returns x to the power of y.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber pow(JSNumber x, JSNumber y){return null;}
   
   /**
     * function random() return a pseudorandom number
     * @memberOf Math
     * @type Number
     * @returns a pseudorandom number greater or equal to 0.0 and less than 1.0
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public static JSNumber random(){return null;}
   
   /**
     * <b>function round(x)</b> round to the nearest integer.
     * @memberOf Math
     * @param x Any number.
     * @type Number
     * @returns The integer closest to x.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.   
    */
   public static JSNumber round(JSNumber x){return null;}
   
   /**
     * <b>function sin(x)</b> compute a sine.
     * @memberOf Math
     * @param x An angle, in radians.
     * @type Number
     * @returns The sine of x. The return value is between -1.0 and 1.0
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber sin(JSNumber x){return null;}
   
   /**
     * <b>function sqrt(x)</b> compute a square root.
     * @memberOf Math
     * @param x a numeric value greater than or equal to zero.
     * @type Number
     * @returns the square root of x. Returns Nan if x is less than 0.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public static JSNumber sqrt(JSNumber x){return null;}
   
   /**
     * <b>function tan(x)</b> compute a tangent.
     * @memberOf Math
     * @param x An angle, in radians.
     * @type Number
     * @returns The tangent of the specified angle <b><i>x</i></b>.
     * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber Number
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber tan(JSNumber x){return null;}
   

}
