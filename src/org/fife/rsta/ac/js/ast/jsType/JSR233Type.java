package org.fife.rsta.ac.js.ast.jsType;

import java.util.ArrayList;
import java.util.Iterator;

import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JSR233Type extends JavaScriptType {

	protected JSR233Type(TypeDeclaration type) {
		super(type);
	}


	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	protected JSCompletion _getCompletion(String completionLookup,
			SourceCompletionProvider provider) {
		JSCompletion completion = (JSCompletion) typeCompletions
				.get(completionLookup);
		if (completion != null) {
			return completion;
		}
		// else
		if (completionLookup.indexOf('(') != -1) {
			// must be a function, so compare function strings
			// get a list of best fit methods
			Logger.log("Completion Lookup : " + completionLookup);
			JavaScriptFunctionType javaScriptFunctionType = JavaScriptFunctionType
					.parseFunction(completionLookup, provider);

			JSCompletion[] matches = getPotentialLookupList(javaScriptFunctionType
					.getName());

			// iterate through types and check best fit parameters
			int bestFitIndex = -1;
			int bestFitWeight = -1;
			for (int i = 0; i < matches.length; i++) {
				Logger.log("Potential match : " + matches[i].getLookupName());
				JavaScriptFunctionType matchFunctionType = javaScriptFunctionType
						.parseFunction(matches[i].getLookupName());
				Logger.log("Matching against completion: " + completionLookup);
				int weight = matchFunctionType.compare(javaScriptFunctionType,
						provider);
				Logger.log("Weight: " + weight);
				if (weight < JavaScriptFunctionType.CONVERSION_NONE
						&& (weight < bestFitWeight || bestFitIndex == -1)) {
					bestFitIndex = i;
					bestFitWeight = weight;
				}
			}
			if (bestFitIndex > -1) {
				Logger
						.log("BEST FIT: "
								+ matches[bestFitIndex].getLookupName());
				return matches[bestFitIndex];
			}
		}

		return null;
	}


	// get a list of all potential method matches
	private JSCompletion[] getPotentialLookupList(String name) {
		ArrayList completionMatches = new ArrayList();

		for (Iterator i = typeCompletions.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			if (key.startsWith(name)) {
				JSCompletion completion = (JSCompletion) typeCompletions
						.get(key);
				if (completion instanceof FunctionCompletion) {
					completionMatches.add(completion);
				}
			}
		}

		return (JSCompletion[]) completionMatches
				.toArray(new JSCompletion[completionMatches.size()]);
	}

}
