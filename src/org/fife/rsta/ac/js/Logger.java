package org.fife.rsta.ac.js;


public class Logger {

	private static boolean DEBUG = false;
	
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
}
