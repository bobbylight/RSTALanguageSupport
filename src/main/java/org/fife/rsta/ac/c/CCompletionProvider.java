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
package org.fife.rsta.ac.c;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.LanguageAwareCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;


/**
 * A completion provider for the C programming language.  It provides
 * code completion support and parameter assistance for the C Standard Library.
 * This information is read from an XML file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CCompletionProvider extends LanguageAwareCompletionProvider {


	/**
	 * Constructor.
	 */
	public CCompletionProvider() {
		setDefaultCompletionProvider(createCodeCompletionProvider());
		setStringCompletionProvider(createStringCompletionProvider());
		setCommentCompletionProvider(createCommentCompletionProvider());
	}


	/**
	 * Adds shorthand completions to the code completion provider.
	 *
	 * @param codeCP The code completion provider.
	 */
	protected void addShorthandCompletions(DefaultCompletionProvider codeCP) {
		codeCP.addCompletion(new ShorthandCompletion(codeCP, "main",
								"int main(int argc, char **argv)"));
//for (int i=0; i<5000; i++) {
//	codeCP.addCompletion(new BasicCompletion(codeCP, "Number" + i));
//}
	}


	/**
	 * Returns the provider to use when editing code.
	 *
	 * @return The provider.
	 * @see #createCommentCompletionProvider()
	 * @see #createStringCompletionProvider()
	 * @see #loadCodeCompletionsFromXml(DefaultCompletionProvider)
	 * @see #addShorthandCompletions(DefaultCompletionProvider)
	 */
	protected CompletionProvider createCodeCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		loadCodeCompletionsFromXml(cp);
		addShorthandCompletions(cp);
		return cp;

	}


	/**
	 * Returns the provider to use when in a comment.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createStringCompletionProvider()
	 */
	protected CompletionProvider createCommentCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "TODO:", "A to-do reminder"));
		cp.addCompletion(new BasicCompletion(cp, "FIXME:", "A bug that needs to be fixed"));
		return cp;
	}


	/**
	 * Returns the completion provider to use when the caret is in a string.
	 *
	 * @return The provider.
	 * @see #createCodeCompletionProvider()
	 * @see #createCommentCompletionProvider()
	 */
	protected CompletionProvider createStringCompletionProvider() {
		DefaultCompletionProvider cp = new DefaultCompletionProvider();
		cp.addCompletion(new BasicCompletion(cp, "%c", "char", "Prints a character"));
		cp.addCompletion(new BasicCompletion(cp, "%i", "signed int", "Prints a signed integer"));
		cp.addCompletion(new BasicCompletion(cp, "%f", "float", "Prints a float"));
		cp.addCompletion(new BasicCompletion(cp, "%s", "string", "Prints a string"));
		cp.addCompletion(new BasicCompletion(cp, "%u", "unsigned int", "Prints an unsigned integer"));
		cp.addCompletion(new BasicCompletion(cp, "\\n", "Newline", "Prints a newline"));
		return cp;
	}


	/**
	 * Returns the name of the XML resource to load (on classpath or a file).
	 *
	 * @return The resource to load.
	 */
	protected String getXmlResource() {
		return "data/c.xml";
	}


	/**
	 * Called from {@link #createCodeCompletionProvider()} to actually load
	 * the completions from XML.  Subclasses that override that method will
	 * want to call this one.
	 *
	 * @param cp The code completion provider.
	 */
	protected void loadCodeCompletionsFromXml(DefaultCompletionProvider cp) {
		// First try loading resource (running from demo jar), then try
		// accessing file (debugging in Eclipse).
		ClassLoader cl = getClass().getClassLoader();
		String res = getXmlResource();
		if (res!=null) { // Subclasses may specify a null value
			InputStream in = cl.getResourceAsStream(res);
			try {
				if (in!=null) {
					cp.loadFromXML(in);
					in.close();
				}
				else {
					cp.loadFromXML(new File(res));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}


}