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

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.ast.TypeDeclarationFactory;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;


public class JSFunctionCompletion extends FunctionCompletion implements
		JSCompletion {

	private JSMethodData methodData;
	private Method method;
	private String compareString;
	

	public JSFunctionCompletion(CompletionProvider provider, MethodInfo method,
			JarManager jarManager) {
		this(provider, method, jarManager, false);
	}


	public JSFunctionCompletion(CompletionProvider provider,
			MethodInfo methodInfo, JarManager jarManager,
			boolean showParameterType) {
		super(provider, methodInfo.getName(), null);
		this.methodData = new JSMethodData(methodInfo, jarManager);
		this.method = methodData.getMethod();
		int count = method.getParameterCount();
		String[] paramTypes = methodInfo.getParameterTypes();
		List params = new ArrayList(count);
		for (int i = 0; i < count; i++) {
			FormalParameter param = method.getParameter(i);
			String name = param.getName();
			params.add(new JSFunctionParam(paramTypes[i], name,
					showParameterType));
		}
		setParams(params);
	}


	/**
	 * Overridden to compare methods by their comparison strings.
	 * 
	 * @param o A <code>Completion</code> to compare to.
	 * @return The sort order.
	 */
	public int compareTo(Object o) {

		int rc = -1;

		if (o == this)
			rc = 0;

		else if (o instanceof JSFunctionCompletion) {
			rc = getCompareString().compareTo(
					((JSFunctionCompletion) o).getCompareString());
		}

		else if (o instanceof Completion) {
			Completion c2 = (Completion) o;
			rc = toString().compareToIgnoreCase(c2.toString());
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
		return (obj instanceof JSFunctionCompletion)
				&& ((JSFunctionCompletion) obj).getCompareString().equals(
						getCompareString());
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = temp.lastIndexOf('.');
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


	public String getLookupName() {
		StringBuffer sb = new StringBuffer(getName());
		sb.append('(');
		int count = method.getParameterCount();
		for (int i = 0; i < count; i++) {
			sb.append("p");
			if (i < count - 1) {
				sb.append(",");
			}
		}
		sb.append(')');
		return sb.toString();
	}


	public String getDefinitionString() {
		return getSignature();
	}


	private String getMethodSummary() {
		String docComment = method.getDocComment();
		return docComment != null ? docComment : method.toString();
	}


	private String getNameAndParameters() {
		return formatMethodAtString(getName(), method);
	}


	private static String formatMethodAtString(String name, Method method) {
		StringBuffer sb = new StringBuffer(name);
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
		return TypeDeclarationFactory.lookupJSType(value, false);
	}


	public String getType(boolean qualified) {
		return TypeDeclarationFactory.lookupJSType(methodData
				.getType(qualified), qualified);
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(IconFactory.FUNCTION_ICON);
	}


	/**
	 * Override the FunctionCompletion.Parameter to lookup the Javascript name
	 * for the completion type
	 */
	public static class JSFunctionParam extends
			ParameterizedCompletion.Parameter {

		private boolean showParameterType;


		public JSFunctionParam(Object type, String name,
				boolean showParameterType) {
			super(type, name);
			this.showParameterType = showParameterType;
		}


		public String getType() {
			return showParameterType ? TypeDeclarationFactory.lookupJSType(
					super.getType(), false) : null;
		}

	}

}