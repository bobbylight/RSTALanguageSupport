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
package org.fife.rsta.ac.js;

import java.util.ResourceBundle;

import org.fife.rsta.ac.ShorthandCompletionCache;
import org.fife.rsta.ac.js.completion.JavaScriptTemplateCompletion;
import org.fife.rsta.ac.js.completion.JavascriptBasicCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * Cache of template and comment completions for JavaScript.
 *
 * @author Steve
 * @version 1.0
 */
public class JavaScriptShorthandCompletionCache extends ShorthandCompletionCache {

	private static final String MSG_CLASS = "org.fife.rsta.ac.js.resources";
	private static final ResourceBundle MSG = ResourceBundle.getBundle(MSG_CLASS);


	public JavaScriptShorthandCompletionCache(DefaultCompletionProvider templateProvider, DefaultCompletionProvider commentsProvider, boolean e4xSupport) {

		super(templateProvider, commentsProvider);

		//add basic keywords
		addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "do"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "if"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "while"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "for"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "switch"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "try"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "catch"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "case"));


        //add template completions
        //iterate array
		String template = "for (var ${i} = 0; ${i} < ${array}.length; ${i}++) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop-array",
				template, MSG.getString("for.array.shortDesc"),  MSG.getString("for.array.summary")));

		//standard for
        template = "for (var ${i} = 0; ${i} < ${10}; ${i}++) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop",
                template, MSG.getString("for.loop.shortDesc"), MSG.getString("for.loop.summary")));

        //for in
        template = "for (var ${iterable_element} in ${iterable})\n{\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop-in",
                template, MSG.getString("for.in.shortDesc"), MSG.getString("for.in.summary")));

        //e4x specific
        if (e4xSupport) {
	        //for each
	        template = "for each (var ${iterable_element} in ${iterable})\n{\n\t${cursor}\n}";
	        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop-in-each",
	                template, MSG.getString("for.in.each.shortDesc"), MSG.getString("for.in.each.summary")));
        }

        //do while
		template = "do {\n\t${cursor}\n} while (${condition});";
		addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "do-while",
				"do-loop", template, MSG.getString("do.shortDesc"), MSG.getString("do.summary")));

		//if condition
        template = "if (${condition}) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "if", "if-cond",
                template, MSG.getString("if.cond.shortDesc"), MSG.getString("if.cond.summary")));

        //if else condition
        template = "if (${condition}) {\n\t${cursor}\n} else {\n\t\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "if", "if-else",
                template, MSG.getString("if.else.shortDesc"), MSG.getString("if.else.summary")));

        //while condition
        template = "while (${condition}) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "while", "while-cond",
                template, MSG.getString("while.shortDesc"), MSG.getString("while.summary")));

        //switch case statement
        template = "switch (${key}) {\n\tcase ${value}:\n\t\t${cursor}\n\t\tbreak;\n\tdefault:\n\t\tbreak;\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "switch", "switch-statement",
                template, MSG.getString("switch.case.shortDesc"), MSG.getString("switch.case.summary")));

        //try catch statement
        template = "try {\n\t ${cursor} \n} catch (${err}) {\n\t\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "try", "try-catch",
                template, MSG.getString("try.catch.shortDesc"), MSG.getString("try.catch.summary")));

        //catch block
        template = "catch (${err}) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "catch", "catch-block",
                template, MSG.getString("catch.block.shortDesc"), MSG.getString("catch.block.summary")));

        // Comments
        addCommentCompletion(new BasicCompletion(commentsProvider, "TODO:", null, MSG.getString("todo")));
        addCommentCompletion(new BasicCompletion(commentsProvider, "FIXME:", null, MSG.getString("fixme")));
	}


}
