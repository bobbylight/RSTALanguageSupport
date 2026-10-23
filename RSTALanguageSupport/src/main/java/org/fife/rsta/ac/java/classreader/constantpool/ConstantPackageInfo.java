/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;


/**
 * Represents a package exported or opened by a module (<code>CONSTANT_Package_info</code>).  These only appear in
 * <code>module-info</code> class files.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantPackageInfo extends ConstantPoolInfo {

	private int nameIndex;


	/**
	 * Constructor.
	 *
	 * @param nameIndex The index into the constant pool containing a
	 *        {@link ConstantUtf8Info} with the name.
	 */
	public ConstantPackageInfo(int nameIndex) {
		super(CONSTANT_Package);
		this.nameIndex = nameIndex;
	}


	public int getNameIndex() {
		return nameIndex;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[ConstantPackageInfo: " +
				"nameIndex=" + getNameIndex() +
				"]";
	}


}
