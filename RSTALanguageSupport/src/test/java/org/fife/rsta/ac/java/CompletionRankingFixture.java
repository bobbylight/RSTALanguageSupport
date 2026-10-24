/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

/**
 * Compiled members used to verify completion ranking independently of the JRE.
 */
public class CompletionRankingFixture {

	/**
	 * A deprecated member that would otherwise sort first alphabetically.
	 *
	 * @deprecated Use {@link #zetaCurrent()} instead.
	 */
	@Deprecated
	public void alphaDeprecated() {
	}

	/**
	 * A current member that should rank above the deprecated one.
	 */
	public void zetaCurrent() {
	}
}
