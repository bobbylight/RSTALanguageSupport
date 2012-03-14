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
   public static Number E;
   
   /**
     * Property LN10
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static Number LN10;
   
   /**
     * Property LN2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public static Number LN2;
   
   /**
     * Property LOG2E
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static Number LOG2E;
   
   /**
     * Property LOG10E
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static Number LOG10E;
   
   /**
     * Property PI
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public static Number PI;
   
   /**
     * Property SQRT1_2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public static Number SQRT1_2;
   
   /**
     * Property SQRT2
     * @memberOf Math
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public static Number SQRT2;
   
   /**
     * function abs(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract Number abs(Number x);
   
   /**
     * function acos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public abstract Number acos(Number x);
   
   /**
     * function asin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract Number asin(Number x);
   
   /**
     * function atan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.
    */
   public abstract Number atan(Number x);
   
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
   public abstract Number atan2(Number x, Number y);
   
   /**
     * function ceil(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract Number ceil(Number x);
   
   /**
     * function cos(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract Number cos(Number x);
   
   /**
     * function exp(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition. 
    */
   public abstract Number exp(Number x);
   
   /**
     * function floor(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract Number floor(Number x);
   
   /**
     * function log(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract Number log(Number x);
   
   /**
     * function max(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.  
    */
   public abstract Number max(Number args);
   
   /**
     * function min(arg)
     * @memberOf Math
     * @param {Number} args
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract Number min(Number args);
   
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
   public abstract Number pow(Number x, Number y);
   
   /**
     * function pow()
     * @memberOf Math
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract Number random();
   
   /**
     * function round(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.   
    */
   public abstract Number round(Number x);
   
   /**
     * function sin(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract Number sin(Number x);
   
   /**
     * function sqrt(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.     
    */
   public abstract Number sqrt(Number x);
   
   /**
     * function tan(x)
     * @memberOf Math
     * @param {Number} x
     * @type Number
     * @returns {Number}
     * @since   Standard ECMA-262 3rd. Edition 
     * @since   Level 2 Document Object Model Core Definition.    
    */
   public abstract Number tan(Number x);
   /**
     * Object RegExp()
     * @super Object
     * @constructor
     * @memberOf RegExp
     * @since Standard ECMA-262 3rd. Edition
     * @since Level 2 Document Object Model Core Definition.
    */

}
