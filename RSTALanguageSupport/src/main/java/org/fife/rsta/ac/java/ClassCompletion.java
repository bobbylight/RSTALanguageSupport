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
import java.util.Iterator;
import javax.swing.Icon;

import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Completion for a Java class, interface or enum.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ClassCompletion extends AbstractJavaSourceCompletion {

	private ClassFile cf;


	ClassCompletion(CompletionProvider provider, ClassFile cf) {
		super(provider, cf.getClassName(false));
		this.cf = cf;
	}


	/*
	 * Fixed error when comparing classes of the same name, which did not allow
	 * classes with same name but different packages.
	 * Thanks to Guilherme Joao Frantz and Jonatas Schuler for the patch!
	 */
	@Override
	public int compareTo(Completion c2) {
		if (c2 == this) {
			return 0;
		}
		// Check for classes with same name, but in different packages
		else if (c2.toString().equalsIgnoreCase(toString())) {
			if (c2 instanceof ClassCompletion) {
				ClassCompletion cc2 = (ClassCompletion)c2;
				return getClassName(true).compareTo(cc2.getClassName(true));
			}
		}
		return super.compareTo(c2);
	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof ClassCompletion) &&
			((ClassCompletion)obj).getReplacementText().equals(getReplacementText());
	}


	/**
	 * Returns the name of the class represented by this completion.
	 *
	 * @param fullyQualified Whether the returned name should be fully
	 *        qualified.
	 * @return The class name.
	 * @see #getPackageName()
	 */
	public String getClassName(boolean fullyQualified){
		return cf.getClassName(fullyQualified);
	}


	@Override
	public Icon getIcon() {

		// TODO: Add functionality to ClassFile to make this simpler.

		boolean isInterface = false;
		boolean isPublic = false;
		//boolean isProtected = false;
		//boolean isPrivate = false;
		boolean isDefault = false;

		int access = cf.getAccessFlags();
		if ((access&AccessFlags.ACC_INTERFACE)>0) {
			isInterface = true;
		}

		else if (org.fife.rsta.ac.java.classreader.Util.isPublic(access)) {
			isPublic = true;
		}
		//else if (org.fife.rsta.ac.java.classreader.Util.isProtected(access)) {
		//	isProtected = true;
		//}
		//else if (org.fife.rsta.ac.java.classreader.Util.isPrivate(access)) {
		//	isPrivate = true;
		//}
		else {
			isDefault = true;
		}

		IconFactory fact = IconFactory.get();
		String key = null;

		if (isInterface) {
			if (isDefault) {
				key = IconFactory.DEFAULT_INTERFACE_ICON;
			}
			else {
				key = IconFactory.INTERFACE_ICON;
			}
		}
		else {
			if (isDefault) {
				key = IconFactory.DEFAULT_CLASS_ICON;
			}
			else if (isPublic) {
				key = IconFactory.CLASS_ICON;
			}
		}

		return fact.getIcon(key, cf.isDeprecated());

	}


	/**
	 * Returns the package this class or interface is in.
	 *
	 * @return The package, or <code>null</code> if it is not in a package.
	 * @see #getClassName(boolean)
	 */
	public String getPackageName() {
		return cf.getPackageName();
	}


	@Override
	public String getSummary() {

		SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
		SourceLocation  loc = scp.getSourceLocForClass(cf.getClassName(true));

		if (loc!=null) {

			CompilationUnit cu = Util.getCompilationUnitFromDisk(loc, cf);
			if (cu!=null) {
				Iterator<TypeDeclaration> i=cu.getTypeDeclarationIterator();
				while (i.hasNext()) {
					TypeDeclaration td = i.next();
					String typeName = td.getName();
					// Avoid inner classes, etc.
					if (typeName.equals(cf.getClassName(false))) {
						String summary = td.getDocComment();
						// Be cautious - might be no doc comment (or a bug?)
						if (summary!=null && summary.startsWith("/**")) {
							return Util.docCommentToHtml(summary);
						}
					}
				}
			}

		}

		// Default to the fully-qualified class name.
		return cf.getClassName(true);

	}


	@Override
	public String getToolTipText() {
		return "class " + getReplacementText();
	}


	@Override
	public int hashCode() {
		return getReplacementText().hashCode();
	}


	@Override
	public void rendererText(Graphics g, int x, int y, boolean selected) {

		String s = cf.getClassName(false);
		g.drawString(s, x, y);
		FontMetrics fm = g.getFontMetrics();
		int newX = x + fm.stringWidth(s);
		if (cf.isDeprecated()) {
			int midY = y + fm.getDescent() - fm.getHeight()/2;
			g.drawLine(x, midY, newX, midY);
		}
		x = newX;

		s = " - ";
		g.drawString(s, x, y);
		x += fm.stringWidth(s);

		String pkgName = cf.getClassName(true);
		int lastIndexOf = pkgName.lastIndexOf('.');
		if (lastIndexOf != -1) { // Class may not be in a package
			pkgName = pkgName.substring(0, lastIndexOf);
			Color origColor = g.getColor();
			if (!selected) {
				g.setColor(Color.GRAY);
			}
			g.drawString(pkgName, x, y);
			if (!selected) {
				g.setColor(origColor);
			}
		}

	}


}
