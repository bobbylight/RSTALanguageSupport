package org.fife.rsta.ac.js.completion;

import org.fife.ui.autocomplete.Completion;


public interface JSCompletionUI extends Completion {

	int LOCAL_VARIABLE_RELEVANCE = 9;
	int GLOBAL_VARIABLE_RELEVANCE = 8;
	int DEFAULT_VARIABLE_RELEVANCE = 7;
	int STATIC_FIELD_RELEVANCE = 6;
	int BEAN_METHOD_RELEVANCE = 5;
	int DEFAULT_FUNCTION_RELEVANCE = 4;
	int GLOBAL_FUNCTION_RELEVANCE = 3;
	int DEFAULT_CLASS_RELEVANCE = 2;
	int BASIC_COMPLETION_RELEVANCE = 1;
	int TEMPLATE_RELEVANCE = 0;


}
