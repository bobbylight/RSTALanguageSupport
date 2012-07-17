package org.fife.rsta.ac.java;

import java.util.ResourceBundle;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * 
 * Basic template and comment completions for Java
 * e.g System.out.println()
 * 
 * @see ShorthandCompletionCache
 */
public class JavaShorthandCompletionCache extends ShorthandCompletionCache {

	private static final String MSG = "org.fife.rsta.ac.java.resources";
	private static final ResourceBundle msg = ResourceBundle.getBundle(MSG);
	
	public JavaShorthandCompletionCache(DefaultCompletionProvider templateProvider, DefaultCompletionProvider commentsProvider)
	{
		super(templateProvider, commentsProvider);
		//load defaults
		String template = "System.out.println(${});${cursor}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "sysout", "sysout", template,
				msg.getString("sysout.shortDesc")));

		template = "System.err.println(${});${cursor}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "syserr", "syserr", template,
				msg.getString("syserr.shortDesc")));

		template =
			"for (int ${i} = 0; ${i} < ${array}.length; ${i}++) {\n\t${cursor}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "for", "for-loop", template,
				msg.getString("for.array.shortDesc")));

		template = "do {\n\t${cursor}\n} while (${condition});";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "do", "do-loop", template,
				msg.getString("do.shortDesc")));

		template = "new Runnable() {\n\tpublic void run() {\n\t\t${cursor}\n\t}\n}";
		addShorthandCompletion(new JavaTemplateCompletion(templateProvider, "runnable", "runnable", template,
				msg.getString("runnable.shortDesc")));
		
		/** Comments **/
        addCommentCompletion(new BasicCompletion(commentsProvider, "TODO:", null, msg.getString("todo")));
        addCommentCompletion(new BasicCompletion(commentsProvider, "FIXME:", null, msg.getString("fixme")));
	}
}
