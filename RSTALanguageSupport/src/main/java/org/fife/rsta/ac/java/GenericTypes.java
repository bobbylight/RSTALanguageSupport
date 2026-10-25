/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.attributes.AttributeInfo;
import org.fife.rsta.ac.java.classreader.attributes.Signature;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.java.rjc.lang.TypeArgument;


/**
 * Utilities for the generic type names used when resolving method chain
 * expressions, such as {@code java.util.Set<String>}, used by
 * {@link SourceCompletionProvider}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class GenericTypes {

	private static final String OBJECT = "java.lang.Object";


	/**
	 * Private constructor to prevent instantiation.
	 */
	private GenericTypes() {
		// Do nothing (comment for Sonar)
	}


	/**
	 * Creates and returns a mapping of type parameters to type arguments.
	 *
	 * @param type The type of a variable/field/etc. whose fields/methods/etc.
	 *        are being code completed, as declared in the source.  This
	 *        includes type arguments.
	 * @param cf The <code>ClassFile</code> representing the actual type of
	 *        the variable/field/etc. being code completed
	 * @return A mapping of type parameter names to type arguments (both
	 *         Strings).
	 */
	static Map<String, String> createTypeParamMap(Type type, ClassFile cf) {
		Map<String, String> typeParamMap = null;
		List<TypeArgument> typeArgs = type.getTypeArguments(type.getIdentifierCount()-1);
		if (typeArgs!=null) {
			typeParamMap = new HashMap<>();
			List<String> paramTypes = cf.getParamTypes();
			// Should be the same size!  Otherwise, the source code has
			// too many/too few type arguments listed for this type.
			int min = Math.min(paramTypes==null ? 0 : paramTypes.size(),
									typeArgs.size());
			for (int i=0; i<min; i++) {
				TypeArgument typeArg = typeArgs.get(i);
				typeParamMap.put(paramTypes.get(i), typeArg.toString());
			}
		}
		return typeParamMap;
	}


	/**
	 * Returns a mapping of a class's type parameters to type arguments.
	 *
	 * @param cf The class.
	 * @param typeArgs The type arguments, such as {@code String} and
	 *        {@code Integer} for {@code Map<String, Integer>}.
	 * @return The mapping, or <code>null</code> if the class isn't generic or
	 *         no type arguments were given (a raw type).  Type parameters
	 *         without a type argument are mapped to <code>Object</code>.
	 */
	static Map<String, String> createTypeParamMap(ClassFile cf,
			List<String> typeArgs) {
		List<String> params = cf==null ? null : cf.getParamTypes();
		if (params==null || params.isEmpty() || typeArgs.isEmpty()) {
			return null;
		}
		Map<String, String> typeParamMap = new HashMap<>();
		for (int i=0; i<params.size(); i++) {
			String arg = i<typeArgs.size() ? resolveWildcard(typeArgs.get(i)) : OBJECT;
			typeParamMap.put(params.get(i), arg);
		}
		return typeParamMap;
	}


	/**
	 * Returns a type name without its type arguments, so it can be looked
	 * up.  A wildcard is replaced by its upper bound.
	 *
	 * @param type The type, such as {@code java.util.Set<String>}.
	 * @return The type without type arguments, such as
	 *         {@code java.util.Set}.
	 */
	static String getRawType(String type) {
		type = resolveWildcard(type);
		int lt = type.indexOf('<');
		return lt==-1 ? type : type.substring(0, lt);
	}


	/**
	 * Returns the type arguments of a type name.
	 *
	 * @param type The type, such as {@code java.util.Map<String, List<Foo>>}.
	 * @return The top-level type arguments, such as {@code String} and
	 *         {@code List<Foo>}.  This is empty if there are none.
	 */
	static List<String> getTypeArguments(String type) {
		List<String> typeArgs = new ArrayList<>(2);
		type = resolveWildcard(type);
		int lt = type.indexOf('<');
		if (lt==-1) {
			return typeArgs;
		}
		int depth = 0;
		int start = lt + 1;
		for (int i=start; i<type.length(); i++) {
			char ch = type.charAt(i);
			if (ch=='<') {
				depth++;
			}
			else if (ch=='>' && depth>0) {
				depth--;
			}
			else if ((ch==',' && depth==0) || ch=='>') {
				typeArgs.add(type.substring(start, i).trim());
				start = i + 1;
				if (ch=='>') {
					break;
				}
			}
		}
		return typeArgs;
	}


	/**
	 * Returns the type arguments of each class and interface in a type
	 * hierarchy, given the type arguments of the first one.  For example,
	 * for {@code Stream<String>}, the type parameters of its superinterface
	 * {@code BaseStream<T, S>} map to {@code String} and
	 * {@code Stream<String>}.
	 *
	 * @param hierarchy The type hierarchy, as returned by
	 *        {@link JarManager#getTypeHierarchy(ClassFile)}.
	 * @param typeArgs The type arguments of the first class in the hierarchy.
	 *        This may be <code>null</code> for a raw type.
	 * @return A map from each class in the hierarchy, in the same order, to
	 *         its type arguments (or <code>null</code> for a raw type).
	 */
	static Map<ClassFile, Map<String, String>> getHierarchyTypeArgs(
			List<ClassFile> hierarchy, Map<String, String> typeArgs) {

		Map<String, ClassFile> byName = new HashMap<>();
		for (ClassFile type : hierarchy) {
			byName.put(type.getClassName(true), type);
		}

		// A supertype comes after the type that declares it in the hierarchy,
		// so its type arguments are always known by the time it's reached
		Map<ClassFile, Map<String, String>> result = new LinkedHashMap<>();
		Map<ClassFile, Map<String, String>> found = new HashMap<>();
		found.put(hierarchy.get(0), typeArgs);
		for (ClassFile type : hierarchy) {
			Map<String, String> args = found.get(type);
			result.put(type, args);
			List<String> supertypes = getSupertypes(type, args);
			for (int i=0; i<supertypes.size(); i++) {
				String name = i==0 ? type.getSuperClassName(true) :
					type.getImplementedInterfaceName(i-1, true);
				ClassFile supertype = name==null ? null : byName.get(name);
				if (supertype!=null && !found.containsKey(supertype)) {
					found.put(supertype, createTypeParamMap(supertype,
							getTypeArguments(supertypes.get(i))));
				}
			}
		}

		return result;
	}


	/**
	 * Returns the superclass and interfaces of a class, with their type
	 * arguments, as declared in its <code>Signature</code> attribute.
	 *
	 * @param cf The class.
	 * @param typeArgs The type arguments of the class, or <code>null</code>
	 *        for a raw type.  The class's type variables are replaced with
	 *        these.
	 * @return The superclass followed by the interfaces, such as
	 *         {@code java.util.Collection<String>}.  This is empty if the
	 *         class has no <code>Signature</code> attribute.
	 */
	private static List<String> getSupertypes(ClassFile cf,
			Map<String, String> typeArgs) {
		List<String> supertypes = new ArrayList<>(2);
		String sig = getSignature(cf);
		if (sig==null) {
			return supertypes;
		}
		int[] pos = { 0 };
		if (sig.charAt(0)=='<') {
			pos[0] = skipTypeParameters(sig);
		}
		while (pos[0] < sig.length()) {
			supertypes.add(parseType(sig, pos, typeArgs));
		}
		return supertypes;
	}


	private static String getSignature(ClassFile cf) {
		for (int i=0; i<cf.getAttributeCount(); i++) {
			AttributeInfo ai = cf.getAttribute(i);
			if (ai instanceof Signature) {
				return ((Signature)ai).getSignature();
			}
		}
		return null;
	}


	/**
	 * Parses one type from a class signature into a type name.
	 *
	 * @param sig The signature.
	 * @param pos The position of the type in the signature.  This is updated
	 *        to the position after it.
	 * @param typeArgs Replacements for type variables, or <code>null</code>
	 *        to replace them all with <code>Object</code>.
	 * @return The type name.
	 */
	private static String parseType(String sig, int[] pos,
			Map<String, String> typeArgs) {

		char ch = sig.charAt(pos[0]++);
		switch (ch) {
			case 'L':
				StringBuilder sb = new StringBuilder();
				List<String> args = new ArrayList<>(2);
				while ((ch=sig.charAt(pos[0]++))!=';') {
					if (ch=='<') {
						args.clear();
						while (sig.charAt(pos[0])!='>') {
							args.add(parseType(sig, pos, typeArgs));
						}
						pos[0]++;
					}
					else if (ch=='.') { // Inner class of a generic class
						sb.append('$');
						args.clear();
					}
					else {
						sb.append(ch=='/' ? '.' : ch);
					}
				}
				if (!args.isEmpty()) {
					sb.append('<').append(String.join(", ", args)).append('>');
				}
				return sb.toString();
			case 'T':
				int semicolon = sig.indexOf(';', pos[0]);
				String typeVar = sig.substring(pos[0], semicolon);
				pos[0] = semicolon + 1;
				String arg = typeArgs==null ? null : typeArgs.get(typeVar);
				return arg==null ? OBJECT : arg;
			case '[':
				return parseType(sig, pos, typeArgs) + "[]";
			case '*':
				return "?";
			case '+':
				return "? extends " + parseType(sig, pos, typeArgs);
			case '-':
				return "? super " + parseType(sig, pos, typeArgs);
			default: // A primitive, in an array type
				return String.valueOf(ch);
		}

	}


	/**
	 * Returns a wildcard type's upper bound.
	 *
	 * @param type The type, such as {@code ? extends Number}.
	 * @return The upper bound, such as {@code Number}, or <code>type</code>
	 *         itself if it isn't a wildcard.
	 */
	private static String resolveWildcard(String type) {
		while (type.startsWith("?")) {
			if (!type.startsWith("? extends ")) {
				return OBJECT;
			}
			type = type.substring("? extends ".length());
		}
		return type;
	}


	/**
	 * Returns the position after the type parameters at the start of a
	 * class signature, such as {@code <E:Ljava/lang/Object;>}.
	 */
	private static int skipTypeParameters(String sig) {
		int depth = 0;
		for (int i=0; i<sig.length(); i++) {
			char ch = sig.charAt(i);
			if (ch=='<') {
				depth++;
			}
			else if (ch=='>' && --depth==0) {
				return i + 1;
			}
		}
		return sig.length();
	}
}
