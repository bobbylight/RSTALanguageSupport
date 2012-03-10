/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.js.ast;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.js.completion.JSFunctionCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


public class JSTypeFunctionsHelper {


	public static void addFunctionCompletionsForJSType(Set completions,
			TypeDeclaration type, JarManager manager,
			DefaultCompletionProvider provider) {
		if (manager == null || type == null) // nothing to add
			return;

		// now try to read the functions from the API
		ClassFile cf = manager.getClassEntry(type.getQualifiedName());
		readClassFile(cf, completions, provider, manager);
	}


	private static void readClassFile(ClassFile cf, Set completions,
			DefaultCompletionProvider provider, JarManager manager) {
		if (cf != null) {
			File file = manager.getSourceLocForClass(cf.getClassName(true));
			if (file != null) {
				CompilationUnit cu = Util.getCompilationUnitFromDisk(file, cf);
				int count = cu.getTypeDeclarationCount();
				for (int i = 0; i < count; i++) {
					org.fife.rsta.ac.java.rjc.ast.TypeDeclaration dec = cu
							.getTypeDeclaration(i);
					readMethodsFromTypeDeclaration(dec, completions, provider,
							cu, manager);
				}
			}
		}
	}


	private static void readMethodsFromTypeDeclaration(
			org.fife.rsta.ac.java.rjc.ast.TypeDeclaration dec, Set completions,
			DefaultCompletionProvider provider, CompilationUnit cu,
			JarManager jarManager) {
		if (dec instanceof NormalClassDeclaration) {
			NormalClassDeclaration ncd = (NormalClassDeclaration) dec;
			int count = ncd.getMemberCount();
			for (int i = 0; i < count; i++) {
				Member m = ncd.getMember(i);
				if (m instanceof Method) {
					Method method = (Method) m;
					if (canAddSystemMethod(method)) // do not add constructors
					{
						JSFunctionCompletion completion = new JSFunctionCompletion(
								provider, method);
						completions.add(completion);
					}
				}
			}
			// get extended class
			Type extended = ncd.getExtendedType();
			if (extended != null) { // e.g., not java.lang.Object
				String superClassName = extended.toString();

				ClassFile cf = getClassFileFor(cu, superClassName, jarManager);
				if (cf != null) {
					readClassFile(cf, completions, provider, jarManager);
				}
			}

		}
		else if (dec instanceof NormalInterfaceDeclaration) {
			NormalInterfaceDeclaration nid = (NormalInterfaceDeclaration) dec;
			int count = nid.getMemberCount();
			for (int i = 0; i < count; i++) {
				Member m = nid.getMember(i);
				if (m instanceof Method) {
					Method method = (Method) m;
					JSFunctionCompletion completion = new JSFunctionCompletion(
							provider, method);
					completions.add(completion);
				}
			}
			// TODO work out extended interfaces
		}

	}


	private static ClassFile getClassFileFor(CompilationUnit cu,
			String className, JarManager jarManager) {

		// System.err.println(">>> Getting class file for: " + className);
		if (className == null) {
			return null;
		}

		ClassFile superClass = null;

		// Determine the fully qualified class to grab
		if (!Util.isFullyQualified(className)) {

			// Check in this source file's package first
			String pkg = cu.getPackageName();
			if (pkg != null) {
				String temp = pkg + "." + className;
				superClass = jarManager.getClassEntry(temp);
			}

			// Next, go through the imports (order is important)
			if (superClass == null) {
				for (Iterator i = cu.getImportIterator(); i.hasNext();) {
					ImportDeclaration id = (ImportDeclaration) i.next();
					String imported = id.getName();
					if (imported.endsWith(".*")) {
						String temp = imported.substring(0,
								imported.length() - 1)
								+ className;
						superClass = jarManager.getClassEntry(temp);
						if (superClass != null) {
							break;
						}
					}
					else if (imported.endsWith("." + className)) {
						superClass = jarManager.getClassEntry(imported);
						break;
					}
				}
			}

			// Finally, try java.lang
			if (superClass == null) {
				String temp = "java.lang." + className;
				superClass = jarManager.getClassEntry(temp);
			}

		}

		else {
			superClass = jarManager.getClassEntry(className);
		}

		return superClass;

	}


	private static boolean canAddSystemMethod(Method method) {
		Modifiers mod = method.getModifiers();
		return mod != null && mod.isPublic() && !method.isConstructor();
	}


}