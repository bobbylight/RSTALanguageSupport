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
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.Icon;

import org.fife.rsta.ac.java.classreader.AccessFlags;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;
import org.fife.rsta.ac.java.rjc.parser.ASTFactory;
import org.fife.ui.autocomplete.CompletionProvider;


/**
 * Completion for a Java class or interface.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class ClassCompletion extends AbstractJavaSourceCompletion {

	private ClassFile cf;


	public ClassCompletion(CompletionProvider provider, ClassFile cf) {
		super(provider, cf.getClassName(false));
		this.cf = cf;
	}


	public boolean equals(Object obj) {
		return (obj instanceof ClassCompletion) &&
			((ClassCompletion)obj).getReplacementText().equals(getReplacementText());
	}


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
//		else if (org.fife.rsta.ac.java.classreader.Util.isProtected(access)) {
//			isProtected = true;
//		}
//		else if (org.fife.rsta.ac.java.classreader.Util.isPrivate(access)) {
//			isPrivate = true;
//		}
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

		return fact.getIcon(key);

	}


	public String getSummary() {

		SourceCompletionProvider scp = (SourceCompletionProvider)getProvider();
		File loc = scp.getSourceLocForClass(cf.getClassName(true));

		if (loc!=null) {

			if (loc.isFile() && loc.getName().endsWith(".zip")) {
				try {
					return getSummaryFromSourceZip(loc);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}

		}

		// Default to the fully-qualified class name.
		return cf.getClassName(true);

	}

private String getSummaryFromSourceZip(File zip) throws IOException {

	String summary = null;

	ZipFile zipFile = new ZipFile(zip);
	String entryName = cf.getClassName(true).replaceAll("\\.", "/");
	entryName += ".java";
	//System.out.println("DEBUG: entry name: " + entryName);
	ZipEntry entry = zipFile.getEntry(entryName);

	if (entry!=null) {
		InputStream in = zipFile.getInputStream(entry);
		Scanner s = new Scanner(new InputStreamReader(in));
		CompilationUnit cu = new ASTFactory().getCompilationUnit(entryName, s);
		for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); ) {
			TypeDeclaration td = (TypeDeclaration)i.next();
			String typeName = td.getName();
			// Avoid inner classes, etc.
			if (typeName.equals(cf.getClassName(false))) {
				summary = td.getDocComment();
				// Be cautious - might be no doc comment (or a bug?)
				if (summary!=null && summary.startsWith("/**")) {
					summary = Util.docCommentToHtml(summary);
					break;
				}
			}
		}
	}

	zipFile.close(); // Closes input streams from getInputStream()

	return summary;

}


	public String getToolTipText() {
		return "class " + getReplacementText();
	}


	public int hashCode() {
		return getReplacementText().hashCode();
	}


	public void rendererText(Graphics g, int x, int y, boolean selected) {

		StringBuffer sb = new StringBuffer();
		sb.append(cf.getClassName(false));
		sb.append(" - ");
		String s = sb.toString();
		g.drawString(s, x, y);
		x += g.getFontMetrics().stringWidth(s);

		Color origColor = g.getColor();
		if (!selected) {
			g.setColor(Color.GRAY);
		}
		String pkgName = cf.getClassName(true);
		pkgName = pkgName.substring(0, pkgName.lastIndexOf('.'));
		g.drawString(pkgName, x, y);
		if (!selected) {
			g.setColor(origColor);
		}

	}


}