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
 * Class representing a <code>CONSTANT_InvokeDynamic_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantInvokeDynamicInfo extends ConstantPoolInfo {

    private int bootstrapMethodAttrIndex;

    private int nameAndTypeIndex;


    /**
     * Constructor.
     *
     * @param bootstrapMethodAttrIndex The index.
     * @param nameAndTypeIndex The name and type index.
     */
    public ConstantInvokeDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
        super(CONSTANT_InvokeDynamic);
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }


    public int getBootstrapMethodAttrIndex() {
        return bootstrapMethodAttrIndex;
    }


    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }


    /**
     * Returns a string representation of this object.  Useful for debugging.
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return "[ConstantInvokeDynamicInfo: " +
                "bootstrapMethodAttrIndex=" + getBootstrapMethodAttrIndex() +
                "; nameAndTypeIndex=" + getNameAndTypeIndex() +
                "]";
    }


}
