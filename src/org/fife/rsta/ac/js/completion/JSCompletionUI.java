package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;


public interface JSCompletionUI {

	static final int LOCAL_VARIABLE_RELEVANCE = 8;
	static final int GLOBAL_VARIABLE_RELEVANCE = 7;
	static final int DEFAULT_VARIABLE_RELEVANCE = 6;
	static final int STATIC_FIELD_RELEVANCE = 5;
	static final int BEAN_METHOD_RELEVANCE = 4;
	static final int DEFAULT_FUNCTION_RELEVANCE = 3;
	static final int GLOBAL_FUNCTION_RELEVANCE = 2;
	static final int BASIC_COMPLETION_RELEVANCE = 1;
	static final int TEMPLATE_RELEVANCE = 0;


	/**
	 * @return Icon for completion
	 */
	Icon getIcon();


}
