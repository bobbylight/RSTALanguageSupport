package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JavaScriptInScriptFunctionCompletion extends FunctionCompletion
		implements JSCompletion {

	public JavaScriptInScriptFunctionCompletion(CompletionProvider provider,
			String name, String returnType) {
		super(provider, name, returnType);
	}


	public String getSummary() {
		String summary = super.getShortDescription(); // Could be just the
														// method name

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	public Icon getIcon() {
		return IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON);
	}


	public int getSortIndex() {
		return DEFAULT_FUNCTION_INDEX;
	}
	
	public String getLookupName() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(');
		int count = getParamCount();
		for (int i = 0; i < count; i++) {
			sb.append("p");
			if (i < count - 1) {
				sb.append(",");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	public String getType(boolean qualified) {
		return getType();
	}
	
	
	public TypeDeclaration getTypeDeclaration() {
		return TypeDeclarationFactory.Instance().getTypeDeclaration(getType(true));
	}
	
	


	public String getEnclosingClassName(boolean fullyQualified) {
		return null;
	}


	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj instanceof JSCompletion)
		{
			JSCompletion jsComp = (JSCompletion) obj;
			return getLookupName().equals(jsComp.getLookupName());
		}
		return super.equals(obj);
	}


	public int hashCode() {
		return getLookupName().hashCode();
	}
	
	
	

}
