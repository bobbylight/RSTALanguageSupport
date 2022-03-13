/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js.ast;

import org.fife.ui.autocomplete.FunctionCompletion;


public class FunctionDeclaration {

	private FunctionCompletion fc;
	private int offset;


	public FunctionDeclaration(FunctionCompletion fc, int offset) {
		this.fc = fc;
		this.offset = offset;
	}


	public FunctionCompletion getFunction() {
		return fc;
	}


	public int getOffset() {
		return offset;
	}


}
