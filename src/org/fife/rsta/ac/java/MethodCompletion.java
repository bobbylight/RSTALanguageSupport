/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
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
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion;

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
	 * @param typeName
	 */
	public MethodCompletion(CompletionProvider provider, Method m,
							String typeName) {

		// NOTE: "void" might not be right - I think this might be constructors
		super(provider, m.getName(), m.getType()==null ? "void" : m.getType().toString());
		setDefinedIn(typeName);
		this.data = new MethodData(m);
		setRelevanceAppropriately();

		int count = m.getParameterCount();
		List params = new ArrayList(count);
		for (int i=0; i<count; i++) {
			FormalParameter param = m.getParameter(i);
			Type type = param.getType();
			String name = param.getName();
			params.add(new ParameterizedCompletion.Parameter(type, name));
		}
		setParams(params);

	}


	/**
	 * Creates a completion for a method discovered when parsing a compiled
	 * class file.
	 *
	 * @param provider
	 * @param info Meta data about the method.
	 * @param typeName
	 */
	public MethodCompletion(CompletionProvider provider, MethodInfo info,
							String typeName) {

		super(provider, info.getName(), info.getReturnTypeString());
		setDefinedIn(typeName);
		this.data = new MethodInfoData(info, (SourceCompletionProvider)provider);
		setRelevanceAppropriately();

		String[] paramTypes = info.getParameterTypes();
		List params = new ArrayList(paramTypes.length);
		for (int i=0; i<paramTypes.length; i++) {
			String name = ((MethodInfoData)data).getParameterName(i);
			String type = paramTypes[i].substring(paramTypes[i].lastIndexOf('.')+1);
			params.add(new ParameterizedCompletion.Parameter(type, name));
		}
		setParams(params);

	}


	public boolean equals(Object obj) {
		return (obj instanceof MethodCompletion) &&
			((MethodCompletion)obj).getSignature().equals(getSignature());
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = temp.lastIndexOf('.');
		if (lastDot>-1) {
			temp = temp.substring(lastDot+1);
		}
		return temp;
	}


	public Icon getIcon() {
		return IconFactory.get().getIcon(data);
	}


	public String getSignature() {
		return data.getSignature();
	}


	public String getSummary() {

		String summary = data.getSummary(); // Could be just the method name

		// If it's the Javadoc for the method...
		if (summary!=null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;
	}


	public int hashCode() {
		return getSignature().hashCode();
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
	 * {@inheritDoc}
	 */
	public String toString() {
		return getSignature();
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
		StringBuffer sb = new StringBuffer(" : ").append(shortType);
		sb.append(" - ");
		String s = sb.toString();
		g.drawString(s, x, y);
		x += fm.stringWidth(s);

		// Append the type of the containing class of this member.
		Color origColor = g.getColor();
		if (!selected) {
			g.setColor(Color.GRAY);
		}
		g.drawString(org.fife.rsta.ac.java.Util.getUnqualified(mc.getDefinedIn()), x, y);
		if (!selected) {
			g.setColor(origColor);
		}

	}


}