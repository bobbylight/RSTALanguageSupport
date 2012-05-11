package org.fife.rsta.ac.js.ast.parser;

import java.util.Set;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.mozilla.javascript.ast.AstRoot;


public abstract class JavaScriptParser {
	
	protected SourceCompletionProvider provider;
	protected int dot;
	protected boolean preProcessingMode;
	
	/**
	 * JavaScriptParser constructor
	 * @param provider
	 * @param dot
	 * @param preProcessingMode
	 */
	public JavaScriptParser(SourceCompletionProvider provider, int dot,
			boolean preProcessingMode) {
		this.provider = provider;
		this.dot = dot;
		this.preProcessingMode = preProcessingMode;
	}
	
	/**
	 * Converts AstRoot to CodeBlock 
	 * @param root AstRoot to iterate
	 * @param set completions set
	 * @param entered text entered by user
	 * @return CodeBlock tree 
	 */
	public abstract CodeBlock convertAstNodeToCodeBlock(AstRoot root, Set set, String entered);
}
