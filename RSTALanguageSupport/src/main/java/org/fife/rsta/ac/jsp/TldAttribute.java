/*
 * 07/05/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.jsp;

import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;


/**
 * An attribute of an element defined in a TLD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TldAttribute extends AttributeCompletion {

	public boolean required;
	public boolean rtexprvalue;


	public TldAttribute(JspCompletionProvider provider,
			TldAttributeParam param) {
		super(provider, param);
	}


	public static class TldAttributeParam extends Parameter {

		private boolean required;
		private boolean rtextprvalue;
		
		public TldAttributeParam(Object type, String name, boolean required,
									boolean rtextprvalue) {
			super(type, name);
			this.required = required;
			this.rtextprvalue = rtextprvalue;
		}

		public boolean isRequired() {
			return required;
		}

		public boolean getRtextprvalue() {
			return rtextprvalue;
		}

	}


}
