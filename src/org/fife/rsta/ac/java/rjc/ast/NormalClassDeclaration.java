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
package org.fife.rsta.ac.java.rjc.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lexer.Scanner;


/**
 * A class declaration:
 * 
 * <pre>
 * NormalClassDeclaration:
 *    'class' Identifier [TypeParameters] ['extends' Type] ['implements' TypeList] ClassBody
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class NormalClassDeclaration extends AbstractTypeDeclarationNode {

	// --- "NormalClassDeclaration" fields ---
	private List typeParams;
	private Type extendedType;
	private List implementedList;


	public NormalClassDeclaration(Scanner s, int offs, String className) {
		super(className, s.createOffset(offs), s.createOffset(offs+className.length()));
		implementedList = new ArrayList(0); // Usually not many
		// If parsing java.lang.Object.java, setExtendedType(null) should be
		// called.  This is here for all other classes without an explicit
		// super class declared.
		extendedType = new Type("java.lang.Object");
	}


	public void addImplemented(Type implemented) {
		implementedList.add(implemented);
	}


	/**
	 * {@inheritDoc}
	 */
	public List getAccessibleMembersOfType(String type, int offs) {

		if (!getBodyContainsOffset(offs)) {
			return null;
		}

		List members = new ArrayList();

		// First, if the offset is in a method, get any local variables in
		// that method.
		Method method = getMethodContainingOffset(offs);
		if (method!=null) {

			// Parameters to the method
			for (Iterator i=method.getParameterIterator(); i.hasNext(); ) {
				FormalParameter param = (FormalParameter)i.next();
				Type paramType = param.getType();
				if (isTypeCompatible(paramType, type)) {
					members.add(param.getName());
				}
			}

			// Local variables in the method
			CodeBlock body = method.getBody();
			if (body!=null) { // Should always be true?
				CodeBlock block = body.getDeepestCodeBlockContaining(offs);
				List vars = block.getLocalVarsBefore(offs);
				for (Iterator i=vars.iterator(); i.hasNext(); ) {
					LocalVariable var = (LocalVariable)i.next();
					Type varType = var.getType();
					if (isTypeCompatible(varType, type)) {
						members.add(var.getName());
					}
				}
			}

		}

		// Next, any fields/getters taking no parameters (for simplicity)
		// in this class.
		for (Iterator i=getMemberIterator(); i.hasNext(); ) {

			Member member = (Member)i.next();

			if (member instanceof Field) {
				Type fieldType = member.getType();
				if (isTypeCompatible(fieldType, type)) {
					members.add(member.getName());
				}
			}
			else { // Method
				method = (Method)member;
				if (method.getParameterCount()==0 &&
						method.getName().startsWith("get")) {
					if (isTypeCompatible(method.getType(), type)) {
						members.add(member.getName() + "()");
					}
				}
			}

		}

		// Finally, any public or protected fields/getters in super-classes.
		// TODO: Implement me

		return members;

	}


	public Type getExtendedType() {
		return extendedType;
	}


	public int getImplementedCount() {
		return implementedList.size();
	}


	public Iterator getImplementedIterator() {
		return implementedList.iterator();
	}


	/**
	 * Gets the method in this class that contains a given offset.
	 *
	 * @param offs The offset.
	 * @return The method containing the offset, or <code>null</code> if no
	 *         method in this class contains the offset.
	 */
	public Method getMethodContainingOffset(int offs) {
		for (Iterator i=getMethodIterator(); i.hasNext(); ) {
			Method method = (Method)i.next();
			if (method.getBodyContainsOffset(offs)) {
				return method;
			}
		}
		return null;
	}


	public List getTypeParameters() {
		return typeParams;
	}


	public String getTypeString() {
		return "class";
	}


	/**
	 * Returns whether a <code>Type</code> and a type name are type
	 * compatible.  This method currently is a sham!
	 *
	 * @param type
	 * @param typeName
	 * @return
	 */
	// TODO: Get me working!  Probably need better parameters passed in!!!
	private boolean isTypeCompatible(Type type, String typeName) {

		String typeName2 = type.getName(false);

		// Remove generics info for now
		// TODO: Handle messy generics cases
		int lt = typeName2.indexOf('<');
		if (lt>-1) {
			String arrayDepth = null;
			int brackets = typeName2.indexOf('[', lt);
			if (brackets>-1) {
				arrayDepth = typeName2.substring(brackets);
			}
			typeName2 = typeName2.substring(lt);
			if (arrayDepth!=null) {
				typeName2 += arrayDepth;
			}
		}

		return typeName2.equalsIgnoreCase(typeName);

	}


	public void setExtendedType(Type type) {
		extendedType = type;
	}


	public void setTypeParameters(List typeParams) {
		this.typeParams = typeParams;
	}


}