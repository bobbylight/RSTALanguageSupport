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

	/**
	 * Documents the overload limitation in {@code findMethodReturnType}.
	 * <p>
	 * The {@code getAlreadyEnteredText} method correctly extracts the chain
	 * text (e.g., {@code sb.append("x").}), but full chain resolution through
	 * JDK types like {@code StringBuilder.append().toString()} requires a
	 * JarManager loaded with JRE classes, which is out of scope for this unit
	 * test.
	 * <p>
	 * NOTE: The private {@code findMethodReturnType} method returns the first
	 * method matching by name only -- it does not distinguish overloads by
	 * parameter count or types. This is a known limitation that cannot be
	 * directly tested from outside the class.
	 */
	@Test
	void testMethodChainWithStringArg_documentsOverloadLimitation() {
		// Verifies getAlreadyEnteredText correctly handles string args in parens
		assertEquals("sb.append(\"x\").", getEnteredText("sb.append(\"x\")."));
	}
}
