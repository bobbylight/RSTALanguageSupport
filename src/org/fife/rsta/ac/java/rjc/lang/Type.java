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
package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.List;



/**
 * A type.
 *
 * <pre>
 * Type:
 *    Identifier [TypeArguments] { . Identifier [TypeArguments] } {[]}
 *    BasicType
 * </pre>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Type {

	private List identifiers;
	private List typeArguments;
	private int bracketPairCount;


	public Type() {
		identifiers = new ArrayList(1);
		typeArguments = new ArrayList(1);
	}


	public Type(String identifier) {
		this();
		addIdentifier(identifier, null);
	}


	public Type(String identifier, int bracketPairCount) {
		this();
		addIdentifier(identifier, null);
		setBracketPairCount(bracketPairCount);
	}


	/**
	 * Adds an identifier to this type.
	 *
	 * @param identifier The identifier.
	 * @param typeArgs The type arguments for the identifier.  This may be
	 *        <code>null</code> or an empty list if there are none.
	 */
	public void addIdentifier(String identifier, List typeArgs) {
		identifiers.add(identifier);
		typeArguments.add(typeArgs);
	}


	public void incrementBracketPairCount(int count) {
		bracketPairCount += count;
	}


	/**
	 * Returns whether this type is an array.
	 *
	 * @return Whether this type is an array.
	 */
	public boolean isArray() {
		return bracketPairCount>0;
	}


	public boolean isBasicType() {
		boolean basicType = false;
		if (!isArray() && identifiers.size()==1 && typeArguments.get(0)==null) {
			String str = (String)identifiers.get(0);
			basicType = "byte".equals(str) ||
						"float".equals(str) ||
						"double".equals(str) ||
						"int".equals(str) ||
						"short".equals(str) ||
						"long".equals(str) ||
						"boolean".equals(str);
		}
		return basicType;
	}


	/*
	 * MethodDeclaratorRest allows bracket pairs after its FormalParameters,
	 * which increment the array depth of the return type.
	 */
	public void setBracketPairCount(int count) {
		bracketPairCount = count;
	}


	public String toString() {

		StringBuffer sb = new StringBuffer();

		int count = identifiers.size();
		for (int i=0; i<count; i++) {
			sb.append(identifiers.get(i).toString());
			if (typeArguments.get(i)!=null) {
				List typeArgs = (List)typeArguments.get(i);
				int typeArgCount = typeArgs.size();
				if (typeArgCount>0) {
					sb.append('<');
					for (int j=0; j<typeArgCount; j++) {
						TypeArgument typeArg = (TypeArgument)typeArgs.get(j);
						//if (typeA)
						sb.append(typeArg.toString());
						if (j<typeArgCount-1) {
							sb.append(", ");
						}
					}
					sb.append('>');
				}
			}
			if (i<count-1) {
				sb.append('.');
			}
		}

		for (int i=0; i<bracketPairCount; i++) {
			sb.append("[]");
		}

		return sb.toString();

	}


}