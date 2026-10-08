/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.ui.autocomplete.Completion;


/**
 * Filters and orders the code completions offered for the text the user has
 * typed so far.
 * <p>
 * Completions are filtered to those whose name matches the typed text as a
 * prefix, as camel-case humps (<code>gCo</code> for
 * <code>getComponent</code>), as a substring, or as a fuzzy subsequence, and
 * are grouped in that order.  Within a group they are ordered by:
 * <ol>
 *    <li>members of the receiver's own class before inherited ones (the
 *        nearer the declaring class, the earlier);
 *    <li>public before protected, package-private and private;
 *    <li>the user's project classes before library classes, and library
 *        classes before JRE classes;
 *    <li>completions picked often or recently in this session;
 *    <li>name.
 * </ol>
 * Deprecated completions sink to the bottom of the whole list.  Names
 * starting with an underscore (engine internals), names containing a
 * <code>$</code> and synthetic members are hidden unless the typed text
 * itself starts with an underscore (or contains a <code>$</code>).
 */
final class CompletionRanker {

	static final int NO_MATCH					= -1;
	static final int TIER_PREFIX_SAME_CASE		= 0;
	static final int TIER_PREFIX				= 1;
	static final int TIER_CAMEL_CASE			= 2;
	static final int TIER_SUBSTRING				= 3;
	static final int TIER_FUZZY					= 4;

	/**
	 * Inheritance depth of local variables and parameters - above members.
	 */
	static final int DEPTH_LOCAL				= -1;

	/**
	 * Inheritance depth of a member of the receiver's own class.
	 */
	static final int DEPTH_OWN					= 0;

	/**
	 * Depth used for completions that are not members of a type (classes,
	 * packages, templates).
	 */
	static final int DEPTH_NOT_A_MEMBER			= 1000;

	static final int ACCESS_PUBLIC				= 0;
	static final int ACCESS_PROTECTED			= 1;
	static final int ACCESS_PACKAGE				= 2;
	static final int ACCESS_PRIVATE				= 3;

	private static final int ORIGIN_OWN			= 0;
	private static final int ORIGIN_PROJECT		= 1;
	private static final int ORIGIN_LIBRARY		= 2;
	private static final int ORIGIN_JRE			= 3;

	/**
	 * How often each completion was picked in this session.  Not persisted.
	 */
	private static final Map<String, Integer> PICK_COUNTS = new HashMap<>();

	/**
	 * When (in pick order) each completion was last picked in this session.
	 */
	private static final Map<String, Long> PICK_TIMES = new HashMap<>();

	private static long pickClock;

	/**
	 * Ranking metadata for the member completions of the current request,
	 * keyed by identity since member completions compare equal by signature.
	 */
	private final Map<Completion, MemberMeta> metas = new IdentityHashMap<>();


	/**
	 * Forgets the member metadata of the previous request.
	 */
	void clear() {
		metas.clear();
	}


	/**
	 * Records ranking metadata for a member read from a class file.
	 *
	 * @param c The completion.
	 * @param depth The inheritance depth of the member's declaring class
	 *        relative to the receiver (0 for the receiver's own class).
	 * @param accessFlags The member's access flags.
	 */
	void recordMember(Completion c, int depth, int accessFlags) {
		int access;
		if (org.fife.rsta.ac.java.classreader.Util.isPublic(accessFlags)) {
			access = ACCESS_PUBLIC;
		}
		else if (org.fife.rsta.ac.java.classreader.Util.isProtected(accessFlags)) {
			access = ACCESS_PROTECTED;
		}
		else if (org.fife.rsta.ac.java.classreader.Util.isPrivate(accessFlags)) {
			access = ACCESS_PRIVATE;
		}
		else {
			access = ACCESS_PACKAGE;
		}
		boolean synthetic = (accessFlags & AccessFlags.ACC_SYNTHETIC)!=0;
		metas.put(c, new MemberMeta(depth, access, synthetic));
	}


