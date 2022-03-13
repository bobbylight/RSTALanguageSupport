package org.fife.rsta.ac.js.ast.jsType;

import java.util.HashSet;
import java.util.LinkedHashSet;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.ast.parser.RhinoJavaScriptAstParser;
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
public class RhinoJavaScriptTypesFactory extends JSR223JavaScriptTypesFactory {

	private LinkedHashSet<String> importClasses = new LinkedHashSet<>();
	private LinkedHashSet<String> importPackages = new LinkedHashSet<>();
	
	public RhinoJavaScriptTypesFactory(TypeDeclarationFactory typesFactory)
	{
		super(typesFactory);
	}

	public void addImportClass(String qualifiedClass) {
		importClasses.add(qualifiedClass);
	}


	public void addImportPackage(String packageName) {
		importPackages.add(packageName);
	}
	
	public void mergeImports(HashSet<String> packages, HashSet<String> classes)
	{
		mergeImports(packages, importPackages, true);
		mergeImports(classes, importClasses, false);
	}
	
	private void mergeImports(HashSet<String> newImports, LinkedHashSet<String> oldImports, boolean packages)
	{
		//iterate through the old imports and check whether the import exists in new. If not then add to remove and remove all types for that package/class
		HashSet<String> remove = new HashSet<>();
		for (String obj : oldImports) {
			if(!newImports.contains(obj)) {
				remove.add(obj);
			}
		}
		
		
		//now iterate through the remove list and remove imports not needed
		if(!remove.isEmpty())
		{
			HashSet<TypeDeclaration> removeTypes = new HashSet<>();
			for (String name : remove) {
				for (TypeDeclaration dec : cachedTypes.keySet()) {
					if((packages && dec.getQualifiedName().startsWith(name)) || (!packages && dec.getQualifiedName().equals(name)))
					{
						removeAllTypes(cachedTypes.get(dec));
						removeTypes.add(dec);
					}
				}
			}
			cachedTypes.keySet().removeAll(removeTypes);
		}
		
		if(canClearCache(newImports, oldImports))
		{
			//now clear and swap
			oldImports.clear();
			//remove types
			clearAllImportTypes();
			//add all imports to cached imports
			oldImports.addAll(newImports);
		}
	}
	
	/**
	 * Validate whether the newImports and old imports contain the same values
	 * @param newImports
	 * @param oldImports
	 * @return
	 */
	private boolean canClearCache(HashSet<String> newImports, LinkedHashSet<String> oldImports) {
		if(newImports.size() != oldImports.size()) {
			return true;
		}
		
		for (String im : oldImports) {
			if(!newImports.contains(im)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void clearImportCache()
	{
		importClasses.clear();
		importPackages.clear();
		clearAllImportTypes();
	}
	
	private void clearAllImportTypes()
	{
		HashSet<TypeDeclaration> removeTypes = new HashSet<>();
		//clear all non ECMA (JavaScript types) for importPackage and importClass to work properly
        for (TypeDeclaration dec : cachedTypes.keySet()) {
            if (!typesFactory.isJavaScriptType(dec) && !dec.equals(typesFactory.getDefaultTypeDeclaration())) {
                removeAllTypes(cachedTypes.get(dec));
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
			typesFactory.removeType(type.getType().getQualifiedName());
			if(type.getExtendedClasses().size() > 0)
			{
                for (JavaScriptType extendedType : type.getExtendedClasses()) {
                    removeAllTypes(extendedType);
                }
			}
		}
	}


	/**
	 * Override getClassFile that checks the imported packages and classnames based on the TypeDeclaration.getAPITypeName()
	 */
	@Override
	public ClassFile getClassFile(JarManager manager, TypeDeclaration type) {

		String qName = removePackagesFromType(type.getQualifiedName());
		ClassFile file = super.getClassFile(manager, JavaScriptHelper.createNewTypeDeclaration(qName));
		if (file == null) {
			file = findFromClasses(manager, qName);
			if (file == null) {
				file = findFromImport(manager, qName);
			}
		}
		return file;
	}
	
	private String removePackagesFromType(String type)
	{
		if(type.startsWith(RhinoJavaScriptAstParser.PACKAGES))
		{
			return RhinoJavaScriptAstParser.removePackages(type);
		}
		return type;
	}

	/**
	 * Look for class file using imported classes
	 * @param manager
	 * @param name
	 * @return
	 */
	private ClassFile findFromClasses(JarManager manager, String name) {
		ClassFile file = null;
		for (String cls : importClasses) {
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
		for (String packageName : importPackages) {
			String cls = name.startsWith(".") ? (packageName + name) : (packageName + "." + name);   
			file = manager.getClassEntry(cls);
			if (file != null)
				break;
		}
		return file;
	}
	
	
}
