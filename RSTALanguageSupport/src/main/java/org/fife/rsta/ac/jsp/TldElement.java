/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.jsp;

import org.fife.ui.autocomplete.MarkupTagCompletion;


/**
 * An element defined in a TLD.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TldElement extends MarkupTagCompletion {


	TldElement(JspCompletionProvider provider, String name, String desc){
		super(provider, name);
		setDescription(desc);
	}


}
