/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.io.StringReader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Tests completion of variables declared by try-with-resources statements.
 */
class TryWithResourcesCompletionTest {


	/**
	 * Returns the names of the completions offered at the '|' in a method
	 * with the given body.
	 */
	private static Set<String> completionNames(String body) throws Exception {
		AtomicReference<Set<String>> names = new AtomicReference<>();
		SwingUtilities.invokeAndWait(() -> {
			String source = "class Test { void foo() { " + body + " } }";
			int caret = source.indexOf('|');
			source = source.replace("|", "");
			JavaCompletionProvider provider = new JavaCompletionProvider(mock(JarManager.class));
			provider.setCompilationUnit(new ASTFactory().getCompilationUnit("Test",
				new Scanner(new StringReader(source))));
			RSyntaxTextArea textArea = new RSyntaxTextArea();
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			textArea.setText(source);
			textArea.setCaretPosition(caret);
			names.set(provider.getSourceProvider().getCompletions(textArea).stream()
				.map(Completion::getInputText).collect(Collectors.toSet()));
		});
		return names.get();
	}


	@Test
	void testResourceCompletesInTryBlock() throws Exception {
		Set<String> names = completionNames(
			"try (java.io.Reader reader = open(); java.io.Writer writer = create()) { r| }");
		assertTrue(names.contains("reader"), names.toString());
	}


	@Test
	void testResourceDoesNotCompleteInCatchBlock() throws Exception {
		Set<String> names = completionNames(
			"try (java.io.Reader reader = open()) { } catch (Exception e) { r| }");
		assertFalse(names.contains("reader"), names.toString());
	}


	@Test
	void testResourceDoesNotCompleteInFinallyBlock() throws Exception {
		Set<String> names = completionNames(
			"try (java.io.Reader reader = open()) { } finally { r| }");
		assertFalse(names.contains("reader"), names.toString());
	}


	@Test
	void testResourceDoesNotCompleteAfterTryStatement() throws Exception {
		Set<String> names = completionNames(
			"try (java.io.Reader reader = open()) { }\nr|");
		assertFalse(names.contains("reader"), names.toString());
	}
}
