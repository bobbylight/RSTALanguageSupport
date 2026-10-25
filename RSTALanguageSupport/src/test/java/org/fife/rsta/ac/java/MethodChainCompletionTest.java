/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for method chain completions such as
 * {@code sb.append(x).}, resolved against the current JRE.
 */
class MethodChainCompletionTest {

	private static JarManager jarManager;

	@BeforeAll
	static void loadJre() throws Exception {
		jarManager = new JarManager();
		jarManager.addCurrentJreClassFileSource();
	}

	private static Set<String> completionsAtEndOf(String params, String body) {
		Set<String> names = new TreeSet<>();
		for (Completion c : completionsListAtEndOf(params, body)) {
			names.add(c.getReplacementText());
		}
		return names;
	}

	private static List<Completion> completionsListAtEndOf(String params, String body) {
		String src = "import javax.swing.AbstractAction;\n"
				+ "public class Probe {\n"
				+ "    private String foo;\n"
				+ "    private StringBuilder sb;\n"
				+ "    private javax.swing.text.StyledDocument doc;\n"
				+ "    private java.util.List<String> list;\n"
				+ "    private java.util.Map<String, Integer> map;\n"
				+ "    private java.util.List<? extends Number> numbers;\n"
				+ "    private java.util.List raw;\n"
				+ "    void m(" + params + ") {\n"
				+ "        " + body;
		String full = src + "\n    }\n}\n";
		CompilationUnit cu = new ASTFactory().getCompilationUnit("Probe.java",
				new Scanner(new StringReader(full)));
		JavaCompletionProvider javaProvider = new JavaCompletionProvider(jarManager);
		javaProvider.setCompilationUnit(cu);
		SourceCompletionProvider provider = new SourceCompletionProvider(jarManager);
		provider.setJavaProvider(javaProvider);

		RSyntaxTextArea textArea = new RSyntaxTextArea(full);
		textArea.setCaretPosition(src.length());
		return provider.getCompletions(textArea);
	}

	@Test
	void fieldChainCompletes() {
		// Positive control: no shadowing, no literals.
		assertTrue(completionsAtEndOf("", "sb.append(1).").contains("reverse"));
	}

	@Test
	void localVariableHidesField() {
		Set<String> names = completionsAtEndOf("",
				"StringBuilder foo = new StringBuilder();\n        foo.append(1).");
		assertTrue(names.contains("reverse"), "local should hide field, got " + names);
	}

	@Test
	void parameterHidesField() {
		Set<String> names = completionsAtEndOf("StringBuilder foo", "foo.append(1).");
		assertTrue(names.contains("reverse"), "parameter should hide field, got " + names);
	}

	@Test
	void primitiveLocalHidesField() {
		// "foo" is an int here, so there is nothing to complete - in particular
		// not the String field's members.
		Set<String> names = completionsAtEndOf("",
				"int foo = 0;\n        foo.toString().");
		assertFalse(names.contains("length"), "got " + names);
	}

	@Test
	void parenInCharArgument() {
		Set<String> names = completionsAtEndOf("", "sb.append(')').");
		assertTrue(names.contains("reverse"), "got " + names);
	}

	@Test
	void commaInStringArgument() {
		Set<String> names = completionsAtEndOf("", "sb.append(\"a,b\").");
		assertTrue(names.contains("reverse"), "got " + names);
	}

	@Test
	void dotInStringArgument() {
		Set<String> names = completionsAtEndOf("", "sb.append(\"a.b\").toString().");
		assertTrue(names.contains("length"), "got " + names);
	}

	@Test
	void methodDeclaredOnSuperinterface() {
		// StyledDocument inherits getDefaultRootElement() from Document.
		Set<String> names = completionsAtEndOf("", "doc.getDefaultRootElement().");
		assertTrue(names.contains("getElementCount"), "got " + names);
	}

	@Test
	void fieldDeclaredOnImplementedInterface() {
		// AbstractAction inherits the NAME constant from Action.
		Set<String> names = completionsAtEndOf("", "AbstractAction.NAME.");
		assertTrue(names.contains("length"), "got " + names);
	}

	@Test
	void typeVariableReturnType() {
		Set<String> names = completionsAtEndOf("", "list.get(0).");
		assertTrue(names.contains("length"), "got " + names);
	}

	@Test
	void genericReturnType() {
		Set<String> names = completionsAtEndOf("", "map.keySet().");
		assertTrue(names.contains("iterator"), "got " + names);
	}

	@Test
	void typeArgumentsCarriedThroughChain() {
		Set<String> names = completionsAtEndOf("", "map.keySet().iterator().next().");
		assertTrue(names.contains("length"), "got " + names);
	}

	@Test
	void secondTypeParameter() {
		Set<String> names = completionsAtEndOf("", "map.get(\"a\").");
		assertTrue(names.contains("intValue"), "got " + names);
	}

	@Test
	void genericMethodDeclaredOnSuperinterface() {
		// stream() is declared on Collection, whose type parameter is also E.
		Set<String> names = completionsAtEndOf("", "list.stream().");
		assertTrue(names.contains("filter"), "got " + names);
	}

	@Test
	void superinterfaceWithDifferentTypeParameters() {
		// parallel() is declared on BaseStream<T, S extends BaseStream<T, S>>
		// and returns S, which Stream<T> sets to Stream<T>.
		Set<String> names = completionsAtEndOf("", "list.stream().parallel().findFirst().get().");
		assertTrue(names.contains("length"), "got " + names);
	}

	@Test
	void nestedTypeInReturnType() {
		Set<String> names = completionsAtEndOf("",
				"map.entrySet().iterator().next().getValue().");
		assertTrue(names.contains("intValue"), "got " + names);
	}

	@Test
	void wildcardTypeArgument() {
		Set<String> names = completionsAtEndOf("", "numbers.get(0).");
		assertTrue(names.contains("intValue"), "got " + names);
	}

	@Test
	void genericLocalVariable() {
		Set<String> names = completionsAtEndOf("",
				"java.util.List<StringBuilder> sbs = null;\n        sbs.get(0).");
		assertTrue(names.contains("reverse"), "got " + names);
	}

	@Test
	void rawTypeGivesObjectMembers() {
		Set<String> names = completionsAtEndOf("", "raw.get(0).");
		assertTrue(names.contains("hashCode"), "got " + names);
		assertFalse(names.contains("length"), "got " + names);
	}

	@Test
	void finalCompletionsUseTypeArguments() {
		// The completions for Set<String> should know its element type, so
		// "iterator().next()." keeps working after completing "iterator".
		List<Completion> completions = completionsListAtEndOf("", "map.keySet().");
		boolean found = false;
		for (Completion c : completions) {
			if (c instanceof MethodCompletion && "iterator".equals(c.getReplacementText())) {
				found = ((MethodCompletion)c).getType().contains("String");
			}
		}
		assertTrue(found, "iterator() should return Iterator<String>");
	}
}
