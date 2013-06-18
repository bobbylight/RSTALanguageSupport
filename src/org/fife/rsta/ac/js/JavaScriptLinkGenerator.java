package org.fife.rsta.ac.js;

import javax.swing.text.BadLocationException;

import org.fife.rsta.ac.js.ast.JavaScriptFunctionDeclaration;
import org.fife.rsta.ac.js.ast.JavaScriptVariableDeclaration;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SelectRegionLinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.Token;


public class JavaScriptLinkGenerator implements LinkGenerator {

	private JavaScriptLanguageSupport language;
	
	public JavaScriptLinkGenerator(JavaScriptLanguageSupport language)
	{
		this.language = language;
	}
	
	public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
		int start = -1;
		int end = -1;

		IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
		if (result!=null) {
			JavaScriptParser parser = language.getJavaScriptParser();
			VariableResolver variableResolver = parser.getVariablesAndFunctions();
			
			Token t = result.token;
			boolean function = result.function;
			
			if(variableResolver != null)
			{
				String name = t.getLexeme();
				if(!function) { //must be a variable
					JavaScriptVariableDeclaration dec = variableResolver.findDeclaration(name, offs);
					if(dec != null)
					{
						start = dec.getStartOffSet();
						end = dec.getEndOffset();
					}
				}
				else { 
					//lookup Function based on the name
					JavaScriptFunctionDeclaration func = variableResolver.findFunctionDeclarationByFunctionName(name);
					if(func != null)
					{
						start = func.getNameStartOffset();
						end = func.getNameEndOffset();
					}
				}
				
			}
			
			if (start>-1) {
				return new SelectRegionLinkGeneratorResult(textArea, t.offset,
						start, end);
			}
		}
		return null;
	}
	
	/**
	 * Checks if the token at the specified offset is possibly a "click-able"
	 * region.
	 *
	 * @param textArea The text area.
	 * @param offs The offset, presumably at the mouse position.
	 * @return A result object.
	 */
	private IsLinkableCheckResult checkForLinkableToken(
			RSyntaxTextArea textArea, int offs) {

		IsLinkableCheckResult result = null;

		if (offs>=0) {

			try {

				int line = textArea.getLineOfOffset(offs);
				Token first = wrapToken(textArea.getTokenListForLine(line));
				Token prev = null; 
				
				for (Token t=first; t!=null && t.isPaintable(); t=wrapToken(t.getNextToken())) {
					if (t.containsPosition(offs)) {

						// RSTA's tokens are pooled and re-used, so we must
						// defensively make a copy of the one we want to keep!
						Token token = wrapToken(t);
						
						boolean isMethod = false;

						if (prev!=null && prev.isSingleChar('.')) {
							// Not a field or method defined in this.
							break;
						}

						Token next = wrapToken(RSyntaxUtilities.getNextImportantToken(
								t.getNextToken(), textArea, line));
						if (next!=null && next.isSingleChar(Token.SEPARATOR, '(')) {
							isMethod = true;
						}

						result = new IsLinkableCheckResult(token, isMethod);
						break;

					}

					else if (!t.isCommentOrWhitespace()) {
						prev = t;
					}

				}

			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Never happens
			}

		}

		return result;

	}
	
	private Token wrapToken(Token token)
	{
		if(token != null)
			return new Token(token);
		return token;
	}
	
	
	/**
	 * The result of checking whether a region of code under the mouse is
	 * <em>possibly</em> link-able.
	 */
	private static class IsLinkableCheckResult {

		/**
		 * The token under the mouse position.
		 */
		private Token token;

		/**
		 * Whether the token is a function invocation (as opposed to a local
		 * variable or field).
		 */
		private boolean function;

		private IsLinkableCheckResult(Token token, boolean function) {
			this.token = token;
			this.function = function;
		}

	}

}
