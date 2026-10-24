/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Regression tests for synthetic members in completion results.
 */
class SourceCompletionProviderTest {

	@ParameterizedTest
	@ValueSource(strings = {
		"class Test { void test() { Fixture.| } }",
		"class Test extends Fixture { void test() { | } }",
		"class Test { void test(Fixture value) { value.| } }"
	})
	void testSyntheticMembers(String source) throws Exception {
		SwingUtilities.invokeAndWait(() -> {
			try {
				assertSyntheticMembers(source);
			} catch (IOException ioe) {
				throw new AssertionError(ioe);
			}
		});
	}

	private void assertSyntheticMembers(String source) throws IOException {
		ClassFile cf = mock(ClassFile.class);
		when(cf.getClassName(true)).thenReturn("example.Fixture");
		when(cf.getClassName(false)).thenReturn("Fixture");
		when(cf.getPackageName()).thenReturn("example");
		when(cf.getUtf8ValueFromConstantPool(1)).thenReturn("generatedField");
		when(cf.getUtf8ValueFromConstantPool(2)).thenReturn("class$0");
		when(cf.getUtf8ValueFromConstantPool(3)).thenReturn("generatedMethod");
		when(cf.getUtf8ValueFromConstantPool(4)).thenReturn("access$0");
		when(cf.getUtf8ValueFromConstantPool(5)).thenReturn("privateField");
		when(cf.getUtf8ValueFromConstantPool(6)).thenReturn("privateMethod");
		when(cf.getUtf8ValueFromConstantPool(7)).thenReturn("I");
		when(cf.getUtf8ValueFromConstantPool(8)).thenReturn("()V");
		when(cf.getUtf8ValueFromConstantPool(9)).thenReturn("instanceField");
		when(cf.getUtf8ValueFromConstantPool(10)).thenReturn("instanceMethod");
		int publicStatic = AccessFlags.ACC_PUBLIC | AccessFlags.ACC_STATIC;
		int syntheticStatic = publicStatic | AccessFlags.ACC_SYNTHETIC;
		int privateStatic = AccessFlags.ACC_PRIVATE | AccessFlags.ACC_STATIC | AccessFlags.ACC_SYNTHETIC;
		int syntheticInstance = AccessFlags.ACC_PUBLIC | AccessFlags.ACC_SYNTHETIC;
		when(cf.getFieldCount()).thenReturn(4);
		when(cf.getFieldInfo(0)).thenReturn(new FieldInfo(cf, syntheticStatic, 1, 7));
		when(cf.getFieldInfo(1)).thenReturn(new FieldInfo(cf, publicStatic, 2, 7));
		when(cf.getFieldInfo(2)).thenReturn(new FieldInfo(cf, privateStatic, 5, 7));
		when(cf.getFieldInfo(3)).thenReturn(new FieldInfo(cf, syntheticInstance, 9, 7));
		when(cf.getMethodCount()).thenReturn(4);
		when(cf.getMethodInfo(0)).thenReturn(new MethodInfo(cf, syntheticStatic, 3, 8));
		when(cf.getMethodInfo(1)).thenReturn(new MethodInfo(cf, publicStatic, 4, 8));
		when(cf.getMethodInfo(2)).thenReturn(new MethodInfo(cf, privateStatic, 6, 8));
		when(cf.getMethodInfo(3)).thenReturn(new MethodInfo(cf, syntheticInstance, 10, 8));

		JarManager jars = mock(JarManager.class);
		when(jars.getClassEntry("example.Fixture")).thenReturn(cf);
		when(jars.getClassesWithUnqualifiedName(eq("Fixture"), anyList())).thenReturn(List.of(cf));
		JavaCompletionProvider provider = new JavaCompletionProvider(jars);
		source = "package example; " + source;
		int caret = source.indexOf('|');
		source = source.replace("|", "");
		provider.setCompilationUnit(new ASTFactory().getCompilationUnit("Test",
			new Scanner(new StringReader(source))));
		RSyntaxTextArea textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setText(source);
		textArea.setCaretPosition(caret);

		SourceCompletionProvider completions = provider.getSourceProvider();
		assertTrue(completions.isHideSyntheticFields());
		Set<String> names = completionNames(completions, textArea);
		assertTrue(names.contains("class$0"), names.toString());
		assertTrue(names.contains("access$0"), names.toString());
		assertFalse(names.contains("generatedField"));
		assertFalse(names.contains("generatedMethod"));
		assertFalse(names.contains("instanceField"));
		assertFalse(names.contains("instanceMethod"));

		completions.setHideSyntheticFields(false);
		names = completionNames(completions, textArea);
		assertTrue(names.contains("generatedField"), names.toString());
		assertTrue(names.contains("generatedMethod"), names.toString());
		assertEquals(!source.contains("Fixture."), names.contains("instanceField"));
		assertEquals(!source.contains("Fixture."), names.contains("instanceMethod"));
		assertFalse(names.contains("privateField"));
		assertFalse(names.contains("privateMethod"));

		completions.setHideSyntheticFields(true);
		names = completionNames(completions, textArea);
		assertFalse(names.contains("generatedField"));
		assertFalse(names.contains("generatedMethod"));
	}