	/**
	 * Records ranking metadata for a member declared in the source being
	 * edited.
	 *
	 * @param c The completion.
	 * @param depth The inheritance depth of the member's declaring class.
	 * @param mods The member's modifiers, possibly <code>null</code>.
	 */
	void recordSourceMember(Completion c, int depth, Modifiers mods) {
		int access = ACCESS_PACKAGE;
		if (mods!=null) {
			if (mods.isPublic()) {
				access = ACCESS_PUBLIC;
			}
			else if (mods.isProtected()) {
				access = ACCESS_PROTECTED;
			}
			else if (mods.isPrivate()) {
				access = ACCESS_PRIVATE;
			}
		}
		metas.put(c, new MemberMeta(depth, access, false));
	}


	/**
	 * Filters and orders completions for the typed text.
	 *
	 * @param candidates All candidate completions.
	 * @param typed The text typed after the final '.', possibly empty.
	 * @param projectClasses Says which fully qualified class names are the
	 *        user's project classes.
	 * @return The matching completions, best first.
	 */
	List<Completion> rank(Collection<Completion> candidates, String typed,
			ProjectClassTest projectClasses) {

		String typedLower = typed.toLowerCase(Locale.ROOT);
		List<Entry> entries = new ArrayList<>();
		Map<String, Integer> originCache = new HashMap<>();

		synchronized (CompletionRanker.class) {
			for (Completion c : candidates) {
				String name = c.getInputText();
				if (name==null) {
					continue;
				}
				MemberMeta meta = metas.get(c);
				if (isHidden(name, typed, meta)) {
					continue;
				}
				int tier = matchTier(name, typed, typedLower);
				if (tier==NO_MATCH) {
					continue;
				}
				Entry e = new Entry(c, name, tier);
				e.deprecated = isDeprecated(c);
				e.depth = depthOf(c, meta);
				e.access = meta!=null ? meta.access : ACCESS_PUBLIC;
				e.origin = originOf(c, e.depth, projectClasses, originCache);
				String key = pickKey(c);
				Integer count = PICK_COUNTS.get(key);
				e.picks = count==null ? 0 : count;
				Long time = PICK_TIMES.get(key);
				e.lastPick = time==null ? 0 : time;
				entries.add(e);
			}
		}

		entries.sort(ENTRY_ORDER);
		List<Completion> result = new ArrayList<>(entries.size());
		for (Entry e : entries) {
			result.add(e.completion);
		}
		return result;

	}


	/**
	 * Returns whether a completion is hidden for the typed text.
	 */
	private static boolean isHidden(String name, String typed,
			MemberMeta meta) {
		if (name.isEmpty() || name.charAt(0)=='<') {
			return true; // constructors and static initializers
		}
		if (meta!=null && meta.synthetic) {
			return true;
		}
		if (name.indexOf('$')>-1 && typed.indexOf('$')==-1) {
			return true;
		}
		return name.charAt(0)=='_' && !typed.startsWith("_");
	}


	private static boolean isDeprecated(Completion c) {
		if (c instanceof MemberCompletion) {
			return ((MemberCompletion)c).isDeprecated();
		}
		if (c instanceof ClassCompletion) {
			return ((ClassCompletion)c).isDeprecated();
		}
		return false;
	}


	private static int depthOf(Completion c, MemberMeta meta) {
		if (meta!=null) {
			return meta.depth;
		}
		if (c instanceof LocalVariableCompletion) {
			return DEPTH_LOCAL;
		}
		if (c instanceof MemberCompletion) {
			return DEPTH_OWN; // e.g. an array's "length"
		}
		return DEPTH_NOT_A_MEMBER;
	}


	private static int originOf(Completion c, int depth,
			ProjectClassTest projectClasses, Map<String, Integer> cache) {
		String className;
		if (c instanceof LocalVariableCompletion) {
			return ORIGIN_OWN;
		}
		else if (c instanceof MemberCompletion) {
			className = ((MemberCompletion)c).getEnclosingClassName(true);
		}
		else if (c instanceof ClassCompletion) {
			className = ((ClassCompletion)c).getClassName(true);
		}
		else {
			return ORIGIN_LIBRARY;
		}
		if (className==null) {
			return ORIGIN_LIBRARY;
		}
		Integer origin = cache.get(className);
		if (origin==null) {
			if (isJreClass(className)) {
				origin = ORIGIN_JRE;
			}
			else if (projectClasses!=null &&
					projectClasses.isProjectClass(className)) {
				origin = ORIGIN_PROJECT;
			}
			else {
				origin = ORIGIN_LIBRARY;
			}
			cache.put(className, origin);
		}
		return origin;
	}


