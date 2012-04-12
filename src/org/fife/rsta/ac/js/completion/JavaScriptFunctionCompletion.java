package org.fife.rsta.ac.js.completion;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JavaScriptFunctionCompletion extends FunctionCompletion 
{

    public JavaScriptFunctionCompletion(CompletionProvider provider, String name, String returnType)
    {
        super(provider, name, returnType);
    }

    public String getSummary()
    {
        String summary = super.getShortDescription(); // Could be just the method name

        // If it's the Javadoc for the method...
        if (summary != null && summary.startsWith("/**")) {
            summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
        }
        
        return summary;
    }
    
    

    
}
