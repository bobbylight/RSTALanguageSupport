package org.fife.rsta.ac.js.completion;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


public class JavaScriptMethodCompletion extends FunctionCompletion implements
		JSCompletionUI, JSCompletion {

	private Method method;

	private String compareString;
	private boolean systemFunction;


	public JavaScriptMethodCompletion(CompletionProvider provider, Method method) {
		super(provider, method.getName(), null);
		this.method = method;
		int count = method.getParameterCount();
		List params = new ArrayList(count);
		for (int i = 0; i < count; i++) {
			FormalParameter param = method.getParameter(i);
			String name = param.getName();
			params.add(new FunctionCompletion.Parameter(null, name));
		}
		setParams(params);
	}


	public Icon getIcon() {
		return IconFactory.getIcon(systemFunction ? IconFactory.FUNCTION_ICON
				: IconFactory.DEFAULT_FUNCTION_ICON);
	}


	public int getRelevance() {
		return systemFunction ? GLOBAL_FUNCTION_RELEVANCE : DEFAULT_FUNCTION_RELEVANCE;
	}


	public void setSystemFunction(boolean systemFunction) {
		this.systemFunction = systemFunction;
	}


	public boolean isSystemFunction() {
		return systemFunction;
	}


	public String getSummary() {
		String summary = getMethodSummary(); // Could be just the method name

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	public String getSignature() {
		return getNameAndParameters();
	}


	private String getNameAndParameters() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(');
		int count = method.getParameterCount();
		for (int i = 0; i < count; i++) {
			FormalParameter fp = method.getParameter(i);
			sb.append(fp.getName());
			if (i < count - 1) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	/**
	 * Overridden since <code>equals()</code> is overridden.
	 */
	public int hashCode() {
		return getCompareString().hashCode();
	}


	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return getSignature();
	}


	private String getMethodSummary() {
		String docComment = method.getDocComment();
		return docComment != null ? docComment : method.toString();
	}


	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object o) {
		int rc = -1;
		if (o==this) {
			rc = 0;
		}
		else if (o instanceof JSCompletion) {
			JSCompletion c2 = (JSCompletion)o;
			rc= getLookupName().compareTo(c2.getLookupName());
		}
		else if (o instanceof Completion) {
			Completion c2 = (Completion) o;
			rc = toString().compareTo(c2.toString());
			if (rc == 0) { // Same text value
				String clazz1 = getClass().getName();
				clazz1 = clazz1.substring(clazz1.lastIndexOf('.'));
				String clazz2 = c2.getClass().getName();
				clazz2 = clazz2.substring(clazz2.lastIndexOf('.'));
				rc = clazz1.compareTo(clazz2);
			}
		}

		return rc;
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


	private String getCompareString() {

		/*
		 * This string compares the following parts of methods in this order, to
		 * optimize sort order in completion lists.
		 * 
		 * 1. First, by name 2. Next, by number of parameters. 3. Finally, by
		 * parameter type.
		 */

		if (compareString == null) {
			StringBuffer sb = new StringBuffer(getName());
			// NOTE: This will fail if a method has > 99 parameters (!)
			int paramCount = getParamCount();
			if (paramCount < 10) {
				sb.append('0');
			}
			sb.append(paramCount);
			for (int i = 0; i < paramCount; i++) {
				String type = getParam(i).getType();
				sb.append(type);
				if (i < paramCount - 1) {
					sb.append(',');
				}
			}
			compareString = sb.toString();
		}

		return compareString;

	}


	public String getDefinitionString() {
		return getSignature();
	}
	
	public String getType(boolean qualified) {
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType("void", qualified);
	}
	
	public String getEnclosingClassName(boolean fullyQualified) {
		return null;
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

	

}