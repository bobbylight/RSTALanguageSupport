package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;

public interface JSCompletion {

	/**
	 * @return a logical lookup name that is unique
	 */
	String getLookupName();


	/**
	 * @return JavaScript type from Completion qualified
	 */
	String getType(boolean qualified);
	
	/**
	 * @return Icon for completion
	 */
	Icon getIcon();
	
}
