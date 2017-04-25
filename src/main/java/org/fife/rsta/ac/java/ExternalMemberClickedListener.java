package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.*;

/**
 * @since 2017.03.06.
 */
public interface ExternalMemberClickedListener
{
    /**
     * Indicates user clicked external class
     * @param className the fully qualified class name clicked
     */
    void openClass(String className);

    /**
     * Indicates user clicked a method in an external class
     * @param className the fully qualified class name containing the method
     * @param methodInfo the MethodInfo structure holding information about clicked method
     */
    void gotoMethodInClass(String className, MethodInfo methodInfo);

    /**
     * Indicates user clicked a field in an external class
     * @param className the fully qualified class name containing the field
     * @param fieldInfo the FieldInfo structure holding information about clicked field
     */
    void gotoFieldInClass(String className, FieldInfo fieldInfo);
}
