package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


/**
 * 
 * ShorthandCompletionCache to store completions for Template completions and Comment completions 
 * Template completions should extend <code>TemplateCompletion</code> that uses parameterised variables/values 
 * e.g
 * 
 * While template completion example:
 * 		while --> while(condition) {
 * 					//cursor here
 * 				  }
 * 
 * Comment completion - example:
 * 		TODO --> //TODO: 
 * 
 * This is really a convenient place to store these types of completions that are re-used 
 * @link JavaShorthandCompletionCache
 * @link JavaScriptShorthandCompletionCache
 */
public abstract class ShorthandCompletionCache {

	
	private ArrayList shorthandCompletion = new ArrayList();
	private ArrayList commentCompletion = new ArrayList();
	
	private DefaultCompletionProvider templateProvider, commentProvider;
	
	public ShorthandCompletionCache(DefaultCompletionProvider templateProvider, DefaultCompletionProvider commentProvider) {
		this.templateProvider = templateProvider;
		this.commentProvider = commentProvider;
	}
	
	public void addShorthandCompletion(Completion completion) {
		shorthandCompletion.add(completion);
	}

	public List getShorthandCompletions() {
		return shorthandCompletion;
	}
	
	public void removeShorthandCompletion(Completion completion)
	{
		shorthandCompletion.remove(completion);
	}
	
	public void clearCache()
	{
		shorthandCompletion.clear();
	}
	
	//comments
	public void addCommentCompletion(Completion completion) {
		commentCompletion.add(completion);
	}

	public List getCommentCompletions() {
		return commentCompletion;
	}
	
	public void removeCommentCompletion(Completion completion)
	{
		commentCompletion.remove(completion);
	}
	
	public DefaultCompletionProvider getTemplateProvider()
	{
		return templateProvider;
	}
	
	public DefaultCompletionProvider getCommentProvider()
	{
		return commentProvider;
	}
}
