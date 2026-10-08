/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * "field." completion for a field the edited class inherits from its
 * superclass - the CC3D case is {@code gameObject.} inside a
 * {@code Component} subclass, which used to produce no completions at all
 * because only fields declared in the source file were considered.
 */
class InheritedFieldCompletionTest {

	private static JarManager jarManager;

	@BeforeAll
	static void loadJre() throws Exception {
		jarManager = new JarManager();
		jarManager.addCurrentJreClassFileSource();
	}

	private static Set<String> completionsAtEndOf(String body) {
		String src = "import javax.swing.JComponent;\n"
				+ "public class Probe extends JComponent {\n"
				+ "    private java.util.ArrayList<String> items;\n"
				+ "    void m() {\n"
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
	void declaredFieldStillCompletes() {
		// Positive control: the harness produces completions for a field
		// declared in the source, the path that always worked.
		assertTrue(completionsAtEndOf("items.").contains("size"),
				"declared field completions");
	}

	@Test
	void inheritedFieldCompletesWithItsTypesMembers() {
		// JComponent declares "protected EventListenerList listenerList".
		Set<String> names = completionsAtEndOf("listenerList.");
		assertTrue(names.contains("getListenerCount"),
				"inherited field must complete with its type's members, got " + names);
	}

	@Test
	void inheritedFieldWorksAsTheHeadOfAChain() {
		// Chains resolve the first segment the same way.
		Set<String> names = completionsAtEndOf("listenerList.toString().");
		assertTrue(names.contains("length"),
				"chain from an inherited field, got " + names);
	}
}
