package org.fife.rsta.ac.js;

import javax.swing.text.BadLocationException;

import org.fife.rsta.ac.js.ast.JavaScriptDeclaration;
import org.fife.rsta.ac.js.ast.VariableResolver;
import org.fife.ui.rsyntaxtextarea.LinkGenerator;
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.SelectRegionLinkGeneratorResult;
import org.fife.ui.rsyntaxtextarea.Token;


public class JavaScriptLinkGenerator implements LinkGenerator {

	private JavaScriptLanguageSupport language;
	private boolean findLocal;
	private boolean findPreprocessed;
	private boolean findSystem;


	public JavaScriptLinkGenerator(JavaScriptLanguageSupport language) {
		this.language = language;
		this.findLocal = true;
	}

	/**
	 * {@inheritDoc}
	 */
	public LinkGeneratorResult isLinkAtOffset(RSyntaxTextArea textArea, int offs) {
		
		JavaScriptDeclaration dec = null;
		IsLinkableCheckResult result = checkForLinkableToken(textArea, offs);
		if (result != null) {
			JavaScriptParser parser = language.getJavaScriptParser();
			VariableResolver variableResolver = parser.getVariablesAndFunctions();

			Token t = result.token;
			boolean function = result.function;

			if (variableResolver != null) {
				String name = t.getLexeme();
				if (!function) { // must be a variable
					dec = variableResolver.findDeclaration(name, offs, findLocal, findPreprocessed, findSystem);
				}
				else {
					String lookup = getLookupNameForFunction(textArea, offs, name);
					// lookup Function based on the name
					dec = variableResolver.findFunctionDeclaration(lookup, findLocal, findPreprocessed);
					if(dec == null) {
						dec = variableResolver.findFunctionDeclarationByFunctionName(name, findLocal, findPreprocessed);
					}
				}

			}

			if (dec != null) {
				return createSelectedRegionResult(textArea, t, dec);
			}
		}
		return null;
	}
	
	/**
	 * @return LinkGeneratorResult based on the JavaScriptDeclaraton and the position 
	 */
	protected LinkGeneratorResult createSelectedRegionResult(RSyntaxTextArea textArea, Token t, JavaScriptDeclaration dec) {
		if(dec.getTypeDeclarationOptions() != null && !dec.getTypeDeclarationOptions().isSupportsLinks()) {
			return null;
		}
		return new SelectRegionLinkGeneratorResult(textArea, t.offset, dec.getStartOffSet(), dec.getEndOffset());
	}
	
	/**
	 * @param find flag to state whether to look in the RSTA editing script for variable/function completions
	 */
	public void setFindLocal(boolean find) {
		this.findLocal = find;
	}
	
	/**
	 * @param find flag to state whether to look in the pre-processed scripts for variable/function completions
	 */
	public void setFindPreprocessed(boolean find) {
		this.findPreprocessed = find;
	}
	
	/**
	 * @param find flag to state whether to look in the system scripts for variable/function completions
	 */
	public void setFindSystem(boolean find) {
		this.findSystem = find;
	}


	/**
	 * Convert the function Token to JavaScript variable resolver lookup name by replacing any parameters with 'p' and stripping any whitespace between the parameters:
	 * e.g
	 * Token may contain the function:
	 * addTwoNumbers(num1, num2);
	 * 
	 * The return result will be:
	 * addTwoNumbers(p,p);
	 * 
	 * @return converted function name to variable resolver lookup name
	 */
	private String getLookupNameForFunction(RSyntaxTextArea textArea, int offs, String name) {
		
		StringBuffer temp = new StringBuffer();
		if (offs>=0) {
			
			try {
				int line = textArea.getLineOfOffset(offs);
				
				Token first = wrapToken(textArea.getTokenListForLine(line));
				for (Token t=first; t!=null && t.isPaintable(); t=wrapToken(t.getNextToken())) {
					if (t.containsPosition(offs)) {
						for (Token tt=t; tt!=null && tt.isPaintable(); tt=wrapToken(tt.getNextToken())) {
							if (tt!=null) {
								temp.append(tt.getLexeme());
							}
							if (tt!=null && tt.isSingleChar(Token.SEPARATOR, ')')) {
								break;
							}
						}
					}
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace(); // Never happens
			}
		}
		
		//now replace all the variables with lookup 'p'
		String function = temp.toString().replaceAll("\\s", ""); //remove all whitespace
		boolean params = false;
		int count = 0;
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<function.length(); i++) {
			char ch = function.charAt(i);
			
			if(ch == '(') {
				params = true;
				count = 0;
				sb.append(ch);
			}
			
			else if(ch == ')') {
				sb.append(ch);
				break;
			}
			
			else if(ch == ',') {
				count = 0;
				sb.append(ch);
				continue;
			}
			
			else if(params && count == 0) {
				sb.append('p');
				count++;
			}
			
			else if(!params) {
				sb.append(ch);
			}
			
			
		}
		
		return sb.toString();
		
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

		if (offs >= 0) {

			try {

				int line = textArea.getLineOfOffset(offs);
				Token first = wrapToken(textArea.getTokenListForLine(line));
				Token prev = null;

				for (Token t = first; t != null && t.isPaintable(); t = wrapToken(t
						.getNextToken())) {
					if (t.containsPosition(offs)) {

						// RSTA's tokens are pooled and re-used, so we must
						// defensively make a copy of the one we want to keep!
						Token token = wrapToken(t);

						boolean isFunction = false;

						if (prev != null && prev.isSingleChar('.')) {
							// Not a field or method defined in this.
							break;
						}

						Token next = wrapToken(RSyntaxUtilities
								.getNextImportantToken(t.getNextToken(),
										textArea, line));
						if (next != null
								&& next.isSingleChar(Token.SEPARATOR, '(')) {
							isFunction = true;
						}

						result = new IsLinkableCheckResult(token, isFunction);
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

	/**
	 * Due to the Tokens being reused, this can cause problems when finding the next token associated with a token. 
	 * Wrap the tokens to stop this problem
	 * @param token to wrap
	 * @return copy of the original token
	 */
	private Token wrapToken(Token token) {
		if (token != null)
			return new Token(token);
		return token;
	}
	
	/**
	 * @return JavaScriptLanguage support
	 */
	public JavaScriptLanguageSupport getLanguage() {
		return language;
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
		 * variable or object).
		 */
		private boolean function;


		private IsLinkableCheckResult(Token token, boolean function) {
			this.token = token;
			this.function = function;
		}

	}

}