	private Set<String> completionNames(SourceCompletionProvider provider, RSyntaxTextArea textArea) {
		return provider.getCompletions(textArea).stream()
			.map(Completion::getInputText).collect(Collectors.toSet());
	}


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

	@Test
	void testMethodChainWithParenInCharArg() {
		assertEquals("sb.append(')').", getEnteredText("sb.append(')')."));
		assertEquals("sb.append('(').", getEnteredText("sb.append('(')."));
	}

	@Test
	void testMethodChainWithParensInStringArg() {
		assertEquals("sb.append(\"a)b(\").", getEnteredText("x = sb.append(\"a)b(\")."));
	}

	@Test
	void testMethodChainWithEscapedQuoteInStringArg() {
		assertEquals("sb.append(\"a\\\")\").", getEnteredText("sb.append(\"a\\\")\")."));
		assertEquals("sb.append('\\'').", getEnteredText("sb.append('\\'')."));
	}

	@Test
	void testMethodChainWithUnterminatedLiteral() {
		assertEquals("", getEnteredText("x\").bar()."));
	}

	// --- Cast expression tests (should NOT scan through cast parens) ---

	@Test
	void testCastExpressionNotIncluded() {
		// (MissionObjective)objectives. should return just "objectives."
		assertEquals("objectives.", getEnteredText("(MissionObjective)objectives."));
	}

	@Test
	void testCastExpressionWithAssignment() {
		assertEquals("objectives.",
			getEnteredText("MissionObjective obj = (MissionObjective)objectives."));
	}

	@Test
	void testCastExpressionWithSpaceBefore() {
		assertEquals("var.", getEnteredText("x = (Type)var."));
	}

	@Test
	void testMethodChainStillWorksAfterCastFix() {
		// Ensure method chains still work correctly
		assertEquals("foo.bar().", getEnteredText("foo.bar()."));
		assertEquals("a.b().c().", getEnteredText("a.b().c()."));
	}

	// --- countArguments tests ---

	@Test
	void testCountArguments_empty() {
		assertEquals(0, ChainExpressions.countArguments("()"));
	}

	@Test
	void testCountArguments_one() {
		assertEquals(1, ChainExpressions.countArguments("(x)"));
	}

	@Test
	void testCountArguments_two() {
		assertEquals(2, ChainExpressions.countArguments("(x, y)"));
	}

	@Test
	void testCountArguments_three() {
		assertEquals(3, ChainExpressions.countArguments("(a, b, c)"));
	}

	@Test
	void testCountArguments_nested() {
		// foo(bar(x, y), z) has 2 top-level args
		assertEquals(2, ChainExpressions.countArguments("(bar(x, y), z)"));
	}

	@Test
	void testCountArguments_literals() {
		assertEquals(1, ChainExpressions.countArguments("(\"a,b\")"));
		assertEquals(1, ChainExpressions.countArguments("(\"a)b\")"));
		assertEquals(2, ChainExpressions.countArguments("(\"a\\\",\", ',')"));
		assertEquals(1, ChainExpressions.countArguments("(')')"));
	}

	@Test
	void testCountArguments_malformed() {
		assertEquals(-1, ChainExpressions.countArguments(null));
		assertEquals(-1, ChainExpressions.countArguments(""));
		assertEquals(-1, ChainExpressions.countArguments("("));
	}
}
