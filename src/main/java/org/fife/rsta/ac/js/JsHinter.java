/*
 * 01/30/2014
 *
 * Copyright (C) 2014 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;


/**
 * Launches jshint as an external process to parse JavaScript in an
 * {@link RSyntaxTextArea}.  Note that this is pretty inefficient, and was
 * mainly done as a test of jshint integration.  In the future, the external
 * process should be launched in a separate thread.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class JsHinter {

	private JavaScriptParser parser;
//	private RSyntaxDocument doc;
	private DefaultParseResult result;

	private static final Map<String, MarkStrategy> MARK_STRATEGIES;
	static {
		MARK_STRATEGIES = new HashMap<String, MarkStrategy>();
		MARK_STRATEGIES.put("E015", MarkStrategy.MARK_CUR_TOKEN); // Unclosed regular expression.
		MARK_STRATEGIES.put("E019", MarkStrategy.MARK_CUR_TOKEN); // Unmatched '{a}'
		MARK_STRATEGIES.put("E030", MarkStrategy.MARK_CUR_TOKEN); // Expected an identifier and instead saw '{a}'.
		MARK_STRATEGIES.put("E041", MarkStrategy.STOP_PARSING); // Unrecoverable syntax error.
		MARK_STRATEGIES.put("E042", MarkStrategy.STOP_PARSING); // Stopping.
		MARK_STRATEGIES.put("E043", MarkStrategy.STOP_PARSING); // Too many errors.
		MARK_STRATEGIES.put("W004", MarkStrategy.MARK_PREV_NON_WS_TOKEN); // '{a}' is already defined.
		MARK_STRATEGIES.put("W015", MarkStrategy.MARK_CUR_TOKEN); // Expected {a} to have an indentation of {b} instead at {c}.
		MARK_STRATEGIES.put("W032", MarkStrategy.MARK_PREV_TOKEN); // Unnecessary semicolon.
		MARK_STRATEGIES.put("W033", MarkStrategy.MARK_PREV_TOKEN); // Missing semicolon.
		MARK_STRATEGIES.put("W060", MarkStrategy.MARK_CUR_TOKEN); // document.write can be a form of eval.
		MARK_STRATEGIES.put("W098", MarkStrategy.MARK_PREV_TOKEN); // '{a}' is defined but never used.
		MARK_STRATEGIES.put("W116", MarkStrategy.MARK_PREV_TOKEN); // Expected '{a}' and instead saw '{b}'.
		MARK_STRATEGIES.put("W117", MarkStrategy.MARK_CUR_TOKEN); // '{a}' is not defined.
	}


	private JsHinter(JavaScriptParser parser, RSyntaxDocument doc,
			DefaultParseResult result) {
		this.parser = parser;
//		this.doc = doc;
		this.result = result;
	}


	public static void parse(JavaScriptParser parser, RSyntaxTextArea textArea,
			DefaultParseResult result) throws IOException {

		String stdout = null;
		RSyntaxDocument doc = (RSyntaxDocument)textArea.getDocument();

		List<String> command = new ArrayList<String>();
		if (File.separatorChar=='\\') {
			command.add("cmd.exe");
			command.add("/c");
		}
		else {
			command.add("/bin/sh");
			command.add("-c");
		}
		command.add("jshint");
		File jshintrc = parser.getJsHintRCFile(textArea);
		if (jshintrc!=null) {
			command.add("--config");
			command.add(jshintrc.getAbsolutePath());
		}
		command.add("--verbose"); // Allows to decide error vs. warning
		command.add("-");

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream();

		Process p = pb.start();
		PrintWriter w = new PrintWriter(p.getOutputStream());

		// Create threads to read the stdout and stderr of the external
		// process.  If we do not do it this way, the process may
		// deadlock.
		InputStream outStream = p.getInputStream();
		InputStream errStream = p.getErrorStream();
		StreamReaderThread stdoutThread = new StreamReaderThread(outStream);
		StreamReaderThread stderrThread = new StreamReaderThread(errStream);
		stdoutThread.start();

		try {

			String text = doc.getText(0, doc.getLength());
			w.print(text);
			w.flush();
			w.close();

			/*rc = */p.waitFor();
			p = null;

			// Save the stdout and stderr. Don't interrupt reader threads;
			// just wait for them to terminate normally.
			//stdoutThread.interrupt();
			//stderrThread.interrupt();
			stdoutThread.join();
			stderrThread.join();
			stdout = stdoutThread.getStreamOutput();

		} catch (InterruptedException ie) {
			//ie.printStackTrace();
			stdoutThread.interrupt();
			//lastError = ie;
		} catch (BadLocationException ble) {
			ble.printStackTrace(); // Never happens
		} finally {
			if (outStream!=null) {
				outStream.close();
			}
			w.close();
			if (p!=null) {
				p.destroy();
			}
		}

		JsHinter hinter = new JsHinter(parser, doc, result);
		hinter.parseOutput(doc, stdout);

	}


	private void parseOutput(RSyntaxDocument doc, String output) {

		// Line format:
		// stdin: line xx, col yy, error-or-warning-text. (Ennn)

//		Element root = doc.getDefaultRootElement();
//		int indent = parser.getJsHintIndent();

		String[] lines = output.split("\r?\n");
//		OUTER:
		for (String line : lines) {

			String origLine = line;
//			System.out.println(line);
			if (line.startsWith("stdin: line ")) {
				line = line.substring("stdin: line ".length());
				int end = 0;
				while (Character.isDigit(line.charAt(end))) {
					end++;
				}
				int lineNum = Integer.parseInt(line.substring(0, end)) - 1;
				if (lineNum==-1) {
					// Probably bad option to jshint, e.g.
					// stdin: line 0, col 0, Bad option: 'ender'. (E001)
					// Just give them the entire error
					DefaultParserNotice dpn = new DefaultParserNotice(parser,
							origLine, 0);
					result.addNotice(dpn);
					continue;
				}
				line = line.substring(end);
				if (line.startsWith(", col ")) {
					line = line.substring(", col ".length());
					end = 0;
					while (Character.isDigit(line.charAt(end))) {
						end++;
					}
//					int col = Integer.parseInt(line.substring(0, end)) - 1;
					line = line.substring(end);
					if (line.startsWith(", ")) {

//						int lineOffs = getLineOffset(lineNum);
//						int offs = lineOffs + col;

						String msg = line.substring(", ".length());
						String errorCode = null;

						// Ends in "(E0xxx)" or "(W0xxx)"
						ParserNotice.Level noticeType= ParserNotice.Level.ERROR;
						if (msg.charAt(msg.length()-1)==')') {
							int openParen = msg.lastIndexOf('(');
							errorCode = msg.substring(openParen+1,
									msg.length()-1);
							if (msg.charAt(openParen+1)=='W') {
								noticeType = ParserNotice.Level.WARNING;
							}
							msg = msg.substring(0, openParen-1);
						}

						DefaultParserNotice dpn = null;
						MarkStrategy markStrategy = getMarkStrategy(errorCode);
						switch (markStrategy) {
//							case MARK_PREV_TOKEN:
//								offs--;
//								// Fall through
//							case MARK_CUR_TOKEN:
//								lineNum = root.getElementIndex(offs);
//								offs = adjustOffset(doc, lineNum, offs, indent);
//								Token t = RSyntaxUtilities.getTokenAtOffset(doc,
//										offs);
//								if (t!=null) {
//									dpn = createNotice(doc, msg, lineNum,
//											t.getOffset(), t.length(), indent);
//								}
//								else if (offs==doc.getLength()) {
//									dpn = createNotice(doc, msg, lineNum,
//											offs, 1, indent);
//								}
//								break;
//							case MARK_PREV_NON_WS_TOKEN:
//								t = RSyntaxUtilities.
//									getPreviousImportantTokenFromOffs(doc, offs-1);
//								lineNum = root.getElementIndex(offs-1);
//								dpn = createNotice(doc, msg, lineNum,
//										t.getOffset(), t.length(), indent);
//								break;
//							case IGNORE:
//								break; // No ParserNotice
//							case STOP_PARSING:
//								break OUTER;
							default:
							case MARK_LINE:
								// Just mark the whole line, as the offset returned
								// by JSHint can vary.
								dpn = new DefaultParserNotice(parser, msg, lineNum);
								break;
						}
//						if (dpn!=null) {
							dpn.setLevel(noticeType);
							result.addNotice(dpn);
//						}

					}
				}
			}

		}

	}


