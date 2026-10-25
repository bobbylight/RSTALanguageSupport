/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java.classreader.constantpool;


/**
 * Class corresponding to the <code>CONSTANT_Double_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantDoubleInfo extends ConstantPoolInfo {

	private int highBytes;
	private int lowBytes;


	/**
	 * Constructor.
	 *
	 * @param highBytes The high bytes.
	 * @param lowBytes The low bytes.
	 */
	public ConstantDoubleInfo(int highBytes, int lowBytes) {
		super(CONSTANT_Double);
		this.highBytes = highBytes;
		this.lowBytes = lowBytes;
	}


	/**
	 * Returns the <code>double</code> value represented by this constant.
	 *
	 * @return The double value.
	 */
	public double getDoubleValue() {
		long bits = (((long)highBytes<<32)) + lowBytes;
		return Double.longBitsToDouble(bits);
	}


	public int getHighBytes() {
		return highBytes;
	}


	public int getLowBytes() {
		return lowBytes;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[ConstantDoubleInfo: " +
				"value=" + getDoubleValue() +
				"]";
	}


}
