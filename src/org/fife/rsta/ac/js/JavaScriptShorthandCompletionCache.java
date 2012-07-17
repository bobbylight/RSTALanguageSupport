package org.fife.rsta.ac.js;

import java.util.ResourceBundle;

import org.fife.rsta.ac.java.ShorthandCompletionCache;
import org.fife.rsta.ac.js.completion.JavaScriptTemplateCompletion;
import org.fife.rsta.ac.js.completion.JavascriptBasicCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


public class JavaScriptShorthandCompletionCache extends ShorthandCompletionCache {

	private static final String MSG = "org.fife.rsta.ac.js.resources";
	private static final ResourceBundle msg = ResourceBundle.getBundle(MSG);


	public JavaScriptShorthandCompletionCache(DefaultCompletionProvider templateProvider, DefaultCompletionProvider commentsProvider) {
		
		super(templateProvider, commentsProvider);
		//add basic keywords
		addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "do"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "if"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "while"));
        addShorthandCompletion(new JavascriptBasicCompletion(templateProvider, "for"));
		
		//add template completions
        //iterate array
		String template = "for (var ${i} = 0; ${i} < ${array}.length; ${i}++) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop-array",
				template, msg.getString("for.array.shortDesc"),  msg.getString("for.array.summary")));
		
		//standard for
        template = "for (var ${i} = 0; ${i} < ${10}; ${i}++) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop",
                template, msg.getString("for.loop.shortDesc"),msg.getString("for.loop.summary")));
        
        //for in
        template = "for (var ${iterable_element} in ${iterable})\n{\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "for", "for-loop-in",
                template, msg.getString("for.in.shortDesc"), msg.getString("for.in.summary")));

        //do while
		template = "do {\n\t${cursor}\n} while (${condition});";
		addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "do-while",
				"do-loop", template, msg.getString("do.shortDesc"), msg.getString("do.summary")));
		
		//if condition
        template = "if (${condition}) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "if", "if-cond",
                template, msg.getString("if.cond.shortDesc"), msg.getString("if.cond.summary")));
        
        //if else condition
        template = "if (${condition}) {\n\t${cursor}\n} else {\n\t\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "if", "if-else",
                template, msg.getString("if.else.shortDesc"), msg.getString("if.else.summary")));
        
        //while condition
        template = "while (${condition}) {\n\t${cursor}\n}";
        addShorthandCompletion(new JavaScriptTemplateCompletion(templateProvider, "while", "while-cond",
                template, msg.getString("while.shortDesc"), msg.getString("while.summary")));
		
        /** Comments **/
        addCommentCompletion(new BasicCompletion(commentsProvider, "TODO:", null, msg.getString("todo")));
        addCommentCompletion(new BasicCompletion(commentsProvider, "FIXME:", null, msg.getString("fixme")));
	}
}
