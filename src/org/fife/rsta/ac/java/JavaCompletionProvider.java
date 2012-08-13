/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.rsta.ac.java.buildpath.LibraryInfo;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;


/**
 * Completion provider for the Java programming language.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JavaCompletionProvider extends LanguageAwareCompletionProvider {

	/**
	 * The provider used for source code, kept here since it's used so much.
	 */
	private SourceCompletionProvider sourceProvider;

	private CompilationUnit cu;


	/**
	 * Constructor.
	 */
	public JavaCompletionProvider() {
		this(null);
	}


	/**
	 * Constructor.
	 *
	 * @param jarManager The jar manager to use when looking up completion
	 *        choices.  This can be passed in to share a single jar manager
	 *        across multiple <tt>RSyntaxTextArea</tt>s.  This may also be
	 *        <code>null</code>, in which case this completion provider will
	 *        have a unique <tt>JarManager</tt>.
	 */
	public JavaCompletionProvider(JarManager jarManager) {

		super(new SourceCompletionProvider(jarManager));
		this.sourceProvider = (SourceCompletionProvider)
										getDefaultCompletionProvider();
		sourceProvider.setJavaProvider(this);
		setShorthandCompletionCache(new JavaShorthandCompletionCache(
				sourceProvider, new DefaultCompletionProvider()));
		setDocCommentCompletionProvider(new DocCommentCompletionProvider());

	}


	/**
	 * Adds a jar to the "build path."
	 *
	 * @param info The jar to add.  If this is <code>null</code>, then
	 *        the current JVM's main JRE jar (rt.jar, or classes.jar on OS X)
	 *        will be added.  If this jar has already been added, adding it
	 *        again will do nothing (except possibly update its attached source
	 *        location).
	 * @throws IOException If an IO error occurs.
	 * @see #removeJar(File)
	 * @see #getJars()
	 */
	public void addJar(LibraryInfo info) throws IOException {
		sourceProvider.addJar(info);
	}


	/**
	 * Removes all jars from the "build path."
	 *
	 * @see #removeJar(File)
	 * @see #addJar(LibraryInfo)
	 * @see #getJars()
	 */
	public void clearJars() {
		sourceProvider.clearJars();
	}


	/**
	 * Defers to the source-analyzing completion provider.
	 *
	 * @return The already entered text.
	 */
	public String getAlreadyEnteredText(JTextComponent comp) {
		return sourceProvider.getAlreadyEnteredText(comp);
	}


	public synchronized CompilationUnit getCompilationUnit() {
		return cu;
	}


	/**
	 * {@inheritDoc}
	 */
	public List getCompletionsAt(JTextComponent tc, Point p) {
		return sourceProvider.getCompletionsAt(tc, p);
	}


	/**
	 * Returns the jars on the "build path."
	 *
	 * @return A list of {@link JarInfo}s.  Modifying a <tt>JarInfo</tt> in
	 *         this list will have no effect on this completion provider; in
	 *         order to do that, you must re-add the jar via
	 *         {@link #addClassFileSource(JarInfo)}. If there are no jars on the
	 *         "build path," this will be an empty list.
	 * @see #addClassFileSource(JarInfo)
	 */
	public List getJars() {
		return sourceProvider.getJars();
	}


	/**
	 * {@inheritDoc}
	 */
	public List getParameterizedCompletions(JTextComponent tc) {
		return null;
	}


	/**
	 * Removes a jar from the "build path."
	 *
	 * @param jar The jar to remove.
	 * @return Whether the jar was removed.  This will be <code>false</code>
	 *         if the jar was not on the build path.
	 * @see #addJar(LibraryInfo)
	 */
	public boolean removeJar(File jar) {
		return sourceProvider.removeJar(jar);
	}


	private void setCommentCompletions(ShorthandCompletionCache shorthandCache) {
		AbstractCompletionProvider provider = shorthandCache.getCommentProvider();
		if(provider != null) {
			for(Iterator i = shorthandCache.getCommentCompletions().iterator();
					i.hasNext();) {
				Completion c = (Completion)i.next();
				provider.addCompletion(c);
			}
			setCommentCompletionProvider(provider);
		}
	}


	public synchronized void setCompilationUnit(CompilationUnit cu) {
		this.cu = cu;
	}


	/**
	 * Set short hand completion cache (template and comment completions)
	 */
	public void setShorthandCompletionCache(ShorthandCompletionCache cache) {
		sourceProvider.setShorthandCache(cache);
		//reset comment completions too
		setCommentCompletions(cache);
	}


}