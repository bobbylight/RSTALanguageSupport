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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.fife.rsta.ac.java.MemberCompletion.Data;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.classreader.Util;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.FormalParameter;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;


/**
 * Metadata about a method as read from a class file.  This class is used by
 * instances of {@link MethodCompletion}.
 *
 * @author Robert Futrell
 * @version 1.0
 */
class MethodInfoData implements Data {

	/**
	 * The parent completion provider.
	 */
	private SourceCompletionProvider provider;

	/**
	 * The actual metadata.
	 */
	private MethodInfo info;


	/**
	 * Constructor.
	 *
	 * @param info
	 * @param provider
	 */
	public MethodInfoData(MethodInfo info, SourceCompletionProvider provider) {
		this.info = info;
		this.provider = provider;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getDefinedIn() {
		return info.getClassFile().getClassName(false);
	}


	/**
	 * {@inheritDoc}
	 */
	public String getIcon() {

		String key = null;
		int flags = info.getAccessFlags();

		if (Util.isDefault(flags)) {
			key = IconFactory.METHOD_DEFAULT_ICON;
		}
		else if (Util.isPrivate(flags)) {
			key = IconFactory.METHOD_PRIVATE_ICON;
		}
		else if (Util.isProtected(flags)) {
			key = IconFactory.METHOD_PROTECTED_ICON;
		}
		else if (Util.isPublic(flags)) {
			key = IconFactory.METHOD_PUBLIC_ICON;
		}
		else {
			key = IconFactory.METHOD_DEFAULT_ICON;
		}

		return key;

	}


	/**
	 * Scours the source in a location (zip file, directory), looking for a
	 * particular class's source.  If it is found, it is parsed, and the
	 * {@link Method} for this method (if any) is returned.
	 *
	 * @param loc The zip file, jar file, or directory to look in.
	 * @param cf The {@link ClassFile} representing the class of this method.
	 * @return The method, or <code>null</code> if it cannot be found, or an
	 *         IO error occurred.
	 */
	private Method getMethodFromSourceLoc(File loc, ClassFile cf) {

		Method res = null;
		CompilationUnit cu = org.fife.rsta.ac.java.Util.
									getCompilationUnitFromDisk(loc, cf);

		// If the class's source was found and successfully parsed, look for
		// this method.
		if (cu!=null) {

			for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); ) {

				TypeDeclaration td = (TypeDeclaration)i.next();
				String typeName = td.getName();

				// Avoid inner classes, etc.
				if (typeName.equals(cf.getClassName(false))) {

					// Get all overloads of this method with the number of
					// parameters we're looking for.  99% of the time, there
					// will only be 1, the method we're looking for.
					List contenders = null;
					for (Iterator j=td.getMemberIterator(); j.hasNext(); ) {
						Member member = (Member)j.next();
						if (member instanceof Method &&
								member.getName().equals(info.getName())) {
							Method m2 = (Method)member;
							if (m2.getParameterCount()==info.getParameterCount()) {
								if (contenders==null) {
									contenders = new ArrayList(1); // Usually just 1
								}
								contenders.add(m2);
							}
						}
					}

					// We found some matches.
					if (contenders!=null) {

						// Common case - only 1 overload with the desired
						// number of parameters => it must be our method.
						if (contenders.size()==1) {
							res = (Method)contenders.get(0);
						}

						// More than 1 overload with the same number of
						// parameters... we decide which contender is the one
						// we're looking for by checking each of its
						// parameters' types and making sure they're correct.
						else {
							for (int j=0; j<contenders.size(); j++) {
								boolean match = true;
								Method method = (Method)contenders.get(j);
								for (int p=0; p<info.getParameterCount(); p++) {
									String type1 = info.getParameterType(p, false);
									FormalParameter fp = method.getParameter(p);
									String type2 = fp.getType().toString();
									if (!type1.equals(type2)) {
										match = false;
										break;
									}
								}
								if (match) {
									res = method;
									break;
								}
							}
						}

					}

					break;

				} // if (typeName.equals(cf.getClassName(false)))

			} // for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); )

		} // if (cu!=null)

		return res;

	}


	public String getSignature() {
		return info.getNameAndParameters();
	}


	public String getSummary() {

		ClassFile cf = info.getClassFile();
		File loc = provider.getSourceLocForClass(cf.getClassName(true));
		String summary = null;

		// First, try to parse the Javadoc for this method from the attached
		// source.
		if (loc!=null) {
			summary = getSummaryFromSourceLoc(loc, cf);
		}

		// Default to the method signature.
		if (summary==null) {
			summary = info.getSignature();
		}
		return summary;

	}


	/**
	 * Scours the source in a location (zip file, directory), looking for a
	 * particular class's source.  If it is found, it is parsed, and the
	 * Javadoc for this method (if any) is returned.
	 *
	 * @param loc The zip file, jar file, or directory to look in.
	 * @param cf The {@link ClassFile} representing the class of this method.
	 * @return The summary, or <code>null</code> if the method has no javadoc,
	 *         the class's source was not found, or an IO error occurred.
	 */
	private String getSummaryFromSourceLoc(File loc, ClassFile cf) {
		Method method = getMethodFromSourceLoc(loc, cf);
		return method!=null ? method.getDocComment() : null;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return info.getReturnTypeString();
	}


	public boolean isAbstract() {
		return info.isAbstract();
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isConstructor() {
		return info.isConstructor();
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isDeprecated() {
		return info.isDeprecated();
	}


	public boolean isFinal() {
		return info.isFinal();
	}


	public boolean isStatic() {
		return info.isStatic();
	}


}