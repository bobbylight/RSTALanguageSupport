package org.fife.rsta.ac.js.ast.jsType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.fife.rsta.ac.js.Logger;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JSR223Type extends JavaScriptType {

	public JSR223Type(TypeDeclaration type) {
		super(type);
	}


	/**
	 * @param completionLookup
	 * @return JSCompletion using lookup name
	 * @see JSCompletion
	 */
	protected JSCompletion _getCompletion(String completionLookup,
			SourceCompletionProvider provider) {
		JSCompletion completion = (JSCompletion) methodFieldCompletions
				.get(completionLookup);
		if (completion != null) {
			return completion;
		}
		// else
		if (completionLookup.indexOf('(') != -1) {
			boolean isJavaScriptType = provider.getTypesFactory().isJavaScriptType(getType());
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
			Logger.log("Potential matches : " + matches.length);
			for (int i = 0; i < matches.length; i++) {
				Logger.log("Potential match : " + matches[i].getLookupName());
				JavaScriptFunctionType matchFunctionType = JavaScriptFunctionType
						.parseFunction(matches[i].getLookupName(), provider);
				Logger.log("Matching against completion: " + completionLookup);
				int weight = matchFunctionType.compare(javaScriptFunctionType,
						provider, isJavaScriptType);
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

	private JSCompletion[] getPotentialLookupList(String name)
	{
		//get a list of all potential matches, including extended
		HashSet completionMatches = new HashSet();
		getPotentialLookupList(name, completionMatches, this);
		return (JSCompletion[]) completionMatches.toArray(new JSCompletion[completionMatches.size()]);
	}

	// get a list of all potential method matches
	private void getPotentialLookupList(String name, HashSet completionMatches, JavaScriptType type) {
		
		Map typeCompletions = type.methodFieldCompletions;
		
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
		
		//loop through extended and add it's methods too recursively
		for(Iterator extended = type.getExtendedClasses().iterator(); extended.hasNext();) {
			JavaScriptType extendedType = (JavaScriptType) extended.next();
			getPotentialLookupList(name, completionMatches, extendedType);
		}

		
	}

}
