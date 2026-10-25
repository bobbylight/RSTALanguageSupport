/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;

/**
 * Class representing a <code>CONSTANT_MethodType</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantMethodTypeInfo extends ConstantPoolInfo {

    private int descriptorIndex;

    /**
     * Constructor.
     *
     * @param descriptorIndex The descriptor index.
     */
    public ConstantMethodTypeInfo(int descriptorIndex) {
        super(CONSTANT_MethodType);
        this.descriptorIndex = descriptorIndex;
    }


    public int getDescriptorIndex() {
        return descriptorIndex;
    }


    /**
     * Returns a string representation of this object.  Useful for debugging.
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return "[ConstantMethodTypeInfo: " +
                "bootstrapMethodAttrIndex=" + getDescriptorIndex() +
                "]";
    }


}
