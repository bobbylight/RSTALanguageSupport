/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.JTextArea;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link SourceCompletionProvider#getAlreadyEnteredText}
 * method, covering balanced parenthesis scanning and method chain extraction.
 */
class SourceCompletionProviderTest {

	private SourceCompletionProvider provider;

	@BeforeEach
	void setUp() {
		provider = new SourceCompletionProvider();
	}

	/**
	 * Helper: creates a JTextArea with the given text and caret at the end,
	 * then returns getAlreadyEnteredText result.
	 */
	private String getEnteredText(String text) {
		JTextArea textArea = new JTextArea(text);
		textArea.setCaretPosition(text.length());
		return provider.getAlreadyEnteredText(textArea);
	}

	@Test
	void testSimpleIdentifierWithDot() {
		assertEquals("foo.", getEnteredText("foo."));
	}

	@Test
	void testMethodChainWithEmptyParens() {
		assertEquals("foo.bar().", getEnteredText("foo.bar()."));
	}

	@Test
	void testNestedMethodCall() {
		assertEquals("foo.bar(baz()).", getEnteredText("foo.bar(baz())."));
	}

	@Test
	void testMethodWithMultipleArgs() {
		assertEquals("foo.bar(x, y).", getEnteredText("foo.bar(x, y)."));
	}

	@Test
	void testMultiLevelChain() {
		assertEquals("a.b().c().d().", getEnteredText("a.b().c().d()."));
	}

	@Test
	void testPrefixBeforeChainIsTrimmed() {
		// The scanner stops at the space/equals, so only the chain is returned
		assertEquals("a.b().", getEnteredText("int z = a.b()."));
	}

	@Test
	void testPlainIdentifierNoDot() {
		assertEquals("foo", getEnteredText("foo"));
	}

	@Test
	void testMethodChainWithStringArg() {
		assertEquals("sb.append(\"x\").", getEnteredText("sb.append(\"x\")."));
	}

	// --- countArguments tests ---

	@Test
	void testCountArguments_empty() {
		assertEquals(0, SourceCompletionProvider.countArguments("()"));
	}

	@Test
	void testCountArguments_one() {
		assertEquals(1, SourceCompletionProvider.countArguments("(x)"));
	}

	@Test
	void testCountArguments_two() {
		assertEquals(2, SourceCompletionProvider.countArguments("(x, y)"));
	}

	@Test
	void testCountArguments_three() {
		assertEquals(3, SourceCompletionProvider.countArguments("(a, b, c)"));
	}

	@Test
	void testCountArguments_nested() {
		// foo(bar(x, y), z) has 2 top-level args
		assertEquals(2, SourceCompletionProvider.countArguments("(bar(x, y), z)"));
	}

	@Test
	void testCountArguments_malformed() {
		assertEquals(-1, SourceCompletionProvider.countArguments(null));
		assertEquals(-1, SourceCompletionProvider.countArguments(""));
		assertEquals(-1, SourceCompletionProvider.countArguments("("));
	}
}
