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
 * Unit tests for annotation-related autocomplete features in
 * {@link SourceCompletionProvider}.
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

	// --- annotation prefix tests ---

	@Test
	void testAnnotationPrefix() {
		// @ is not a Java identifier part, so it should NOT be included
		assertEquals("Serialize", getEnteredText("@Serialize"));
	}

	@Test
	void testAnnotationPrefixPartial() {
		assertEquals("Ser", getEnteredText("@Ser"));
	}

}
