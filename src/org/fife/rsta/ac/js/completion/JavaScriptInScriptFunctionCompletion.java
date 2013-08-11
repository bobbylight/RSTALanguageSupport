package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;

import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JavaScriptInScriptFunctionCompletion extends FunctionCompletion
		implements JSCompletion {

	private TypeDeclaration returnType;
	
	public JavaScriptInScriptFunctionCompletion(CompletionProvider provider,
			String name, TypeDeclaration returnType) {
		super(provider, name, null);
		setRelevance(DEFAULT_FUNCTION_RELEVANCE);
		this.returnType = returnType;
	}


	@Override
	public String getSummary() {
		String summary = super.getShortDescription(); // Could be just the
														// method name

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	@Override
	public Icon getIcon() {
		return IconFactory.getIcon(IconFactory.DEFAULT_FUNCTION_ICON);
	}


	public String getLookupName() {
		StringBuilder sb = new StringBuilder(getName());
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


	@Override
	public String getType() {
		String value = getType(true);
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(value, false);
	}


	public String getType(boolean qualified) {
		String type = returnType != null ? returnType.getQualifiedName() : null;
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(type, qualified);
	}
	
	
	
	public String getEnclosingClassName(boolean fullyQualified) {
		return null;
	}


	@Override
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


	@Override
	public int hashCode() {
		return getLookupName().hashCode();
	}
	
	@Override
	public String toString() {
		return getLookupName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Completion other) {
		if (other==this) {
			return 0;
		}
		else if (other instanceof JSCompletion) {
			JSCompletion c2 = (JSCompletion)other;
			return getLookupName().compareTo(c2.getLookupName());
		}
		else if (other != null) {
			return toString().compareTo(other.toString());
		}
		return -1;
	}


}