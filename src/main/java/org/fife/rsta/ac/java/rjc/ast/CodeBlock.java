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
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Offset;
import org.fife.rsta.ac.java.rjc.lexer.TokenTypes;


/**
 * A block of code in curly braces in a class.<p>
 * 
 * This class implements the <code>Member</code> interface because a block
 * can be a member (say, a static block in a class declaration), but usually
 * it's not actually a <code>Member</code>, but something else, e.g. the body
 * of a method, or the content of an <code>if</code>-statement, etc.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class CodeBlock extends AbstractMember {

	/**
	 * The name of all <code>CodeBlock</code>s.
	 */
	public static final String NAME		= "{...}";

	private CodeBlock parent;
	private List<CodeBlock> children;
	private List<LocalVariable> localVars;
	private boolean isStatic;


	public CodeBlock(boolean isStatic, Offset startOffs) {
		super(NAME, startOffs);
		this.isStatic = isStatic;
	}


	public void add(CodeBlock child) {
		if (children==null) {
			children = new ArrayList<>();
		}
		children.add(child);
		child.setParent(this);
	}


	public void addLocalVariable(LocalVariable localVar) {
		if (localVars==null) {
			localVars = new ArrayList<>();
		}
		localVars.add(localVar);
	}


	public boolean containsOffset(int offs) {
		// Do endOffset first since we'll often iterate from first CodeBlock
		// to last, so checking it first should be faster.
		return getNameEndOffset()>=offs && getNameStartOffset()<=offs;
	}


	public CodeBlock getChildBlock(int index) {
		return children.get(index);
	}


	public int getChildBlockCount() {
		return children==null ? 0 : children.size();
	}


	/**
	 * Returns the deepest code block nested under this one (or this one
	 * itself) containing a given offset.
	 *
	 * @param offs The offset to look for.
	 * @return The deepest-nested code block containing the offset, or
	 *         <code>null</code> if this code block and none of its children
	 *         contain the offset.
	 */
	public CodeBlock getDeepestCodeBlockContaining(int offs) {
		if (!containsOffset(offs)) {
			return null;
		}
		for (int i=0; i<getChildBlockCount(); i++) {
			CodeBlock child = getChildBlock(i);
			if (child.containsOffset(offs)) {
				return child.getDeepestCodeBlockContaining(offs);
			}
		}
		return this;
	}


	@Override
	public String getDocComment() {
		// TODO: Can static blocks have doc comments?
		return null;
	}


	public LocalVariable getLocalVar(int index) {
		return localVars.get(index);
	}


	public int getLocalVarCount() {
		return localVars==null ? 0 : localVars.size();
	}


	/**
	 * Returns all local variables declared before a given offset, both in
	 * this code block and in all parent blocks.
	 *
	 * @param offs The offset.
	 * @return The {@link LocalVariable}s, or an empty list of none were
	 *         declared before the offset.
	 */
	public List<LocalVariable> getLocalVarsBefore(int offs) {

		List<LocalVariable> vars = new ArrayList<>();

		if (localVars!=null) {
			for (int i=0; i<getLocalVarCount(); i++) {
				LocalVariable localVar = getLocalVar(i);
				if (localVar.getNameStartOffset()<offs) {
					vars.add(localVar);
				}
				else {
					break;
				}
			}
		}

		if (parent!=null) {
			vars.addAll(parent.getLocalVarsBefore(offs));
		}

		return vars;

	}


	@Override
	public Modifiers getModifiers() {
		Modifiers modifiers = new Modifiers();
		if (isStatic) {
			modifiers.addModifier(TokenTypes.KEYWORD_STATIC);
		}
		return modifiers;
	}


	public CodeBlock getParent() {
		return parent;
	}


	/**
	 * Returns <code>null</code>, since blocks don't have types.
	 *
	 * @return <code>null</code> always.
	 */
	@Override
	public Type getType() {
		return null;
	}


	@Override
	public boolean isDeprecated() {
		return false;
	}


	/**
	 * Returns whether this block is a static block (in a class declaration).
	 *
	 * @return Whether this is a static code block.
	 */
	@Override
	public boolean isStatic() {
		return isStatic;
	}


	public void setParent(CodeBlock parent) {
		this.parent = parent;
	}


}
