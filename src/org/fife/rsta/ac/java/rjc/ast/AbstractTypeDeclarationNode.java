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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lexer.Offset;


public abstract class AbstractTypeDeclarationNode extends AbstractASTNode
												implements TypeDeclaration {

	private Package pkg;
	private Modifiers modifiers;
	private List childTypes;
	private Offset bodyStartOffs;
	private Offset bodyEndOffs;
	private boolean deprecated;
	private String docComment;

	// --- "ClassBody"/"InterfaceBody"/EnumConstant fields ---
	private List memberList;


	public AbstractTypeDeclarationNode(String name, Offset start) {
		super(name, start);
		init();
	}


	public AbstractTypeDeclarationNode(String name, Offset start, Offset end) {
		super(name, start, end);
		init();
	}


	public void addMember(Member member) {
		member.setParentTypeDeclaration(this);
		memberList.add(member);
	}


	public void addTypeDeclaration(TypeDeclaration type) {
		if (childTypes==null) {
			childTypes = new ArrayList(1); // Usually small
		}
		childTypes.add(type);
	}


	public boolean getBodyContainsOffset(int offs) {
		return offs>=getBodyStartOffset() && offs<getBodyEndOffset();
	}


	public int getBodyEndOffset() {
		return bodyEndOffs!=null ? bodyEndOffs.getOffset() : Integer.MAX_VALUE;
	}


	public int getBodyStartOffset() {
		return bodyStartOffs==null ? 0 : bodyStartOffs.getOffset();
	}


	public TypeDeclaration getChildType(int index) {
		return (TypeDeclaration)childTypes.get(index);
	}


	/**
	 * {@inheritDoc}
	 */
	public TypeDeclaration getChildTypeAtOffset(int offs) {

		TypeDeclaration typeDec = null;

		for (int i=0; i<getChildTypeCount(); i++) {
			TypeDeclaration td = getChildType(i);
			if (td.getBodyContainsOffset(offs)) {
				typeDec = td;
				break;
			}
		}

		return typeDec;

	}


	public int getChildTypeCount() {
		return childTypes==null ? 0 : childTypes.size();
	}


	public String getDocComment() {
		return docComment;
	}


	/**
	 * {@inheritDoc}
	 */
	public Iterator getFieldIterator() {
		List fields = new ArrayList();
		for (Iterator i=getMemberIterator(); i.hasNext(); ) {
			Member member = (Member)i.next();
			if (member instanceof Field) {
				fields.add(member);
			}
		}
		return fields.iterator();
	}


	public Member getMember(int index) {
		return (Member)memberList.get(index);
	}


	public int getMemberCount() {
		return memberList.size();
	}


	/**
	 * {@inheritDoc}
	 */
	public Iterator getMemberIterator() {
		return memberList.iterator();
	}


	/**
	 * {@inheritDoc}
	 */
	public Iterator getMethodIterator() {
		List methods = new ArrayList();
		for (Iterator i=getMemberIterator(); i.hasNext(); ) {
			Member member = (Member)i.next();
			if (member instanceof Method) {
				methods.add(member);
			}
		}
		return methods.iterator();
	}


	/**
	 * {@inheritDoc}
	 */
	public List getMethodsByName(String name) {
		List methods = new ArrayList();
		for (Iterator i=getMemberIterator(); i.hasNext(); ) {
			Member member = (Member)i.next();
			if (member instanceof Method && name.equals(member.getName())) {
				methods.add(member);
			}
		}
		return methods;
	}


	public Modifiers getModifiers() {
		return modifiers;
	}


	/**
	 * {@inheritDoc}
	 */
	public Package getPackage() {
		return pkg;
	}


	private void init() {
		memberList = new ArrayList();
	}


	public boolean isDeprecated() {
		return deprecated;
	}


	public void setBodyEndOffset(Offset end) {
		bodyEndOffs = end;
	}


	public void setBodyStartOffset(Offset start) {
		bodyStartOffs = start;
	}


	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}


	public void setDocComment(String comment) {
		docComment = comment;
	}


	public void setModifiers(Modifiers modifiers) {
		this.modifiers = modifiers;
	}


	/**
	 * Sets the package this type is in.
	 *
	 * @param pkg The package, or <code>null</code> if this is in the
	 *        default package.
	 * @see #getPackage()
	 */
	public void setPackage(Package pkg) {
		this.pkg = pkg;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (modifiers!=null) {
			sb.append(modifiers.toString()).append(' ');
		}
		sb.append(getTypeString()).append(' ');
		sb.append(getName());
		return sb.toString();
	}


}