package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.*;

/**
 * @since 2017.03.06.
 */
public interface MemberClickedListener extends ExternalMemberClickedListener
{
    /**
     * Indicates user clicked on an inner class
     * @param typeDeclaration the typeDeclaration structure holding information about the inner class
     */
    void gotoInnerClass(TypeDeclaration typeDeclaration);

    /**
     * Indicates user clicked a method within current document
     * @param method the method structure holding information about the clicked method
     */
    void gotoMethod(Method method);

    /**
     * Indicates user clicked a field within current document
     * @param field the field structure holding information about the clicked field
     */
    void gotoField(Field field);

    /**
     * Indicates user clicked a local variable within current document
     * @param localVar the localvariable structure holding information about the clicked local variable
     */
    void gotoLocalVar(LocalVariable localVar);

    /**
     * Indicates user clicked a variable which is a parameter in the current method
     * @param parameter the formalparameter structure holding information about the clicked method parameter
     */
    void gotoMethodParameter(FormalParameter parameter);
}
