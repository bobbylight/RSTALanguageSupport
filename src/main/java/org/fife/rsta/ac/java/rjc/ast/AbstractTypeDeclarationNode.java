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
	private TypeDeclaration parentType;
	private List<TypeDeclaration> childTypes;
	private Offset bodyStartOffs;
	private Offset bodyEndOffs;
	private boolean deprecated;
	private String docComment;

	// --- "ClassBody"/"InterfaceBody"/EnumConstant fields ---
	private List<Member> memberList;


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


	@Override
	public void addTypeDeclaration(TypeDeclaration type) {
		if (childTypes==null) {
			childTypes = new ArrayList<>(1); // Usually small
		}
		type.setParentType(this);
		childTypes.add(type);
	}


	@Override
	public boolean getBodyContainsOffset(int offs) {
		return offs>=getBodyStartOffset() && offs<getBodyEndOffset();
	}


	@Override
	public int getBodyEndOffset() {
		return bodyEndOffs!=null ? bodyEndOffs.getOffset() : Integer.MAX_VALUE;
	}


	@Override
	public int getBodyStartOffset() {
		return bodyStartOffs==null ? 0 : bodyStartOffs.getOffset();
	}


	@Override
	public TypeDeclaration getChildType(int index) {
		return childTypes.get(index);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
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


	@Override
	public int getChildTypeCount() {
		return childTypes==null ? 0 : childTypes.size();
	}


	@Override
	public String getDocComment() {
		return docComment;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Field> getFieldIterator() {
		List<Field> fields = new ArrayList<>();
		for (Iterator<Member> i=getMemberIterator(); i.hasNext(); ) {
			Member member = i.next();
			if (member instanceof Field) {
				fields.add((Field)member);
			}
		}
		return fields.iterator();
	}


	@Override
	public Member getMember(int index) {
		return memberList.get(index);
	}


	@Override
	public int getMemberCount() {
		return memberList.size();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Member> getMemberIterator() {
		return memberList.iterator();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Method> getMethodIterator() {
		List<Method> methods = new ArrayList<>();
		for (Iterator<Member> i=getMemberIterator(); i.hasNext(); ) {
			Member member = i.next();
			if (member instanceof Method) {
				methods.add((Method)member);
			}
		}
		return methods.iterator();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Method> getMethodsByName(String name) {
		List<Method> methods = new ArrayList<>();
		for (Iterator<Member> i=getMemberIterator(); i.hasNext(); ) {
			Member member = i.next();
			if (member instanceof Method && name.equals(member.getName())) {
				methods.add((Method)member);
			}
		}
		return methods;
	}


	@Override
	public Modifiers getModifiers() {
		return modifiers;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName(boolean fullyQualified) {
		String name = getName();
		if (fullyQualified) {
			Package pkg = getPackage();
			if (pkg!=null) {
				name = pkg.getName() + "." + name;
			}
		}
		return name;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Package getPackage() {
		return pkg;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDeclaration getParentType() {
		return parentType;
	}


	private void init() {
		memberList = new ArrayList<>();
	}


	@Override
	public boolean isDeprecated() {
		return deprecated;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStatic() {
		return modifiers==null ? false : modifiers.isStatic();
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


	@Override
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


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentType(TypeDeclaration parentType) {
		this.parentType = parentType;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (modifiers!=null) {
			sb.append(modifiers.toString()).append(' ');
		}
		sb.append(getTypeString()).append(' ');
		sb.append(getName());
		return sb.toString();
	}


}