	/**
	 * Returns whether a class belongs to the JRE.  J2ME's
	 * <code>javax.microedition</code> APIs are libraries, not JRE classes.
	 */
	private static boolean isJreClass(String className) {
		if (className.startsWith("javax.")) {
			return !className.startsWith("javax.microedition.");
		}
		return className.startsWith("java.") || className.startsWith("jdk.") ||
				className.startsWith("sun.") || className.startsWith("com.sun.");
	}


	/**
	 * Returns how well a completion name matches the typed text.
	 *
	 * @param name The completion's name.
	 * @param typed The typed text.
	 * @return One of the <code>TIER_*</code> constants, or
	 *         {@link #NO_MATCH}.
	 */
	static int matchTier(String name, String typed) {
		return matchTier(name, typed, typed.toLowerCase(Locale.ROOT));
	}


	private static int matchTier(String name, String typed,
			String typedLower) {
		if (typed.isEmpty() || name.startsWith(typed)) {
			return TIER_PREFIX_SAME_CASE;
		}
		if (name.regionMatches(true, 0, typed, 0, typed.length())) {
			return TIER_PREFIX;
		}
		if (isCamelCaseMatch(name, typed)) {
			return TIER_CAMEL_CASE;
		}
		String nameLower = name.toLowerCase(Locale.ROOT);
		if (nameLower.contains(typedLower)) {
			return TIER_SUBSTRING;
		}
		if (isSubsequence(nameLower, typedLower)) {
			return TIER_FUZZY;
		}
		return NO_MATCH;
	}


	/**
	 * Returns whether the typed text matches the starts of the name's
	 * camel-case humps, e.g. <code>gCo</code> or <code>gco</code> for
	 * <code>getComponent</code>.  The first typed character must match the
	 * first character of the name; each following one either continues the
	 * current hump or starts a later hump.
	 */
	static boolean isCamelCaseMatch(String name, String typed) {
		if (typed.length()<2 || name.length()<typed.length()) {
			return false;
		}
		if (!sameIgnoreCase(name.charAt(0), typed.charAt(0))) {
			return false;
		}
		boolean[] humps = humpStarts(name);
		boolean[][] failed = new boolean[typed.length()+1][name.length()+1];
		return camelMatchFrom(name, humps, typed, 1, 1, failed);
	}


	/**
	 * Matches <code>typed[qi..]</code> against the name, where
	 * <code>next</code> is the name index just after the last matched
	 * character.
	 */
	private static boolean camelMatchFrom(String name, boolean[] humps,
			String typed, int qi, int next, boolean[][] failed) {
		if (qi==typed.length()) {
			return true;
		}
		if (failed[qi][next]) {
			return false;
		}
		char q = typed.charAt(qi);
		// Continue the current hump.
		if (next<name.length() && sameIgnoreCase(name.charAt(next), q) &&
				camelMatchFrom(name, humps, typed, qi+1, next+1, failed)) {
			return true;
		}
		// Or jump to the start of a later hump.
		for (int i=next+1; i<name.length(); i++) {
			if (humps[i] && sameIgnoreCase(name.charAt(i), q) &&
					camelMatchFrom(name, humps, typed, qi+1, i+1, failed)) {
				return true;
			}
		}
		failed[qi][next] = true;
		return false;
	}


	private static boolean[] humpStarts(String name) {
		int len = name.length();
		boolean[] humps = new boolean[len];
		for (int i=0; i<len; i++) {
			char c = name.charAt(i);
			if (i==0) {
				humps[i] = true;
				continue;
			}
			char prev = name.charAt(i-1);
			if (prev=='_' && c!='_') {
				humps[i] = true;
			}
			else if (Character.isUpperCase(c)) {
				boolean nextLower = i+1<len &&
						Character.isLowerCase(name.charAt(i+1));
				humps[i] = !Character.isUpperCase(prev) || nextLower;
			}
			else if (Character.isDigit(c) && !Character.isDigit(prev)) {
				humps[i] = true;
			}
		}
		return humps;
	}


