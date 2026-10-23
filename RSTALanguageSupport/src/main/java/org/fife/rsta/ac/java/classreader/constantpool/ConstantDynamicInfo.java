/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;


/**
 * Represents a dynamically-computed constant (<code>CONSTANT_Dynamic_info</code>,
 * added in class file version 55 / Java 11).  Its value is produced at
 * runtime by a bootstrap method.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantDynamicInfo extends ConstantPoolInfo {

	private int bootstrapMethodAttrIndex;

	private int nameAndTypeIndex;


	/**
	 * Constructor.
	 *
	 * @param bootstrapMethodAttrIndex An index into the
	 *        <code>bootstrap_methods</code> table of the class file.
	 * @param nameAndTypeIndex The index into the constant pool containing a
	 *        {@link ConstantNameAndTypeInfo}.
	 */
	public ConstantDynamicInfo(int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		super(CONSTANT_Dynamic);
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
		return "[ConstantDynamicInfo: " +
				"bootstrapMethodAttrIndex=" + getBootstrapMethodAttrIndex() +
				"; nameAndTypeIndex=" + getNameAndTypeIndex() +
				"]";
	}


}
