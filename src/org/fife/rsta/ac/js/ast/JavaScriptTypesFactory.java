/*
 * 02/25/2012
 *
 * Copyright (C) 2012 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This code is licensed under the LGPL.  See the "license.txt" file included
 * with this project.
 */
package org.fife.rsta.ac.js.ast;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.ImportDeclaration;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.Method;
import org.fife.rsta.ac.java.rjc.ast.NormalClassDeclaration;
import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.lang.Modifiers;
import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.rsta.ac.js.completion.JSFieldCompletion;
import org.fife.rsta.ac.js.completion.JSFunctionCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;


public abstract class JavaScriptTypesFactory {

	private HashMap cachedTypes = new HashMap();
	private boolean useBeanproperties;


	private static class DefaultJavaScriptTypeFactory extends
			JavaScriptTypesFactory {

		public DefaultJavaScriptTypeFactory() {
		}
	}


	public static JavaScriptTypesFactory getDefaultJavaScriptTypesFactory() {
		return new DefaultJavaScriptTypeFactory();
	}


	public void setUseBeanProperties(boolean useBeanproperties) {
		this.useBeanproperties = useBeanproperties;
	}


	public boolean isUseBeanProperties() {
		return useBeanproperties;
	}


	/**
	 * Find CachedType for TypeDeclaration. If it is not found, then lookup the
	 * type and add all completions, then cache. Extracts all function and type
	 * completions from API based on the <code>TypeDeclaration</code>.
	 * 
	 * @param type TypeDeclaration to read from the API e.g JSString
	 * @param manager JarManager containing source and classes
	 * @param provider CompletionsProvider to bind the <code>Completion</code>
	 */
	public JavaScriptType getCachedType(TypeDeclaration type,
			JarManager manager, DefaultCompletionProvider provider) {

		if (manager == null || type == null) // nothing to add
			return null;

		// already processed type, so no need to do again
		if (cachedTypes.containsKey(type))
			return (JavaScriptType) cachedTypes.get(type);

		// now try to read the functions from the API
		ClassFile cf = manager.getClassEntry(type.getQualifiedName());
		JavaScriptType cachedType = new JavaScriptType(type);
		// cache if required
		cachedTypes.put(type, cachedType);
		readClassFile(cachedType, cf, provider, manager, type);
		return cachedType;

	}


	/**
	 * Read the class file and extract all completions, add them all to the
	 * CachedType
	 * 
	 * @param cachedType CachedType to populate all completions
	 * @param cf ClassFile to read
	 * @param provider CompletionsProvider to bind to <code>Completion</code>
	 * @param manager JarManager containing source and classes
	 * @param type TypeDeclaration to read from the API e.g JString
	 */
	private void readClassFile(JavaScriptType cachedType, ClassFile cf,
			DefaultCompletionProvider provider, JarManager manager,
			TypeDeclaration type) {

		if (cf != null) {
			File file = manager.getSourceLocForClass(cf.getClassName(true));
			if (file != null) {
				CompilationUnit cu = Util.getCompilationUnitFromDisk(file, cf);
				int count = cu.getTypeDeclarationCount();
				for (int i = 0; i < count; i++) {
					org.fife.rsta.ac.java.rjc.ast.TypeDeclaration dec = cu
							.getTypeDeclaration(i);
					readMethodsAndFieldsFromTypeDeclaration(cachedType, dec,
							provider, cu, manager);
				}
			}
		}
	}


	/**
	 * Extract all methods and fields from CompilationUnit and add to the
	 * completions map. Only public methods and fields will be added to
	 * completions
	 * 
	 * @param cachedType CachedType to populate all completions
	 * @param dec TypeDeclaration to read from the API e.g JString
	 * @param provider CompletionsProvider to bind to <code>Completion</code>
	 * @param cu CompilationUnit that binds source to class
	 * @param jarManager JarManager containing source and classes
	 * @return map of all completions
	 */
	private void readMethodsAndFieldsFromTypeDeclaration(
			JavaScriptType cachedType,
			org.fife.rsta.ac.java.rjc.ast.TypeDeclaration dec,
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
								provider, method, useBeanproperties);
						cachedType.addCompletion(completion);
					}
				}
				else if (m instanceof Field) {
					Field f = (Field) m;
					if (canAddSystemField(f)) {
						JSFieldCompletion fc = new JSFieldCompletion(provider,
								f);
						cachedType.addCompletion(fc);
					}
				}
			}
			// get extended class
			Type extended = ncd.getExtendedType();
			if (extended != null) {
				String superClassName = extended.toString();

				ClassFile cf = getClassFileFor(cu, superClassName, jarManager);
				if (cf != null) {
					TypeDeclaration type = TypeDeclarationFactory.Instance()
							.getTypeDeclaration(superClassName, true);
					if (type != null) {
						JavaScriptType extendedType = new JavaScriptType(type);
						cachedType.addExtension(extendedType);
						readClassFile(extendedType, cf, provider, jarManager,
								type);
					}
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
					cachedType.addCompletion(completion);
				}
			}
			// TODO work out extended interfaces
		}
	}


	/**
	 * @return returns the ClassFile for the class name, checks all packages
	 *         available to match to the class name
	 */
	private ClassFile getClassFileFor(CompilationUnit cu, String className,
			JarManager jarManager) {

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


	/**
	 * @param method
	 * @return method is public and not a constructor
	 */
	private boolean canAddSystemMethod(Method method) {
		Modifiers mod = method.getModifiers();
		return mod != null && mod.isPublic() && !method.isConstructor();
	}


	/**
	 * @param field
	 * @return field is public and must be static
	 */
	private boolean canAddSystemField(Field field) {
		Modifiers mod = field.getModifiers();
		return mod != null && mod.isPublic() && mod.isStatic();
	}


	/**
	 * Populate Completions for types... included extended classes. TODO
	 * optimise this.
	 * 
	 * @param completionsMap
	 * @param completions
	 * @param type
	 * @param manager
	 */
	public void populateCompletionsForType(JavaScriptType cachedType,
			Set completions) {

		if (cachedType != null) {
			HashMap completionsForType = cachedType.getCompletions();
			for (Iterator i = completionsForType.values().iterator(); i
					.hasNext();) {
				completions.add(i.next());
			}

			// get any extended classes and recursivley populate
			List extendedClasses = cachedType.getExtendedClasses();
			for (Iterator i = extendedClasses.iterator(); i.hasNext();) {
				JavaScriptType extendedType = (JavaScriptType) i.next();
				populateCompletionsForType(extendedType, completions);
			}
		}
	}


	public void removeCachedType(TypeDeclaration typeDef) {
		cachedTypes.remove(typeDef);
	}


	public void clearCache() {
		cachedTypes.clear();
	}
}