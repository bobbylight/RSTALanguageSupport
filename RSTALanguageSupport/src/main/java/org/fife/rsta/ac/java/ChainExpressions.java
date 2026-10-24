/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.List;


/**
 * Text utilities for method chain expressions such as
 * {@code foo.bar(x, ")").baz}, used by {@link SourceCompletionProvider}.
 * Parentheses, commas and dots inside string and char literals are ignored.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class ChainExpressions {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private ChainExpressions() {
		// Do nothing (comment for Sonar)
	}


	/**
	 * Returns the chain expression that ends at the end of a line of text,
	 * scanning back through balanced parentheses so that, for example,
	 * {@code "int x = foo.bar()."} returns {@code "foo.bar()."}.
	 *
	 * @param lineText The text of the line up to the caret.
	 * @return The chain expression, or an empty string if there isn't one.
	 */
	static String getTrailingChain(String lineText) {

		// Scan backward from end, accepting identifier chars, dots,
		// and balanced parentheses (for method calls in chains).
		// Track whether the last accepted token was a dot to distinguish
		// method call parens (foo.bar().) from cast parens ((Cast)var.).
		int i = lineText.length() - 1;
		boolean lastWasDot = false;
		while (i >= 0) {
			char c = lineText.charAt(i);
			if (Character.isJavaIdentifierPart(c)) {
				lastWasDot = false;
				i--;
			}
			else if (c == '.') {
				lastWasDot = true;
				i--;
			}
			else if (c == ')') {
				// In a method chain like foo.bar().baz, ')' follows
				// '.' when scanning backward (baz -> . -> )). In a cast
				// like (Type)var., ')' follows identifier chars
				// (var -> )). Only skip parens for method calls.
				if (!lastWasDot) {
					break;
				}
				// Skip balanced parens, ignoring any in string/char literals
				int depth = 1;
				i--;
				while (i >= 0 && depth > 0) {
					c = lineText.charAt(i);
					if (c == '"' || c == '\'') {
						i = findLiteralStart(lineText, i);
						if (i < 0) {
							return ""; // Unterminated literal
						}
					}
					else if (c == ')') {
						depth++;
					}
					else if (c == '(') {
						depth--;
					}
					i--;
				}
				lastWasDot = false;
				// After balanced parens, continue scanning (the method
				// name before '(' will be picked up in the next iteration)
			}
			else {
				break;
			}
		}
		i++;

		return (i < lineText.length()) ? lineText.substring(i) : "";
	}


	/**
	 * Splits a chain expression on dots, keeping parenthesized segments
	 * together. For example, {@code "AudioEngine.sharedEngine()"} returns
	 * {@code ["AudioEngine", "sharedEngine()"]}.
	 *
	 * @param prefix The chain expression.
	 * @return The segments.
	 */
	static List<String> splitSegments(String prefix) {
		List<String> segments = new ArrayList<>();
		int depth = 0;
		int segStart = 0;
		for (int i = 0; i < prefix.length(); i++) {
			char c = prefix.charAt(i);
			if (c == '"' || c == '\'') {
				i = findLiteralEnd(prefix, i);
			}
			else if (c == '(') {
				depth++;
			}
			else if (c == ')') {
				depth--;
			}
			else if (c == '.' && depth == 0) {
				segments.add(prefix.substring(segStart, i));
				segStart = i + 1;
			}
		}
		if (segStart < prefix.length()) {
			segments.add(prefix.substring(segStart));
		}
		return segments;
	}


	/**
	 * Counts the number of arguments in a parenthesized argument list.
	 * Handles nested parentheses. Returns 0 for empty args {@code "()"}.
	 *
	 * @param argsWithParens The argument string starting with '(' (e.g.,
	 *        "(x, y)" or "()")
	 * @return The argument count, or -1 if the string is malformed.
	 */
	static int countArguments(String argsWithParens) {
		if (argsWithParens == null || argsWithParens.length() < 2) {
			return -1;
		}
		// Strip outer parens
		String inner = argsWithParens.substring(1, argsWithParens.length() - 1).trim();
		if (inner.isEmpty()) {
			return 0;
		}
		int count = 1;
		int depth = 0;
		for (int i = 0; i < inner.length(); i++) {
			char c = inner.charAt(i);
			if (c == '"' || c == '\'') {
				i = findLiteralEnd(inner, i);
			}
			else if (c == '(') {
				depth++;
			}
			else if (c == ')') {
				depth--;
			}
			else if (c == ',' && depth == 0) {
				count++;
			}
		}
		return count;
	}


	/**
	 * Returns the index of the quote that opens the string or char literal
	 * closed by the quote at {@code end}.
	 *
	 * @param text The text being scanned.
	 * @param end The index of the closing quote.
	 * @return The index of the opening quote, or <code>-1</code> if there
	 *         isn't one.
	 */
	private static int findLiteralStart(String text, int end) {
		char quote = text.charAt(end);
		for (int i = end - 1; i >= 0; i--) {
			if (text.charAt(i) == quote) {
				// The quote is escaped if preceded by an odd number of '\'s
				int backslashes = 0;
				for (int j = i - 1; j >= 0 && text.charAt(j) == '\\'; j--) {
					backslashes++;
				}
				if (backslashes % 2 == 0) {
					return i;
				}
			}
		}
		return -1;
	}


	/**
	 * Returns the index of the quote that closes the string or char literal
	 * opened by the quote at {@code start}.
	 *
	 * @param text The text being scanned.
	 * @param start The index of the opening quote.
	 * @return The index of the closing quote, or the last index in
	 *         {@code text} if the literal is unterminated.
	 */
	private static int findLiteralEnd(String text, int start) {
		char quote = text.charAt(start);
		for (int i = start + 1; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '\\') {
				i++;
			}
			else if (c == quote) {
				return i;
			}
		}
		return text.length() - 1;
	}
}
