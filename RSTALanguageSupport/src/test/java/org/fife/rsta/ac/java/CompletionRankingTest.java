/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Ranking, filtering and documentation of member completions: own members
 * before inherited ones, public before protected, prefix before camel-case,
 * substring and fuzzy matches, internals hidden, deprecated members last,
 * and a description for every completion.
 */
class CompletionRankingTest {

	private static JarManager jarManager;

	@BeforeAll
	static void loadJre() throws Exception {
		jarManager = new JarManager();
		jarManager.addCurrentJreClassFileSource();
	}

	@AfterEach
	void forgetPicks() {
		CompletionRanker.clearPickHistory();
	}

	/**
	 * Runs a real completion through the Java provider (as the popup does)
	 * with the caret at the end of <code>body</code>.
	 */
	private static List<Completion> completeAtEndOf(String body) {
		String src = "import java.util.ArrayList;\n"
				+ "public class Probe {\n"
				+ "    private int _secret;\n"
				+ "    public int visible;\n"
				+ "    private ArrayList<String> items;\n"
				+ "    private Thread thread;\n"
				+ "    void m() {\n"
				+ "        " + body;
		String full = src + "\n    }\n}\n";
		CompilationUnit cu = new ASTFactory().getCompilationUnit("Probe.java",
				new Scanner(new StringReader(full)));
		JavaCompletionProvider javaProvider = new JavaCompletionProvider(jarManager);
		javaProvider.setCompilationUnit(cu);

		RSyntaxTextArea textArea = new RSyntaxTextArea(full);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCaretPosition(src.length());
		return javaProvider.getCompletions(textArea);
	}

	private static List<String> names(List<Completion> completions) {
		List<String> names = new ArrayList<>();
		for (Completion c : completions) {
			names.add(c.getInputText());
		}
		return names;
	}

	private static int indexOf(List<String> names, String name) {
		int index = names.indexOf(name);
		assertTrue(index>-1, name + " must be offered, got " + names);
		return index;
	}

	@Test
	void ownMembersRankAboveInheritedOnes() {
		List<String> names = names(completeAtEndOf("items."));
		// Positive control: both an own and an inherited member are offered.
		int own = indexOf(names, "trimToSize");       // ArrayList
		int inherited = indexOf(names, "getClass");   // Object
		assertTrue(own<inherited, "own before inherited: " + names);
		// AbstractList's protected field ranks below ArrayList's members.
		assertTrue(indexOf(names, "ensureCapacity")<indexOf(names, "modCount"),
				names.toString());
	}

	@Test
	void publicRanksAboveProtectedInTheSameClass() {
		List<String> names = names(completeAtEndOf("items."));
		// ArrayList declares both public trimToSize() and protected
		// removeRange().
		assertTrue(indexOf(names, "trimToSize")<indexOf(names, "removeRange"),
				names.toString());
	}

	@Test
	void prefixThenCamelCaseThenSubstringThenFuzzy() {
		assertEquals(CompletionRanker.TIER_PREFIX_SAME_CASE,
				CompletionRanker.matchTier("getComponent", "getC"));
		assertEquals(CompletionRanker.TIER_PREFIX,
				CompletionRanker.matchTier("getComponent", "GETC"));
		assertEquals(CompletionRanker.TIER_CAMEL_CASE,
				CompletionRanker.matchTier("getComponent", "gCo"));
		assertEquals(CompletionRanker.TIER_CAMEL_CASE,
				CompletionRanker.matchTier("getComponentInChildren", "gcic"));
		assertEquals(CompletionRanker.TIER_SUBSTRING,
				CompletionRanker.matchTier("getComponent", "ponent"));
		assertEquals(CompletionRanker.TIER_FUZZY,
				CompletionRanker.matchTier("getComponent", "gtcmp"));
		assertEquals(CompletionRanker.NO_MATCH,
				CompletionRanker.matchTier("getComponent", "xyz"));

		// In a real list: "size" is a prefix match for "s", "isEmpty" only a
		// substring match.
		List<String> names = names(completeAtEndOf("items.s"));
		assertTrue(indexOf(names, "size")<indexOf(names, "subList"),
				names.toString());
		assertTrue(indexOf(names, "subList")<indexOf(names, "isEmpty"),
				"prefix before substring: " + names);
		// Fuzzy: "tts" matches trimToSize only as a subsequence/camel case.
		assertTrue(names(completeAtEndOf("items.tts")).contains("trimToSize"));
	}

	@Test
	void underscoreMembersHiddenUnlessTyped() {
		// Positive control: typing the underscore shows the internal field.
		assertTrue(names(completeAtEndOf("this._")).contains("_secret"),
				"typing _ must offer _secret");
		List<String> names = names(completeAtEndOf("this."));
		assertTrue(names.contains("visible"), "control: " + names);
		assertFalse(names.contains("_secret"), "internal shown: " + names);
	}

	@Test
	void deprecatedMembersSinkToTheBottom() {
		List<Completion> completions = completeAtEndOf("thread.");
		List<String> names = names(completions);
		// Positive control: Thread.stop() is deprecated and still offered.
		int stop = indexOf(names, "stop");
		assertTrue(((MemberCompletion)completions.get(stop)).isDeprecated());
		boolean seenDeprecated = false;
		for (Completion c : completions) {
			boolean deprecated = c instanceof MemberCompletion &&
					((MemberCompletion)c).isDeprecated();
			if (seenDeprecated) {
				assertTrue(deprecated, "non-deprecated " + c.getInputText() +
						" after a deprecated member: " + names);
			}
			seenDeprecated |= deprecated;
		}
		assertTrue(stop>indexOf(names, "start"), names.toString());
	}

	@Test
	void recentlyPickedMembersGetABoost() {
		List<String> before = names(completeAtEndOf("items."));
		assertTrue(indexOf(before, "add")<indexOf(before, "trimToSize"),
				"control: alphabetical within the same group " + before);

		List<Completion> completions = completeAtEndOf("items.");
		CompletionRanker.recordPick(
				completions.get(names(completions).indexOf("trimToSize")));
		List<String> after = names(completeAtEndOf("items."));
		assertTrue(indexOf(after, "trimToSize")<indexOf(after, "add"),
				"picked member must move up: " + after);
	}

	@Test
	void everyCompletionHasADescription() {
		List<Completion> completions = new ArrayList<>();
		completions.addAll(completeAtEndOf("items."));
		completions.addAll(completeAtEndOf("Arr"));
		completions.addAll(completeAtEndOf("vis"));
		completions.addAll(completeAtEndOf("ite"));
		assertTrue(completions.size()>20, "control: " + completions.size());
		for (Completion c : completions) {
			String summary = c.getSummary();
			assertNotNull(summary, "no summary for " + c);
			String text = summary.replaceAll("<[^>]*>", "").trim();
			assertFalse(text.isEmpty(), "empty summary for " + c);
		}
		// Without attached source, a member's description still names its
		// signature and declaring class.
		List<Completion> members = completeAtEndOf("items.trimT");
		String summary = members.get(0).getSummary();
		assertTrue(summary.contains("trimToSize()"), summary);
		assertTrue(summary.contains("Declared in java.util.ArrayList"), summary);
	}
}
