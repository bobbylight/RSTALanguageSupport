/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;

/**
 * Class representing a <code>CONSTANT_MethodHandle</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantMethodHandleInfo extends ConstantPoolInfo {

    private int referenceKind;

    private int referenceIndex;


    /**
     * Constructor.
     *
     * @param referenceKind
     * @param referenceIndex
     */
    public ConstantMethodHandleInfo(int referenceKind, int referenceIndex) {
        super(CONSTANT_MethodHandle);
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }


    public int getReferenceKind() {
        return referenceKind;
    }

    public int getReferenceIndex() {
        return referenceIndex;
    }


    /**
     * Returns a string representation of this object.  Useful for debugging.
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return "[ConstantMethodHandleInfo: " +
                "referenceKind=" + getReferenceKind() +
                "; referenceIndex=" + getReferenceIndex() +
                "]";
    }


}