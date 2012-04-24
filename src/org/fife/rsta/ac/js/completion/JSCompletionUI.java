package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;


public interface JSCompletionUI {

	static final int LOCAL_VARIABLE_INDEX = 0;
	static final int DEFAULT_VARIABLE_INDEX = 1;
	static final int STATIC_FIELD_INDEX = 2;
	static final int BEAN_METHOD_INDEX = 3;
	static final int DEFAULT_FUNCTION_INDEX = 4;
	static final int GLOBAL_FUNCTION_INDEX = 5;
	static final int BASIC_COMPLETION_INDEX = 6;
	static final int TEMPLATE_INDEX = 7;
	
	
	
	/**
	 * @return Icon for completion
	 */
	Icon getIcon();


	/**
	 * @return sort index of the completion for sorting in order to be displayed
	 */
	int getSortIndex();
}
