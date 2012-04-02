package org.fife.rsta.ac.js.ecma.api;

public abstract class JSMath extends JSObject {

    /**
     * Object Math(\s)
     * @super Object
     * @constructor
     * @memberOf Math
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
    */
   public JSMath(){};
   
   /**
     * Property E
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber E;
   
   /**
     * Property LN10
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber LN10;
   
   /**
     * Property LN2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber LN2;
   
   /**
     * Property LOG2E
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber LOG2E;
   
   /**
     * Property LOG10E
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber LOG10E;
   
   /**
     * Property PI
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber PI;
   
   /**
     * Property SQRT1_2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber SQRT1_2;
   
   /**
     * Property SQRT2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber SQRT2;
   
   /**
     * function abs(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract JSNumber abs(JSNumber x);
   
   /**
     * function acos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public abstract JSNumber acos(JSNumber x);
   
   /**
     * function asin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract JSNumber asin(JSNumber x);
   
   /**
     * function atan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public abstract JSNumber atan(JSNumber x);
   
   /**
     * function atan2(x,y)
     * @memberOf Math
     * @param {Number} x
     * @param {Number} y
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract JSNumber atan2(JSNumber x, JSNumber y);
   
   /**
     * function ceil(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber ceil(JSNumber x);
   
   /**
     * function cos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract JSNumber cos(JSNumber x);
   
   /**
     * function exp(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public abstract JSNumber exp(JSNumber x);
   
   /**
     * function floor(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract JSNumber floor(JSNumber x);
   
   /**
     * function log(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber log(JSNumber x);
   
   /**
     * function max(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract JSNumber max(JSNumber args);
   
   /**
     * function min(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber min(JSNumber args);
   
   /**
     * function pow(x,y)
     * @memberOf Math
     * @param {Number} x
     * @param {Number} y
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber pow(JSNumber x, JSNumber y);
   
   /**
     * function pow()
     * @memberOf Math
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract JSNumber random();
   
   /**
     * function round(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.   
    */
   public abstract JSNumber round(JSNumber x);
   
   /**
     * function sin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber sin(JSNumber x);
   
   /**
     * function sqrt(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract JSNumber sqrt(JSNumber x);
   
   /**
     * function tan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract JSNumber tan(JSNumber x);
   

}
