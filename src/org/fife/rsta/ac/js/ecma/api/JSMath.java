package org.fife.rsta.ac.js.ecma.api;

public class JSMath extends JSObject {

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
    * Property prototype
    * 
    * @type Math
    * @memberOf Math
    * @see Math
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   public JSMath protype;
   
   /**
    * Property constructor
    * 
    * @type Function
    * @memberOf Math
    * @see Function
    * @since Standard ECMA-262 3rd. Edition
    * @since Level 2 Document Object Model Core Definition.
    */
   protected JSFunction constructor;
   
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
   public static JSNumber abs(JSNumber x){return null;}
   
   /**
     * function acos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber acos(JSNumber x){return null;}
   
   /**
     * function asin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber asin(JSNumber x){return null;}
   
   /**
     * function atan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static JSNumber atan(JSNumber x){return null;}
   
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
   public static JSNumber atan2(JSNumber x, JSNumber y){return null;}
   
   /**
     * function ceil(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber ceil(JSNumber x){return null;}
   
   /**
     * function cos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber cos(JSNumber x){return null;}
   
   /**
     * function exp(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static JSNumber exp(JSNumber x){return null;}
   
   /**
     * function floor(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber floor(JSNumber x){return null;}
   
   /**
     * function log(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber log(JSNumber x){return null;}
   
   /**
     * function max(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static JSNumber max(JSNumber args){return null;}
   
   /**
     * function min(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber min(JSNumber args){return null;}
   
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
   public static JSNumber pow(JSNumber x, JSNumber y){return null;}
   
   /**
     * function pow()
     * @memberOf Math
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public static JSNumber random(){return null;}
   
   /**
     * function round(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.   
    */
   public static JSNumber round(JSNumber x){return null;}
   
   /**
     * function sin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber sin(JSNumber x){return null;}
   
   /**
     * function sqrt(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public static JSNumber sqrt(JSNumber x){return null;}
   
   /**
     * function tan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static JSNumber tan(JSNumber x){return null;}
   

}
