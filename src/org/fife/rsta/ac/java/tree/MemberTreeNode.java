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
package org.fife.rsta.ac.java.tree;

import javax.swing.Icon;

import org.fife.rsta.ac.java.DecoratableIcon;
import org.fife.rsta.ac.java.IconFactory;
import org.fife.rsta.ac.java.rjc.ast.CodeBlock;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;


/**
 * Tree node for a field or method.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MemberTreeNode extends JavaTreeNode {

	private String text;


	public MemberTreeNode(CodeBlock cb) {
		super(cb);
		text = "<html>" + cb.getName();
		IconFactory fact = IconFactory.get();
		Icon base = fact.getIcon(IconFactory.METHOD_PRIVATE_ICON);
		DecoratableIcon di = new DecoratableIcon(base);
		int priority = PRIORITY_METHOD;
		if (cb.isStatic()) {
			di.addDecorationIcon(fact.getIcon(IconFactory.STATIC_ICON));
			priority += PRIORITY_BOOST_STATIC;
		}
		setIcon(di);
		setSortPriority(priority);
	}

 
	public MemberTreeNode(Field field) {

		super(field);

		Modifiers mods = field.getModifiers();
		String icon = null;

		if (mods==null) {
			icon = IconFactory.FIELD_DEFAULT_ICON;
		}
		else if (mods.isPrivate()) {
			icon = IconFactory.FIELD_PRIVATE_ICON;
		}
		else if (mods.isProtected()) {
			icon = IconFactory.FIELD_DEFAULT_ICON;
		}
		else if (mods.isPublic()) {
			icon = IconFactory.FIELD_PUBLIC_ICON;
		}
		else {
			icon = IconFactory.FIELD_DEFAULT_ICON;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append(field.getName());
		sb.append(" : ");
		sb.append("<font color='#888888'>");

		appendType(field.getType(), sb);
		text = sb.toString();
		int priority = PRIORITY_FIELD;

		IconFactory fact = IconFactory.get();
		Icon base = fact.getIcon(icon);
		DecoratableIcon di = new DecoratableIcon(base);
		di.setDeprecated(field.isDeprecated());
		if (mods!=null) {
			if (mods.isStatic()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.STATIC_ICON));
				priority += PRIORITY_BOOST_STATIC;
			}
			if (mods.isFinal()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.FINAL_ICON));
			}
		}
		setIcon(di);

		setSortPriority(priority);

	}


	public MemberTreeNode(Method method) {

		super(method);

		String icon = null;
		int priority = PRIORITY_METHOD;

		Modifiers mods = method.getModifiers();
		if (mods==null) {
			icon = IconFactory.METHOD_DEFAULT_ICON;
		}
		else if (mods.isPrivate()) {
			icon = IconFactory.METHOD_PRIVATE_ICON;
		}
		else if (mods.isProtected()) {
			icon = IconFactory.METHOD_PROTECTED_ICON;
		}
		else if (mods.isPublic()) {
			icon = IconFactory.METHOD_PUBLIC_ICON;
		}
		else {
			icon = IconFactory.METHOD_DEFAULT_ICON;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append(method.getName());
		sb.append('(');
		int paramCount = method.getParameterCount();
		for (int i=0; i<paramCount; i++) {
			FormalParameter param = method.getParameter(i);
			appendType(param.getType(), sb);
			if (i<paramCount-1) {
				sb.append(", ");
			}
		}
		sb.append(')');
		if (method.getType()!=null) {
			sb.append(" : ");
			sb.append("<font color='#888888'>");
			appendType(method.getType(), sb);
		}

		text = sb.toString();

		IconFactory fact = IconFactory.get();
		Icon base = fact.getIcon(icon);
		DecoratableIcon di = new DecoratableIcon(base);
		di.setDeprecated(method.isDeprecated());
		if (mods!=null) {
			if (mods.isAbstract()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.ABSTRACT_ICON));
			}
			if (method.isConstructor()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.CONSTRUCTOR_ICON));
				priority = PRIORITY_CONSTRUCTOR; // Overrides previous value
			}
			if (mods.isStatic()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.STATIC_ICON));
				priority += PRIORITY_BOOST_STATIC;
			}
			if (mods.isFinal()) {
				di.addDecorationIcon(fact.getIcon(IconFactory.FINAL_ICON));
			}
		}
		setIcon(di);

		setSortPriority(priority);

	}


	static void appendType(Type type, StringBuffer sb) {
		if (type!=null) {
			String t = type.toString();
			t = t.replaceAll("<", "&lt;");
			t = t.replaceAll(">", "&gt;");
			sb.append(t);
		}
	}


	public String getText(boolean selected) {
		// Strip out HTML tags
		return selected ? text.replaceAll("<[^>]*>", "").
				replaceAll("&lt;", "<").replaceAll("&gt;", ">") : text;
	}


}