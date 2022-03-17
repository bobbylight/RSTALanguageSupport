/*
 * 07/05/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.html.AttributeCompletion;
import org.fife.rsta.ac.html.HtmlCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.MarkupTagCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;


/**
 * Completion provider for JSP.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class JspCompletionProvider extends HtmlCompletionProvider {

	/**
	 * Mapping of prefixes to TLD's.
	 */
	private Map<String, TldFile> prefixToTld;


	public JspCompletionProvider() {

		prefixToTld = new HashMap<>();
		/*
		String fileName = File.separatorChar=='/' ?
				"/users/robert/struts-2.2.3/lib/struts2-core-2.2.3.jar" :
				"c:/dev/struts/struts-2.2.3/lib/struts2-core-2.2.3.jar";
		File file = new File(fileName);
		if (!file.exists()) {
			file = new File("C:/temp/struts2-core-2.2.1.jar");
		}

		try {
			prefixToTld.put("s", new TldFile(this, file));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		*/
		setAutoActivationRules(false, "<:");

	}


	/**
	 * Overridden to handle JSP tags on top of standard HTML tags.
	 */
	@Override
	protected List<AttributeCompletion> getAttributeCompletionsForTag(
			String tagName) {

		List<AttributeCompletion> list = super.
				getAttributeCompletionsForTag(tagName);

		if (list==null) {

			int colon = tagName.indexOf(':');
			if (colon>-1) {

				String prefix = tagName.substring(0, colon);
				tagName = tagName.substring(colon+1);

				TldFile tldFile = prefixToTld.get(prefix);
				if (tldFile!=null) {
					List<Parameter> attrs = tldFile.getAttributesForTag(tagName);
					if (attrs!=null && attrs.size()>-1) {
						list = new ArrayList<>();
						for (Parameter param : attrs) {
							list.add(new AttributeCompletion(this, param));
						}
					}
				}

			}

		}

		return list;

	}


	/**
	 * Overridden to include JSP-specific tags in addition to the standard
	 * HTML tags.
	 *
	 * @return The list of tags.
	 */
	@Override
	protected List<Completion> getTagCompletions() {

		List<Completion> completions = new ArrayList<>(
                super.getTagCompletions());

		for (Map.Entry<String, TldFile> entry : prefixToTld.entrySet()) {
			String prefix = entry.getKey();
			TldFile tld = entry.getValue();
			for (int j=0; j<tld.getElementCount(); j++) {
				TldElement elem = tld.getElement(j);
				MarkupTagCompletion mtc = new MarkupTagCompletion(this,
						prefix + ":" + elem.getName());
				mtc.setDescription(elem.getDescription());
				completions.add(mtc);
			}
		}

		Collections.sort(completions);
		return completions;

	}


	/**
	 * Overridden to load <code>jsp:*</code> tags also.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void initCompletions() {

		super.initCompletions();

		// Load our JSP completions, but remember the basic HTML ones too.
		try {
			loadFromXML("data/jsp.xml");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// The completions array is expected to be sorted alphabetically.
		// We must re-sort since we added to it.
		completions.sort(comparator);

	}


	@Override
	protected boolean isValidChar(char ch) {
		return super.isValidChar(ch) || ch==':';
	}


}
