package org.fife.rsta.ac.js.ast.jsType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;


/**
 * Rhino Specific JavaScriptTypesFactory
 * 
 * Supports: importPackage and importClass
 * 
 * importPackage(java.util)
 * importClass(java.util.HashSet)
 * 
 * Clears the cache every time document is parsed for importPackage and importClass to work properly
 */
public class RhinoJavaScriptTypesFactory extends JavaScriptTypesFactory {

	private LinkedHashSet importClasses = new LinkedHashSet();
	private LinkedHashSet importPackages = new LinkedHashSet();


	public void addImportClass(String qualifiedClass) {
		importClasses.add(qualifiedClass);
	}


	public void addImportPackage(String packageName) {
		importPackages.add(packageName);
	}
	
	public void clearImports()
	{
		importClasses.clear();
		importPackages.clear();
		HashSet removeTypes = new HashSet();
		//clear all non ECMA (JavaScript types) for importPackage and importClass to work properly
		for(Iterator i = cachedTypes.keySet().iterator(); i.hasNext();) {
			TypeDeclaration dec = (TypeDeclaration) i.next();
			if(!dec.getQualifiedName().startsWith("org.fife.rsta.ac.js.ecma") && !dec.equals(TypeDeclarationFactory.getDefaultTypeDeclaration())) {
				removeAllTypes((JavaScriptType) cachedTypes.get(dec));
				removeTypes.add(dec);
			}
		}
		cachedTypes.keySet().removeAll(removeTypes);
	}
	
	/**
	 * Remove all TypeDeclarations from the TypeDeclarationFactory from the JavaScriptType and all it's extended classes
	 * @param type
	 */
	private void removeAllTypes(JavaScriptType type)
	{
		if(type != null)
		{
			TypeDeclarationFactory.Instance().removeType(type.getType().getQualifiedName());
			if(type.getExtendedClasses().size() > 0)
			{
				for(Iterator i = type.getExtendedClasses().iterator(); i.hasNext();)
				{
					JavaScriptType extendedType = (JavaScriptType) i.next();
					removeAllTypes(extendedType);
				}
			}
		}
	}


	/**
	 * Override getClassFile that checks the imported packages and classnames based on the TypeDeclaration.getAPITypeName()
	 */
	public ClassFile getClassFile(JarManager manager, TypeDeclaration type) {

		ClassFile file = super.getClassFile(manager, type);
		if (file == null) {
			// try the class names
			file = findFromClasses(manager, type.getAPITypeName());
			if (file == null) {
				file = findFromImport(manager, type.getAPITypeName());
			}
		}
		return file;
	}


	/**
	 * Look for class file using imported classes
	 * @param manager
	 * @param name
	 * @return
	 */
	private ClassFile findFromClasses(JarManager manager, String name) {
		ClassFile file = null;
		for (Iterator i = importClasses.iterator(); i.hasNext();) {
			String cls = (String) i.next();
			if (cls.endsWith(name)) {
				file = manager.getClassEntry(cls);
				if (file != null)
					break;
			}
		}
		return file;
	}

	/**
	 * Look for class file using imported packages
	 * @param manager
	 * @param name
	 * @return
	 */
	private ClassFile findFromImport(JarManager manager, String name) {
		ClassFile file = null;
		for (Iterator i = importPackages.iterator(); i.hasNext();) {
			String packageName = (String) i.next();
			String cls = name.startsWith(".") ? (packageName + name) : (packageName + "." + name);   
			file = manager.getClassEntry(cls);
			if (file != null)
				break;
		}
		return file;
	}
}
