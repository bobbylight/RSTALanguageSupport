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

import java.io.UnsupportedEncodingException;


/**
 * Class representing a <code>CONSTANT_Utf8_info</code> structure.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class ConstantUtf8Info extends ConstantPoolInfo {

	//private byte[] bytes;
	private String representedString;


	/**
	 * Constructor.
	 */
	public ConstantUtf8Info(byte[] bytes) {
		super(CONSTANT_Utf8);
		//this.bytes = bytes;
		representedString = createRepresentedString(bytes);
	}


//	public byte[] getBytes() {
//		return bytes;
//	}

/*
	private static final boolean isBitSet(int b, int bit) {
		return ((b>>bit)&1)>0;
	}
*/

	private String createRepresentedString(byte[] bytes) {
/*
		StringBuilder sb = new StringBuilder();

		int pos = 0;
		while (pos<bytes.length) {

			int b = bytes[pos++];

			// A single-byte char, '\u0001' to '\u007F'.
			if (!isBitSet(b, 7)) {
				sb.append((char)(b&0x7f));
			}

			// null char ('\u0000') or char in range '\u0080' - '\u07FF'.
			else if (6==(b>>5)) {
				// x = 110 (bits 10-6)
				// y = 10 (bits 5-0)
				// ch = ((x & 0x1f) << 6) + (y & 0x3f)
				int x = b;
				int y = bytes[pos++];
				char ch = (char)(((x&0x1f)<<6) + (y&0x3f));
				sb.append(ch);
			}

			// chars in range '\u0800' - '\uFFFF'.
			else if (15==(b>>4)) {
				// x = 1110 (bits 15-12)
				// y = 10 (bits 11-6)
				// z = 10 (bits 5-0)
				// ch = ((x & 0xf) << 12) + ((y & 0x3f) << 6) + (z & 0x3f)
				int x = b;
				int y = bytes[pos++];
				int z = bytes[pos++];
				char ch = (char)(((x&0xf)<<12) + ((y&0x3f)<<6) + (z&0x3f));
				sb.append(ch);
			}

			// An unknown bit header.
			else {
				throw new InternalError("Unknown bit header in Utf8 string: " +
						b);
			}

		}

		representedString = sb.toString();
*/
try {
	representedString = new String(bytes, "UTF-8");
} catch (UnsupportedEncodingException uee) { // Never happens.
	uee.printStackTrace();
	// System.exit(0);
	throw new Error(uee);
}
		return representedString;

	}


	/**
	 * Returns the string represented by this info.
	 *
	 * @param quoted Whether to add enclosing quotation marks, and "escape"
	 *        any quotation marks in the represented string.
	 * @return The string represented.
	 */
	public String getRepresentedString(boolean quoted) {
		if (!quoted) {
			return representedString;
		}
		String temp = "\"" + representedString.replaceAll("\"", "\\\"") + "\"";
		return temp;
	}


	/**
	 * Returns a string representation of this object.  Useful for debugging.
	 *
	 * @return A string representation of this object.
	 */
	@Override
	public String toString() {
		return "[ConstantUtf8Info: " + representedString +
				"]";
	}


}