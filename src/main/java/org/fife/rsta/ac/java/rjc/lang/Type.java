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

	private List<String> identifiers;
	private List<List<TypeArgument>> typeArguments;
	private int bracketPairCount;


	public Type() {
		identifiers = new ArrayList<>(1);
		typeArguments = new ArrayList<>(1);
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
	public void addIdentifier(String identifier, List<TypeArgument> typeArgs) {
		identifiers.add(identifier);
		typeArguments.add(typeArgs);
	}


	public int getIdentifierCount() {
		return identifiers.size();
	}


	/**
	 * Returns the name of this type.
	 *
	 * @param fullyQualified Whether the returned value should be
	 *        fully qualified.
	 * @return The name of this type.  This will include type arguments,
	 *         if any.
	 * @see #getName(boolean, boolean)
	 */
	public String getName(boolean fullyQualified) {
		return getName(fullyQualified, true);
	}


	/**
	 * Returns the name of this type.
	 *
	 * @param fullyQualified Whether the returned value should be
	 *        fully qualified.
	 * @param addTypeArgs Whether type arguments should be at the end of
	 *        the returned string, if any.
	 * @return The name of this type.
	 * @see #getName(boolean)
	 */
	public String getName(boolean fullyQualified, boolean addTypeArgs) {

		StringBuilder sb = new StringBuilder();

		int count = identifiers.size();
		int start = fullyQualified ? 0 : count-1;
		for (int i=start; i<count; i++) {
			sb.append(identifiers.get(i));
			if (addTypeArgs && typeArguments.get(i)!=null) {
				List<TypeArgument> typeArgs = typeArguments.get(i);
				int typeArgCount = typeArgs.size();
				if (typeArgCount>0) {
					sb.append('<');
					for (int j=0; j<typeArgCount; j++) {
						TypeArgument typeArg = typeArgs.get(j);
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


	public List<TypeArgument> getTypeArguments(int index) {
		return typeArguments.get(index);
	}


	/*
	 * MethodDeclaratorRest allows bracket pairs after its FormalParameters,
	 * which increment the array depth of the return type.
	 */
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
			String str = identifiers.get(0);
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


	public void setBracketPairCount(int count) {
		bracketPairCount = count;
	}


	/**
	 * Returns a string representation of this type.  The type name will be
	 * fully qualified.
	 *
	 * @return A string representation of this type.
	 * @see #getName(boolean)
	 */
	@Override
	public String toString() {
		return getName(true);
	}


}
