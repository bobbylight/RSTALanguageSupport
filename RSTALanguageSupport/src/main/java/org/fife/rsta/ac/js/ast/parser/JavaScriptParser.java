package org.fife.rsta.ac.js.ast.parser;

import java.util.Set;

import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.CodeBlock;
import org.fife.rsta.ac.js.ast.TypeDeclarationOptions;
import org.fife.ui.autocomplete.Completion;
import org.mozilla.javascript.ast.AstRoot;


public abstract class JavaScriptParser {

	protected SourceCompletionProvider provider;
	protected int dot;
	protected TypeDeclarationOptions options;

	/**
	 * JavaScriptParser constructor.
	 *
	 * @param provider
	 * @param dot
	 * @param options
	 */
	public JavaScriptParser(SourceCompletionProvider provider, int dot,
			TypeDeclarationOptions options) {
		this.provider = provider;
		this.dot = dot;
		this.options = options;
	}

	/**
	 * Converts AstRoot to CodeBlock.
	 *
	 * @param root AstRoot to iterate
	 * @param set completions set
	 * @param entered text entered by user
	 * @return CodeBlock tree
	 */
	public abstract CodeBlock convertAstNodeToCodeBlock(AstRoot root,
			Set<Completion> set, String entered);

	/**
	 * If options are null, then it is assumed that the main editor text is being parsed.
	 *
	 * @return whether options is not null and is in pre-processing mode.
	 *
	 */
	public boolean isPreProcessing() {
		return options != null && options.isPreProcessing();
	}
}
