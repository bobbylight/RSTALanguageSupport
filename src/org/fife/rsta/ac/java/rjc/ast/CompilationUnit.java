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
package org.fife.rsta.ac.java.rjc.ast;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Annotation;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.Token;
import org.fife.rsta.ac.java.rjc.notices.ParserNotice;


/**
 * A <code>CompilationUnit</code> is the root node of an AST for a Java
 * source file.
 *
 * <pre>
 * CompilationUnit:
 *    [[Annotations] 'package' QualifiedIdentifier ';' ] {ImportDeclaration} {TypeDeclaration}
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CompilationUnit extends AbstractASTNode
							implements TypeDeclarationContainer {

	private List<Annotation> annotations;
	private Package pkg;
	private List<ImportDeclaration> imports;
	private List<TypeDeclaration> typeDeclarations;
	private List<ParserNotice> notices;

	private static final Offset ZERO_OFFSET = new ZeroOffset();


	public CompilationUnit(String name) {
		super(name, ZERO_OFFSET);
		imports = new ArrayList<ImportDeclaration>(3); // Usually not many,
		typeDeclarations = new ArrayList<TypeDeclaration>(1); // Usually only 1
	}


	public void addImportDeclaration(ImportDeclaration dec) {
		imports.add(dec);
	}


	/**
	 * Shorthand for "<tt>addParserNotice(new ParserNotice(t, msg))</tt>".
	 *
	 * @param t
	 * @param msg
	 */
	public void addParserNotice(Token t, String msg) {
		addParserNotice(new ParserNotice(t, msg));
	}


	public void addParserNotice(ParserNotice notice) {
		if (notices==null) {
			notices = new ArrayList<ParserNotice>();
			notices.add(notice);
		}
	}


	public void addTypeDeclaration(TypeDeclaration typeDec) {
		typeDeclarations.add(typeDec);
	}


	public int getAnnotationCount() {
		return annotations.size();
	}


	public Iterator<Annotation> getAnnotationIterator() {
		return annotations.iterator();
	}


	/**
	 * Returns the deepest-nested type declaration that contains a given
	 * offset.
	 *
	 * @param offs The offset.
	 * @return The deepest-nested type declaration containing the offset, or
	 *         <code>null</code> if the offset is outside of any type
	 *         declaration (such as in the import statements, etc.).
	 * @see #getTypeDeclarationAtOffset(int)
	 */
	public TypeDeclaration getDeepestTypeDeclarationAtOffset(int offs) {

		TypeDeclaration td = getTypeDeclarationAtOffset(offs);

		if (td!=null) {
			TypeDeclaration next = td.getChildTypeAtOffset(offs);
			while (next!=null) {
				td = next;
				next = td.getChildTypeAtOffset(offs);
			}
		}

		return td;

	}


	/**
	 * TODO: Return range for more instances than just class methods.
	 * Also handle child TypeDeclarations.
	 *
	 * @param offs
	 * @return The starting and ending offset of the enclosing method range.
	 */
	public Point getEnclosingMethodRange(int offs) {

		Point range = null;

		for (Iterator<TypeDeclaration> i=getTypeDeclarationIterator(); i.hasNext(); ) {

			TypeDeclaration td = i.next();
			int start = td.getBodyStartOffset();
			int end = td.getBodyEndOffset();

			if (offs>=start && offs<=end) {

				if (td instanceof NormalClassDeclaration) {
					NormalClassDeclaration ncd = (NormalClassDeclaration)td;
					for (Iterator<Member> j=ncd.getMemberIterator(); j.hasNext(); ) {
						Member m = j.next();
						if (m instanceof Method) {
							Method method = (Method)m;
							CodeBlock body = method.getBody();
							if (body!=null) {
								int start2 = method.getNameStartOffset();
								//int start2 = body.getStartOffset();
								int end2 = body.getNameEndOffset();
								if (offs>=start2 && offs<=end2) {
									range = new Point(start2, end2);
									break;
								}
							}
						}
					}
				}

				if (range==null) { // Default to the entire class' body.
					range = new Point(start, end);
				}

			}

		}

		return range;

	}


	public int getImportCount() {
		return imports.size();
	}


	/**
	 * Returns the import declarations of this compilation unit.  This is a
	 * copy of the list of imports, but the actual individual
	 * {@link ImportDeclaration}s are not copies, so modifying them will modify
	 * this compilation unit!
	 *
	 * @return A list or imports, or an empty list if there are none.
	 */
	public List<ImportDeclaration> getImports() {
		return new ArrayList<ImportDeclaration>(imports);
	}


	public Iterator<ImportDeclaration> getImportIterator() {
		return imports.iterator();
	}


	/**
	 * Returns the package of this compilation unit.
	 *
	 * @return The package of this compilation unit, or <code>null</code> if
	 *         this compilation unit is not in a package.
	 * @see #getPackageName()
	 */
	public Package getPackage() {
		return pkg;
	}


	/**
	 * Returns the fully-qualified package name of this compilation unit.
	 *
	 * @return The package name, or <code>null</code> if this compilation unit
	 *         is not in a package (in the default package).
	 * @see #getPackage()
	 */
	public String getPackageName() {
		return pkg==null ? null : pkg.getName();
	}


	public ParserNotice getParserNotice(int index) {
		if (notices==null) {
			throw new IndexOutOfBoundsException("No parser notices available");
		}
		return notices.get(index);
	}


	public int getParserNoticeCount() {
		return notices==null ? 0 : notices.size();
	}


	public TypeDeclaration getTypeDeclaration(int index) {
		return typeDeclarations.get(index);
	}


	/**
	 * Returns the type declaration in this file that contains the specified
	 * offset.
	 *
	 * @param offs The offset.
	 * @return The type declaration, or <code>null</code> if the offset is
	 *         outside of any type declaration.
	 * @see #getDeepestTypeDeclarationAtOffset(int)
	 */
	public TypeDeclaration getTypeDeclarationAtOffset(int offs) {

		TypeDeclaration typeDec = null;

		for (TypeDeclaration td : typeDeclarations) {
			if (td.getBodyContainsOffset(offs)) {
				typeDec = td;
				break;
			}
		}

		return typeDec;

	}


	public int getTypeDeclarationCount() {
		return typeDeclarations.size();
	}


	public Iterator<TypeDeclaration> getTypeDeclarationIterator() {
		return typeDeclarations.iterator();
	}


	public void setPackage(Package pkg) {
		this.pkg = pkg;
	}


	/**
	 * An offset that always returns 0.
	 */
	private static class ZeroOffset implements Offset {

		public int getOffset() {
			return 0;
		}

	}


}