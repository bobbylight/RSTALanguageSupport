package com.example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A sample annotation for testing.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationTypeDecl {

    /**
     * The primary value.
     */
    String value() default "";

    /**
     * An integer element.
     */
    int count() default 0;

    /** Array element. */
    String[] tags() default {};

    /** Element without default. */
    boolean required();
}
