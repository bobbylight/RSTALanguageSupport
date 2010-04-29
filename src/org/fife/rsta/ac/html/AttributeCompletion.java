package org.fife.rsta.ac.html;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;


/**
 * A completion for an HTML attribute.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class AttributeCompletion extends AbstractCompletion {

	private Parameter param;


	public AttributeCompletion(CompletionProvider provider, Parameter param) {
		super(provider);
		this.param = param;
	}


	public String getSummary() {
		return param.getDescription();
	}


	public String getReplacementText() {
		return param.getName();
	}


}