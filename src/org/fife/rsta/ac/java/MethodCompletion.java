/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;


/**
 * A completion for a Java method.  This completion gets its information from
 * one of two sources:
 * 
 * <ul>
 *    <li>A {@link MethodInfo} instance, which is loaded by parsing a class
 *        file.  This is used when this completion represents a method found
 *        in a compiled library.</li>
 *    <li>A {@link Method} instance, which is created when parsing a Java
 *        source file.  This is used when the completion represents a method
 *        found in uncompiled source, such as the source in an
 *        <tt>RSyntaxTextArea</tt>, or in a loose file on disk.</li>
 * </ul>
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MethodCompletion extends FunctionCompletion implements MemberCompletion {

	/**
	 * The data source for our completion attributes.
	 */
	private Data data;

	/**
	 * Used to compare this method completion with another.
	 */
	private String compareString;

	/**
	 * The relevance of methods.  This allows methods to be "higher" in
	 * the completion list than other types.
	 */
	private static final int NON_CONSTRUCTOR_RELEVANCE		= 2;


	/**
	 * Creates a completion for a method discovered when parsing a Java
	 * source file.
	 *
	 * @param provider
	 * @param m Meta data about the method.
	 */
	public MethodCompletion(CompletionProvider provider, Method m) {

		// NOTE: "void" might not be right - I think this might be constructors
		super(provider, m.getName(), m.getType()==null ? "void" : m.getType().toString());
		setDefinedIn(m.getParentTypeDeclaration().getName());
		this.data = new MethodData(m);
		setRelevanceAppropriately();

		int count = m.getParameterCount();
		List<Parameter> params = new ArrayList<Parameter>(count);
		for (int i=0; i<count; i++) {
			FormalParameter param = m.getParameter(i);
			Type type = param.getType();
			String name = param.getName();
			params.add(new Parameter(type, name));
		}
		setParams(params);

	}


	/**
	 * Creates a completion for a method discovered when parsing a compiled
	 * class file.
	 *
	 * @param provider
	 * @param info Meta data about the method.
	 */
	public MethodCompletion(CompletionProvider provider, MethodInfo info) {

		super(provider, info.getName(), info.getReturnTypeString(false));
		setDefinedIn(info.getClassFile().getClassName(false));
		this.data = new MethodInfoData(info, (SourceCompletionProvider)provider);
		setRelevanceAppropriately();

		String[] paramTypes = info.getParameterTypes();
		List<Parameter> params = new ArrayList<Parameter>(paramTypes.length);
		for (int i=0; i<paramTypes.length; i++) {
			String name = ((MethodInfoData)data).getParameterName(i);
			String type = paramTypes[i].substring(paramTypes[i].lastIndexOf('.')+1);
			params.add(new Parameter(type, name));
		}
		setParams(params);

	}


	/**
	 * Overridden to compare methods by their comparison strings.
	 *
	 * @param c2 A <code>Completion</code> to compare to.
	 * @return The sort order.
	 */
	@Override
	public int compareTo(Completion c2) {

		int rc = -1;

		if (c2==this) {
			rc = 0;
		}

		else if (c2 instanceof MethodCompletion) {
			rc = getCompareString().compareTo(
					((MethodCompletion)c2).getCompareString());
		}

		else if (c2!=null) {
			rc = toString().compareToIgnoreCase(c2.toString());
			if (rc==0) { // Same text value
				String clazz1 = getClass().getName();
				clazz1 = clazz1.substring(clazz1.lastIndexOf('.'));
				String clazz2 = c2.getClass().getName();
				clazz2 = clazz2.substring(clazz2.lastIndexOf('.'));
				rc = clazz1.compareTo(clazz2);
			}
		}

		return rc;

	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MethodCompletion) &&
			//((MethodCompletion)obj).getSignature().equals(getSignature());
		((MethodCompletion)obj).getCompareString().equals(getCompareString());
	}


	@Override
	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = temp.lastIndexOf('.');
		if (lastDot>-1) {
			temp = temp.substring(lastDot+1);
		}
		return temp;
	}


	/**
	 * Returns a string used to compare this method completion to another.
	 *
	 * @return The comparison string.
	 */
	private String getCompareString() {

		/*
		 * This string compares the following parts of methods in this order,
		 * to optimize sort order in completion lists.
		 *
		 * 1. First, by name
		 * 2. Next, by number of parameters.
		 * 3. Finally, by parameter type.
		 */

		if (compareString==null) {
			StringBuilder sb = new StringBuilder(getName());
			// NOTE: This will fail if a method has > 99 parameters (!)
			int paramCount = getParamCount();
			if (paramCount<10) {
				sb.append('0');
			}
			sb.append(paramCount);
			for (int i=0; i<paramCount; i++) {
				String type = getParam(i).getType();
				sb.append(type);
				if (i<paramCount-1) { 
					sb.append(',');
				}
			}
			compareString = sb.toString();
		}

		return compareString;

	}


	public String getEnclosingClassName(boolean fullyQualified) {
		return data.getEnclosingClassName(fullyQualified);
	}


	@Override
	public Icon getIcon() {
		return IconFactory.get().getIcon(data);
	}


	public String getSignature() {
		return data.getSignature();
	}


	@Override
	public String getSummary() {

		String summary = data.getSummary(); // Could be just the method name

		// If it's the Javadoc for the method...
		if (summary!=null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	@Override
	public int hashCode() {
		return getCompareString().hashCode();
	}


	public boolean isDeprecated() {
		return data.isDeprecated();
	}


	/**
	 * {@inheritDoc}
	 */
	public void rendererText(Graphics g, int x, int y, boolean selected) {
		rendererText(this, g, x, y, selected);
	}


	/**
	 * Sets the relevance of this constructor based on its properties.
	 */
	private void setRelevanceAppropriately() {
		// Only change relevance from the default if this isn't a constructor.
		if (!data.isConstructor()) {
			setRelevance(NON_CONSTRUCTOR_RELEVANCE);
		}
	}


	/**
	 * Renders a member completion.
	 *
	 * @param mc
	 * @param g
	 * @param x
	 * @param y
	 * @param selected
	 */
	public static void rendererText(MemberCompletion mc, Graphics g, int x,
									int y, boolean selected) {

		String shortType = mc.getType();
		int dot = shortType.lastIndexOf('.');
		if (dot>-1) {
			shortType = shortType.substring(dot+1);
		}

		// Draw the method signature
		String sig = mc.getSignature();
		FontMetrics fm = g.getFontMetrics();
		g.drawString(sig, x, y);
		int newX = x + fm.stringWidth(sig);
		if (mc.isDeprecated()) {
			int midY = y + fm.getDescent() - fm.getHeight()/2;
			g.drawLine(x, midY, newX, midY);
		}
		x = newX;

		// Append the return type
		StringBuilder sb = new StringBuilder(" : ").append(shortType);
		sb.append(" - ");
		String s = sb.toString();
		g.drawString(s, x, y);
		x += fm.stringWidth(s);

		// Append the type of the containing class of this member.
		Color origColor = g.getColor();
		if (!selected) {
			g.setColor(Color.GRAY);
		}
		g.drawString(mc.getEnclosingClassName(false), x, y);
		if (!selected) {
			g.setColor(origColor);
		}

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getSignature();
	}


}