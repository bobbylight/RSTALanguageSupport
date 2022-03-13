package org.fife.rsta.ac.js;


public class Logger {

	private static boolean DEBUG;
	
	static
	{
		DEBUG = Boolean.parseBoolean(System.getProperty("javascript.debug"));
	}
	
	/**
	 * TODO change logging to Log4J?
	 * Log message to console
	 * @param msg
	 */
	public static final void log(String msg) {
		if (DEBUG) {
			System.out.println(msg);
		}
	}
	
	public static final void logError(String msg) {
		System.err.println(msg);
	}
}
