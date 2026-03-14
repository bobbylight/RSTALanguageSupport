/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Regression tests for annotation completions.
 */
class AnnotationCompletionTest {

	@ParameterizedTest
	@ValueSource(strings = { "Serialize", "Ser" })
	void testAnnotationPrefix(String name) {
		SourceCompletionProvider provider = new SourceCompletionProvider();
		JTextArea textArea = new JTextArea("@" + name);
		textArea.setCaretPosition(textArea.getText().length());
		assertEquals(name, provider.getAlreadyEnteredText(textArea));
	}

	@ParameterizedTest
	@ValueSource(strings = { "@Demo|", "@example.Demo|", "@ Demo|" })
	void testAnnotationTypesOnly(String source) throws Exception {
		assertCompletions(source, List.of("Demo"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"@Demo(co|)", "@example.Demo(co|)", "@Demo (co|)",
		"@Demo(value = \"(\", co|)", "@Demo(value = \"),\", co|)",
		"@Demo(value = {\"a\", \"b\"}, co|)",
		"@Demo(\n\n\n\n\nco|)", "@Demo(/* ( */ co|)",
		"@Demo(value = @Nested(1), co|)"
	})
	void testAnnotationElements(String source) throws Exception {
		assertCompletions(source, List.of("count"));
	}

	@Test
	void testAllAnnotationElements() throws Exception {
		assertCompletions("@Demo(|)", List.of("count", "value", "values"));
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"@Demo(value = |)", "@Demo(value = {1, |})", "@Demo(value = \"co|\")",
		"// @Demo(co|)", "/* @Demo(co|) */", "String s = \"@Demo(co|)\";"
	})
	void testNoElementNamesInsideValuesOrComments(String source) throws Exception {
		assertCompletions(source, List.of());
	}

	private void assertCompletions(String source, List<String> expected) throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			try {
				ClassFile annotation = mock(ClassFile.class);
				when(annotation.getClassName(false)).thenReturn("Demo");
				when(annotation.getClassName(true)).thenReturn("example.Demo");
				when(annotation.getAccessFlags()).thenReturn(AccessFlags.ACC_PUBLIC | AccessFlags.ACC_ANNOTATION);
				String[] names = { "count", "value", "values", "syntheticElement" };
				when(annotation.getMethodCount()).thenReturn(names.length);
				for (int i = 0; i < names.length; i++) {
					MethodInfo method = mock(MethodInfo.class);
					when(method.getClassFile()).thenReturn(annotation);
					when(method.getName()).thenReturn(names[i]);
					when(method.getReturnTypeString(false)).thenReturn("int");
					when(method.getAccessFlags()).thenReturn(AccessFlags.ACC_PUBLIC | AccessFlags.ACC_ABSTRACT |
						(i == 3 ? AccessFlags.ACC_SYNTHETIC : 0));
					when(annotation.getMethodInfo(i)).thenReturn(method);
				}
				ClassFile ordinary = mock(ClassFile.class);
				when(ordinary.getClassName(false)).thenReturn("DemoClass");
				when(ordinary.getClassName(true)).thenReturn("example.DemoClass");
				when(ordinary.getAccessFlags()).thenReturn(AccessFlags.ACC_PUBLIC);
				JarManager jars = mock(JarManager.class);
				when(jars.getClassEntry("example.Demo")).thenReturn(annotation);
				SourceLocation location = mock(SourceLocation.class);
				when(location.getCompilationUnit(annotation)).thenReturn(new ASTFactory().getCompilationUnit("Demo",
					new Scanner(new StringReader("package example; public @interface Demo { " +
						"/** Number of repetitions. */ int count() default 1; }"))));
				when(jars.getSourceLocForClass("example.Demo")).thenReturn(location);
				doAnswer(invocation -> {
					SourceCompletionProvider provider = invocation.getArgument(0);
					Set<Completion> set = invocation.getArgument(2);
					set.add(new ClassCompletion(provider, annotation));
					set.add(new ClassCompletion(provider, ordinary));
					set.add(new BasicCompletion(provider, "DemoVariable"));
					return null;
				}).when(jars).addCompletions(any(), anyString(), any());
				JavaCompletionProvider provider = new JavaCompletionProvider(jars);
				provider.setCompilationUnit(new ASTFactory().getCompilationUnit("Test",
					new Scanner(new StringReader("package example; class Test {}"))));
				RSyntaxTextArea textArea = new RSyntaxTextArea();
				textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
				textArea.setText(source.replace("|", ""));
				textArea.setCaretPosition(source.indexOf('|'));
				List<Completion> completions = provider.getSourceProvider().getCompletions(textArea);
				List<String> actual = completions.stream().map(Completion::getInputText).collect(Collectors.toList());
				if (expected.isEmpty()) {
					assertFalse(actual.contains("count"), actual.toString());
					assertFalse(actual.contains("value"), actual.toString());
					assertFalse(actual.contains("values"), actual.toString());
				}
				else {
					assertEquals(expected, actual);
					if (expected.contains("count")) {
						assertEquals("count=", completions.get(0).getReplacementText());
						assertTrue(completions.get(0).getSummary().contains("Number of repetitions."));
					}
				}
			} catch (Exception e) {
				throw new AssertionError(e);
			}
		});
	}
}
