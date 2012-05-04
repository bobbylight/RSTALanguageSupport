package org.fife.rsta.ac.js;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fife.rsta.ac.js.ast.CodeBlock;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

/**
*
* Scripts to be processed before  parsing main script text
* 
* Useful for includes within JavaScript client
* 
* Caches the completions so they do not have to parsed every single time the main script text is parsed
*/
public class PreProcesssingScripts {
	
	private SourceCompletionProvider provider;
	
	private Set preProcessingCompletions = new HashSet();
	
	
	public PreProcesssingScripts(SourceCompletionProvider provider)
	{
		this.provider = provider;
	}
	
	public void parseScript(String scriptText)
	{
		if(scriptText != null && scriptText.length() > 0)
		{
			CompilerEnvirons env = JavaScriptParser.createCompilerEnvironment(new JavaScriptParser.JSErrorReporter(), provider.getLanguageSupport());
			Parser parser = new Parser(env);
			StringReader r = new StringReader(scriptText);
			try {
				AstRoot root = parser.parse(r, null, 0);
				CodeBlock block = provider.iterateAstRoot(root, preProcessingCompletions, "", Integer.MAX_VALUE, true);
				provider.recursivelyAddLocalVars(preProcessingCompletions, block, 0, null, false, true);
			}
			catch(IOException io) {
				//ignore this
			}
		}
	}
	
	
	public void reset()
	{
		preProcessingCompletions.clear();
		//remove all preProcessing Variables
		provider.getVariableResolver().resetPreProcessingVariables(true);
	}
	
	public Set getCompletions()
	{
		return preProcessingCompletions;
	}
	
	
	public void addPreProcessedCompletions(List completions)
	{
		for(Iterator i = preProcessingCompletions.iterator(); i.hasNext();)
		{
			Object o = i.next();
			if(!completions.contains(o))
			{
				completions.add(o);
			}
		}
		
	}
}
