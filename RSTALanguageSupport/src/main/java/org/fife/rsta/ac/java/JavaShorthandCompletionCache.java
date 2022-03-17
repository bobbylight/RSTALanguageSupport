/*
 * 07/22/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.ResourceBundle;

import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * A cache of basic template and comment completions for Java, e.g.
 * <code>System.out.println()</code>.
 *
 * @author Steve
 * @see ShorthandCompletionCache
 */
public class JavaShorthandCompletionCache extends ShorthandCompletionCache {

	private static final ResourceBundle MSG = ResourceBundle.getBundle("org.fife.rsta.ac.java.resources");

	public JavaShorthandCompletionCache(DefaultCompletionProvider
			templateProvider, DefaultCompletionProvider commentsProvider) {

		super(templateProvider, commentsProvider);
		String template;

		// load defaults
		template = "System.out.println(${});${cursor}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "sysout", "sysout", template,
				MSG.getString("sysout.shortDesc"), MSG.getString("sysout.summary")));

		template = "System.err.println(${});${cursor}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "syserr", "syserr", template,
				MSG.getString("syserr.shortDesc"), MSG.getString("syserr.summary")));

		template = "for (int ${i} = 0; ${i} < ${array}.length; ${i}++) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "for", "for-loop-array", template,
				MSG.getString("for.array.shortDesc"), MSG.getString("for.array.summary")));

		template = "for (int ${i} = 0; ${i} < ${10}; ${i}++) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "for", "for-loop",
				template, MSG.getString("for.loop.shortDesc"), MSG.getString("for.loop.summary")));

		template = "if (${condition}) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "if", "if-cond",
				template, MSG.getString("if.cond.shortDesc"), MSG.getString("if.cond.summary")));

		template = "if (${condition}) {\n\t${cursor}\n}\nelse {\n\t\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "if", "if-else",
				template, MSG.getString("if.else.shortDesc"), MSG.getString("if.else.summary")));

		template = "do {\n\t${cursor}\n} while (${condition});";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "do", "do-loop", template,
				MSG.getString("do.shortDesc"), MSG.getString("do.summary")));

		template = "while (${condition}) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "while", "while-cond",
				template, MSG.getString("while.shortDesc"), MSG.getString("while.summary")));

		template = "new Runnable() {\n\tpublic void run() {\n\t\t${cursor}\n\t}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "runnable", "runnable", template,
				MSG.getString("runnable.shortDesc")));

		template = "switch (${key}) {\n\tcase ${value}:\n\t\t${cursor}\n\t\tbreak;\n\tdefault:\n\t\tbreak;\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "switch", "switch-statement",
				template, MSG.getString("switch.case.shortDesc"), MSG.getString("switch.case.summary")));

		template = "try {\n\t ${cursor} \n} catch (${err}) {\n\t\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "try", "try-catch",
				template, MSG.getString("try.catch.shortDesc"), MSG.getString("try.catch.summary")));

		template = "catch (${err}) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "catch", "catch-block",
				template, MSG.getString("catch.block.shortDesc"), MSG.getString("catch.block.summary")));

		// Comments
		addCommentCompletion(new BasicCompletion(commentsProvider, "TODO:", null, MSG.getString("todo")));
		addCommentCompletion(new BasicCompletion(commentsProvider, "FIXME:", null, MSG.getString("fixme")));

	}


}