//	private final int adjustOffset(RSyntaxDocument doc, int line, int offs,
//			int indent) {
//		if (indent>-1) {
//			Element root = doc.getDefaultRootElement();
//			Element elem = root.getElement(line);
//			if (elem!=null) {
//				int start = elem.getStartOffset();
//				int cur = start;
//				try {
//					while ((start-cur)<offs) {
//						char ch = doc.charAt(cur);
//						if (ch=='\t') {
//							offs -= indent - 1;
//						}
//						else {
//							break;
//						}
//						cur++;
//					}
//				} catch (BadLocationException ble) {
//					ble.printStackTrace(); // Never happens
//				}
//			}
//		}
//		return offs;
//	}
//
//
//	/*
//	 * Creates a parser notice for a specific region of text in the document.
//	 * This method attempts to work around JSHint's annoying behavior of
//	 * converting tabs to spaces when calculating the "offset" into the line of
//	 * errors.
//	 * NOTE: We can't do this here because we don't know the tab size that
//	 * JSHint is using!  Our only hope is to disable these warnings; the user
//	 * just can't get indentation-related warnings unfortunately.
//	 */
//	private final DefaultParserNotice createNotice(RSyntaxDocument doc,
//			String msg, int line, int offs, int len, int indent) {
//		return new DefaultParserNotice(parser, msg, line, offs, len);
//	}
//
//
//	private final int getLineOffset(int line) {
//		Element root = doc.getDefaultRootElement();
//		return root.getElement(line).getStartOffset();
//	}


	private static final MarkStrategy getMarkStrategy(String msgCode) {
		MarkStrategy strategy = MARK_STRATEGIES.get(msgCode);
		return strategy!=null ? strategy : MarkStrategy.MARK_LINE;
	}


	/**
	 * A thread dedicated to reading either the stdout or stderr stream of
	 * an external process.  These streams are read in a dedicated thread
	 * to ensure they are consumed appropriately to prevent deadlock.  This
	 * idea was taken from
	 * <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps_p.html">
	 * this JavaWorld article</a>.
	 * 
	 * @author Robert Futrell
	 */
	static class StreamReaderThread extends Thread {

		private BufferedReader r;
		private StringBuilder buffer;

		/**
		 * Constructor.
		 * 
		 * @param in The stream (stdout or stderr) to read from.
		 */
		public StreamReaderThread(InputStream in) {
			r = new BufferedReader(new InputStreamReader(in));
			this.buffer = new StringBuilder();
		}

		/**
		 * Returns the output read from the stream.
		 * 
		 * @return The stream's output, as a <code>String</code>.
		 */
		public String getStreamOutput() {
			return buffer.toString();
		}

		/**
		 * Continually reads from the output stream until this thread is
		 * interrupted.
		 */
		@Override
		public void run() {
			String line;
			try {
				while ((line=r.readLine())!=null) {
					buffer.append(line).append('\n');
					//System.out.println(line);
				}
			} catch (IOException ioe) {
				buffer.append("IOException occurred: " + ioe.getMessage());
			}
		}

	}


	/**
	 * What exactly to mark as the error in the document, based on an error
	 * code from JSHint.
	 */
	private enum MarkStrategy {
		MARK_LINE, MARK_CUR_TOKEN, MARK_PREV_TOKEN, MARK_PREV_NON_WS_TOKEN,
		IGNORE, STOP_PARSING
    }


}
