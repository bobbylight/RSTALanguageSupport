package org.fife.rsta.ac.js;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

/**
 * Scripts to be processed before  parsing main script text
 * 
 * Useful for includes within JavaScript client
 * 
 * Caches the completions so they do not have to parsed every single time the main script text is parsed
 */
public class PreProcessingScripts {
	
	private SourceCompletionProvider provider;
	
	private Set<Completion> preProcessingCompletions = new HashSet<Completion>();
	
	
	public PreProcessingScripts(SourceCompletionProvider provider)
	{
		this.provider = provider;
	}
	
	public void parseScript(String scriptText, TypeDeclarationOptions options)
	{
		if(scriptText != null && scriptText.length() > 0)
		{
			CompilerEnvirons env = JavaScriptParser.createCompilerEnvironment(new JavaScriptParser.JSErrorReporter(), provider.getLanguageSupport());
			Parser parser = new Parser(env);
			StringReader r = new StringReader(scriptText);
			try {
				AstRoot root = parser.parse(r, null, 0);
				CodeBlock block = provider.iterateAstRoot(root, preProcessingCompletions, "", Integer.MAX_VALUE, options);
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
	
	public Set<Completion> getCompletions()
	{
		return preProcessingCompletions;
	}
	
	
}