	private static boolean isSubsequence(String nameLower, String typedLower) {
		int j = 0;
		for (int i=0; i<nameLower.length() && j<typedLower.length(); i++) {
			if (nameLower.charAt(i)==typedLower.charAt(j)) {
				j++;
			}
		}
		return j==typedLower.length();
	}


	private static boolean sameIgnoreCase(char a, char b) {
		return Character.toLowerCase(a)==Character.toLowerCase(b);
	}


	/**
	 * Returns the key the session pick history stores a completion under.
	 */
	static String pickKey(Completion c) {
		if (c instanceof MemberCompletion) {
			MemberCompletion mc = (MemberCompletion)c;
			return mc.getEnclosingClassName(true) + '#' + mc.getSignature();
		}
		if (c instanceof ClassCompletion) {
			return ((ClassCompletion)c).getClassName(true);
		}
		return c.getClass().getName() + ':' + c.getReplacementText();
	}


	/**
	 * Records that the user picked a completion, so that it ranks a little
	 * higher for the rest of this session.  Nothing is persisted.
	 *
	 * @param c The completion picked.
	 */
	static synchronized void recordPick(Completion c) {
		if (c==null) {
			return;
		}
		String key = pickKey(c);
		Integer count = PICK_COUNTS.get(key);
		PICK_COUNTS.put(key, count==null ? 1 : count+1);
		PICK_TIMES.put(key, ++pickClock);
	}


	/**
	 * Forgets the session pick history.  Used by tests.
	 */
	static synchronized void clearPickHistory() {
		PICK_COUNTS.clear();
		PICK_TIMES.clear();
		pickClock = 0;
	}


	/**
	 * Says whether a class is one of the user's own project classes.
	 */
	interface ProjectClassTest {

		/**
		 * Returns whether a class is a project class.
		 *
		 * @param className The fully qualified class name.
		 * @return Whether it is a project class.
		 */
		boolean isProjectClass(String className);

	}


	/**
	 * Ranking metadata for a member completion.
	 */
	private static final class MemberMeta {

		private final int depth;
		private final int access;
		private final boolean synthetic;

		MemberMeta(int depth, int access, boolean synthetic) {
			this.depth = depth;
			this.access = access;
			this.synthetic = synthetic;
		}

	}


	/**
	 * A matching completion and its sort keys.
	 */
	private static final class Entry {

		private final Completion completion;
		private final String name;
		private final String nameLower;
		private final int tier;
		private boolean deprecated;
		private int depth;
		private int access;
		private int origin;
		private int picks;
		private long lastPick;

		Entry(Completion completion, String name, int tier) {
			this.completion = completion;
			this.name = name;
			this.nameLower = name.toLowerCase(Locale.ROOT);
			this.tier = tier;
		}

	}


	private static final Comparator<Entry> ENTRY_ORDER = new Comparator<Entry>() {
		@Override
		public int compare(Entry a, Entry b) {
			if (a.deprecated!=b.deprecated) {
				return a.deprecated ? 1 : -1;
			}
			int diff = Integer.compare(a.tier, b.tier);
			if (diff==0) {
				diff = Integer.compare(a.depth, b.depth);
			}
			if (diff==0) {
				diff = Integer.compare(a.access, b.access);
			}
			if (diff==0) {
				diff = Integer.compare(a.origin, b.origin);
			}
			if (diff==0) {
				diff = Integer.compare(b.picks, a.picks);
			}
			if (diff==0) {
				diff = Long.compare(b.lastPick, a.lastPick);
			}
			if (diff==0) {
				diff = a.nameLower.compareTo(b.nameLower);
			}
			if (diff==0) {
				diff = a.name.compareTo(b.name);
			}
			if (diff==0) {
				diff = String.valueOf(a.completion).compareTo(
						String.valueOf(b.completion));
			}
			return diff;
		}
	};


}
