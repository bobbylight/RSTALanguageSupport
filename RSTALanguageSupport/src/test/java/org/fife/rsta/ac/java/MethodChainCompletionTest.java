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
		String src = "import javax.swing.AbstractAction;\n"
				+ "public class Probe {\n"
				+ "    private String foo;\n"
				+ "    private StringBuilder sb;\n"
				+ "    private javax.swing.text.StyledDocument doc;\n"
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
		List<Completion> completions = provider.getCompletions(textArea);
		Set<String> names = new TreeSet<>();
		for (Completion c : completions) {
			names.add(c.getReplacementText());
		}
		return names;
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
}
