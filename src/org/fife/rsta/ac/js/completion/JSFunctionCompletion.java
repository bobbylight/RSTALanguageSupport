/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.completion;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;


public class JSFunctionCompletion extends FunctionCompletion implements
		JSCompletion {

	private JSMethodData methodData;
	private String compareString;


	public JSFunctionCompletion(CompletionProvider provider, MethodInfo method) {
		this(provider, method, false);
	}


	public JSFunctionCompletion(CompletionProvider provider,
			MethodInfo methodInfo, boolean showParameterType) {
		super(provider, getMethodName(methodInfo, provider), null);
		this.methodData = new JSMethodData(methodInfo,
				((SourceCompletionProvider) provider).getJarManager());
		List params = populateParams(methodData, showParameterType);
		setParams(params);
	}
	
	private static String getMethodName(MethodInfo info, CompletionProvider provider)
	{
		if(info.isConstructor()){
			return ((SourceCompletionProvider) provider).getTypesFactory().convertJavaScriptType(info.getClassFile().getClassName(true), false);
		} else {
			return info.getName();
		}
	}

	private List populateParams(JSMethodData methodData,
			boolean showParameterType) {
		MethodInfo methodInfo = methodData.getMethodInfo();
		int count = methodInfo.getParameterCount();
		String[] paramTypes = methodInfo.getParameterTypes();
		List params = new ArrayList(count);
		for (int i = 0; i < count; i++) {
			String name = methodData.getParameterName(i);
			String type = methodData.getParameterType(paramTypes, i, getProvider());
			params.add(new JSFunctionParam(type, name, showParameterType, getProvider()));
		}

		return params;
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
			rc = getLookupName().compareTo(c2.getLookupName());
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
		return (obj instanceof JSCompletion)
				&& ((JSCompletion) obj).getLookupName().equals(
						getLookupName());
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			temp = temp.substring(lastDot + 1);
		}
		return temp;
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

			compareString = getLookupName();
		}

		return compareString;

	}


	public String getLookupName() {
		SourceCompletionProvider provider = (SourceCompletionProvider) getProvider();
		return provider.getJavaScriptEngine().getJavaScriptResolver(provider).getLookupText(methodData, getName());
	}


	public String getDefinitionString() {
		return getSignature();
	}


	private String getMethodSummary() {

		// String summary = methodData.getSummary(); // Could be just the method
		// name

		Method method = methodData.getMethod();
		String summary = method != null ? method.getDocComment() : null;
		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary != null ? summary : getNameAndParameters();
	}


	private String getNameAndParameters() {
		return formatMethodAtString(getName(), methodData);
	}


	private static String formatMethodAtString(String name, JSMethodData method) {
		StringBuffer sb = new StringBuffer(name);
		sb.append('(');
		int count = method.getParameterCount();
		for (int i = 0; i < count; i++) {
			sb.append(method.getParameterName(i));
			if (i < count - 1) {
				sb.append(", ");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	public String getSignature() {
		return getNameAndParameters();
	}


	public String getSummary() {
		String summary = getMethodSummary(); // Could be just the method name

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	public int hashCode() {
		return getCompareString().hashCode();
	}


	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return getSignature();
	}


	public String getType() {
		String value = getType(true);
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(value, false);
	}


	public String getType(boolean qualified) {
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(methodData
				.getType(qualified), qualified);
	}


	public Icon getIcon() {
		return methodData.isStatic() ? IconFactory
				.getIcon(IconFactory.PUBLIC_STATIC_FUNCTION_ICON) : IconFactory
				.getIcon(IconFactory.DEFAULT_FUNCTION_ICON);
	}


	public int getRelevance() {
		return DEFAULT_FUNCTION_RELEVANCE;
	}


	public String getEnclosingClassName(boolean fullyQualified) {
		return methodData.getEnclosingClassName(fullyQualified);
	}
	
	public JSMethodData getMethodData()
	{
		return methodData;
	}

	/**
	 * Override the FunctionCompletion.Parameter to lookup the Javascript name
	 * for the completion type
	 */
	public static class JSFunctionParam extends
			ParameterizedCompletion.Parameter {

		private boolean showParameterType;
		private CompletionProvider provider;

		public JSFunctionParam(Object type, String name,
				boolean showParameterType, CompletionProvider provider) {
			super(type, name);
			this.showParameterType = showParameterType;
			this.provider = provider;
		}


		public String getType() {
			return showParameterType ? ((SourceCompletionProvider) provider).getTypesFactory()
					.convertJavaScriptType(super.getType(), false) : null;
		}

	}

}