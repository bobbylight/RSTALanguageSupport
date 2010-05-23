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
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;


/**
 * Tree node for type declarations.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class TypeDeclarationTreeNode extends JavaTreeNode {


	public TypeDeclarationTreeNode(TypeDeclaration typeDec) {

		super(typeDec);
		//System.out.println("... " + typeDec);
		String iconName = null;

		if (typeDec instanceof NormalClassDeclaration) {
			NormalClassDeclaration ncd = (NormalClassDeclaration)typeDec;
			if (ncd.getModifiers()!=null) {
				if (ncd.getModifiers().isPublic()) {
					iconName = IconFactory.CLASS_ICON;
				}
				else if (ncd.getModifiers().isProtected()) {
					iconName = IconFactory.INNER_CLASS_PROTECTED_ICON;
				}
				else if (ncd.getModifiers().isPrivate()) {
					iconName = IconFactory.INNER_CLASS_PRIVATE_ICON;
				}
				else {
					iconName = IconFactory.INNER_CLASS_DEFAULT_ICON;
				}
			}
			else {
//System.out.println("...  " + value);
				iconName = IconFactory.DEFAULT_CLASS_ICON;
			}
		}
		else if (typeDec instanceof NormalInterfaceDeclaration) {
			NormalInterfaceDeclaration nid = (NormalInterfaceDeclaration)typeDec;
			if (nid.getModifiers()!=null && nid.getModifiers().isPublic()) {
				iconName = IconFactory.INTERFACE_ICON;
			}
			else {
				iconName = IconFactory.DEFAULT_INTERFACE_ICON;
			}
		}

		IconFactory fact = IconFactory.get();
		Icon mainIcon = fact.getIcon(iconName);

		if (mainIcon==null) { // Unknown type ???
			System.out.println("*** " + typeDec);
		}
		else {
			DecoratableIcon di = new DecoratableIcon(mainIcon);
			di.setDeprecated(typeDec.isDeprecated());
			Modifiers mods = typeDec.getModifiers();
			if (mods!=null) {
				if (mods.isAbstract()) {
					di.addDecorationIcon(fact.getIcon(IconFactory.ABSTRACT_ICON));
				}
				else if (mods.isFinal()) {
					di.addDecorationIcon(fact.getIcon(IconFactory.FINAL_ICON));
				}
				if (mods.isStatic()) {
					di.addDecorationIcon(fact.getIcon(IconFactory.STATIC_ICON));
				}
			}
			setIcon(di);
		}

	}


	public String getText(boolean selected) {
		TypeDeclaration typeDec = (TypeDeclaration)getUserObject();
		//System.out.println("... " + typeDec);
		return typeDec!=null ? typeDec.getName() : null;
	}


}