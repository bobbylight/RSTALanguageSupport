package org.fife.rsta.ac.js.completion;


public interface JSCompletion extends JSCompletionUI {


	/**
	 * @return a logical lookup name that is unique
	 */
	String getLookupName();


	/**
	 * @return JavaScript type from Completion qualified
	 */
	String getType(boolean qualified);


	/**
	 * Returns the name of the enclosing class.
	 *
	 * @param fullyQualified Whether the name returned should be fully
	 *        qualified.
	 * @return The class name.
	 */
	String getEnclosingClassName(boolean fullyQualified);


}