/*
 * 04/22/2013
 *
 * Copyright (C) 2013 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.js;

import javax.swing.Icon;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.TemplateCompletion;


/**
 * Completion provider for JSDoc.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JsDocCompletionProvider extends DefaultCompletionProvider {


	public JsDocCompletionProvider() {

		// Simple tags
		String[] simpleTags = {
			"abstract", "access", "alias", "augments", "author", "borrows",
			"callback", "classdesc", "constant", "constructor", "constructs",
			"copyright", "default", "deprecated", "desc", "enum", "event",
			"example", "exports", "external", "file", "fires", "global",
			"ignore", "inner", "instance", "kind", "lends", "license",
			/*"link",*/ "member", "memberof", "method", "mixes", "mixin", "module",
			"name", "namespace", /*"param",*/ "private", "property", "protected",
			"public", "readonly", "requires", /*"return", /*"returns",*/ "see", "since",
			"static", "summary", "this", "throws", "todo", /*"tutorial",*/
			"type", "typedef", "variation", "version",
		};
        for (String simpleTag : simpleTags) {
            addCompletion(new JsDocCompletion(this, "@" + simpleTag));
        }

		// Parameterized (simple) tags
		addCompletion(new JsDocParameterizedCompletion(this, "@param", "@param {type} varName",
				"@param {${}} ${varName} ${cursor}"));
		addCompletion(new JsDocParameterizedCompletion(this, "@return", "@return {type} description",
				"@return {${type}} ${}"));
		addCompletion(new JsDocParameterizedCompletion(this, "@returns", "@returns {type} description",
				"@returns {${type}} ${}"));

		// Inline tags
		addCompletion(new JsDocParameterizedCompletion(this, "{@link}", "{@link}", "{@link ${}}${cursor}"));
		addCompletion(new JsDocParameterizedCompletion(this, "{@linkplain}", "{@linkplain}", "{@linkplain ${}}${cursor}"));
		addCompletion(new JsDocParameterizedCompletion(this, "{@linkcode}", "{@linkcode}", "{@linkcode ${}}${cursor}"));
		addCompletion(new JsDocParameterizedCompletion(this, "{@tutorial}", "{@tutorial}", "{@tutorial ${tutorialID}}${cursor}"));

		// Other common stuff
		addCompletion(new JsDocCompletion(this, "null", "<code>null</code>", "&lt;code>null&lt;/code>", IconFactory.TEMPLATE_ICON));
		addCompletion(new JsDocCompletion(this, "true", "<code>true</code>", "&lt;code>true&lt;/code>", IconFactory.TEMPLATE_ICON));
		addCompletion(new JsDocCompletion(this, "false", "<code>false</code>", "&lt;code>false&lt;/code>", IconFactory.TEMPLATE_ICON));

		setAutoActivationRules(false, "{@");

	}


	@Override
	protected boolean isValidChar(char ch) {
		return Character.isLetterOrDigit(ch) || ch=='_' || ch=='@' ||
					ch=='{' || ch=='}';
	}


	/**
	 * A Javadoc completion.
	 */
	private static class JsDocCompletion extends BasicCompletion {

		private String inputText;
		private String icon;

		public JsDocCompletion(CompletionProvider provider,
									String replacementText) {
			super(provider, replacementText);
			this.inputText = replacementText;
			this.icon = IconFactory.JSDOC_ITEM_ICON;
		}

		public JsDocCompletion(CompletionProvider provider,
				String inputText, String replacementText, String shortDesc,
				String icon) {
			super(provider, replacementText, shortDesc, shortDesc);
			this.inputText = inputText;
			this.icon = icon;
		}

		@Override
		public Icon getIcon() {
			return IconFactory.getIcon(icon);
		}

		@Override
		public String getInputText() {
			return inputText;
		}

	}


	private static class JsDocParameterizedCompletion extends TemplateCompletion {

		private String icon;

		public JsDocParameterizedCompletion(CompletionProvider provider,
				String inputText, String definitionString, String template) {
			this(provider, inputText, definitionString, template,
					IconFactory.JSDOC_ITEM_ICON);
		}

		public JsDocParameterizedCompletion(CompletionProvider provider,
				String inputText, String definitionString, String template,
				String icon) {
			super(provider, inputText, definitionString, template);
			setIcon(icon);
		}

		@Override
		public Icon getIcon() {
			return IconFactory.getIcon(icon);
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
	}


}
