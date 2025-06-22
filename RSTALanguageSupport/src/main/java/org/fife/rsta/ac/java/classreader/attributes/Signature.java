/*
 * 01/16/2011
 *
 * Copyright (C) 2011 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java.classreader.attributes;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;


/**
 * The Signature attribute is an optional fixed-length attribute in the
 * attribute table of the ClassFile, field_info and method_info
 * structures.<p>
 * WARNING: This code is a complete mess.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Signature extends AttributeInfo {

	private static final Logger LOG =
		System.getLogger(Signature.class.getName());

	private String signature;


	public Signature(ClassFile cf, String signature) {
		super(cf);
		this.signature = signature;
	}


	/**
	 * Returns the class-param types of this method.
	 *
	 * @return The class-param types.
	 */
	public List<String> getClassParamTypes() {

		List<String> types = null;

		if (signature!=null && signature.startsWith("<")) {

			types = new ArrayList<>(1); // Usually a small number
			int afterMatchingGT = skipLtGt(signature, 1);

			// We're assuming we don't come across corrupt signatures...

			String temp = signature.substring(1, afterMatchingGT-1);
			int offs = 0;
			int colon = temp.indexOf(':', offs);
			while (offs<temp.length() && colon>-1) {
				String ident = temp.substring(offs, colon);
				int colonCount = 1;
				char ch = temp.charAt(colon+colonCount);
				if (ch==':') { // sometimes, there is another ':'
				    colonCount++;
				    ch = temp.charAt(colon+colonCount);
				}
				if (ch=='L') { // A ClassTypeSignature
					int semicolon = temp.indexOf(';', colon+colonCount+1);
					if (semicolon>-1) {
						//String type = temp.substring(colon+2, semicolon);
						// TODO: ...
						types.add(ident);
						offs = semicolon + 1;
						colon = temp.indexOf(':', offs);
					}
					else {
						LOG.log(System.Logger.Level.WARNING,
							"Can't parse signature (1): " + signature);
						break;
					}
				}
				else {
					LOG.log(System.Logger.Level.WARNING,
						"Can't parse signature (2): " + signature);
					break;
				}
			}
		}

		return types;

	}


	private int skipLtGt(String str, int start) {

		int ltCount = 1;
		int offs = start;

		while (offs<str.length() && ltCount>0) {
			char ch = str.charAt(offs++);
			switch (ch) {
				case '<':
					ltCount++;
					break;
				case '>':
					ltCount--;
					break;
			}
		}

		return offs;

	}


	/**
	 * Returns the parameter types for parameters of this method.
	 *
	 * @param mi The method information.
	 * @param cf The class file being parsed.
	 * @param qualified Whether the results should be fully-qualified.
	 * @return The return types.
	 */
	public List<String> getMethodParamTypes(MethodInfo mi, ClassFile cf,
			boolean qualified) {

		List<String> paramTypeList = null;
		String signature = this.signature; // Since we modify it

		if (signature!=null) {

			paramTypeList = new ArrayList<>();

			// Handle "<...>", which essentially defines extra type args
			Map<String, String> additionalTypeArgs = null;
			if (signature.charAt(0)=='<') {
				int afterMatchingGT = skipLtGt(signature, 1);
				String typeParams = signature.substring(1, afterMatchingGT-1);
				additionalTypeArgs = parseAdditionalTypeArgs(typeParams);
				signature = signature.substring(afterMatchingGT);
			}

			if (signature.charAt(0)=='(') {

				int rparen = signature.indexOf(')', 1);
				String paramDescriptors = signature.substring(1, rparen);
				ParamDescriptorResult res = new ParamDescriptorResult();

				while (!paramDescriptors.isEmpty()) {
					parseParamDescriptor(paramDescriptors, cf, additionalTypeArgs,
							mi, "Error parsing method signature for ", res, qualified);
					paramTypeList.add(res.type);
					if (paramDescriptors.length()>res.pos) {
						paramDescriptors = paramDescriptors.substring(res.pos);
					} else {
						break;
					}
				}

			}

			else {
				LOG.log(System.Logger.Level.INFO,
					"TODO: Unhandled method signature for " + mi.getName() +
					": " + signature);
			}

		}

		return paramTypeList;

	}


	/**
	 * Returns the return type of this method.
	 *
	 * @param mi The method information.
	 * @param cf The class file being parsed.
	 * @param qualified Whether the result should be fully-qualified.
	 * @return The return type.
	 */
	public String getMethodReturnType(MethodInfo mi, ClassFile cf, boolean qualified) {

		String signature = this.signature; // Since we modify it
		String sig = null;

		if (signature!=null) {

			// Handle "<...>", which essentially defines extra type args
			Map<String, String> additionalTypeArgs = null;
			if (signature.charAt(0)=='<') {
				int afterMatchingGT = skipLtGt(signature, 1);
				String typeParams = signature.substring(1, afterMatchingGT-1);
				additionalTypeArgs = parseAdditionalTypeArgs(typeParams);
				signature = signature.substring(afterMatchingGT);
			}

			if (signature.charAt(0)=='(') {
				int rparen = signature.indexOf(')', 1);
				if (rparen>-1 && rparen<signature.length()-3) { // Should always be true
					String afterRParen = signature.substring(rparen+1);
					ParamDescriptorResult res = new ParamDescriptorResult();
					parseParamDescriptor(afterRParen, cf, additionalTypeArgs, mi,
							"Can't parse return type from method sig for ", res, qualified);
					sig = res.type;
				}
			}

			else {
				LOG.log(System.Logger.Level.INFO,
					"TODO: Unhandled method signature for " + mi.getName() +
					": " + signature);
			}

		}

		return sig;

	}


	public String getSignature() {
		return signature;
	}


	/**
	 * Returns the type argument specified for a given type parameter.
	 *
	 * @param typeVar The type parameter name.
	 * @param cf The class file with generic methods.
	 * @param additionalTypeArgs Additional type arguments for a method (such
	 *        as for "<code>&lt;T&gt; T[] toArray(T[] a)</code>", where the
	 *        "<code>T</code>" type parameter is the type of an argument passed
	 *        to it).
	 * @return The type argument, or <code>null</code> if the given type
	 *         parameter isn't defined.
	 */
	private String getTypeArgument(String typeVar, ClassFile cf,
									Map<String, String> additionalTypeArgs) {
		String type = cf.getTypeArgument(typeVar);
		if (type==null && additionalTypeArgs!=null) {
			//type = (String)additionalTypeArgs.get(typeVar);
			type = typeVar;
		}
		return type;
	}


	private Map<String, String> parseAdditionalTypeArgs(String typeParams) {

		Map<String, String> additionalTypeArgs = new HashMap<>();
		int offs = 0;
		int colon = typeParams.indexOf(':', offs);

		while (offs<typeParams.length()) {
			String param = typeParams.substring(offs, colon);
			int semicolon = typeParams.indexOf(';', offs+1);
			int lt = typeParams.indexOf('<', offs+1);
			if (lt>-1 && lt<semicolon) { // Type parameters in class
				int afterMatchingGT = skipLtGt(typeParams, lt+1);
				String typeArg = typeParams.substring(colon+1, afterMatchingGT);
				additionalTypeArgs.put(param, typeArg);
				offs = afterMatchingGT + 1; // Skip trailing ';' also
			}
			else { // No type parameters, just a class name
				String typeArg = typeParams.substring(colon+1, semicolon);
				additionalTypeArgs.put(param, typeArg);
				offs = semicolon + 1;
			}
			colon = typeParams.indexOf(':', offs);
		}

		return additionalTypeArgs;

	}


	private ParamDescriptorResult parseParamDescriptor(String str,
						ClassFile cf, Map<String, String> additionalTypeArgs,
						MethodInfo mi, String errorDesc,
						ParamDescriptorResult res, boolean qualified) {

		// Can't do lastIndexOf() as there may be > 1 array parameter
		// in the descriptors.
		// int braceCount = str.lastIndexOf('[') + 1;
		int braceCount = -1;
		while (str.charAt(++braceCount) == '[');
		int pos = braceCount;
		String type;
		boolean extendingGenericType = false;

		switch (str.charAt(pos)) {

			// BaseType
			case 'B':
				type = "byte";
				pos++;
				break;
			case 'C':
				type = "char";
				pos++;
				break;
			case 'D':
				type = "double";
				pos++;
				break;
			case 'F':
				type = "float";
				pos++;
				break;
			case 'I':
				type = "int";
				pos++;
				break;
			case 'J':
				type = "long";
				pos++;
				break;
			case 'S':
				type = "short";
				pos++;
				break;
			case 'Z':
				type = "boolean";
				pos++;
				break;

			// ObjectType
			case 'L':
				int semicolon = str.indexOf(';', pos+1);
				int lt = str.indexOf('<', pos+1);
				if (lt>-1 && lt<semicolon) { // Type parameters in type class
					int offs = skipLtGt(str, lt+1);
					// There should be a ';' after type parameters
					if (offs==str.length() || str.charAt(offs)!=';') {
						LOG.log(System.Logger.Level.INFO, "TODO: " + errorDesc +
							mi.getName() + ": " + signature);
						type = "ERROR_PARSING_METHOD_SIG";
					}
					else {
						// Set "type" to class name, without type params
						type = str.substring(pos+1, lt);
						//type = org.fife.rsta.ac.java.Util.replaceChar(type, '/', '.');
						//type = type.substring(type.lastIndexOf('/')+1);
						type = qualified ? type.replace('/', '.') : type.substring(type.lastIndexOf('/')+1);
						// Get type parameters
						String paramDescriptors = str.substring(lt+1, offs-1);
						ParamDescriptorResult res2 = new ParamDescriptorResult();
						List<String> paramTypeList = new ArrayList<>();
						// Recursively parse type parameters of this parameter
						while (!paramDescriptors.isEmpty()) {
							parseParamDescriptor(paramDescriptors, cf, additionalTypeArgs,
									mi, "Error parsing method signature for ", res2, qualified);
							paramTypeList.add(res2.type);
							if (paramDescriptors.length()>res2.pos) {
								paramDescriptors = paramDescriptors.substring(res2.pos);
							} else {
								break;
							}

						}
						StringBuilder sb = new StringBuilder(type).append('<');
						for (int i=0; i<paramTypeList.size(); i++) {
							sb.append(paramTypeList.get(i));
							if (i<paramTypeList.size()-1) {
								sb.append(", ");
							}
						}
						type = sb.append('>').toString();
						pos = offs+1;//semicolon + 1; Skip semicolon that came AFTER "<...>"
					}
				}
				else {
					String clazz = str.substring(pos + 1, semicolon);
					//clazz = org.fife.rsta.ac.java.Util.replaceChar(clazz, '/', '.');
					//clazz = clazz.substring(clazz.lastIndexOf('/')+1);
					clazz = qualified ? clazz.replace('/', '.') : clazz.substring(clazz.lastIndexOf('/')+1);
					type = clazz;
					pos += semicolon + 1;
				}
				break;

			case '+': // "super extends T"
				extendingGenericType = true;
				pos++;
				// Fall through

			case 'T': // Generic type
				semicolon = str.indexOf(';', pos+1);
				String typeVar = str.substring(pos+1, semicolon);
				type = getTypeArgument(typeVar, cf, additionalTypeArgs);
				if (type==null) {
					type = "UNKNOWN_GENERIC_TYPE_" + typeVar;
				}
				else if (extendingGenericType) {
					type = "? extends " + type;
				}
				pos = semicolon + 1;
				break;

			case '*':
				type = "?";
				pos++;
				break;

			// Invalid method descriptor
			default:
				String temp = "INVALID_TYPE_" + str;
				type = temp;
				pos += str.length();
				break;

		}

		for (int i = 0; i < braceCount; i++) {
			type += "[]";
		}

		return res.set(type, pos);

	}


	@Override
	public String toString() {
		return "[Signature: signature=" + getSignature() + "]";
	}


	private static final class ParamDescriptorResult {

		private String type;
		private int pos;

		public ParamDescriptorResult set(String type, int pos) {
			this.type = type;
			this.pos = pos;
			return this;
		}

	}


}
