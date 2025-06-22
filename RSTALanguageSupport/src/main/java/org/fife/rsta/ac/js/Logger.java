package org.fife.rsta.ac.js;


public final class Logger {

	private static final System.Logger LOG = System.getLogger(Logger.class.getName());

	private static final boolean DEBUG;

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Logger() {
		// Do nothing
	}

	static {
		DEBUG = Boolean.parseBoolean(System.getProperty("javascript.debug"));
	}

	/**
	 * Log message to console if debug logging is enabled.
	 *
	 * @param msg
	 */
	public static void log(String msg) {
		if (DEBUG) {
			LOG.log(System.Logger.Level.DEBUG, msg);
		}
	}

	public static void logError(String msg) {
		LOG.log(System.Logger.Level.ERROR, msg);
	}
}
